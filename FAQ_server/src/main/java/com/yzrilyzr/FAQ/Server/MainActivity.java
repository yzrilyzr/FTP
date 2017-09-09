package com.yzrilyzr.FAQ.Server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.SocketException;

public class MainActivity extends Activity 
{
	static MainActivity ctx;
	static boolean started=false;
	static TextView te;
	static ServerSocket ss;
 	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		ctx=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		te=(TextView) findViewById(R.id.mainTextView1);
		File f=new File(Data.datafile);
		if(!f.exists())f.mkdirs();
		File u=new File(Data.datafile+"/users");
		try
		{
			if(!u.exists())u.createNewFile();
		}
		catch (IOException e)
		{}
		Data.readUserData();
		Data.readBlackList();
		startServer();
    }
	public static void toast(final Object o)
	{
		ctx.runOnUiThread(new Runnable(){
			@Override
			public void run()
			{
				te.append(o+"\n");
				((ScrollView)te.getParent()).fullScroll(View.FOCUS_DOWN);
			}
		});

	}
	public static void startServer()
	{
		if(!started)
		{
			started=true;
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					try
					{
						String strIP="";
						try
						{
							URL url = new URL("http://1212.ip138.com/ic.asp");
							URLConnection con = url.openConnection();
							BufferedReader bReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "GB2312"));
							StringBuffer webContent = new StringBuffer();
							String str = null;
							while ((str = bReader.readLine()) != null)
							{
								webContent.append(str);
							}
							int start = webContent.indexOf("[") + 1;
							int end = webContent.indexOf("]");
							strIP=webContent.substring(start, end);
						} 
						catch(Throwable e)
						{
							try
							{
								strIP=InetAddress.getLocalHost().getHostAddress();
							}
							catch (UnknownHostException e2)
							{
								strIP="未知";
							}
						}
						ss=new ServerSocket(10000);
						toast("<Server>(主线程)服务器已启动，IP在["+strIP+":10000]");
						while(started)
						{
							Socket s=ss.accept();
							ClientService c=new ClientService(s,ctx);
							Data.onlineClient.add(c);
							c.start();
						}
						toast("<Server>(主线程)服务器已关闭");
					}
					catch(SocketException e){
						toast("<Server>(主线程)服务器已关闭");
					}
					catch (IOException e)
					{
						toast("<Error>(主线程)无法启动服务器:"+e);
					}
				}
			}).start();
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					while(started)
					{
						try
						{
							for(ClientService cs:Data.onlineClient)
							{
								if(!cs.isActive)
								{
									cs.running=false;
									Data.onlineClient.remove(cs);
									if(cs.user!=null)Data.loginClient.remove(cs.user.faq+"");
								}
								if(cs.running)
								{
									cs.isActive=false;
									cs.sendMsg(C.HBT);
								}
							}
							Data.saveMsgBuffer();
							Thread.sleep(60000);
						}
						catch(Throwable e)
						{
							toast("<Error>(心跳包发送器)"+e);
						}
					}
				}
			}).start();
		}
	}
	public void open(View v)
	{
		try
		{
			if(started)
			{
				started=false;
				ss.close();
			}
			else startServer();
		}
		catch (IOException e)
		{}
	}
	public void us(View v)
	{
		startActivity(new Intent(this,UserManager.class));
	}
	public void control(View v)
	{
		startActivity(new Intent(this,ControlActivity.class));
	}
	public void clear(View v)
	{
		te.setText("");
	}
}
