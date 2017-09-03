package com.yzrilyzr.FAQ.Main;

import java.io.*;

import android.util.Base64;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.ListActivity;
import com.yzrilyzr.myclass.util;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Data
{
	public static User me;
	public static ListActivity ctx;
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
	public static byte[] getHead(String faq,boolean isg)
	{
		try
		{

			BufferedInputStream is=new BufferedInputStream(new FileInputStream(util.mainDir+"/head/"+faq+(isg?".group":".user")+".png"));
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
