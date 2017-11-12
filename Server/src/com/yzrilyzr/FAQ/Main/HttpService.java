package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Server.Server;
import com.yzrilyzr.FAQ.Server.Mime;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.io.IOException;

public class HttpService extends BaseService
{
	public HttpService(Socket s,Server m)
	{
		super(s,m);
		TAG="HttpClient";
		setName("FAQServer_HttpService");
	}
	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
	}

	@Override
	public void onRead(BufferedInputStream buff)
	{
		// TODO: Implement this method
		try
		{
			byte[] buffer=new byte[buff.available()];
			buff.read(buffer);
			String bu=new String(buffer);
			int g=bu.indexOf("/");
			if(g==-1){
				running=false;
				return;
			}
			String getf=bu.substring(g);
			getf=getf.substring(1,getf.indexOf(" "));
			if("".equals(getf))getf="index.html";
			getf=URLDecoder.decode(getf);
			File fi=new SafeFile(Data,false,Data.datafile+"/http");
			if(!fi.exists())fi.mkdirs();
			fi=new SafeFile(Data,false,String.format("%s/http/%s",Data.datafile,getf));
			if(fi.exists())
			{
				RandomAccessFile raf=new RandomAccessFile(fi,"r");
				Writer.write("HTTP/1.1 200 OK\r\n".getBytes());
				Writer.write("Server:FAQ_HTTP_server/1.0\r\n".getBytes());
				Writer.write(String.format("Content-Length:%d\r\n",raf.length()).getBytes());
				Writer.write(String.format("Content-Type:%s;charset=UTF-8\r\n",Mime.getMIMEType(fi)).getBytes());
				if(!getf.contains("html")&&!getf.contains("css"))Writer.write(String.format("Content-disposition:attachment;filename=%s\r\n",fi.getName()).getBytes());
				Writer.write("\r\n".getBytes());
				try
				{
					byte[] bb=new byte[10240];int indes=0;
					while((indes=raf.read(bb))!=-1)
					{
						Writer.write(bb,0,indes);
						Writer.flush();
					}
				}
				catch(SocketException se)
				{
					running=false;
				}
				finally
				{raf.close();}
			}
			Toast("HTTP","处理HTTP请求完毕");
		}
		catch(Throwable e)
		{
			Toast("ERROR",Data.getStackTrace(e));
		}
		finally{
			running=false;
		}
	}

}
