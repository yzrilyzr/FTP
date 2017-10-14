package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Data.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import android.os.Environment;
import com.yzrilyzr.FAQ.Server.MainActivity;
import java.net.Socket;

public class Data extends RU
{
	public static String datafile=Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/FAQ_server";
	public static final ConcurrentHashMap<String,User> users=new ConcurrentHashMap<String,User>();
	public static final ConcurrentHashMap<String,String> mailCd=new ConcurrentHashMap<String,String>();
	public static final ConcurrentHashMap<String,ClientService> loginClient=new ConcurrentHashMap<String,ClientService>();
	public static final ConcurrentHashMap<String,String> blacklist=new ConcurrentHashMap<String,String>();
	public static final CopyOnWriteArrayList<BaseService> onlineClient=new CopyOnWriteArrayList<BaseService>();
	public static final CopyOnWriteArrayList<MessageObj> msgBuffer=new CopyOnWriteArrayList<MessageObj>();
	public static final ConcurrentHashMap<String,Group> groups=new ConcurrentHashMap<String,Group>();
	public static User register(Socket soc,String pwd,String email)
	{
		User u=new User();
		u.ip=soc.getInetAddress().getHostAddress();
		Iterator iter=users.entrySet().iterator();
		while(iter.hasNext())
		{
			ConcurrentHashMap.Entry enty=(ConcurrentHashMap.Entry)iter.next();
			User us=(User)enty.getValue();
			if(us.ip.equals(u.ip)||us.email==email)return null;
		}
		u.pwd=pwd;
		u.email=email;
		u.faq=10000+new Random().nextInt(999999999);
		u.sign="个性签名";
		u.nick="新用户";
		/*u.friends=new int[]{};
		 u.groups=new int[]{};
		 u.groups=new int[]{};*/
		users.put(u.faq+"",u);
		saveUserData();
		return u;
	}
	public static void saveMsgBuffer()
	{
		try
		{
			if(msgBuffer.size()==0)return;
			RandomAccessFile r=new RandomAccessFile(datafile+"/msg","rw");
			r.seek(r.length());
			for(MessageObj m:msgBuffer)
			{
				byte[] a=m.o2s().getBytes();
				r.write(getIBytes(a.length));
				r.write(a);
			}
			msgBuffer.clear();
			r.close();
		}
		catch (Exception e)
		{MainActivity.toast(getStackTrace(e));}
	}
	public static void saveBlackList()
	{
		try
		{
			BufferedOutputStream b=new BufferedOutputStream(new FileOutputStream(datafile+"/blacklist"));
			writeInt(b,blacklist.size());
			Iterator iter=blacklist.entrySet().iterator();
			while(iter.hasNext())
			{
				ConcurrentHashMap.Entry e=(ConcurrentHashMap.Entry) iter.next();
				String ip=(String)e.getKey();
				if("2".equals((String)e.getValue()))writeStr(b,ip);
			}
			b.flush();
			b.close();
		}
		catch (IOException e)
		{
			MainActivity.toast(getStackTrace(e));
		}
	}
	public static void readBlackList()
	{
		try
		{
			BufferedInputStream b=new BufferedInputStream(new FileInputStream(datafile+"/blacklist"));
			int f=readInt(b);
			blacklist.clear();
			for(int i=0;i<f;i++)blacklist.put(readStr(b),"2");
			b.close();
		}
		catch (IOException e)
		{}
	}
	public static void saveUserData()
	{
		try
		{
			//users.clear();
			BufferedOutputStream br=new BufferedOutputStream(new FileOutputStream(datafile+"/users"));
			writeInt(br,users.size());
			Iterator iter=users.entrySet().iterator();
			while(iter.hasNext())
			{
				ConcurrentHashMap.Entry enty=(ConcurrentHashMap.Entry)iter.next();
				User us=(User)enty.getValue();
				writeInt(br,us.faq);
				writeStr(br,us.pwd);
				writeStr(br,us.nick);
				writeStr(br,us.sign);
				writeStr(br,us.email);
				writeStr(br,us.ip);
				writeInt(br,us.friends.size());
				for(int u:us.friends)writeInt(br,u);
				writeInt(br,us.groups.size());
				for(int u:us.groups)writeInt(br,u);
			}
			br.flush();
			br.close();
		}
		catch (Exception e)
		{
			MainActivity.toast(getStackTrace(e));
		}
	}
	public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try
        {
            t.printStackTrace(pw);
            return sw.toString();
        }
        finally
        {
            pw.close();
        }
    }
	public static void readUserData()
	{
		try
		{
			users.clear();
			BufferedInputStream br=new BufferedInputStream(new FileInputStream(datafile+"/users"));
			int usercount=readInt(br);
			for(int i=0;i<usercount;i++)
			{
				User us=new User();
				us.faq=readInt(br);
				us.pwd=readStr(br);
				us.nick=readStr(br);
				us.sign=readStr(br);
				us.email=readStr(br);
				us.ip=readStr(br);
				int frlen=readInt(br);
				us.friends=new ToStrList<Integer>();
				for(int u=0;u<frlen;u++)us.friends.add(readInt(br));
				int grlen=readInt(br);
				us.groups=new ToStrList<Integer>();
				for(int u=0;u<grlen;u++)us.groups.add(readInt(br));
				users.put(us.faq+"",us);
			}
			br.close();
		}
		catch (Exception e)
		{
			MainActivity.toast(getStackTrace(e));
		}
	}
	

}
