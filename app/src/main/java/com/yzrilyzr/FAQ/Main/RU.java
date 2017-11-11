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
	public static void block(BufferedInputStream is,long size,BO b) throws IOException, InterruptedException{
		while(is.available()<size&&b.g())Thread.sleep(1);
	}
	public static String readStrFully(BufferedInputStream b,BO bo) throws IOException, InterruptedException
	{
		int pwdlen=readInt(b);
		block(b,pwdlen,bo);
		byte[] bb=new byte[pwdlen];
		b.read(bb);
		return new String(bb);
	}
	public static int readIntFully(BufferedInputStream b,BO bo) throws IOException, InterruptedException
	{
		block(b,4,bo);
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
	public static long readLongFully(BufferedInputStream b,BO bo) throws IOException, InterruptedException
	{
		block(b,8,bo);
		byte[] a=new byte[8];
		b.read(a);
		return getLong(a);
	}
	public static long getLong(byte[] readBuffer)
	{
        return (((long)readBuffer[0] << 56) +
			((long)(readBuffer[1] & 255) << 48) +
			((long)(readBuffer[2] & 255) << 40) +
			((long)(readBuffer[3] & 255) << 32) +
			((long)(readBuffer[4] & 255) << 24) +
			((readBuffer[5] & 255) << 16) +
			((readBuffer[6] & 255) <<  8) +
			((readBuffer[7] & 255) <<  0));
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
