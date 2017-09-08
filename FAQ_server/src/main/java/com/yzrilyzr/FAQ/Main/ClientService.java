package com.yzrilyzr.FAQ.Main;

import java.io.*;

import android.util.Base64;
import com.yzrilyzr.FAQ.Data.Group;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.FAQ.Server.MainActivity;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ClientService extends Thread
{
	public Socket socket;
	public boolean isActive=true;
	public boolean running=true;
	public User user=null;
	String deckey=null;
	public String IP,LOCATION;
	private String verifycode;
	static MainActivity ctx;
	public BufferedOutputStream Writer;
	public ClientService(Socket s,MainActivity ctx)
	{
		try
		{
			this.ctx=ctx;
			socket=s;
			s.setKeepAlive(true);
			s.setTcpNoDelay(true);
			s.setSendBufferSize(1024);
			isActive=true;
			Writer=new BufferedOutputStream(s.getOutputStream());
		}
		catch (Exception e)
		{
			ctx.toast(e);
		}
	}
	@Override
	public void run()
	{
		// TODO: Implement this method
		super.run();
		try
		{
			IP=socket.getInetAddress().getHostAddress();
			if(Data.blacklist.get(IP)!=null)
			{
				ctx.toast("<Client>(Thread)["+IP+"]:This ip is at blacklist");
				socket.close();
				return;
			}
			if(Data.mailCd.get(IP)==null)Data.mailCd.put(IP,"0");
			ctx.toast("<Client>(Thread)["+IP+"]:Connected,Location:"+(LOCATION=getAddressByIP(IP)));
			BufferedInputStream buff=new BufferedInputStream(socket.getInputStream());
			while(running)
			{
				String str=null;
				try
				{
					int msglen=0;
					//for(int pa=0;pa<by.length;pa+=(msglen+5))
					{
						while(buff.available()<4)
						{}
						msglen=getInt(new byte[]{(byte)buff.read(),(byte)buff.read(),(byte)buff.read(),(byte)buff.read()});
						if(msglen>10240)
						{
							byte[] err=new byte[buff.available()];
							buff.read(err);
							str=new String(err);
							throw new HttpRequest(new String(getIBytes(msglen))+str);
						}
						while(buff.available()<1)
						{}
						byte cmd=(byte)buff.read();
						byte[] by=new byte[msglen];
						while(buff.available()<msglen)
						{}
						buff.read(by);
						str=new String(by);
						if(deckey!=null&&str!=null&&!"".equals(str))str=AES.decrypt(deckey,str);
						//str=new String(Base64.decode(str,0));
						if(cmd==C.HBT)isActive=true;
						else if(cmd==C.ENC)
						{
							String enc=Integer.toHexString(new Random().nextInt(866511684)+100000);
							sendMsg(C.ENC,enc);
							deckey=enc;
						}
						else if(cmd==C.USL)
						{
							user=Data.users.get(str);
							if(user==null)sendMsg(C.USE);
						}
						else if(cmd==C.PWL&&user!=null)
						{
							if(user.pwd.equals(str))
							{
								sendMsg(C.LSU);
								ClientService sc=Data.loginClient.get(user.faq+"");
								if(sc!=null)
								{
									sc.sendMsg(C.FLO,"您的帐号在另外一个客户端登录，您已被强制下线");
									Data.loginClient.remove(user.faq+"");
								}
								Data.loginClient.put(user.faq+"",this);
							}
							else sendMsg(C.PWE);
						}
						else if(cmd==C.PWR)
						{
							user=new User();
							user.pwd=str;
						}
						else if(cmd==C.VFR)
						{
							user.sign=str;
						}
						else if(cmd==C.EMR)
						{
							if(!verifycode.equals(user.sign))
							{
								sendMsg(C.VFE);
								user=null;
							}
							user=Data.register(socket,user.pwd,str);
							if(user!=null)sendMsg(C.RSU,""+user.faq);
							else sendMsg(C.RFL);
						}
						else if(cmd==C.LOG)
						{
							ctx.toast("<Client>(Thread)["+IP+"]:Log:"+str);
						}
						else if(cmd==C.VFC)
						{
							long cd=Long.parseLong(Data.mailCd.get(IP));
							if(System.currentTimeMillis()-cd>300000)
							{
								verifycode=(new Random().nextInt(899999)+100000)+"";
								ctx.toast("<Client>(Thread)["+IP+"]:Log:VFC"+verifycode);
								sendEmail("faq_service@163.co","a","FAQ注册验证码("+str.substring(3)+")",str.substring(3),"本次验证码:"+verifycode+"\n请尽快注册");
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
							ctx.toast("<Client>(Thread)["+IP+"]:CMD:"+cmd+",From:"+m.from+",To:"+m.to+",Type:"+m.type+",IsGroup:"+m.isGroup+",Msg:"+m.msg);
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
							Data.msgBuffer.add(m);
							//ctx.toast("<Client>(Thread)["+IP+"]:Send to:"+m.to+" Success");
						}
						else if(cmd==C.GHU)
						{
							try
							{
								BufferedInputStream his=new BufferedInputStream(new FileInputStream(Data.datafile+"/head/"+str+".user.png"));
								byte[] bbbb=new byte[his.available()+4];
								byte[] cc=getIBytes(Integer.parseInt(str));
								for(int ic=0;ic<4;ic++)bbbb[ic]=cc[ic];
								his.read(bbbb,4,bbbb.length-4);his.close();
								sendMsg(C.GHU,Base64.encodeToString(bbbb,0));
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
								BufferedInputStream his=new BufferedInputStream(new FileInputStream(Data.datafile+"/head/"+str+".group.png"));
								byte[] bbbb=new byte[his.available()+4];
								byte[] cc=getIBytes(Integer.parseInt(str));
								for(int ic=0;ic<4;ic++)bbbb[ic]=cc[ic];
								his.read(bbbb,4,bbbb.length-4);his.close();
								sendMsg(C.GHG,Base64.encodeToString(bbbb,0));
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
							boolean bo=false;
							for(int n:u1.friends)if(n==u2.faq)
								{bo=true;break;}
							ctx.toast("<Client>(Thread)["+IP+"]:CMD:"+cmd+",AFD:"+bo);
							if(!bo)
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
						else ctx.toast("<Client>(Thread)["+IP+"]:CMD:"+cmd+",Receive:"+str);
					}
				}
				catch(HttpRequest h)
				{
					String getf=h.m.substring(h.m.indexOf("/"));
					getf=getf.substring(1,getf.indexOf(" "));
					if("".equals(getf))getf="index.html";
					getf=URLDecoder.decode(getf);
					File fi=new File(Data.datafile+"/http");
					if(!fi.exists())fi.mkdirs();
					fi=new File(String.format("%s/http/%s",Data.datafile,getf));
					if(fi.exists())
					{
						BufferedInputStream read=new BufferedInputStream(new FileInputStream(fi));
						ctx.toast("<Client>(Thread)["+IP+"]:"+h.m);
						Writer.write("HTTP/1.1 200 OK\r\n".getBytes());
						Writer.write("Server:FAQ_server/1.0\r\n".getBytes());
						Writer.write(String.format("Content-Length:%d\r\n",read.available()).getBytes());
						Writer.write("Content-Type:text/html;charset=UTF-8\r\n".getBytes());
						Writer.write("\r\n".getBytes());
						byte[] bb=new byte[4096];
						while(read.read(bb)!=-1)Writer.write(bb);
						read.close();
					}
				}
				catch(Throwable e)
				{
					ctx.toast("<Client>(Thread)["+IP+"]:Unknown request:"+str+",Caused by:"+Data.getStackTrace(e));
				}

			}
			Writer.close();
			buff.close();
			isActive=false;
			ctx.toast("<Client>(Thread)["+IP+"]:Disconnected");
		}
		catch(Throwable e)
		{
			ctx.toast("<Error>(Thread)["+IP+"]:"+Data.getStackTrace(e));
			running=false;
			isActive=false;
		}
	}
	public static byte[] getIBytes(int data)  
	{  
		byte[] bytes = new byte[4];  
		bytes[0] = (byte) (data & 0xff);  
		bytes[1] = (byte) ((data & 0xff00) >> 8);  
		bytes[2] = (byte) ((data & 0xff0000) >> 16);  
		bytes[3] = (byte) ((data & 0xff000000) >> 24);  
		return bytes;  
	}  
	public static byte[] getFBytes(float data)  
	{  
		//return getIBytes((int)data);/*
		int intBits = Float.floatToIntBits(data);  
		return getIBytes(intBits);  
	} 
	public static int getInt(byte[] bytes)  
	{  
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));  
	}  
	public static float getFloat(byte[] bytes)  
	{  
		return Float.intBitsToFloat(getInt(bytes));  
	}
	public void sendMsg(byte cmd)
	{
		sendMsg(cmd,null);
	}
	public void sendMsg(byte CMD,String s)
	{
		try
		{
			if(s!=null)
			{
				byte[] a=s.getBytes();
				//String aa=Base64.encodeToString(a,0);
				if(deckey!=null)a=AES.encrypt(deckey,s).getBytes();
				byte[] b=new byte[a.length+5];
				byte[] c=getIBytes(a.length);
				b[4]=CMD;
				b[0]=c[0];
				b[1]=c[1];
				b[2]=c[2];
				b[3]=c[3];
				int e=5;
				for(byte d:a)b[e++]=d;
				Writer.write(b);
			}
			else
			{
				byte[] b=new byte[5];
				byte[] c=getIBytes(0);
				b[4]=CMD;
				b[0]=c[0];
				b[1]=c[1];
				b[2]=c[2];
				b[3]=c[3];
				Writer.write(b);
			}
			Writer.flush();
		}
		catch (Exception e)
		{
			ctx.toast("<Error>(Send)["+IP+"]:"+Data.getStackTrace(e));
		}
		if(CMD!=C.HBT&&CMD!=C.MSG&&CMD!=C.GUS&&
		   CMD!=C.GHG&&CMD!=C.GHU)ctx.toast("<Client>(Send)["+IP+"]:CMD:"+CMD+",Msg:"+s);
	}
	public static void sendEmail(final String sender,final String pwd,final String sub,final String to,final String s)
	{
		new Thread(new Runnable(){
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
						MainActivity.toast(Data.getStackTrace(e));
					}
				}
			}).start();
	}
	public static String getAddressByIP(String strIP)
	{ 
		try
		{
			URL url = new URL("http://m.ip138.com/ip.asp?ip=" + strIP); 
			URLConnection conn = url.openConnection(); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK")); 
			String line = null; 
			StringBuffer result = new StringBuffer(); 
			while((line = reader.readLine()) != null)result.append(line); 
			reader.close();
			strIP=new String(result.toString().getBytes("GB2312"));
			strIP = strIP.substring(strIP.indexOf("本站主数据：")+6,strIP.indexOf("参考数据一"));
			strIP=strIP.substring(0,strIP.indexOf(" "));
			return strIP;
		}
		catch( IOException e)
		{ 
			return "读取失败"; 
		}
	}
	private class HttpRequest extends Throwable
	{
		public String m;
		public HttpRequest(String s)
		{
			m=s;
		}
	}
}
