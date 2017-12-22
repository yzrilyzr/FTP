package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Server.ConsoleMsg;
import com.yzrilyzr.FAQ.Server.Server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public abstract class UdpService extends RU implements Runnable
{
	private DatagramPacket packet;
	Server ctx;
	public String TAG="BaseClient";
	protected Data Data;
	public String IP,LOCATION,deckey;
	public SocketAddress address;
	public UdpService(DatagramPacket packet, Server ctx)
	{
		this.packet = packet;
		this.ctx = ctx;
		this.Data=ctx.Data;
		address=packet.getSocketAddress();
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
			DatagramPacket p=null;
			if(ss!=null)
			{
				if(deckey!=null)ss=AES.encrypt(deckey,ss);
				byte[] b=ss.getBytes();
				byte[] b2=new byte[b.length+1];
				b2[0]=CMD;
				System.arraycopy(b,0,b2,0,b.length);
				b=null;
				p=new DatagramPacket(b2,b2.length,address);
			}
			else
			{
				p=new DatagramPacket(new byte[]{CMD},1,address);
			}
			DatagramSocket so=new DatagramSocket();
			so.send(p);
			so.close();
		}
		catch (Exception e)
		{
			Toast("Error","sendMsg",Data.getStackTrace(e));
		}
		if(CMD!=C.HBT&&CMD!=C.MSG&&CMD!=C.GUS&&
		CMD!=C.GHG&&CMD!=C.GHU
		)Toast("sendMsg","指令:"+CMD+",消息:"+ss);
	}
	public static void sendMsg(byte CMD,String deckey,SocketAddress address,String ss) throws Exception
	{
		DatagramPacket p=null;
		if(ss!=null)
		{
			if(deckey!=null)ss=AES.encrypt(deckey,ss);
			byte[] b=ss.getBytes();
			byte[] b2=new byte[b.length+1];
			b2[0]=CMD;
			System.arraycopy(b,0,b2,0,b.length);
			b=null;
			p=new DatagramPacket(b2,b2.length,address);
		}
		else
		{
			p=new DatagramPacket(new byte[]{CMD},1,address);
		}
		DatagramSocket so=new DatagramSocket();
		so.send(p);
		so.close();
	}
	public abstract void rev(DatagramPacket p)throws Throwable;
}
