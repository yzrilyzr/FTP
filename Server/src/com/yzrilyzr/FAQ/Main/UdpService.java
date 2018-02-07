package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Server.ConsoleMsg;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class UdpService extends RU implements Runnable
{
	private DatagramPacket packet;
	public Server ctx;
	public String TAG="BaseClient";
	public Data Data;
	public String IP,LOCATION,deckey;
	public InetSocketAddress address;
	public UdpService(DatagramPacket packet,Server ctx)
	{
		this.packet = packet;
		this.ctx = ctx;
		this.Data=ctx.Data;
		address=(InetSocketAddress) packet.getSocketAddress();
		IP=packet.getAddress().getHostAddress();
		if(!Data.connectedClient.contains(IP))Data.connectedClient.add(IP);
	}
	public void setName(String s)
	{
		Thread.currentThread().setName(s);
	}
	@Override
	public void run()
	{
		try
		{
			//Toast("Thread","已连接:"+address);
			if(Data.blacklist.get(IP)!=null)
			{
				Toast("Thread","此ip在黑名单");
				return;
			}
			if(Data.mailCd.get(IP)==null)Data.mailCd.put(IP,"0");
			rev(packet);
		}
		catch(Throwable e)
		{
			Toast("Error","Thread",Data.getStackTrace(e));
		}
	}
	public void Toast(String tag,String at,String msg)
	{
		ConsoleMsg c=new ConsoleMsg(tag,at,msg,IP);
		ctx.toast(c);
	}
	public void Toast(String at,String msg)
	{
		Toast(TAG,at,msg);
	}
	public void sendMsg(byte cmd)
	{
		sendMsg(cmd,null);
	}
	public void sendMsg(byte CMD,String ss)
	{
		try
		{
			sendMsg(CMD,deckey,address,ss);
		}
		catch (Exception e)
		{
			Toast("Error","sendMsg",Data.getStackTrace(e));
		}
		if(CMD!=C.HBT&&CMD!=C.MSG&&CMD!=C.GUS&&
		CMD!=C.GHG&&CMD!=C.GHU&&CMD!=C.GFE
		)Toast("sendMsg","指令:"+CMD+",消息:"+ss);
	}
	public static void sendMsg(byte CMD,String deckey,SocketAddress address,String ss) throws Exception
	{
		DatagramPacket p=null;
		if(ss!=null)
		{
			if(deckey!=null)ss=AES.encrypt(deckey,ss);
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			os.write(CMD);
			os.write(ss.getBytes());
			os.flush();
			os.close();
			byte[] b2=os.toByteArray();
			p=new DatagramPacket(b2,b2.length,address);
		}
		else
		{
			p=new DatagramPacket(new byte[]{CMD},1,address);
		}
		DatagramSocket soo=new DatagramSocket();
		soo.send(p);
		soo.close();
	}
	public static void sendMsgToOther(Data data,byte cmd,int to,String str) throws Exception
	{
		Iterator iter=data.loginClient.entrySet().iterator();
		while(iter.hasNext())
		{
			ConcurrentHashMap.Entry e=(Map.Entry)iter.next();
			try
			{
				LoginClient v=(LoginClient)e.getValue();
				InetSocketAddress k=(InetSocketAddress)e.getKey();
				if(v.user.faq==to)UdpService.sendMsg(cmd,v.deckey,k,str);
			}
			catch(Throwable e2)
			{}
		}
	}
	public static void sendMsgToOtherControl(Data data,byte cmd,String str) throws Exception
	{
		Iterator iter=data.loginControl.entrySet().iterator();
		while(iter.hasNext())
		{
			ConcurrentHashMap.Entry e=(ConcurrentHashMap.Entry)iter.next();
			LoginClient v=(LoginClient)e.getValue();
			InetSocketAddress k=(InetSocketAddress)e.getKey();
			UdpService.sendMsg(cmd,v.deckey,k,str);
		}
	}
	public abstract void rev(DatagramPacket p)throws Throwable;
}
