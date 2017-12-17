package com.yzrilyzr.FAQ.Main;

import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import com.yzrilyzr.FAQ.Data.FileObj;
import org.apache.commons.codec.binary.Base64;

public class ControlService extends BaseService
{
	private String deckey;
	private boolean isLogin=false;
	private CopyOnWriteArrayList<String> mCmds;
	private Thread cmdexec;
	private Server.Scan scan;
	public ControlService(Socket s,Server m)
	{
		super(s,m);
		TAG="ControlClient";
		setName("FAQServer_ControlService");
		cmdexec=new Thread("FAQServer_ControlCmdExec("+IP+")"){
			@Override public void run(){
				mCmds=new CopyOnWriteArrayList<String>();
				scan=new Server.Scan(mCmds);
				while(running){
					ctx.exec(scan);
				}
			}
		};
		cmdexec.start();
	}
	@Override
	public void onRead(BufferedInputStream buff)
	{
		String str=null;
		try
		{
			BO bo=new BO(){
				@Override
				public boolean g()
				{
					return running;
				}
			};
			block(buff,1,bo);
			if(running)
			{
				byte cmd=(byte)buff.read();
				int pwdlen=readIntFully(buff,bo);
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
				if(cmd==C.HBT)isActive=true;
				else if(cmd==C.ENC)
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
					Data.loginControl.put(IP,this);
					isLogin=true;
				}
				else if(isLogin)
				{
					if(cmd==C.EXE){
						String[] cmds=str.split(" ");
						for(String g:cmds)mCmds.add(g);
					}
					else if(cmd==C.GFE){
						File file=new SafeFile(Data,true,Data.rootFile+str);
						if(!file.exists())sendMsg(C.GFE,"FNE");
						File[] fs=file.listFiles();
						StringBuilder bd=new StringBuilder();
						bd.append(file.getTotalSpace());
						bd.append("<?|*>");
						bd.append(file.getFreeSpace());
						bd.append("<?|*>");
						for(File f:fs){
							FileObj o=new FileObj(Data.rootFile,f);
							bd.append(o.o2s());
							bd.append("<?|*>");
						}
						sendMsg(C.GFE,bd.toString());
					}
				}
				else Toast("Thread","指令:"+cmd+",接收:"+str);
			}
		}
		catch(Throwable e)
		{
			Toast("ParseError","未知请求:"+str+",原因:"+Data.getStackTrace(e));
		}
	}
	@Override
	public void onDestroy()
	{
		scan.stop();
		cmdexec.interrupt();
	}
	public void sendMsg(byte cmd)
	{
		sendMsg(cmd,null);
	}
	public void sendMsg(byte CMD,String ss)
	{
		String s=ss;
		try
		{
			if(s!=null)
			{
				if(deckey!=null)s=AES.encrypt(deckey,s);
				Writer.write(CMD);
				writeStr(Writer,s);
			}
			else
			{
				Writer.write(CMD);
				writeStr(Writer,"");
			}
			Writer.flush();
		}
		catch (Exception e)
		{
			//Toast("Error","sendMsg",Data.getStackTrace(e));
		}
		/*if(CMD!=C.HBT&&CMD!=C.MSG&&CMD!=C.GUS&&
		CMD!=C.GHG&&CMD!=C.GHU
		)Toast("sendMsg","指令:"+CMD+",消息:"+ss);*/
	}
}
