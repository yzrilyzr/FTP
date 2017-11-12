package com.yzrilyzr.FAQ.Main;

import com.yzrilyzr.FAQ.Data.Group;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;

public class ClientService extends BaseService
{
	public User user=null;
	String deckey=null;
	private String verifycode="";
	public ClientService(Socket s,Server ctx)
	{
		super(s,ctx);
		TAG="FAQClient";
		setName("FAQServer_ClientService");
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
					// TODO: Implement this method
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
				while(ind<pwdlen){
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
				else if(cmd==C.LGN)
				{
					User u=(User) ToStrObj.s2o(str);
					User u2=Data.users.get(u.faq+"");
					if(u2==null)sendMsg(C.LFL);
					else if(u2.pwd.equals(u.pwd))
					{
						sendMsg(C.LSU);
						user=u2;
						ClientService sc=Data.loginClient.get(user.faq+"");
						if(sc!=null)
						{
							sc.sendMsg(C.FLO,"您的帐号在另外一个客户端登录，您已被强制下线");
							Data.loginClient.remove(user.faq+"");
						}
						Data.loginClient.put(user.faq+"",this);
					}
					else sendMsg(C.LFL);
				}
				else if(cmd==C.REG)
				{
					User u=(User) ToStrObj.s2o(str);
					if(!verifycode.equals(u.nick))
					{
						sendMsg(C.VFE);
					}
					else
					{
						u=Data.register(socket,u.pwd,u.email);
						if(u!=null)sendMsg(C.RSU,""+u.faq);
						else sendMsg(C.RFL);
					}
				}
				else if(cmd==C.LGO){
					Data.loginClient.remove(user.faq+"");
					Toast("Thread","用户登出");
				}
				else if(cmd==C.LOG)
				{
					Toast("Thread","日志:"+str);
				}
				else if(cmd==C.VFC)
				{
					long cd=Long.parseLong(Data.mailCd.get(IP));
					if(System.currentTimeMillis()-cd>300000)
					{
						verifycode="123456";//(new Random().nextInt(899999)+100000)+"";
						Toast("Thread","验证码"+verifycode);
						sendEmail("faq_service@16.com","noti supporti","FAQ——为所欲为的开车(注册)",str.substring(3),"本次验证码:"+verifycode+"\n请尽快注册");
						Data.mailCd.put(IP,""+System.currentTimeMillis());
					}
					else
					{
						sendMsg(C.VFD,""+((300000l-System.currentTimeMillis()+cd)/1000l));
					}
				}
				else if(cmd==C.MSG)
				{
					MessageObj m=(MessageObj) ToStrObj.s2o(str);

					if(m.isGroup)
					{
						Group g=Data.groups.get(m.to+"");
						for(int f:g.members)
						{
							ClientService c=Data.loginClient.get(f+"");
							if(c!=null)c.sendMsg(C.MSG,str);
						}
					}
					else
					{
						ClientService c=Data.loginClient.get(m.to+"");
						if(c!=null)c.sendMsg(C.MSG,str);
					}
					if(m.type==T.MSG||m.type==T.VMS)
					{
						Toast("Thread",String.format("指令:%d,来自:%d,发给:%d,种类:%d,是否群组:%b,消息:%s",cmd,m.from,m.to,m.type,m.isGroup,m.msg));
						Data.msgBuffer.add(m);
					}
					//ctx.toast("<Client>(Thread)["+IP+"]:Send to:"+m.to+" Success");
				}
				else if(cmd==C.GHU)
				{
					try
					{
						BufferedInputStream his=new BufferedInputStream(new FileInputStream(new SafeFile(Data,false,Data.datafile+"/head/"+str+".user.png")));
						byte[] bbbb=new byte[his.available()+4];
						byte[] cc=getIBytes(Integer.parseInt(str));
						for(int ic=0;ic<4;ic++)bbbb[ic]=cc[ic];
						his.read(bbbb,4,bbbb.length-4);his.close();
						sendMsg(C.GHU,new String(Base64.encodeBase64(bbbb)));
						Toast("getHead",str);
					}
					catch(Throwable e)
					{
						sendMsg(C.GHU,"-1");
					}
				}
				else if(cmd==C.GHG)
				{
					try
					{
						BufferedInputStream his=new BufferedInputStream(new FileInputStream(new SafeFile(Data,false,Data.datafile+"/head/"+str+".group.png")));
						byte[] bbbb=new byte[his.available()+4];
						byte[] cc=getIBytes(Integer.parseInt(str));
						for(int ic=0;ic<4;ic++)bbbb[ic]=cc[ic];
						his.read(bbbb,4,bbbb.length-4);his.close();
						sendMsg(C.GHG,new String(Base64.encodeBase64(bbbb)));
						Toast("getGroupHead",str);
					}
					catch(Throwable e)
					{
						sendMsg(C.GHG,"-1");
					}
				}
				else if(cmd==C.GGR)
				{
					Group uu=Data.groups.get(str);
					if(uu==null)sendMsg(C.GGR,"-1");
					else
					{
						Group u2=(Group)ToStrObj.s2o(uu.o2s());
						sendMsg(C.GGR,u2.o2s());
					}
				}
				else if(cmd==C.AFD)
				{
					User u1=Data.users.get(user.faq+"");
					User u2=Data.users.get(str);
					boolean bool=false;
					for(int n:u1.friends)if(n==u2.faq)
						{bool=true;break;}
					Toast("Thread","指令:"+cmd+",添加好友:"+bo);
					if(!bool)
					{
						u1.friends.add(u2.faq);
						u2.friends.add(u1.faq);
						Data.saveUserData();
						sendMsg(C.GUS,u1.o2s());
						sendMsg(C.MSG,new MessageObj(u2.faq,u1.faq,T.MSG,false,"我们已经是好友了，快来一起开车吧！").setTime().o2s());
						ClientService ll=Data.loginClient.get(u2.faq+"");
						if(ll!=null)
						{
							ll.sendMsg(C.GUS,u2.o2s());
							ll.sendMsg(C.MSG,new MessageObj(u1.faq,u2.faq,T.MSG,false,"我们已经是好友了，快来一起开车吧！").setTime().o2s());
						}
					}
				}
				else if(cmd==C.GUS)
				{
					if(!"1000".equals(str))
					{
						User uu=Data.users.get(str);
						Toast("Thread",String.format("获取用户:%s",str));
						if(uu==null)sendMsg(C.GUS,"-1");
						else
						{
							User u2=(User)ToStrObj.s2o(uu.o2s());
							u2.pwd="";
							u2.ip="";u2.email="";
							sendMsg(C.GUS,u2.o2s());
						}
					}
				}
				else if(cmd==C.UPD)
				{
					//最新，最低支持=
					sendMsg(C.UPD,new String(getIBytes(2))+"/"+new String(getIBytes(2)));
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
		// TODO: Implement this method
		if(user!=null)Data.loginClient.remove(user.faq+"");
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
			Toast("Error","sendMsg",Data.getStackTrace(e));
		}
		if(CMD!=C.HBT&&CMD!=C.MSG&&CMD!=C.GUS&&
		CMD!=C.GHG&&CMD!=C.GHU
		)Toast("sendMsg","指令:"+CMD+",消息:"+ss);
	}
	public void sendEmail(final String sender,final String pwd,final String sub,final String to,final String s)
	{
		new Thread(Thread.currentThread().getThreadGroup(),new Runnable(){
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				try
				{
					Properties props = new Properties();
					props.put("mail.smtp.host", "smtp.163.com");
					props.put("mail.smtp.auth", "true");
					props.setProperty("mail.transport.protocol", "smtp");
					Session session = Session.getInstance(props);
					MimeMessage message = new MimeMessage(session);
					message.setSubject(sub);
					message.setFrom(new InternetAddress(sender));
					message.setRecipient(Message.RecipientType.CC,new InternetAddress(sender));
					message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
					message.setText(s);
					message.setSentDate(new Date());
					message.saveChanges();
					Transport transport = session.getTransport();
					transport.connect(sender,pwd);
					transport.sendMessage(message,message.getAllRecipients());
					transport.close();
				}
				catch (Throwable e)
				{
					Toast("sendEmail",Data.getStackTrace(e));
				}
			}
		},"FAQServer_ClientService_Email").start();
	}
}
