package com.yzrilyzr.FAQ.Main;

import java.io.*;

import com.yzrilyzr.myclass.util;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;

public class FileService extends RU
{
	File file;
	String path;
	byte[] sha1;
	Socket socket;
	int download;
	CallBack call=null;
	public FileService(File localFile,String remotePath,int down)
	{
		file=localFile;
		path=remotePath;
		sha1=getFileSha1(file.getAbsolutePath());
		download=down;
	}

	public void setCallBack(CallBack call)
	{
		this.call = call;
	}

	public CallBack getCallBack()
	{
		return call;
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
			socket.connect(new InetSocketAddress(ClientService.hostIp,20001));
			BufferedInputStream is=new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream os=new BufferedOutputStream(socket.getOutputStream());
			os.write(download);
			if(download==1)
			{
				writeInt(os,(int)file.length());
				writeStr(os,byte2hex(sha1));
				writeStr(os,path);
				os.flush();
				int res=is.read();
				if(res==1)
				{
					if(call!=null)call.onFinish(this,true);
				}
				else if(res==2)
				{
					int ii=0;
					byte[] by=new byte[10240];
					RandomAccessFile r=new RandomAccessFile(file,"r");
					while((ii=r.read(by))!=-1)
					{
						os.write(by,0,ii);
						call.onProgress(this,r.getFilePointer(),r.length());
					}
					os.flush();
					int result=is.read();
					if(call!=null)
						if(result==1)call.onFinish(this,true);
						else call.onFinish(this,false);
				}
				else if(call!=null)call.onFinish(this,false);
			}
			else if(download==2)
			{
				//download
			}
			else if(download==3)
			{
				//mkdir
				writeStr(os,path);
				os.flush();
				int res=is.read();
				if(call!=null)
					if(res==1)call.onFinish(this,true);
					else call.onFinish(this,false);
			}
			else if(download==4)
			{
				//delete
			}
			else if(download==5)
			{
				//rename
			}
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
	public interface CallBack
	{
		public abstract void onFinish(FileService fs,boolean success);
		public abstract void onProgress(FileService s,long p,long m);
	}
}
