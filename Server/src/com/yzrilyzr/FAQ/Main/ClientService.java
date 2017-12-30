package com.yzrilyzr.FAQ.Main;

import com.yzrilyzr.FAQ.Data.Group;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.net.InetSocketAddress;

public class ClientService extends UdpService
{
	public ClientService(DatagramPacket s,Server m)
	{
		super(s,m);
		TAG="FAQClient";
		setName("FAQServer_ClientService");
	}

	@Override
	public void rev(DatagramPacket p)throws Throwable
	{
		byte[] byt=p.getData();
		byte cmd=byt[0];
		String str=new String(byt,p.getOffset()+1,p.getLength()-1);
		LoginClient cli=Data.loginClient.get(address);
		if(cli!=null)deckey=cli.deckey;
		if(deckey!=null&&str!=null&&!"".equals(str))str=AES.decrypt(deckey,str);
		if(cmd==C.ENC)
		{
			int r=new Random().nextInt(866511684)+100000;
			String enc=Integer.toHexString(r);
			sendMsg(C.ENC,r+"");
			cli.deckey=enc;
		}
		else if(cmd==C.CON){
			Data.loginClient.put(address,new LoginClient());
			sendMsg(C.CON);
		}
		else if(cmd==C.LGN)
		{
			User user=(User) ToStrObj.s2o(str);//用户提供
			User db=Data.users.get(user.faq+"");//数据库的
			if(db==null)sendMsg(C.LFL);
			else if(db.pwd.equals(user.pwd))
			{
				sendMsg(C.LSU);
				Data.users.get(Integer.toString(db.faq)).ip=IP;
				cli.user=db;
				cli.isLogin=true;
				//if(cli!=null)UdpService.sendMsg(C.FLO,sc.deckey,sc.address,"您的帐号在另外一个客户端登录，您已被强制下线");
			}
			else sendMsg(C.LFL);
		}
		else if(cmd==C.REG)
		{
			User u=(User) ToStrObj.s2o(str);
			if(!cli.verifycode.equals(u.nick))
			{
				sendMsg(C.VFE);
			}
			else
			{
				u=Data.register(IP,u.pwd,u.email);
				if(u!=null)sendMsg(C.RSU,""+u.faq);
				else sendMsg(C.RFL);
			}
		}
		else if(cmd==C.LGO)
		{
			cli.isLogin=false;
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
				cli.verifycode="123456";//(new Random().nextInt(899999)+100000)+"";
				Toast("Thread","验证码"+cli.verifycode);
				sendEmail("faq_service@16.com","noti supporti","FAQ——为所欲为的开车(注册)",str.substring(3),"本次验证码:"+cli.verifycode+"\n请尽快注册");
				Data.mailCd.put(IP,""+System.currentTimeMillis());
			}
			else
			{
				sendMsg(C.VFD,""+((300000l-System.currentTimeMillis()+cd)/1000l));
			}
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
				//sendMsg(C.GHU,new String(Base64.encodeBase64(bbbb)));
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
				//sendMsg(C.GHG,new String(Base64.encodeBase64(bbbb)));
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
		else if(cli.isLogin)
		{
			if(cmd==C.MSG)
			{
				MessageObj m=(MessageObj) ToStrObj.s2o(str);
				if(m.isGroup)
				{
					Group g=Data.groups.get(m.to+"");
					for(int f:g.members)sendMsgToOther(C.MSG,f,str);
				}
				else sendMsgToOther(C.MSG,m.to,str);
				if(m.type==T.MSG||m.type==T.VMS)
				{
					Toast("Thread",String.format("指令:%d,来自:%d,发给:%d,种类:%d,是否群组:%b,消息:%s",cmd,m.from,m.to,m.type,m.isGroup,m.msg));
					Data.msgBuffer.add(m);
				}
				//ctx.toast("<Client>(Thread)["+IP+"]:Send to:"+m.to+" Success");
			}
			else if(cmd==C.AFD)
			{
				User u1=Data.users.get(cli.user.faq+"");
				User u2=Data.users.get(str);
				boolean bool=false;
				for(int n:u1.friends)if(n==u2.faq)
					{bool=true;break;}
				Toast("Thread","指令:"+cmd+",添加好友:"+str);
				if(!bool)
				{
					u1.friends.add(u2.faq);
					u2.friends.add(u1.faq);
					Data.saveUserData();
					sendMsg(C.GUS,u1.o2s());
					sendMsg(C.MSG,new MessageObj(u2.faq,u1.faq,T.MSG,false,"我们已经是好友了，快来一起开车吧！").setTime().o2s());
					sendMsgToOther(C.GUS,u2.faq,u2.o2s());
					sendMsgToOther(C.MSG,u2.faq,new MessageObj(u1.faq,u2.faq,T.MSG,false,"我们已经是好友了，快来一起开车吧！").setTime().o2s());
				}
			}
		}
		else Toast("Thread","指令:"+cmd+",接收:"+str);
	}
	public void sendMsgToOther(byte cmd,int to,String str) throws Exception
	{
		sendMsgToOther(Data,cmd,to,str);
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
