package com.yzrilyzr.FAQ.Server;

import com.yzrilyzr.FAQ.Main.*;
import java.io.*;
import java.net.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.Enumeration;
import android.widget.ScrollView;

public class MainActivity extends Activity 
{
	static MainActivity ctx;
	static boolean started=false;
	static TextView te;
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
		if(!started)
		{
			new Thread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						try
						{
							ServerSocket ss=new ServerSocket(10000);
							for(;;)
							{
								Socket s=ss.accept();
								ClientService c=new ClientService(s,ctx);
								Data.onlineClient.add(c);
								c.start();
							}
						}
						catch (IOException e)
						{
							toast("<Error>(Main)Cannot create server:"+e);
						}
					}
				}).start();
			new Thread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						while(true)
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
								Thread.sleep(3000);
							}
							catch(Throwable e)
							{
								toast("<Error>(HBT Sender)"+e);
							}
						}
					}
				}).start();
			new Thread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						String strIP="";
						try {
							URL url = new URL("http://1212.ip138.com/ic.asp");
							URLConnection con = url.openConnection();
							BufferedReader bReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "GB2312"));
							StringBuffer webContent = new StringBuffer();
							String str = null;
							while ((str = bReader.readLine()) != null) {
								webContent.append(str);
							}
							int start = webContent.indexOf("[") + 1;
							int end = webContent.indexOf("]");
							strIP=webContent.substring(start, end);
						} 
						catch(Throwable e){
							try
							{
								strIP=InetAddress.getLocalHost().getHostAddress();
							}
							catch (UnknownHostException e2)
							{
								strIP="未知";
							}
						}
						toast("<Server>(Main)Started,At["+strIP+":10000]");
					}
				}).start();

			started=true;
		}
    }
	public static void toast(final Object o)
	{
		ctx.runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					//Toast.makeText(ctx,o+"",0).show();
					te.append(o+"\n");
					((ScrollView)te.getParent().getParent()).fullScroll(View.FOCUS_DOWN);
				}
			});

	}
	public void us(View v)
	{
		startActivity(new Intent(this,UserManager.class));
	}
	public void control(View v){
		startActivity(new Intent(this,ControlActivity.class));
	}
	public void clear(View v){
		te.setText("");
	}
}
