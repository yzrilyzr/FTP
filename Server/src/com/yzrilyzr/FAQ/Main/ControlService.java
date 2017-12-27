package com.yzrilyzr.FAQ.Main;

import com.yzrilyzr.FAQ.Data.FileObj;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.File;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ControlService extends UdpService
{
	public ControlService(DatagramPacket s,Server m)
	{
		super(s,m);
		TAG="ControlClient";
		setName("FAQServer_ControlService");
	}
	@Override
	public void rev(DatagramPacket p)
	{
		byte[] byt=p.getData();
		byte cmd=byt[0];
		String str=new String(byt,p.getOffset()+1,p.getLength()-1);
		if(cmd==C.ENC)
		{
			int r=new Random().nextInt(866511684)+100000;
			String enc=Integer.toHexString(r);
			sendMsg(C.ENC,r+"");
			deckey=enc;
		}
		else if(cmd==C.LOG)
		{
			Toast("Thread","日志:"+str);
		}
		else if(cmd==C.LGN)
		{
			Data.loginControl.put(IP,new LoginClient(deckey,null,address));
			Toast("Thread","控制端已登录");
			sendMsg(C.LGN);
		}
		else if(Data.loginControl.get(IP)!=null)
		{
			if(cmd==C.EXE)
			{
				String[] cmds=str.split(" ");
				for(String g:cmds)ctx.mCmds.add(g);
			}
			else if(cmd==C.GFE)
			{
				File file=new SafeFile(Data,true,Data.rootFile+str);
				if(!file.exists())sendMsg(C.GFE,"FNE");
				File[] fs=file.listFiles();
				StringBuilder bd=new StringBuilder();
				bd.append(file.getTotalSpace());
				bd.append("<?|*>");
				bd.append(file.getFreeSpace());
				bd.append("<?|*>");
				for(File f:fs)
				{
					FileObj o=new FileObj(Data.rootFile,f);
					bd.append(o.o2s());
					bd.append("<?|*>");
				}
				sendMsg(C.GFE,bd.toString());
			}
		}
		///else
		Toast("Thread","指令:"+cmd+",接收:"+str);
	}
}
