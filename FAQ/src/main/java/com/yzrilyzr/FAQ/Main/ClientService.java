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

public class ClientService
{
	public static Socket socket;
	public static BufferedOutputStream Writer;
	public static String hostIp="112.194.46.115";
	public static String deckey=null;
	public static boolean running=false;
	public static boolean isLogin=false;
	private static String myfaq,mypwd;
	public interface Listener
	{
		public abstract void rev(byte cmd,String msg);
	}
	private static CopyOnWriteArrayList<Listener> msginf=new CopyOnWriteArrayList<Listener>();
	public static void connect() throws IOException
	{
		if(socket==null)
		{
			socket=new Socket();
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);
			socket.setSendBufferSize(10240);
			socket.setTrafficClass(0x04|0x10);
			socket.setSoTimeout(40000);
			socket.setReceiveBufferSize(10240);
			socket.connect(new InetSocketAddress(hostIp,10000),40000);
			running=true;
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
							if(System.currentTimeMillis()-HBTtime>40000l)running=false;
							try
							{
								int msglen=0;
								while(buff.available()<4&&running)
								{}
								msglen=Data.getInt(new byte[]{(byte)buff.read(),(byte)buff.read(),(byte)buff.read(),(byte)buff.read()});
								while(buff.available()<1&&running)
								{}
								byte cmd=(byte)buff.read();
								byte[] by=new byte[msglen];
								while(buff.available()<msglen&&running)
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
	public static void sendStream(InputStream is)
	{

	}
	public static void login(String fa,String pwd)
	{
		//sendMsg("LGN");
		try
		{
			sendMsg(C.USL,fa);
			sendMsg(C.PWL,pwd);
			myfaq=fa;
			mypwd=pwd;
		}
		catch(Throwable e)
		{
		}
	}
	
}
