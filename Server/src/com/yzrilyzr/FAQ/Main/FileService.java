package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileService extends BaseService
{
	private boolean publicmode;
	public FileService(Socket s,Server c,boolean publicmode)
	{
		super(s,c);
		this.publicmode=publicmode;
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
					return running;
				}
			};
			block(buff,1,bo);
			int download=buff.read();
			if(download==1)
			{//用户上传
				long size=readIntFully(buff,bo);
				String sha1=readStrFully(buff,bo);
				String name=readStrFully(buff,bo);
				String path=null;
				//存储目录
				if(publicmode)path=Data.rootFile;
				else path=Data.datafile+"/upload_files";
				File dir=new SafeFile(Data,publicmode,path);
				if(!dir.exists())dir.mkdirs();
				//临时目录
				dir=new SafeFile(Data,publicmode,Data.datafile+"/upload_files/tmp");
				if(!dir.exists())dir.mkdirs();
				//存放目标
				File dstfile=new SafeFile(Data,publicmode,path+"/"+name);
				File tmpfile=new SafeFile(Data,publicmode,Data.datafile+"/upload_files/tmp/"+name.substring(name.lastIndexOf("/")));
				Toast("上传",dstfile.getPath());
				if(dstfile.exists())
				{
					byte[] sha1_local_b=getFileSha1(dstfile.getAbsolutePath());
					String sha1_local=byte2hex(sha1_local_b);
					if(sha1_local.equals(sha1))
					{
						Writer.write(1);//秒传
						Writer.write(1);//接受完毕
						Writer.flush();
						disconnect();
						Toast("Thread","上传完毕:文件相同");
						return;
					}
				}
				Writer.write(2);//允许
				Writer.flush();
				RandomAccessFile ra=new RandomAccessFile(tmpfile,"rw");
				ra.setLength(size);
				long index=0;int ii=0;
				byte[] buffer=new byte[10240];
				while(index<size)
				{
					ii=buff.read(buffer);
					ra.write(buffer,0,ii);
					index+=ii;
				}
				ra.close();
				dstfile.delete();
				FileChannel is=new FileInputStream(tmpfile).getChannel();
				FileChannel os=new FileOutputStream(dstfile).getChannel();
				is.transferTo(0,is.size(),os);
				tmpfile.delete();
				Writer.write(1);//表示接受完毕
				Writer.flush();
				disconnect();
				Toast("Thread","上传完毕");
			}
			else if(download==2)
			{
				//下载

			}
			else if(download==3){
				//创建文件夹
				String name=readStrFully(buff,bo);
				String path=null;
				//存储目录
				if(publicmode)path=Data.rootFile;
				else path=Data.datafile+"/upload_files";
				File dir=new SafeFile(Data,publicmode,path);
				if(!dir.exists())dir.mkdirs();
				//临时目录
				dir=new SafeFile(Data,publicmode,Data.datafile+"/upload_files/tmp");
				if(!dir.exists())dir.mkdirs();
				//存放目标
				File file=new SafeFile(Data,publicmode,path+"/"+name);
				Toast("新建文件夹",file.getPath());
				file.mkdirs();
				Writer.write(1);
				Writer.flush();
				disconnect();
			}
			else if(download==4){
				//删除文件
			}
			else if(download==5){
				//重命名
			}
			else disconnect();
		}
		catch (Exception e)
		{
			try
			{
				Writer.write(0);//拒绝
			}
			catch (IOException e2)
			{}
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
