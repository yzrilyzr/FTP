package com.yzrilyzr.FAQ.Main;
import java.io.*;

import android.app.Activity;
import android.widget.Toast;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.myclass.util;
import java.net.Socket;
import java.util.ArrayList;

public class ClientService
{
	public static Socket socket;
	public static BufferedOutputStream Writer;
	public static String hostIp="112.194.46.115";
	static String deckey=null;
	private static boolean running=true;
	public static boolean isLogin=false;
	public interface Listener
	{
		public abstract void rev(byte cmd,String msg);
	}
	private static ArrayList<Listener> msginf=new ArrayList<Listener>();
	public static void connect() throws IOException
	{
		if(socket==null)
		{
			running=true;
			//hostIp="192.168.0.2";
			//hostIp="192.168.43.1";
			socket=new Socket(hostIp,10000);
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);
			socket.setSendBufferSize(10240);
			startService();
			Writer=new BufferedOutputStream(socket.getOutputStream());
			sendMsg(C.ENC);
		}
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
						long HBTtime=System.currentTimeMillis();
						BufferedInputStream buff=new BufferedInputStream(socket.getInputStream());
						while(running)
						{
							if(System.currentTimeMillis()-HBTtime>5000l)running=false;
							try
							{
								int msglen=0;
								//for(int p=0;p<by.length;p+=(msglen+5))
								{
									while(buff.available()<4)
									{}
									msglen=Data.getInt(new byte[]{(byte)buff.read(),(byte)buff.read(),(byte)buff.read(),(byte)buff.read()});
									while(buff.available()<1)
									{}
									byte cmd=(byte)buff.read();
									byte[] by=new byte[msglen];
									while(buff.available()<msglen)
									{}
									buff.read(by);
									String str=new String(by);
									if(deckey!=null&&str!=null&&!"".equals(str))str=AES.decrypt(deckey,str);
									//str=new String(Base64.decode(str,0));
									if(cmd==C.HBT)
									{
										HBTtime=System.currentTimeMillis();
										sendMsg(C.HBT);
									}
									else if(cmd==C.ENC)deckey=str;
									else
									{
										for(Listener o:msginf)
											o.rev(cmd,str);
									}
								}
							}
							catch(Throwable e)
							{
								sendMsg(C.LOG,"<Client>"+getStackTrace(e));
							}

						}
						socket=null;
						boolean isc=true;
						while(isc)
							try
							{
								deckey=null;
								connect();
								Thread.sleep(1000);
								login(Data.me.faq+"",Data.me.pwd);
								isc=false;
							}
							catch (Exception e)
							{
								try
								{
									Thread.sleep(1000);
								}
								catch (InterruptedException ey)
								{}
							}
					}
					catch(Throwable e)
					{
						sendMsg(C.LOG,getStackTrace(e));
					}
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
				byte[] a=s.getBytes();
				//String aa=Base64.encodeToString(a,0);
				if(deckey!=null)a=AES.encrypt(deckey,s).getBytes();
				byte[] b=new byte[a.length+5];
				byte[] c=Data.getIBytes(a.length);
				b[4]=cmd;
				b[0]=c[0];
				b[1]=c[1];
				b[2]=c[2];
				b[3]=c[3];
				int e=5;
				for(byte d:a)b[e++]=d;
				Writer.write(b);
			}
			else
			{
				byte[] b=new byte[5];
				byte[] c=Data.getIBytes(0);
				b[4]=cmd;
				b[0]=c[0];
				b[1]=c[1];
				b[2]=c[2];
				b[3]=c[3];
				Writer.write(b);
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
			Data.me=new User();
			Data.me.faq=Integer.parseInt(fa);
			Data.me.pwd=pwd;
			sendMsg(C.USL,fa);
			sendMsg(C.PWL,pwd);
		}
		catch(Throwable e)
		{
		}
	}
	
	public static void toast(final Activity ctx,final Object o)
	{
		ctx.runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					Toast.makeText(ctx,o+"",0).show();
				}
			});
	}
	

}
