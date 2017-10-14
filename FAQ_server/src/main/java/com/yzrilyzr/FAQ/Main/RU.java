package com.yzrilyzr.FAQ.Main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class RU extends Thread
{
	public static void writeInt(BufferedOutputStream b,int i) throws IOException
	{
		byte[] bb=getIBytes(i);
		b.write(bb);
	}
	public static void writeStr(BufferedOutputStream b,String s) throws IOException
	{
		byte[] bb=s.getBytes();
		writeInt(b,bb.length);
		b.write(bb);
	}
	public static int readInt(BufferedInputStream b) throws IOException
	{
		byte[] a=new byte[4];
		b.read(a);
		return getInt(a);
	}
	public static void block(BufferedInputStream is,long size,BO BO) throws IOException, InterruptedException{
		while(is.available()<size&&BO.g())Thread.sleep(1);
	}
	public static String readStrFully(BufferedInputStream b,BO BO) throws IOException, InterruptedException
	{
		int pwdlen=readInt(b);
		block(b,pwdlen,BO);
		byte[] bb=new byte[pwdlen];
		b.read(bb);
		return new String(bb);
	}
	public static int readIntFully(BufferedInputStream b,BO BO) throws IOException, InterruptedException
	{
		block(b,4,BO);
		byte[] a=new byte[4];
		b.read(a);
		return getInt(a);
	}
	public static String readStr(BufferedInputStream b) throws IOException
	{
		int pwdlen=readInt(b);
		byte[] bb=new byte[pwdlen];
		b.read(bb);
		return new String(bb);
	}
	public static void writeLong(BufferedOutputStream b,long i) throws IOException
	{
		byte[] bb=getLBytes(i);
		b.write(bb);
	}
	public static long readLong(BufferedInputStream b) throws IOException
	{
		byte[] a=new byte[8];
		b.read(a);
		return getLong(a);
	}
	public static long readLongFully(BufferedInputStream b,BO BO) throws IOException, InterruptedException
	{
		block(b,8,BO);
		byte[] a=new byte[8];
		b.read(a);
		return getLong(a);
	}
	public static long getLong(byte[] b)
	{
        return (0xff & b[0]) | 
		(0xff00 & (b[1] << 8)) |
		(0xff0000 & (b[2] << 16)) |
		(0xff000000 & (b[3] << 24))|
		(0xff00000000 & (b[4] << 32))|
		(0xff0000000000 & (b[5] << 40))|
		(0xff000000000000 & (b[6] << 48))|
		(0xff00000000000000 & (b[7] << 56));
    }
	public static byte[] getLBytes(long data)  
	{  
		byte[] bytes = new byte[8];  
		bytes[0] = (byte) (data & 0xff);  
		bytes[1] = (byte) ((data & 0xff00) >> 8);  
		bytes[2] = (byte) ((data & 0xff0000) >> 16);  
		bytes[3] = (byte) ((data & 0xff000000) >> 24);  
		bytes[4] = (byte) ((data & 0xff00000000) >> 32);  
		bytes[5] = (byte) ((data & 0xff0000000000) >> 40);  
		bytes[6] = (byte) ((data & 0xff000000000000) >> 48);  
		bytes[7] = (byte) ((data & 0xff00000000000000) >> 56);  
		return bytes;  
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
