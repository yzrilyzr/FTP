package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Data.User;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientService extends RU
{
	public static String hostIp="192.168.0.3";
	public static String deckey=null;
	public static boolean isLogin=false;
	private static String myfaq,mypwd;
	private static DatagramSocket so;
	public interface Listener
	{
		public abstract void rev(byte cmd,String msg);
	}
	private static CopyOnWriteArrayList<Listener> msginf=new CopyOnWriteArrayList<Listener>();
	private static CopyOnWriteArrayList<Runnable> sendmsg=new CopyOnWriteArrayList<Runnable>();
	public static void startService()
	{
		try
		{
			so=new DatagramSocket();
		}
		catch (SocketException e)
		{}
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					try
					{
						while(true)
						{
							byte[] bb=new byte[1024];
							DatagramPacket pa=new DatagramPacket(bb,bb.length);
							so.receive(pa);
							try
							{
								byte[] byt=pa.getData();
								byte cmd=byt[0];
								String str=new String(byt,pa.getOffset()+1,pa.getLength()-1);
								if(deckey!=null&&str!=null&&!"".equals(str))str=AES.decrypt(deckey,str);
								//str=new String(Base64.decode(str,0));
								if(cmd==C.HBT)
								{
									sendMsg(C.HBT);
								}
								else if(cmd==C.ENC)deckey=Integer.toHexString(Integer.parseInt(str));
								else
								{
									for(Listener o:msginf)
										o.rev(cmd,str);
								}
							}
							catch(Throwable e)
							{
								sendMsg(C.LOG,"<Client>"+getStackTrace(e));
							}

						}
					}
					catch(Throwable e)
					{
						sendMsg(C.LOG,getStackTrace(e));
					}
				}
			}).start();
		new Thread(){
			@Override public void run(){
				while(true)
					if(sendmsg.size()>0)sendmsg.remove(0).run();
			}
		}.start();
	}
	public static void addListener(Listener o)
	{
		msginf.add(o);
	}
	public static void removeListener(Listener o)
	{
		msginf.remove(o);
	}
	public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try
        {
            t.printStackTrace(pw);
            return sw.toString();
        }
        finally
        {
            pw.close();
        }
    }
	public static void sendMsg(byte cmd)
	{
		sendMsg(cmd,null);
	}
	public static void sendMsg(final byte cmd,final String ss)
	{
		sendmsg.add(new Runnable(){
				@Override
				public void run()
				{
					try
					{
						DatagramPacket p=null;
						String s=ss;
						if(s!=null)
						{
							if(deckey!=null)s=AES.encrypt(deckey,s);
							ByteArrayOutputStream os=new ByteArrayOutputStream();
							os.write(cmd);
							os.write(s.getBytes());
							os.flush();
							os.close();
							byte[] b2=os.toByteArray();
							p=new DatagramPacket(b2,b2.length,new InetSocketAddress(hostIp,10000));
						}
						else
						{
							p=new DatagramPacket(new byte[]{cmd},1,new InetSocketAddress(hostIp,10000));
						}
						so.send(p);
					}
					catch (Throwable e)
					{
					}
				}
			});
	}
	public static void login(String fa,String pwd)
	{
		//sendMsg("LGN");
		try
		{
			User u=new User();
			u.faq=Integer.parseInt(fa);
			Data.myfaq=u.faq;
			Data.resetMyself();
			u.pwd=pwd;
			sendMsg(C.LGN,u.o2s());
			myfaq=fa;
			mypwd=pwd;
		}
		catch(Throwable e)
		{
		}
	}

}
