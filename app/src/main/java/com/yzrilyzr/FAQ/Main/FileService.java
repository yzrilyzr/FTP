package com.yzrilyzr.FAQ.Main;

import java.io.*;

import com.yzrilyzr.myclass.util;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;

public class FileService extends RU
{
	File file;
	byte[] sha1;
	Socket socket;
	int download;
	public FileService(String localFile,int down)
	{
		file=new File(localFile);
		sha1=getFileSha1(file.getAbsolutePath());
		download=down;
	}
	@Override
	public void run()
	{
		// TODO: Implement this method
		socket=new Socket();
		try
		{
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);
			socket.setSendBufferSize(10240);
			socket.setTrafficClass(0x04|0x10);
			socket.setSoTimeout(10000);
			socket.setReceiveBufferSize(10240);
			socket.connect(new InetSocketAddress(ClientService.hostIp,10001));
			BufferedInputStream is=new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream os=new BufferedOutputStream(socket.getOutputStream());
			os.write(download);
			writeInt(os,(int)file.length());
			writeStr(os,byte2hex(sha1));
			writeStr(os,file.getName());
			os.flush();
			readStrFully(is,new BO(){
					@Override
					public boolean g()
					{
						// TODO: Implement this method
						return true;
					}
				});
			int ii=0;
			byte[] by=new byte[10240];
			RandomAccessFile r=new RandomAccessFile(file.getAbsolutePath(),"r");
			while((ii=r.read(by))!=-1)
			{
				//String sha=byte2hex(getByteSha1(by));
				//writeStr(os,sha);
				os.write(by,0,ii);
				os.flush();
			}
			r.close();
		}
		catch(Throwable e)
		{
			ClientService.sendMsg(C.LOG,util.getStackTrace(e));
		}
		finally
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{}
		}
	}
	public void download()
	{
		try
		{
			DataInputStream is=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			while(socket.isConnected())
			{
				while(is.available()<4)Thread.sleep(1);
				int hl=is.readInt();
				while(is.available()<hl)Thread.sleep(1);
				byte[] bu=new byte[hl];
				is.read(bu);
				String[] hd=new String(bu).substring(1).split("/");
				long len=Long.parseLong(hd[0]);
				String sha1=hd[1],name=hd[2];

			}
		}
		catch(Throwable e)
		{
			try
			{
				socket.close();
			}
			catch (IOException e2)
			{}
		}
	}
	public static byte[] getFileSha1(String path)
	{  
		try
		{  
			File file=new File(path);  
			FileInputStream in = new FileInputStream(file);  
			MessageDigest messagedigest=MessageDigest.getInstance("SHA-1");  
			byte[] buffer = new byte[10240];  
			int len = 0;  
			while ((len = in.read(buffer)) >0)
			{   
				messagedigest.update(buffer, 0, len);  
			}
			return messagedigest.digest();  
		}
		catch (Exception e)
		{  
		}  
		return null;  
	}
	public static byte[] getByteSha1(byte[] b)
	{  
		try
		{  
			MessageDigest messagedigest= MessageDigest.getInstance("SHA-1");  
			messagedigest.update(b);  
			return messagedigest.digest();  
		}
		catch (Exception e)
		{  
		}  
		return null;  
	}
	public static String byte2hex(byte[] b)
	{
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++)
		{
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs = hs.append("0").append(stmp);
			else
			{
				hs = hs.append(stmp);
			}
		}
		return String.valueOf(hs);
	}
}
