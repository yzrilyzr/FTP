package com.yzrilyzr.FAQ.Server;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Environment;
import android.os.PowerManager;
import dalvik.system.DexClassLoader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

public class Loader
{
	private Object Server;
	private MainActivity ctx;
	private CopyOnWriteArrayList<String> cmd;
	private Object Scan;
	private Class<?> cls;
	public Loader(MainActivity mm)
	{
		ctx=mm;
		cmd=new CopyOnWriteArrayList<String>();
		init();
	}
	public void setCtx(MainActivity mm)
	{
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
					Constructor con=cls.getConstructor(new Class<?>[]{Object.class});
					Server=con.newInstance(new Object[]{this});
					Object o=getField("Data");
					o.getClass().getField("rootFile").set(o,Environment.getExternalStorageDirectory().getAbsolutePath());
					o.getClass().getField("datafile").set(o,f.getAbsolutePath());
					Class<?>[] cs=cls.getClasses();
					for(Class<?> c:cs)
					{
						if(c.getName().contains("Scan"))
						{
							Scan=c.getConstructor(CopyOnWriteArrayList.class).newInstance(cmd);
						}
					}
					new Thread(){
						@Override
						public void run()
						{

							while(Server!=null)
								try
								{
									cls.getMethod("exec",new Class<?>[]{Scan.getClass()}).invoke(Server,new Object[]{Scan});
								}
								catch(Throwable e)
								{
								}
						}
					}.start();
					onPrint("内核已载入，信息:"+getField("info"));
				}
			}
		}
		catch (Throwable e)
		{
			onPrint(e.toString());
		}
	}
	public Object getField(String name)
	{
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
	public Object setField(String name,Object v)
	{
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
	public Object invoke(String name,Class[] cl,Object... param)
	{
		try
		{
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
		String[] cm=s.split(" ");
		for(String b:cm)cmd.add(b);
	}
	public void onReload(int code)
	{
		Server=null;
		MainActivity.loader=null;
		cmd("");
		cls=null;
		cmd=null;
		System.gc();
		MainActivity.loader=new Loader(ctx);
		ctx=null;
		MainActivity.loader.invoke("reloadServer",new Class[]{int.class},code);
	}
	public void onClearView()
	{
		ctx.te.clear();
	}
	public void onPrint(String s)
	{
		ctx.toast(s);
	}
	public void onDevice(int c,String p){
		DevicePolicyManager m=(DevicePolicyManager)ctx.getSystemService(ctx.DEVICE_POLICY_SERVICE);
		if(c==1)m.lockNow();
		else if(c==2)m.resetPassword(p,DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED);
		else if(c==3){
			PowerManager pm = (PowerManager)ctx.getSystemService(ctx.POWER_SERVICE);
			PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|
			PowerManager.SCREEN_DIM_WAKE_LOCK,"target");
			mWakelock.acquire();
			mWakelock.release();
			KeyguardManager km = (KeyguardManager)ctx.getSystemService(ctx.KEYGUARD_SERVICE);
			KeyguardManager.KeyguardLock kl = km.newKeyguardLock("faq");
			if (km.inKeyguardRestrictedInputMode())kl.disableKeyguard(); 
			}
	}
	public byte[] onGetScreen()
	{
		Image image = ctx.imr.acquireNextImage();
		int height = image.getHeight();
		final Image.Plane planes = image.getPlanes()[0];
		final ByteBuffer buffer = planes.getBuffer();
		Bitmap bitmap = Bitmap.createBitmap(planes.getRowStride()/planes.getPixelStride(), height, Bitmap.Config.ARGB_8888);
		bitmap.copyPixelsFromBuffer(buffer);
		Matrix mt=new Matrix();
		mt.postScale(0.3f,0.3f);
		bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),mt,false);
		ByteArrayOutputStream o=new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG,2,o);
		bitmap.recycle();
		return o.toByteArray();
	}
}
