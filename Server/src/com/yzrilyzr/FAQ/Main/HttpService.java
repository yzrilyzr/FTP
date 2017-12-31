package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Server.Mime;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Date;

public class HttpService extends TcpService
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
	}

	@Override
	public void onRead(BufferedInputStream buff)
	{
		try
		{
			byte[] buffer=new byte[buff.available()];
			buff.read(buffer);
			String bu=new String(buffer);
			String reqfile=bu.substring(bu.indexOf("/"),bu.indexOf(" HTTP/"));
			if("/".equals(reqfile))reqfile="/index.html";
			File fi=new SafeFile(Data,false,Data.datafile+"/http");
			if(!fi.exists())fi.mkdirs();
			fi=new SafeFile(Data,false,String.format("%s/http%s",Data.datafile,reqfile));
			if(fi.exists())
			{
				RandomAccessFile raf=new RandomAccessFile(fi,"r");
				Writer.write("HTTP/1.1 200 OK\r\n".getBytes());
				Writer.write(String.format("Date:%s\r\n",new Date().toString()).getBytes());
				Writer.write("Server:FAQ_HTTP_server/1.0\r\n".getBytes());
				Writer.write(String.format("Content-Length:%d\r\n",raf.length()).getBytes());
				Writer.write(String.format("Content-Type:%s;charset=UTF-8\r\n",Mime.getMIMEType(fi)).getBytes());
				if(!reqfile.contains("html")&&!reqfile.contains("css"))Writer.write(String.format("Content-disposition:attachment;filename=%s\r\n",fi.getName()).getBytes());
				Writer.write("\r\n".getBytes());
				byte[] bb=new byte[10240];int indes=0;
				while((indes=raf.read(bb))!=-1)
				{
					Writer.write(bb,0,indes);
					Writer.flush();
				}
				raf.close();
			}
			else
			{
				Writer.write("HTTP/1.1 404 NOT FOUND\r\n".getBytes());
				Writer.write("Server:FAQ_HTTP_server/1.0\r\n".getBytes());
				Writer.write("\r\n".getBytes());
				Writer.flush();
			}
			Toast("HTTP","处理HTTP请求完毕");
		}
		catch(Throwable e)
		{
			Toast("ERROR",Data.getStackTrace(e));
		}
		finally
		{
			disconnect();
		}
	}

}
