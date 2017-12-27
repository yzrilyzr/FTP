package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Server.Server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

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
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			//os.write(1);
			ctx.onGetScreen(os);
			//os.close();
			byte[] bb=os.toByteArray();
			if(bb.length!=0){
				DatagramSocket s=new DatagramSocket();
				s.send(new DatagramPacket(bb,bb.length,address));
				s.close();
			}
		}
	}

}
