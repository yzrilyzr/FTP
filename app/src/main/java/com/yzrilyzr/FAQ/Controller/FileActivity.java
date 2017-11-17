package com.yzrilyzr.FAQ.Controller;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.yzrilyzr.FAQ.Data.FileObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;

public class FileActivity extends BaseActivity
{
	ListView local,remote;
	TextView pathview,infoview;
	File[] localFiles;
	FileObj[] remoteFiles;
	File localFile;
	String remoteFile;
	String remoteInfo1,remoteInfo2;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);
		local=(ListView) findViewById(R.id.filemyListView1);
		remote=(ListView) findViewById(R.id.filemyListView2);
		pathview=(TextView) findViewById(R.id.fileTextView1);
		infoview=(TextView) findViewById(R.id.fileTextView2);
		SharedPreferences s=getSharedPreferences("fileManager",MODE_PRIVATE);
		localFile=new File(s.getString("local",Environment.getExternalStorageDirectory().getAbsolutePath()));
		remoteFile=s.getString("remote","");
		local.setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View p1, MotionEvent p2)
				{
					if(p2.getAction()==MotionEvent.ACTION_DOWN)setInfo(0);
					return false;
				}
			});
		remote.setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View p1, MotionEvent p2)
				{
					if(p2.getAction()==MotionEvent.ACTION_DOWN)setInfo(1);
					return false;
				}
			});
		local.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					File f=localFiles[p3];
					if(f.isDirectory())
					{
						localFile=f;
						local.setAdapter(new LocalAdapter());
					}
					else if(f.isFile())
					{
						Mime.openFile(ctx,f);
					}
					else
					{
						localFile=localFile.getParentFile();
						local.setAdapter(new LocalAdapter());
					}
					setInfo(0);
				}
			});
		remote.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					FileObj f=remoteFiles[p3];
					if(f.isDir)
					{
						remoteFile=f.path;
						ClientService.sendMsg(C.GFE,remoteFile);
					}
					else if(f.isFile)
					{
						//Mime.openFile(ctx,f);
					}
					else
					{
						remoteFile=f.parent;
						ClientService.sendMsg(C.GFE,remoteFile);
					}
				}
			});
		local.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{

					return true;
				}
			});
		remote.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{

					return true;
				}
			});
		setInfo(0);
		local.setAdapter(new LocalAdapter());
		ClientService.sendMsg(C.GFE,remoteFile);
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		SharedPreferences.Editor s=getSharedPreferences("fileManager",MODE_PRIVATE).edit();
		s.putString("local",localFile.getAbsolutePath())
			.putString("remote",remoteFile)
			.commit();
	}
	private void setInfo(int w)
	{
		if(w==0)
		{
			pathview.setText(localFile.getAbsolutePath());
			infoview.setText("总共:"+getUnit(localFile.getTotalSpace())+" 剩余:"+getUnit(localFile.getFreeSpace()));
		}
		else
		{
			pathview.setText(remoteFile);
			infoview.setText("总共:"+getUnit(Long.parseLong(remoteInfo1))+" 剩余:"+getUnit(Long.parseLong(remoteInfo2)));
		}
	}
	@Override
	public void rev(byte cmd, String msg)
	{
		if(cmd==C.GFE)
		{
			String[] s=msg.split("<\\?\\|\\*>");
			remoteInfo1=s[0];
			remoteInfo2=s[1];
			FileObj[] fs=new FileObj[s.length-3];
			for(int i=0;i<fs.length;i++)
			{
				fs[i]=(FileObj)ToStrObj.s2o(s[i+2]);
			}
			FileObj[] dst=new FileObj[fs.length+1];
			Arrays.sort(fs,new Comparator<FileObj>(){
					@Override
					public int compare(FileObj p1, FileObj p2)
					{
						// TODO: Implement this method
						return p1.name.compareToIgnoreCase(p2.name);
					}
				});
			int k=0;
			FileObj par=new FileObj();
			par.name="…";
			dst[k++]=par;
			for(FileObj f:fs)if(f.isDir)dst[k++]=f;
			for(FileObj f:fs)if(f.isFile)dst[k++]=f;
			remoteFiles=dst;
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						remote.setAdapter(new RemoteAdapter());
						setInfo(1);
					}
				});
		}
	}
	class LocalAdapter extends BaseAdapter
	{
		public LocalAdapter()
		{
			File[] src=localFile.listFiles();
			File[] dst=new File[src.length+1];
			Arrays.sort(src,new Comparator<File>(){
					@Override
					public int compare(File p1, File p2)
					{
						// TODO: Implement this method
						return p1.getName().compareToIgnoreCase(p2.getName());
					}
				});
			int k=0;
			dst[k++]=new File("…");
			for(File f:src)if(f.isDirectory())dst[k++]=f;
			for(File f:src)if(f.isFile())dst[k++]=f;
			localFiles=dst;
		}
		@Override
		public int getCount()
		{
			// TODO: Implement this method
			return localFiles.length;
		}
		@Override
		public Object getItem(int p1)
		{
			// TODO: Implement this method
			return null;
		}
		@Override
		public long getItemId(int p1)
		{
			// TODO: Implement this method
			return 0;
		}
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			// TODO: Implement this method
			ViewHolder h=new ViewHolder(ctx);
			File f=localFiles[p1];
			h.setText(f.getName());
			if(p1!=0)
			{
				h.setSubText(new SimpleDateFormat("yy-MM-dd hh:mm").format(f.lastModified())+" "+getUnit(f.length()));
			}
			return h.v;
		}
	}
	private String getUnit(long size)
	{
		final String[] unit=new String[]{"B","KB","MB","GB","TB"};
		int i=0;
		double siz=size;
		while(siz>1024.0)
		{
			siz/=1024.0;
			i++;
		}
		siz=Math.floor(siz*100.0)/100.0;
		return siz+unit[i];
	}
	class RemoteAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			// TODO: Implement this method
			return remoteFiles.length;
		}
		@Override
		public Object getItem(int p1)
		{
			// TODO: Implement this method
			return null;
		}
		@Override
		public long getItemId(int p1)
		{
			// TODO: Implement this method
			return 0;
		}
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			// TODO: Implement this method
			ViewHolder h=new ViewHolder(ctx);
			FileObj f=remoteFiles[p1];
			h.setText(f.name);
			if(p1!=0)
			{
				h.setSubText(new SimpleDateFormat("yy-MM-dd hh:mm").format(f.lastMod)+" "+getUnit(f.length));
			}
			return h.v;
		}
	}
	static class ViewHolder
	{
		public ViewGroup v;
		public ViewHolder(myActivity ctx)
		{
			v=(ViewGroup) LayoutInflater.from(ctx).inflate(R.layout.layout_entry,null);
		}
		public void setText(String s)
		{
			((TextView)v.findViewById(R.id.fileentryTextView1)).setText(s);
		}
		public void setSubText(String s)
		{
			TextView tv=(TextView)v.findViewById(R.id.fileentryTextView2);
			tv.setText(s);
			tv.setTextSize(util.dip2px(3));
		}
		public void setImage(Bitmap s)
		{
			((ImageView)v.findViewById(R.id.fileentryImageView1)).setImageBitmap(s);
		}
	}
}
