package com.yzrilyzr.FAQ.Server;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MediaPlayer implements InvocationHandler
{
	private static Class cls,intf;
	private Object player;
	private OnCompletionListener lis=null;
	public File[] playlist;
	private int playIndex=0;
	private boolean isPause=false;
	public MediaPlayer()
	{
		try
		{
			cls=Class.forName("android.media.MediaPlayer");
			intf=Class.forName("android.media.MediaPlayer$OnCompletionListener");
			player=cls.newInstance();
		}
		catch (Exception e)
		{}
	}
	public boolean isPause()
	{
		return isPause;
	}
	public void start()
	{
		try
		{
			cls.getMethod("start").invoke(player);
			isPause=false;
		}
		catch (Exception e)
		{}
	}
	public void stop()
	{
		try
		{
			cls.getMethod("stop").invoke(player);
			isPause=true;
		}
		catch (Exception e)
		{}
	}
	public void next()
	{
		reset();
		if(++playIndex==playlist.length)playIndex=0;
		setDataSource(playlist[playIndex].getAbsolutePath());
		prepare();
		start();
	}
	public void prev()
	{
		reset();
		if(--playIndex==-1)playIndex=playlist.length-1;
		setDataSource(playlist[playIndex].getAbsolutePath());
		prepare();
		start();
	}
	public void select(int i)
	{
		playIndex=i;
		reset();
		if(playIndex<=-1)playIndex=playlist.length-1;
		if(playIndex>=playlist.length)playIndex=0;
		setDataSource(playlist[playIndex].getAbsolutePath());
		prepare();
		start();
	}
	public void playNow()
	{
		reset();
		setDataSource(playlist[playIndex].getAbsolutePath());
		prepare();
		start();
	}
	public void setDir(File f)
	{
		playlist=new File[1];
		if(f.isFile())playlist[0]=f;
		else if(f.isDirectory())
			playlist=f.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File p1, String p2)
				{
					p2=p2.toLowerCase();
					if(p2.endsWith("mp3"))return true;
					return false;
				}
			});
	}
	public void pause()
	{
		try
		{
			cls.getMethod("pause").invoke(player);
			isPause=true;
		}
		catch (Exception e)
		{}
	}
	public void reset()
	{
		try
		{
			cls.getMethod("reset").invoke(player);
			isPause=true;
		}
		catch (Exception e)
		{}
	}
	public void setDataSource(String str)
	{
		try
		{
			cls.getMethod("setDataSource",String.class).invoke(player,str);
		}
		catch (Exception e)
		{}
	}
	public void prepare()
	{
		try
		{
			cls.getMethod("prepare").invoke(player);
		}
		catch (Exception e)
		{}
	}
	public void setOnCompletionListener(OnCompletionListener lis)
	{
		try
		{
			this.lis=lis;
			Object mObj = java.lang.reflect.Proxy.newProxyInstance(MediaPlayer.class.getClassLoader(),new Class[]{intf},this);
			cls.getMethod("setOnCompletionListener",intf).invoke(player,mObj);
		}
		catch (Exception e)
		{}
	}
	@Override
	public Object invoke(Object p1, Method p2, Object[] p3) throws Throwable
	{
		if(p2.getName().equals("onCompletion")&&lis!=null)lis.onCompletion(this);
		return null;
	}
	public interface OnCompletionListener
	{
		public abstract void onCompletion(MediaPlayer mp);
	}
}
