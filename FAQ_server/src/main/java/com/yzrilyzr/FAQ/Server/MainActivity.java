package com.yzrilyzr.FAQ.Server;

import com.yzrilyzr.FAQ.Main.*;
import java.io.*;
import java.net.*;
import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends Activity 
{
	static MainActivity ctx;
	static boolean started=false;
	static LongTextView te;
	static String TAG="Server";
	static StringBuilder log=new StringBuilder();
	static ServerSocket faqServer,fileServer,httpServer;
	static int filterType=0;
	static String filterKey="";
	static CopyOnWriteArrayList<ConsoleMsg> cmsg=new CopyOnWriteArrayList<ConsoleMsg>();
 	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		Thread.currentThread().setName("FAQServer_MainActivity");
		ctx=this;
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
		te=(LongTextView) findViewById(R.id.mainTextView1);
		te.setText(log.toString());
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
		toast(new ConsoleMsg(TAG,"主线程","数据载入成功","local"));
    }
	public static void toast(ConsoleMsg m)
	{
		cmsg.add(m);
		if(filterType==0||
		(filterType==1&&filterKey.equals(m.ip))||
		(filterType==2&&filterKey.equals(m.tag))||
		(filterType==3&&filterKey.equals(m.at)))
			toast(m.toString());
	}
	public static void toast(final String o)
	{
		ctx.runOnUiThread(new Runnable(){
			@Override
			public void run()
			{
				log.append(o);
				log.append("\n");
				postConsole();
			}
		});

	}
	private static void postConsole()
	{
		try
		{
			te.setText(log.toString());
			String[] a=log.toString().split("\n");
			te.currentLine=a.length-1;
			te.cursorStart=a[a.length-1].length();
			te.yOff=-te.th*a.length+ctx.getWindowManager().getDefaultDisplay().getHeight()*0.8f;
		}
		catch(Throwable e)
		{}
	}
	public void logout(View v)
	{
		try
		{
			BufferedOutputStream os= new BufferedOutputStream(new FileOutputStream(Data.datafile+"/日志导出"+System.currentTimeMillis()+".txt"));
			for(ConsoleMsg c:cmsg)
			{
				os.write(c.toString().getBytes());
				os.write("\n".getBytes());
			}
			os.flush();
			os.close();
			toast(new ConsoleMsg("Info","主线程","日志保存成功","local"));
		}
		catch (Exception e)
		{
			toast(new ConsoleMsg("Info","主线程","日志保存失败","local"));
		}
	}
	public static void startServer()
	{
		if(!started)
		{
			started=true;
			if(Build.VERSION.SDK_INT>9)
			{
				StrictMode.ThreadPolicy po=new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(po);
			}
			new Thread(Thread.currentThread().getThreadGroup(),new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					try
					{
						faqServer=new ServerSocket(10000);
						toast(new ConsoleMsg(TAG,"主线程","服务器已启动","local"));
						while(started)
						{
							Socket s=faqServer.accept();
							ClientService c=new ClientService(s,ctx);
							Data.onlineClient.add(c);
							c.start();
						}
						toast(new ConsoleMsg(TAG,"主线程","服务器已关闭","local"));
					}
					catch(SocketException e)
					{
						toast(new ConsoleMsg(TAG,"主线程","服务器已关闭","local"));
					}
					catch (IOException e)
					{
						toast(new ConsoleMsg("Error","主线程","服务器已关闭:"+e,"local"));
					}
				}
			},"FAQServer_ClientService_Server").start();
			new Thread(Thread.currentThread().getThreadGroup(),new Runnable(){
				@Override
				public void run()
				{
					String strIP="";
					try
					{
						URL url = new URL("http://m.tool.chinaz.com/ipsel"); 
						URLConnection conn = url.openConnection(); 
						BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); 
						String line = null; 
						StringBuffer result = new StringBuffer(); 
						while((line = reader.readLine()) != null)result.append(line);
						reader.close();
						strIP=result.toString();
						strIP = strIP.substring(strIP.indexOf("您的IP地址")+30);
						strIP=strIP.substring(0,strIP.indexOf("</b>"));
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
					toast(new ConsoleMsg("Info","主线程","外网IP:"+strIP,"local"));
				}},"FAQServer_getIP_Server").start();
			new Thread(Thread.currentThread().getThreadGroup(),new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					while(started)
					{
						try
						{
							for(BaseService css:Data.onlineClient)
							{
								if(css instanceof ClientService)
								{
									ClientService cs=(ClientService)css;
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
							}
							Data.saveMsgBuffer();
							Thread.sleep(30000);
						}
						catch(Throwable e)
						{
							toast(new ConsoleMsg("Error","主线程","心跳包发送器错误:"+e,"local"));
						}
					}
				}
			},"FAQServer_HBTSender_Server").start();
			new Thread(Thread.currentThread().getThreadGroup(),new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					try
					{
						fileServer=new ServerSocket(10001);
						toast(new ConsoleMsg(TAG,"主线程","文件服务器已启动","local"));
						while(started)
						{
							Socket s=fileServer.accept();
							FileService c=new FileService(s,ctx);
							c.start();
						}
						toast(new ConsoleMsg(TAG,"主线程","文件服务器已关闭","local"));
					}
					catch(SocketException e)
					{
						toast(new ConsoleMsg(TAG,"主线程","文件服务器已关闭","local"));
					}
					catch (IOException e)
					{
						toast(new ConsoleMsg("Error","主线程","无法启动文件服务器"+e,"local"));
					}
				}
			},"FAQServer_FileService_Server").start();
			new Thread(Thread.currentThread().getThreadGroup(),new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					try
					{
						httpServer=new ServerSocket(10002);
						toast(new ConsoleMsg(TAG,"主线程","HTTP服务器已启动","local"));
						while(started)
						{
							Socket s=httpServer.accept();
							HttpService c=new HttpService(s,ctx);
							Data.onlineClient.add(c);
							c.start();
						}
						toast(new ConsoleMsg(TAG,"主线程","HTTP服务器已关闭","local"));
					}
					catch(SocketException e)
					{
						toast(new ConsoleMsg(TAG,"主线程","HTTP服务器已关闭","local"));
					}
					catch (IOException e)
					{
						toast(new ConsoleMsg("Error","主线程","无法启动HTTP服务器:"+e,"local"));
					}
				}
			},"FAQServer_HttpService_Server").start();
		}
	}
	public void open(View v)
	{
		try
		{
			if(started)
			{
				started=false;
				for(BaseService c:Data.onlineClient)c.running=false;
				Data.onlineClient.clear();
				Data.loginClient.clear();
				faqServer.close();
				httpServer.close();
				fileServer.close();
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
		log=new StringBuilder();
		cmsg.clear();
		te.setText("");
	}
	public void stat(View v)
	{
		Runtime r=Runtime.getRuntime();
		long mm=r.maxMemory();
		long tm=r.totalMemory();
		long fm=r.freeMemory();
		long mb=1024*1024;
		ThreadGroup group=Thread.currentThread().getThreadGroup();  
		ThreadGroup topGroup=group;  
		while (group!=null)
		{  
			topGroup=group;  
			group=group.getParent();  
		}
		int estimatedSize=topGroup.activeCount() * 2;  
		Thread[] slackList=new Thread[estimatedSize];  
		int actualSize=topGroup.enumerate(slackList);  
		Thread[] list=new Thread[actualSize];  
		System.arraycopy(slackList, 0, list, 0, actualSize);
		Arrays.sort(list,new Comparator<Thread>(){
			@Override
			public int compare(Thread p1, Thread p2)
			{
				// TODO: Implement this method
				return p1.getName().compareToIgnoreCase(p2.getName());
			}
		});
		toast(new ConsoleMsg("Info","主线程",String.format("使用运存:%dMB/%dMB/%dMB,活动线程数:%d",(tm-fm)/mb,tm/mb,mm/mb,list.length),"local"));
		for(Thread th:list)
		{
			toast(new ConsoleMsg("Info","主线程","线程名:"+th.getName(),"local"));
		}
	}
	public void loglist(View v)
	{
		new AlertDialog.Builder(this)
		.setItems("默认,按IP,按标签,按源代码位置".split(","),new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, final int p2)
			{
				// TODO: Implement this method
				if(p2!=0)
				{
					int i=0;
					HashMap<String,Object> mp=new HashMap<String,Object>();
					for(ConsoleMsg m:cmsg)
					{
						String k=null;
						if(p2==1)k=m.ip;
						else if(p2==2)k=m.tag;
						else if(p2==3)k=m.at;
						if(mp.get(k)==null)
						{
							mp.put(k,0);
							i++;
						}
					}
					final String[] pe=new String[i];i=0;
					Iterator it=mp.entrySet().iterator();
					while(it.hasNext())pe[i++]=(String)((Map.Entry)it.next()).getKey();
					new AlertDialog.Builder(ctx).setItems(pe,new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int pp2)
						{
							// TODO: Implement this method
							log.delete(0,log.length());
							filterType=p2;
							filterKey=pe[pp2];
							for(ConsoleMsg m:cmsg)
							{
								String k=null;
								if(p2==1)k=m.ip;
								else if(p2==2)k=m.tag;
								else if(p2==3)k=m.at;
								if(pe[pp2].equals(k))
								{
									log.append(m);
									log.append("\n");
								}
							}
							postConsole();
						}
					}).show();
				}
				else
				{
					filterType=0;
					log.delete(0,log.length());
					for(ConsoleMsg m:cmsg)
					{
						log.append(m);
						log.append("\n");
					}
					postConsole();
				}
			}
		}).show();
	}
	public void fstop(View v)
	{
		System.exit(0);
	}
}
