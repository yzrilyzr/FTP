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
	public void rev(DatagramPacket p)throws Throwable
	{
		byte[] byt=p.getData();
		byte cmd=byt[0];
		String str=new String(byt,p.getOffset()+1,p.getLength()-1);
		LoginClient cli=Data.loginControl.get(address);
		if(cli!=null)deckey=cli.deckey;
		if(deckey!=null&&str!=null&&!"".equals(str))str=AES.decrypt(deckey,str);
		if(cmd==C.ENC)
		{
			int r=new Random().nextInt(866511684)+100000;
			String enc=Integer.toHexString(r);
			sendMsg(C.ENC,r+"");
			cli.deckey=enc;
		}
		else if(cmd==C.LOG)
		{
			Toast("Thread","日志:"+str);
		}
		else if(cmd==C.CON){
			Data.loginControl.put(address,new LoginClient());
			sendMsg(C.CON);
		}
		else if(cmd==C.LGN)
		{
			cli.isLogin=true;
			Toast("Thread","控制端已登录");
			sendMsg(C.LGN);
		}
		else if(cli.isLogin)
		{
			if(cmd==C.EXE)
			{
				String[] cmds=str.split(" ");
				for(String g:cmds)ctx.mCmds.add(g);
			}
			else if(cmd==C.GFE)
			{
				File file=new SafeFile(Data,true,Data.rootFile+str);
				if(!file.exists())sendMsg(C.GFE,"N");
				File[] fs=file.listFiles();
				StringBuilder bd=new StringBuilder();
				bd.append("H")
				.append(file.getTotalSpace())
				.append(",")
				.append(file.getFreeSpace())
				.append(",")
				.append(fs.length);
				sendMsg(C.GFE,bd.toString());
				for(File f:fs)
				{
					FileObj o=new FileObj(Data.rootFile,f);
					sendMsg(C.GFE,new StringBuilder().append("F").append(o.o2s()).toString());
				}
				Thread.sleep(100);
				sendMsg(C.GFE,"E");
			}
		}
		///else
		Toast("Thread","指令:"+cmd+",接收:"+str);
	}
}
