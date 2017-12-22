package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Server.Server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RemoteControlService extends UdpService
{
	public RemoteControlService(DatagramPacket s,Server m)
	{
		super(s,m);
		TAG="RemoteControlClient";
		setName("FAQServer_RemoteControlService");
	}
	@Override
	public void rev(DatagramPacket p)throws Throwable
	{
		byte[] byt=p.getData();
		int r=byt[0];
		if(r==1)
		{
			byte[] bb=ctx.onGetScreen();
			if(bb!=null)
			{
				DatagramSocket s=new DatagramSocket();
				s.send(new DatagramPacket(bb,bb.length,address));
				s.close();
			}
		}
	}

}
