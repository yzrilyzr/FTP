package com.yzrilyzr.FAQ.Main;
import java.io.*;

import android.app.Activity;
import android.widget.Toast;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.myclass.util;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import android.util.Base64;

public class ClientService extends RU
{
	public static Socket socket;
	public static BufferedOutputStream Writer;
	public static String hostIp="112.194.46.115";
	public static String deckey=null;
	public static boolean running=false;
	public static boolean isLogin=false;
	private static String myfaq,mypwd;
	private static long HBTtime;
	public interface Listener
	{
		public abstract void rev(byte cmd,String msg);
	}
	private static CopyOnWriteArrayList<Listener> msginf=new CopyOnWriteArrayList<Listener>();
	public static void connect() throws IOException
	{
		if(socket!=null)
		{
			socket.close();
			socket=null;
		}
		socket=new Socket();
		socket.setKeepAlive(true);
		socket.setTcpNoDelay(true);
		//socket.setSendBufferSize(10240);
		socket.setTrafficClass(0x04|0x10);
		socket.setSoTimeout(10000);
		//socket.setReceiveBufferSize(10240);
		socket.connect(new InetSocketAddress(hostIp,10000),40000);
		running=true;
		startService();
		Writer=new BufferedOutputStream(socket.getOutputStream());
		sendMsg(C.ENC);
	}
	public static void startService()
	{
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					try
					{
						HBTtime=System.currentTimeMillis();
						BufferedInputStream buff=new BufferedInputStream(socket.getInputStream());
						BO bo=new BO(){
							@Override
							public boolean g()
							{
								// TODO: Implement this method
								return running=getHbt(HBTtime);
							}
						};
						while(running)
						{
							running=getHbt(HBTtime);
							try
							{
								block(buff,1,bo);
								byte cmd=(byte)buff.read();
								int pwdlen=readIntFully(buff,bo);
								String str=null;
								ByteArrayOutputStream str2=new ByteArrayOutputStream();
								int ind=0;
								while(ind<pwdlen)
								{
									byte[] bb=new byte[pwdlen-ind];
									int ii=buff.read(bb);
									ind+=ii;
									str2.write(bb,0,ii);
								}
								str2.close();
								str=str2.toString();
								str2=null;
								if(deckey!=null&&str!=null&&!"".equals(str))str=AES.decrypt(deckey,str);
								//str=new String(Base64.decode(str,0));
								if(cmd==C.HBT)
								{
									HBTtime=System.currentTimeMillis();
									sendMsg(C.HBT);
								}
								else if(cmd==C.ENC)deckey=Integer.toHexString(Integer.parseInt(str));
								else if(cmd==C.GHG||cmd==C.GHU)
								{
									if("-1".equals(str))return;
									byte[] by=Base64.decode(str,0);
									Data.saveHead(cmd==C.GHG,by);
								}
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
						running=false;
						socket.close();
						socket=null;
						boolean isc=true;
						while(isc)
							try
							{
								deckey=null;
								connect();
								while(ClientService.deckey==null)
								{Thread.sleep(1);}
								login(myfaq,mypwd);
								isc=false;
							}
							catch (Exception e)
							{
							}
					}
					catch(Throwable e)
					{
						sendMsg(C.LOG,getStackTrace(e));
					}
				}

				private boolean getHbt(long HBTtim)
				{
					return running=System.currentTimeMillis()-HBTtim<40000l;
				}
			}).start();
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					try
					{
						while(running)
						{
							Data.saveMsgBuffer();
							Thread.sleep(5000);
						}
					}
					catch(Throwable e)
					{}
				}
			}).start();
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
	public static boolean sendMsg(byte cmd)
	{
		return sendMsg(cmd,null);
	}
	public static boolean sendMsg(byte cmd,String s)
	{
		try
		{
			if(s!=null)
			{
				if(deckey!=null)s=AES.encrypt(deckey,s);
				Writer.write(cmd);
				writeStr(Writer,s);
			}
			else
			{
				Writer.write(cmd);
				writeStr(Writer,"");
			}
			Writer.flush();
			return true;
		}
		catch (Throwable e)
		{
			return false;
		}
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
