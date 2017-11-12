package com.yzrilyzr.FAQ.Server;

import android.os.Environment;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.concurrent.CopyOnWriteArrayList;
import java.lang.reflect.InvocationTargetException;
import java.io.FileNotFoundException;

public class Loader extends OutputStream
{
	private Object Server;
	private MainActivity ctx;
	private CopyOnWriteArrayList<String> cmd;
	private Class<?> cls;
	public Loader(MainActivity mm)
	{
		System.setOut(new PrintStream(this));
		System.setErr(new PrintStream(this));
		ctx=mm;
		cmd=new CopyOnWriteArrayList<String>();
		init();
	}
	public void setCtx(MainActivity mm){
		ctx=mm;
	}
	private void init()
	{
		try
		{
			if(Server==null)
			{
				File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/FAQ_server");
				if(!f.exists())f.mkdirs();
				File u=new File(f.getAbsolutePath()+"/server.dat");
				if(!u.exists())
				{
					throw new FileNotFoundException("找不到内核文件:"+u.getAbsolutePath());
				}
				else
				{
					DexClassLoader d=new DexClassLoader(u.getAbsolutePath(),ctx.getDir("dex",ctx.MODE_PRIVATE).getAbsolutePath(),null,ctx.getClassLoader());
					cls=d.loadClass("com.yzrilyzr.FAQ.Server.Server");
					Constructor con=cls.getConstructor(new Class<?>[]{});
					Server=con.newInstance(new Object[]{});
					Object o=getField("Data");
					o.getClass().getField("datafile").set(o,f.getAbsolutePath());
					new Thread(){
						@Override
						public void run()
						{
							while(true)
								try
								{
									cls.getMethod("exec",new Class<?>[]{CopyOnWriteArrayList.class}).invoke(Server,new Object[]{cmd});
								}
								catch(Throwable e)
								{
									e.printStackTrace();
								}
						}
					}.start();
					System.out.println("内核已载入，信息:"+getField("info"));
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	public Object getField(String name){
		try
		{
			return cls.getField(name).get(Server);
		}
		catch (NoSuchFieldException e)
		{}
		catch (IllegalAccessException e)
		{}
		catch (IllegalArgumentException e)
		{}
		return null;
	}
	public Object setField(String name,Object v){
		try
		{
			cls.getField(name).set(Server,v);
		}
		catch (NoSuchFieldException e)
		{}
		catch (IllegalAccessException e)
		{}
		catch (IllegalArgumentException e)
		{}
		return null;
	}
	public Object invoke(String name,Object... param)
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
			return cls.getMethod(name,cl).invoke(Server,param);
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
	public void cmd(String s)
	{
		cmd.add(s);
	}
	@Override
	public void write(int p1) throws IOException
	{
		// TODO: Implement this method
	}
	@Override
	public void write(byte[] buffer) throws IOException
	{
		// TODO: Implement this method
		super.write(buffer);
		ctx.toast(new String(buffer));
	}
	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException
	{
		// TODO: Implement this method
		super.write(buffer, offset, count);
		ctx.toast(new String(buffer,offset,count));
	}
}
