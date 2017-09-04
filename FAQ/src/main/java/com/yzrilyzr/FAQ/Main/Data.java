package com.yzrilyzr.FAQ.Main;

import java.io.*;

import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.ListActivity;
import com.yzrilyzr.myclass.util;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Data
{
	public static int myfaq=0;
	private static User me=null;
	public static ListActivity ctx;
	static{
		Thread.setDefaultUncaughtExceptionHandler(new java.lang.Thread.UncaughtExceptionHandler(){
				@Override
				public void uncaughtException(Thread p1, Throwable p2)
				{
					// TODO: Implement this method
					try
					{
						ClientService.sendMsg(C.LOG,util.getStackTrace(p2));
					}
					catch(Throwable e)
					{}
				}
			});
	}
	public static void saveHead(boolean isg,byte[] b)
	{
		try
		{
			File hd=new File(util.mainDir+"/head");
			if(!hd.exists())hd.mkdirs();
			BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(util.mainDir+"/head/"+getInt(new byte[]{b[0],b[1],b[2],b[3]})+(isg?".group":".user")+".png"));
			bos.write(b,4,b.length-4);
			bos.flush();
			bos.close();
		}
		catch(Throwable e)
		{}

	}
	public static User getUser(int faq)
	{
		User u=users.get(faq+"");
		if(u==null)ClientService.sendMsg(C.GUS,faq+"");
		else if(u.faq==myfaq)me=u;
		return u;
	}
	public static User getMyself()
	{
		if(me!=null)return me;
		return getUser(myfaq);
	}
	public static byte[] getMyHead()
	{
		return getHead(myfaq,false);
	}
	public static byte[] getHead(int faq,boolean isg)
	{
		try
		{
			String p=String.format("%s/head/%d%s.png",util.mainDir,faq,isg?".group":".user");
			File f=new File(p);
			if(!f.exists())
			{
				ClientService.sendMsg(isg?C.GHG:C.GHU,faq+"");
				return null;
			}
			BufferedInputStream is=new BufferedInputStream(new FileInputStream(p));
			byte[] by=new byte[is.available()];
			is.read(by);
			is.close();
			return by;
		}
		catch(Throwable e)
		{
		}
		return null;
	}
	public static void saveMsgBuffer()
	{
		try
		{
			if(msgBuffer.size()==0)return;
			RandomAccessFile r=new RandomAccessFile(util.mainDir+"/msg","rw");
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
		{}
	}
	public static final ArrayList<MessageObj> msgs=new ArrayList<MessageObj>();
	public static final ArrayList<MessageObj> msgBuffer=new ArrayList<MessageObj>();
	public static ConcurrentHashMap<String,MessageObj> msglist=new ConcurrentHashMap<String,MessageObj>();
	public static ConcurrentHashMap<String,User> users=new ConcurrentHashMap<String,User>();
	public static void writeHashmap(ConcurrentHashMap<?,ToStrObj> map,String file)
	{
		try
		{
			BufferedOutputStream b=new BufferedOutputStream(new FileOutputStream(util.mainDir+"/"+file));
		}
		catch (FileNotFoundException e)
		{}
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
}
