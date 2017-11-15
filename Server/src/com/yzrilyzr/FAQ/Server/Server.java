package com.yzrilyzr.FAQ.Server;

import com.yzrilyzr.FAQ.Main.*;
import java.io.*;
import java.net.*;
import java.util.*;

import com.yzrilyzr.FAQ.Server.ConsoleMsg;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server
{
	int filterType=0;
	String filterKey="";
	final String TAG="Server";
	ServerThread faqServer,fileServer,httpServer,controlServer,controlFileServer,hbtServer;
	CopyOnWriteArrayList<ConsoleMsg> cmsg=null;
	public Data Data;
	private boolean initedData=false;
	private Object interf;
	public static final String info="FAQ Server v1.1_alpha (2017 11 15) by yzrilyzr";
	public static void main(String[] args)
	{
		System.out.println("输入'astart'自动配置并启动服务器");
		final Server server=new Server(new Object(){
			public void onClearView()
			{System.out.println("clear");}
			public void onReload(int code)
			{
				System.out.println(code);
			}
			public void onPrint(String s)
			{System.out.println(s);}
		});
		server.Data.datafile="/sdcard/yzr的app/FAQ_server";
		final CopyOnWriteArrayList<String> is=new CopyOnWriteArrayList<String>();
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				// TODO: Implement this method
				while(true)
					server.exec(is);
			}
		}).start();
		Scanner s=new Scanner(System.in);
		while(true)
			is.add(s.next());
	}
	public void exec(CopyOnWriteArrayList<String> is)
	{
		Scan s=new Scan(is);
		try
		{
			String tm=s.next();
			toast(">"+tm);
			switch(tm)
			{
				case "help":
				case "?":
					String[] help=new String[]{
					"start <服务器类型:int> 开服","stop <服务器类型:int> 关服",
					"readdata <路径:String> 读取数据","setworkdir 设置工作目录",
					"setrootdir <路径:String> 设置根目录","gc 清理内存",
					"stat 查看运行状态","fstop 强制停止并退出",
					"help 查看帮助","? 查看帮助",
					"astart 自动配置并启动服务器","astop 自动配置并关闭服务器",
					"sort <按照:int> <哪个:int> 筛选控制台消息",
					"clear 清除控制台","getip 获取外网ip",
					"disconnect <索引值:int> 断开客户端","loadoutlog 导出日志",
					"ban <索引值:int> <方式:int> 封禁客户端",
					"pardon <IP:String> 解封客户端",
					"reload 重载服务器","listfile <路径:String> 列表路径下的文件"};
					Arrays.sort(help,String.CASE_INSENSITIVE_ORDER);
					for(String a:help)toast(a);
					break;
				case "listfile":
					File file=new SafeFile(Data,true,Data.rootFile+s.next());
					File[] dir=file.listFiles();
					if(!file.exists())
					{
						toast("文件不存在");
						break;
					}
					toast("在'"+file.getAbsolutePath()+"'下的文件");
					Arrays.sort(dir,new Comparator<File>(){
						@Override
						public int compare(File p1, File p2)
						{
							return p1.getName().compareToIgnoreCase(p2.getName());
						}
					});
					File[] dir2=new File[dir.length];
					int i=0;
					for(File f:dir)if(f.isDirectory())dir2[i++]=f;
					for(File f:dir)if(f.isFile())dir2[i++]=f;
					i=0;
					for(File f:dir2)toast((i++)+(f.isFile()?"(F)":"(D)")+":"+f.getName());
					break;
				case "ban":
					if(Data.onlineClient.size()==0)break;
					i=0;
					for(BaseService ser:Data.onlineClient)
						toast((i++)+":"+ser.IP);
					int o=s.nextInt();
					toast("0:断开连接,1:暂时封禁,2:永久封禁");
					int p=s.nextInt();
					ban(Data.onlineClient.get(o),p);
					break;
				case "pardon":
					if(Data.blacklist.size()==0)break;
					Iterator it=Data.blacklist.entrySet().iterator();
					while(it.hasNext())
					{
						Map.Entry e=(Map.Entry)it.next();
						String type="",v=(String)e.getValue();
						if("1".equals(v))type="暂时封禁";
						else if("2".equals(v))type="永久封禁";
						toast(e.getKey()+"("+type+")");
					}
					pardon(s.next());
					break;
				case "disconnect":
					if(Data.onlineClient.size()==0)break;
					i=0;
					toast("all:断开所有");
					for(BaseService c:Data.onlineClient)
						toast((i++)+":"+c.IP);
					disconnect(s.next());
					break;
				case "reload":
					onReload();
					break;
				case "sort":
					toast("0:默认,1:IP,2:标签,3:源码位置");
					i=s.nextInt();int oo=0;
					if(i<0||i>3)
					{
						toast("参数错误");
						break;
					}
					String[] k=sortMsg(i,-1);
					if(k==null)break;
					for(String a:k)toast((oo++)+":"+a);
					oo=s.nextInt();
					sortMsg(i,oo);
					break;
				case "loadoutlog":
					loadoutLog();
					break;
				case "astart":
					System.gc();
					readData();
					startServer(0);
					getIP();
					break;
				case "astop":
					stopServer(0);
					disconnect("all");
					System.gc();
					break;
				case "start":
					toast("0:所有,1:FAQ服务器,2:心跳包发送器,3:文件服务器,4:HTTP服务器,5:控制服务器,6:控制 文件 服务器");
					startServer(s.nextInt());
					break;
				case "stop":
					toast("0:所有,1:FAQ服务器,2:心跳包发送器,3:文件服务器,4:HTTP服务器,5:控制服务器,6:控制 文件 服务器");
					stopServer(s.nextInt());
					break;
				case "getip":
					getIP();
					break;
				case "readdata":
					readData();
					break;
				case "setworkdir":
					Data.datafile=s.next();
					break;
				case "setrootdir":
					Data.rootFile=s.next();
					break;
				case "gc":
					System.gc();
					break;
				case "stat":
					getStat();
					break;
				case "fstop":
					System.exit(0);
					break;
				case "clear":
					clearLog();
					break;
				default:
					toast("未知指令，输入\"help\"或\"?\"查看帮助");
			}
		}
		catch(Throwable e)
		{
			toast("指令执行错误:"+Data.getStackTrace(e));
		}
		s.br();
	}
	private Object invoke(String name,Object... param)
	{
		try
		{
			Class<?>[] cl=null;
			if(param!=null)
			{
				cl=new Class<?>[param.length];
				int i=0;
				for(Object o:param)
					if(o!=null)cl[i++]=o.getClass();
			}
			return interf.getClass().getMethod(name,cl).invoke(interf,param);
		}
		catch (NoSuchMethodException e)
		{}
		catch (IllegalArgumentException e)
		{}
		catch (InvocationTargetException e)
		{}
		catch (IllegalAccessException e)
		{}
		return null;
	}
	private Object invoke(String name,Class cl[],Object... param)
	{
		try
		{
			return interf.getClass().getMethod(name,cl).invoke(interf,param);
		}
		catch (NoSuchMethodException e)
		{}
		catch (IllegalArgumentException e)
		{}
		catch (InvocationTargetException e)
		{}
		catch (IllegalAccessException e)
		{}
		return null;
	}
	public Server(Object interfac)
	{
		interf=interfac;
		Data=new Data();
		cmsg=new CopyOnWriteArrayList<ConsoleMsg>();
		Thread.currentThread().setName("FAQServer_Main");
		faqServer=new ServerThread("FAQServer_ClientService_Server"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				try
				{
					ser=new ServerSocket(10000);
					toast(new ConsoleMsg(TAG,"主线程","服务器已启动","local"));
					while(runn)
					{
						Socket s=ser.accept();
						ClientService c=new ClientService(s,Server.this);
						Data.onlineClient.add(c);
						c.start();
					}
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
		};
		fileServer=new ServerThread("FAQServer_FileService_Server"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				try
				{
					ser=new ServerSocket(10001);
					toast(new ConsoleMsg(TAG,"主线程","文件服务器已启动","local"));
					while(runn)
					{
						Socket s=ser.accept();
						FileService c=new FileService(s,Server.this,false);
						Data.onlineClient.add(c);
						c.start();
					}
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
		};
		httpServer=new ServerThread("FAQServer_HttpService_Server"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				try
				{
					ser=new ServerSocket(10002);
					toast(new ConsoleMsg(TAG,"主线程","HTTP服务器已启动","local"));
					while(runn)
					{
						Socket s=ser.accept();
						HttpService c=new HttpService(s,Server.this);
						Data.onlineClient.add(c);
						c.start();
					}
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
		};
		controlServer=new ServerThread("FAQServer_ControlService_Server"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				try
				{
					ser=new ServerSocket(20000);
					toast(new ConsoleMsg(TAG,"主线程","控制服务器已启动","local"));
					while(runn)
					{
						Socket s=ser.accept();
						ControlService c=new ControlService(s,Server.this);
						Data.onlineClient.add(c);
						c.start();
					}
				}
				catch(SocketException e)
				{
					toast(new ConsoleMsg(TAG,"主线程","控制服务器已关闭","local"));
				}
				catch (IOException e)
				{
					toast(new ConsoleMsg("Error","主线程","无法启动控制服务器:"+e,"local"));
				}
			}
		};
		controlFileServer=new ServerThread("FAQServer_ControlFileService_Server"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				try
				{
					ser=new ServerSocket(20001);
					toast(new ConsoleMsg(TAG,"主线程","控制 文件 服务器已启动","local"));
					while(runn)
					{
						Socket s=ser.accept();
						FileService c=new FileService(s,Server.this,true);
						Data.onlineClient.add(c);
						c.start();
					}
				}
				catch(SocketException e)
				{
					toast(new ConsoleMsg(TAG,"主线程","控制 文件 服务器已关闭","local"));
				}
				catch (IOException e)
				{
					toast(new ConsoleMsg("Error","主线程","无法启动控制 文件 服务器:"+e,"local"));
				}
			}
		};
		hbtServer=new ServerThread("FAQServer_HBTSender_Server"){
			@Override
			public void run()
			{
				toast(new ConsoleMsg(TAG,"主线程","心跳包服务器已启动","local"));
				while(runn)
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
							else if(css instanceof ControlService)
							{
								ControlService cs=(ControlService)css;
								if(!cs.isActive)
								{
									cs.running=false;
									Data.onlineClient.remove(cs);
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
		};
	}
	public void readData() throws IOException
	{
		File f=new SafeFile(Data,false,Data.datafile);
		if(!f.exists())f.mkdirs();
		File u=new SafeFile(Data,false,Data.datafile+"/users");
		if(!u.exists())u.createNewFile();
		Data.readUserData();
		Data.readBlackList();
		toast(new ConsoleMsg(TAG,"主线程","数据载入成功","local"));
		initedData=true;
	}
	public String[] sortMsg(int p2,int pp2)
	{
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
			if(pp2==-1)
				return pe;
			else
			{
				filterType=p2;
				filterKey=pe[pp2];
				onClearView();
				for(ConsoleMsg m:cmsg)
					if((filterType==1&&filterKey.equals(m.ip))||
					(filterType==2&&filterKey.equals(m.tag))||
					(filterType==3&&filterKey.equals(m.at)))
						toast(m.toString());
			}
		}
		else
		{
			filterType=0;
			filterKey="";
			for(ConsoleMsg m:cmsg)toast(m.toString());
		}
		return null;
	}
	public void disconnect(String p)
	{
		if(p.equals("all"))
		{
			for(BaseService c:Data.onlineClient)c.running=false;
			Data.onlineClient.clear();
			Data.loginClient.clear();
		}
		else
		{
			BaseService s=Data.onlineClient.get(Integer.parseInt(p));
			Data.onlineClient.remove(Integer.parseInt(p));
			Data.loginClient.remove(s.IP);
			s.running=false;
		}
		toast(new ConsoleMsg(TAG,"主线程","断开客户端成功","local"));
	}

	public void stopServer(int w) throws IOException
	{
		if(w==0||w==1)faqServer.stopServer();
		if(w==0||w==2)hbtServer.stopServer();
		if(w==0||w==3)fileServer.stopServer();
		if(w==0||w==4)httpServer.stopServer();
		if(w==0||w==5)controlServer.stopServer();
		if(w==0||w==6)controlFileServer.stopServer();
	}
	public void ban(BaseService c,int p2) throws IOException
	{
		if(p2==1)Data.blacklist.put(c.IP,"1");
		if(p2==2)
		{
			Data.blacklist.put(c.IP,"2");
			Data.saveBlackList();
		}
		Data.onlineClient.remove(c);
		c.isActive=false;
		c.running=false;
		toast(new ConsoleMsg(TAG,"主线程","封禁成功","local"));
	}
	public void pardon(String ip) throws IOException
	{
		String s=Data.blacklist.remove(ip);
		if("2".equals(s))Data.saveBlackList();
		toast(new ConsoleMsg(TAG,"主线程","解封成功","local"));
	}
	public void getStat()
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
	public void clearLog()
	{
		onClearView();
		cmsg.clear();
	}
	public void loadoutLog() throws IOException
	{
		BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(new SafeFile(Data,false,Data.datafile+"/日志导出"+System.currentTimeMillis()+".txt")));
		for(ConsoleMsg c:cmsg)
		{
			os.write(c.toString().getBytes());
			os.write("\n".getBytes());
		}
		os.flush();
		os.close();
		toast(new ConsoleMsg("Info","主线程","日志保存成功","local"));
	}
	public void toast(ConsoleMsg m)
	{
		cmsg.add(m);
		if(filterType==0||
		(filterType==1&&filterKey.equals(m.ip))||
		(filterType==2&&filterKey.equals(m.tag))||
		(filterType==3&&filterKey.equals(m.at)))
			toast(m.toString());
	}
	public void startServer(int w)
	{
		if(w==0||w==1)faqServer.start();
		if(w==0||w==2)hbtServer.start();
		if(w==0||w==3)fileServer.start();
		if(w==0||w==4)httpServer.start();
		if(w==0||w==5)controlServer.start();
		if(w==0||w==6)controlFileServer.start();
	}
	public void getIP()
	{
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
	}
	abstract class ServerThread implements Runnable
	{
		protected boolean runn=false;
		protected ServerSocket ser=null;
		protected Thread th;
		protected String n;
		public ServerThread(String n)
		{
			this.n=n;
		}
		public void stopServer() throws IOException
		{
			if(!runn)return;
			runn=false;
			if(ser!=null)ser.close();
			ser=null;
		}
		public void start()
		{
			if(runn)return;
			runn=true;
			th=new Thread(this,n);
			th.start();
		}
		public abstract void run();
	}
	class Scan
	{
		CopyOnWriteArrayList<String> is;
		int i=0;
		boolean r=false;
		public Scan(CopyOnWriteArrayList<String> is)
		{
			this.is=is;
			r=true;
		}
		public int nextInt()
		{
			while(i>=is.size()&&r)
			{}
			return Integer.parseInt(is.get(i++));
		}
		public String next()
		{
			while(i>=is.size()&&r)
			{}
			return is.get(i++);
		}
		public void br()
		{
			r=false;
			is.clear();
		}
	}
	public void onReload() throws IOException
	{
		toast(new ConsoleMsg(TAG,"onReload","服务器正在重载","local"));
		int code=0;
		if(faqServer.runn)code|=1;
		if(httpServer.runn)code|=2;
		if(hbtServer.runn)code|=4;
		if(fileServer.runn)code|=8;
		if(controlFileServer.runn)code|=16;
		if(controlServer.runn)code|=32;
		if(initedData)code|=64;
		stopServer(0);
		System.gc();
		invoke("onReload",new Class[]{int.class},code);
	}
	public void reloadServer(int code) throws IOException
	{
		if((code&1)==1)faqServer.start();
		if((code&2)==2)httpServer.start();
		if((code&4)==4)hbtServer.start();
		if((code&8)==8)fileServer.start();
		if((code&16)==16)controlFileServer.start();
		if((code&32)==32)controlServer.start();
		if((code&64)==64)readData();
	}
	public void onClearView()
	{
		invoke("onClearView");
		Iterator it=Data.loginControl.entrySet().iterator();
		while(it.hasNext())
		{
			ControlService c=(ControlService)((Map.Entry)it.next()).getValue();
			c.sendMsg(C.CLV);
		}
	}
	public void toast(String s)
	{
		invoke("onPrint",s);
		Iterator it=Data.loginControl.entrySet().iterator();
		while(it.hasNext())
		{
			ControlService c=(ControlService)((Map.Entry)it.next()).getValue();
			c.sendMsg(C.LOG,s);
		}
	}
}
