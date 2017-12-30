package com.yzrilyzr.FAQ.Server;

import com.yzrilyzr.FAQ.Main.*;
import java.io.*;
import java.net.*;
import java.util.*;

import com.yzrilyzr.FAQ.Server.ConsoleMsg;
import com.yzrilyzr.JavaExp.Exp;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Server implements Thread.UncaughtExceptionHandler
{
	int filterType=0;
	String filterKey="";
	public final String TAG="Server";
	ServerThread faqServer,fileServer,httpServer,controlServer,controlFileServer,remoteControlServer;
	CopyOnWriteArrayList<ConsoleMsg> cmsg=null;
	public Data Data;
	private boolean initedData=false;
	private Object interf;
	private Exp exp;
	private ExecutorService pool;
	private MediaPlayer player;
	public static final String info="FAQ Server v1.2.2.1_alpha (2017 11 26) by yzrilyzr";
	public final CopyOnWriteArrayList<String> mCmds=new CopyOnWriteArrayList<String>();
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
		server.Data.rootFile="/sdcard";
		final CopyOnWriteArrayList<String> mCmds=new CopyOnWriteArrayList<String>();
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				// TODO: Implement this method
				while(true)
					server.exec(new Scan(mCmds));
			}
		}).start();
		Scanner s=new Scanner(System.in);
		while(true)
			mCmds.add(s.next());
	}
	public void exec(Scan s)
	{
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
					"help 查看帮助","? 查看帮助","exec <JavaExp:String> 执行JavaExp",
					"astart 自动配置并启动服务器","astop 自动配置并关闭服务器",
					"sort <按照:int> <哪个:int> 筛选控制台消息",
					"clear 清除控制台","getip 获取外网ip",
					"loadoutlog 导出日志",
					"ban <索引值:int> 封禁客户端",
					"pardon <IP:String> 解封客户端",
					"play <文件夹或文件路径:String> 播放音乐",
					"reload 重载服务器","listfile <路径:String> 列表路径下的文件",
					"lock 锁定服务器","setpassword <密码:String> 设置服务器密码",
					"unlock 解锁服务器"};
					Arrays.sort(help,String.CASE_INSENSITIVE_ORDER);
					for(String a:help)toast(a);
					break;
				case "lock":
					onDevice(1,"");
					toast("已锁定");
					break;
				case "unlock":
					onDevice(3,"");
					toast("已解锁");
					break;
				case "setpassword":
					toast("请输入密码");
					onDevice(2,s.next());
					toast("设置成功");
					break;
				case "play":
					toast("play:播放,stop:停止,pause:暂停,next:下一首,prev:上一首,select:选择");
					if(player==null)player=new MediaPlayer();
					String ch=s.next();
					if("stop".equals(ch))player.stop();
					else if("next".equals(ch))player.next();
					else if("select".equals(ch))
					{
						int i=0;
						for(File f:player.playlist)
							toast((i++)+":"+f.getName());
						player.select(s.nextInt());
					}
					else if("prev".equals(ch))player.prev();
					else if("pause".equals(ch))player.pause();
					else if("play".equals(ch))
					{
						player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
							@Override
							public void onCompletion(MediaPlayer mp)
							{
								player.next();
							}
						});
						if(!player.isPause())player.playNow();
						else player.start();
					}
					else
					{
						File f=new SafeFile(Data,true,Data.rootFile+ch);
						toast("路径是:"+f.getAbsolutePath());
						player.setDir(f);
					}
					break;
				case "exec":
					toast("i:初始化,n <名称:String>:新分组,s <名称:String>:设置当前操作的组,g:获取当前操作的组");
					ch=s.next();
					toast(">"+ch);
					if("i".equals(ch))exp=new Exp();
					else if("n".equals(ch))exp.newGroup(s.next());
					else if("s".equals(ch))exp.setNowGroup(s.next());
					else if("g".equals(ch))toast("当前组:"+exp.getNowGroup());
					else exp.parse(ch);
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
					if(Data.connectedClient.size()==0)break;
					i=0;
					for(String ser:Data.connectedClient)
						toast((i++)+":"+ser);
					int o=s.nextInt();
					toast("0:断开连接,1:暂时封禁,2:永久封禁");
					int p=s.nextInt();
					ban(Data.connectedClient.get(o),p);
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
					System.gc();
					break;
				case "start":
					toast("0:所有,1:FAQ服务器,2:心跳包发送器,3:文件服务器,4:HTTP服务器,5:控制服务器,6:控制 文件 服务器,7:远程控制服务器");
					startServer(s.nextInt());
					break;
				case "stop":
					toast("0:所有,1:FAQ服务器,2:心跳包发送器,3:文件服务器,4:HTTP服务器,5:控制服务器,6:控制 文件 服务器,7:远程控制服务器");
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
		s.reset();
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
		pool=Executors.newCachedThreadPool();
		cmsg=new CopyOnWriteArrayList<ConsoleMsg>();
		Thread.currentThread().setName("FAQServer_Main");
		Thread.currentThread().setDefaultUncaughtExceptionHandler(this);
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				// TODO: Implement this method
				while(true)
					exec(new Scan(mCmds));
			}
		}).start();
		faqServer=new ServerThread("FAQServer_ClientService_Server"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				try
				{
					server=new DatagramSocket(10000);
					toast(new ConsoleMsg(TAG,"主线程","服务器已启动","local"));
					while(runn)
					{
						byte[] bb=new byte[1024];
						DatagramPacket pa=new DatagramPacket(bb,bb.length);
						((DatagramSocket)server).receive(pa);
						pool.execute(new ClientService(pa,Server.this));
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
					server=new ServerSocket(10001);
					toast(new ConsoleMsg(TAG,"主线程","文件服务器已启动","local"));
					while(runn)
					{
						Socket s=((ServerSocket)server).accept();
						pool.execute(new FileService(s,Server.this,false));
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
					server=new ServerSocket(10002);
					toast(new ConsoleMsg(TAG,"主线程","HTTP服务器已启动","local"));
					while(runn)
					{
						Socket s=((ServerSocket)server).accept();
						pool.execute(new HttpService(s,Server.this));
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
					server=new DatagramSocket(20000);
					toast(new ConsoleMsg(TAG,"主线程","控制服务器已启动","local"));
					while(runn)
					{
						byte[] bb=new byte[1024];
						DatagramPacket pa=new DatagramPacket(bb,bb.length);
						((DatagramSocket)server).receive(pa);
						pool.execute(new ControlService(pa,Server.this));
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
					server=new ServerSocket(20001);
					toast(new ConsoleMsg(TAG,"主线程","控制 文件 服务器已启动","local"));
					while(runn)
					{
						Socket s=((ServerSocket)server).accept();
						pool.execute(new FileService(s,Server.this,true));
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
		remoteControlServer=new ServerThread("FAQServer_RemoteControlService_Server"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				try
				{
					server=new DatagramSocket(20002);
					toast(new ConsoleMsg(TAG,"主线程","远程控制服务器已启动","local"));
					while(runn)
					{
						byte[] bb=new byte[1024];
						DatagramPacket pa=new DatagramPacket(bb,bb.length);
						((DatagramSocket)server).receive(pa);
						pool.execute(new RemoteControlService(pa,Server.this));
					}
				}
				catch(SocketException e)
				{
					toast(new ConsoleMsg(TAG,"主线程","远程控制服务器已关闭","local"));
				}
				catch (IOException e)
				{
					toast(new ConsoleMsg("Error","主线程","无法启动远程控制服务器:"+e,"local"));
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
	public void stopServer(int w) throws IOException
	{
		if(w==0||w==1)faqServer.stopServer();
		if(w==0||w==2)fileServer.stopServer();
		if(w==0||w==3)httpServer.stopServer();
		if(w==0||w==4)controlServer.stopServer();
		if(w==0||w==5)controlFileServer.stopServer();
		if(w==0||w==6)remoteControlServer.stopServer();
	}
	public void ban(String c,int p2) throws IOException
	{
		if(p2==1)Data.blacklist.put(c,"1");
		if(p2==2)
		{
			Data.blacklist.put(c,"2");
			Data.saveBlackList();
		}
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
	@Override
	public void uncaughtException(Thread p1, Throwable p2)
	{
		try
		{
			BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(Data.datafile+"/错误日志_"+System.currentTimeMillis()+".txt"));
			PrintStream p=new PrintStream(os);
			p.println("ThreadName:"+p1.getName());
			p.println("StackTrace:");
			p2.printStackTrace(p);
			os.close();
			p.close();
		}
		catch (Exception e)
		{}
		finally
		{
			System.exit(0);
		}
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
		if(w==0||w==2)fileServer.start();
		if(w==0||w==3)httpServer.start();
		if(w==0||w==4)controlServer.start();
		if(w==0||w==5)controlFileServer.start();
		if(w==0||w==6)remoteControlServer.start();
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
		protected Closeable server=null;
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
			if(server!=null)server.close();
			server=null;
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
	public static class Scan
	{
		CopyOnWriteArrayList<String> is;
		int i=0;
		boolean r=false;
		public Scan(CopyOnWriteArrayList<String> is)
		{
			this.is=is;
			r=true;
		}
		public int nextInt() throws InterruptedException
		{
			while(i>=is.size()&&r)
			{
				Thread.sleep(1);
			}
			return Integer.parseInt(is.get(i++));
		}
		public String next() throws InterruptedException
		{
			while(i>=is.size()&&r)
			{
				Thread.sleep(1);
			}
			return is.get(i++);
		}
		public void reset()
		{
			i=0;
			is.clear();
		}
		public void stop()
		{
			is.clear();
			r=false;
		}
	}
	public void onReload() throws IOException
	{
		toast(new ConsoleMsg(TAG,"onReload","服务器正在重载","local"));
		int code=0;
		if(faqServer.runn)code|=1;
		if(httpServer.runn)code|=2;
		if(fileServer.runn)code|=4;
		if(controlFileServer.runn)code|=8;
		if(controlServer.runn)code|=16;
		if(initedData)code|=32;
		if(remoteControlServer.runn)code|=64;
		stopServer(0);
		System.gc();
		invoke("onReload",new Class[]{int.class},code);
	}
	public void reloadServer(int code) throws IOException
	{
		if((code&1)==1)faqServer.start();
		if((code&2)==2)httpServer.start();
		if((code&4)==4)fileServer.start();
		if((code&8)==8)controlFileServer.start();
		if((code&16)==16)controlServer.start();
		if((code&32)==32)readData();
		if((code&64)==64)remoteControlServer.start();
	}
	public void onClearView()
	{
		invoke("onClearView");
		try
		{
			UdpService.sendMsgToOtherControl(Data,C.CLV,null);
		}
		catch (Exception e)
		{}
	}
	public void toast(String s)
	{
		invoke("onPrint",s);
		try
		{
			UdpService.sendMsgToOtherControl(Data,C.LOG,null);
		}
		catch (Exception e)
		{}
	}
	public void onGetScreen(ByteArrayOutputStream os)
	{
		invoke("onGetScreen",os);
	}
	public void onDevice(int c,String p)
	{
		invoke("onDevice",new Class[]{int.class,String.class},c,p);
	}
}
