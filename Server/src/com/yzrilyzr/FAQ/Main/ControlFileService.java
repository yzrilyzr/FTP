package com.yzrilyzr.FAQ.Main;

import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.security.MessageDigest;

public class ControlFileService extends BaseService
{
	public ControlFileService(Socket s, Server c)
	{
		super(s,c);
		TAG="FileClient";
		setName("FAQServer_FileService");
	}

	@Override
	public void onRead(BufferedInputStream buff)
	{
		try
		{
			BO bo=new BO(){
				@Override
				public boolean g()
				{
					// TODO: Implement this method
					return true;
				}
			};
			block(buff,1,bo);
			int download=buff.read();
			if(download==1)
			{//上传
				//long len,string sha1,string name "/xxx/xxx"
				Toast("上传","");
				long size=readIntFully(buff,bo);
				String sha1=readStrFully(buff,bo);
				String path=readStrFully(buff,bo);
				File f=new SafeFile(Data,true,Data.rootFile+path);
				if(f.exists()&&f.isFile())f.delete();
				RandomAccessFile ra=new RandomAccessFile(f,"rw");
				ra.setLength(size);
				writeStr(Writer,sha1);
				Writer.flush();
				long index=0;int ii=0;
				byte[] buffer=new byte[10240];
				//ArrayList<long[]> list=new ArrayList<long[]>();
				while(index<size)
				{
					//String buffsha1=readStrFully(buff,bo);
					ii=buff.read(buffer);
					//String cbsha1=byte2hex(getByteSha1(buffer));
					/*if(buffsha1.equals(cbsha1))*/
					ra.write(buffer,0,ii);
					/*else{
					 Toast("上传失败","校验错误");
					 running=false;
					 }*/
					index+=ii;
				}
				ra.close();
				running=false;
				Toast("Thread","上传完毕");
			}
			//else if(download==2)
			{//false

			}
			//else running=false;
		}
		catch (Exception e)
		{
			Toast("Thread","上传失败:"+e.toString());
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
	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
	}

}
