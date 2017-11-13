package com.yzrilyzr.FAQ.Main;

import com.yzrilyzr.FAQ.Server.ConsoleMsg;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
public abstract class BaseService extends RU
{
	protected Socket socket;
	protected Server ctx;
	protected BufferedOutputStream Writer;
	public String IP,LOCATION;
	public String TAG="BaseClient";
	public boolean isActive=true;
	public boolean running=true;
	protected Data Data;
	public BaseService(Socket s, Server c)
	{
		setName("FAQServer_BaseService");
		try
		{
			this.ctx=c;
			this.Data=ctx.Data;
			socket=s;
			s.setKeepAlive(true);
			s.setTcpNoDelay(true);
			//s.setSendBufferSize(1024);
			s.setTrafficClass(0x04|0x10);
			s.setSoTimeout(10000);
			//s.setReceiveBufferSize(10240);
			isActive=true;
			Writer=new BufferedOutputStream(s.getOutputStream());
			IP=socket.getInetAddress().getHostAddress();
		}
		catch (Exception e)
		{
			Toast("Error","Thread",Data.getStackTrace(e));
		}
	}

	@Override
	public void run()
	{
		// TODO: Implement this method
		super.run();
		try
		{
			if(Data.blacklist.get(IP)!=null)
			{
				Toast("Thread","此ip在黑名单");
				running=false;
				isActive=false;
				socket.close();
				return;
			}
			if(Data.mailCd.get(IP)==null)Data.mailCd.put(IP,"0");
			Toast("Thread","已连接,地区:"+(LOCATION=getAddressByIP(IP)));
			BufferedInputStream buff=new BufferedInputStream(socket.getInputStream());
			while(running)
			{
				onRead(buff);
			}
			Writer.close();
			buff.close();
			socket.close();
		}
		catch(Throwable e)
		{
			Toast("Error","Thread",Data.getStackTrace(e));
		}
		finally
		{
			running=false;
			isActive=false;
			Data.onlineClient.remove(this);
			onDestroy();
			Toast("Thread","断开连接");
		}
	}
	public abstract void onDestroy();
	public abstract void onRead(BufferedInputStream buff);
	public void Toast(String tag,String at,String msg)
	{
		ConsoleMsg c=new ConsoleMsg(tag,at,msg,IP);
		ctx.toast(c);
	}
	public void Toast(String at,String msg)
	{
		Toast(TAG,at,msg);
	}
	public String getAddressByIP(String strIP)
	{ 
		try
		{
			URL url = new URL("http://m.tool.chinaz.com/ipsel?IP="+ strIP); 
			URLConnection conn = url.openConnection(); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); 
			String line = null; 
			StringBuffer result = new StringBuffer(); 
			while((line = reader.readLine()) != null)result.append(line);
			reader.close();
			strIP=result.toString();
			//Toast(TAG,"gL",strIP);
			strIP = strIP.substring(strIP.indexOf("物理位置")+28);
			strIP=strIP.substring(0,strIP.indexOf("</b>"));
			//strIP=strIP.substring(0,strIP.indexOf(" "));
			return strIP;
		}
		catch( IOException e)
		{
			Toast("getLocation",e.toString());
			return "读取失败"; 
		}
	}
}
