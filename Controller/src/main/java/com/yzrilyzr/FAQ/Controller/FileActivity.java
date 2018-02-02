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
import com.yzrilyzr.FAQ.Main.BO;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.FileService;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myAlertDialog;
import com.yzrilyzr.ui.myDialogInterface;
import com.yzrilyzr.ui.myProgressBar;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;

public class FileActivity extends BaseActivity
{
	BaseAdapter local,remote;
	TextView pathview,infoview;
	ArrayList<File> localFiles=new ArrayList<File>();
	ArrayList<FileObj> remoteFiles=new ArrayList<FileObj>();
	ArrayList<FileObj> remoteFilesTmp=new ArrayList<FileObj>();
	File localFile;
	String remoteFile;
	String remoteInfo1,remoteInfo2;
	String localInfo1,localInfo2;
	uiRunnable remotePro;
	myProgressBar prog;
	UploadThread upload;
	Comparator<File> localCom=new Comparator<File>(){
		@Override
		public int compare(File p1, File p2)
		{
			return p1.getName().compareToIgnoreCase(p2.getName());
		}
	};
	Comparator<FileObj> remoteCom=new Comparator<FileObj>(){
		@Override
		public int compare(FileObj p1, FileObj p2)
		{
			return p1.name.compareToIgnoreCase(p2.name);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);
		ListView locall=(ListView) findViewById(R.id.filemyListView1);
		ListView remotel=(ListView) findViewById(R.id.filemyListView2);
		pathview=(TextView) findViewById(R.id.fileTextView1);
		infoview=(TextView) findViewById(R.id.fileTextView2);
		prog=(myProgressBar) findViewById(R.id.filemyProgressBar1);
		SharedPreferences s=getSharedPreferences("fileManager",MODE_PRIVATE);
		localFile=new File(s.getString("local",Environment.getExternalStorageDirectory().getAbsolutePath()));
		remoteFile=s.getString("remote","");
		/*locall.setOnTouchListener(new OnTouchListener(){
		 @Override
		 public boolean onTouch(View p1, MotionEvent p2)
		 {
		 if(p2.getAction()==MotionEvent.ACTION_DOWN)setInfo(0);
		 return false;
		 }
		 });
		 remotel.setOnTouchListener(new OnTouchListener(){
		 @Override
		 public boolean onTouch(View p1, MotionEvent p2)
		 {
		 if(p2.getAction()==MotionEvent.ACTION_DOWN)setInfo(1);
		 return false;
		 }
		 });*/
		locall.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					File f=localFiles.get(p3);
					if(f.isDirectory())
					{
						localFile=f;
						local.notifyDataSetChanged();
					}
					else if(f.isFile())
					{
						Mime.openFile(ctx,f);
					}
					else
					{
						localFile=localFile.getParentFile();
						local.notifyDataSetChanged();
					}
					setInfo(0);
				}
			});
		remotel.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					FileObj f=remoteFiles.get(p3);
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
						remoteFile=remoteFile.substring(0,remoteFile.lastIndexOf("/"));
						ClientService.sendMsg(C.GFE,remoteFile);
					}
				}
			});
		locall.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4)
				{
					if(p3!=0)
						new myAlertDialog(ctx)
							.setTitle("操作")
							.setItems("上传".split(","),new myDialogInterface(){
								@Override public void click(View p1,int p2)
								{
									if(p2==0)
									{
										if(upload==null)
										{
											upload=new UploadThread(localFiles.get(p3),remoteFile);
											upload.start();
										}
										else upload.getFile(localFiles.get(p3));
									}
								}
							})
							.setNegativeButton("取消",null)
							.show();
					return true;
				}
			});
		remotel.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{

					return true;
				}
			});
		locall.setAdapter(local=new LocalAdapter());
		remotel.setAdapter(remote=new RemoteAdapter());
		ClientService.sendMsg(C.GFE,remoteFile);
		pathview.setTextSize(util.dip2px(3));
		infoview.setTextSize(util.dip2px(3));
		local.notifyDataSetChanged();
		remotePro=new uiRunnable();
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
			infoview.setText("总共:"+localInfo1+" 剩余:"+localInfo2);
		}
		else
		{
			pathview.setText(remoteFile);
			infoview.setText("总共:"+remoteInfo1+" 剩余:"+remoteInfo2);
		}
	}
	@Override
	public void rev(byte cmd, String msg)
	{
		if(cmd==C.GFE)
		{
			char c=msg.charAt(0);
			if(c=='F'){
				remoteFilesTmp.add((FileObj)ToStrObj.s2o(msg.substring(1)));
				remotePro.setProg(remoteFilesTmp.size(),remotePro.s,remotePro.m);
			}
			else if(c=='N')
			{
				util.toast(ctx,"远程文件不存在");
				ClientService.sendMsg(C.GFE,"");
				return;
			}
			else if(c=='H')
			{
				String[] s=msg.substring(1).split(",");
				remoteInfo1=getUnit(Long.parseLong(s[0]));
				remoteInfo2=getUnit(Long.parseLong(s[1]));
				remotePro.setProg(0,remotePro.s,Integer.parseInt(s[2]));
				remoteFilesTmp.clear();
			}
			else if(c=='E')
			{
				if(remotePro.m!=remoteFilesTmp.size()&&(remotePro.s+=remotePro.m/4)<remotePro.m){
					util.toast(ctx,"发生丢包，个数为:"+(remotePro.m-remoteFilesTmp.size())+"\n还将重试"+(4-remotePro.s*4/remotePro.m)+"次");
					ClientService.sendMsg(C.GFE,remoteFile);
				}
				else
				{
					Collections.sort(remoteFilesTmp,remoteCom);
					remoteFiles.clear();
					FileObj par=new FileObj();
					par.name="…";
					remoteFiles.add(par);
					for(FileObj f:remoteFilesTmp)if(f.isDir)remoteFiles.add(f);
					for(FileObj f:remoteFilesTmp)if(f.isFile)remoteFiles.add(f);
					runOnUiThread(new Runnable(){
							@Override
							public void run()
							{
								remote.notifyDataSetChanged();
								setInfo(1);
							}
						});
				}
			}
			
		}
	}
	class LocalAdapter extends BaseAdapter
	{
		@Override
		public void notifyDataSetChanged()
		{
			localInfo1=getUnit(localFile.getTotalSpace());
			localInfo2=getUnit(localFile.getFreeSpace());
			File[] src=localFile.listFiles();
			Arrays.sort(src,localCom);
			localFiles.clear();
			localFiles.add(new File("…"));
			for(File f:src)if(f.isDirectory())localFiles.add(f);
			for(File f:src)if(f.isFile())localFiles.add(f);
			setInfo(0);
			super.notifyDataSetChanged();
		}
		@Override
		public int getCount()
		{
			return localFiles.size();
		}
		@Override
		public Object getItem(int p1)
		{
			return null;
		}
		@Override
		public long getItemId(int p1)
		{
			return 0;
		}
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			ViewHolder h=new ViewHolder(ctx);
			File f=localFiles.get(p1);
			h.setText(f.getName());
			if(p1!=0)
			{
				h.setSubText(new SimpleDateFormat("yy-MM-dd hh:mm").format(f.lastModified())+" "+getUnit(f.length()));
				if(f.isFile())h.setImage(R.drawable.file);
				else if(f.isDirectory())h.setImage(R.drawable.folder);
			}
			else h.setImage(R.drawable.upparent);
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
			return remoteFiles.size();
		}
		@Override
		public Object getItem(int p1)
		{
			return null;
		}
		@Override
		public long getItemId(int p1)
		{
			return 0;
		}
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			ViewHolder h=new ViewHolder(ctx);
			FileObj f=remoteFiles.get(p1);
			h.setText(f.name);
			if(p1!=0)
			{
				h.setSubText(new SimpleDateFormat("yy-MM-dd hh:mm").format(f.lastMod)+" "+getUnit(f.length));
				if(f.isFile)h.setImage(R.drawable.file);
				else if(f.isDir)h.setImage(R.drawable.folder);
			}
			else h.setImage(R.drawable.upparent);
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
			TextView tv=(TextView)v.findViewById(R.id.fileentryTextView1);
			tv.setText(s);
			tv.setTextSize(util.dip2px(4));
		}
		public void setSubText(String s)
		{
			TextView tv=(TextView)v.findViewById(R.id.fileentryTextView2);
			tv.setText(s);
			tv.setTextSize(util.dip2px(2));
		}
		public void setImage(Bitmap s)
		{
			((ImageView)v.findViewById(R.id.fileentryImageView1)).setImageBitmap(s);
		}
		public void setImage(int s)
		{
			((ImageView)v.findViewById(R.id.fileentryImageView1)).setImageResource(s);
		}
	}
	class UploadThread extends Thread implements FileService.CallBack
	{
		String local,remote;
		ArrayList<File> list;
		File file;
		int thind=0;
		uiRunnable runn;
		CopyOnWriteArrayList<FileService> threads;
		public UploadThread(File fie,String remote)
		{
			file=fie;
			local=file.getParentFile().getAbsolutePath();
			this.remote=remote;
			list=new ArrayList<File>();
			threads=new CopyOnWriteArrayList<FileService>();
			runn=new uiRunnable();
		}
		private void getFile(File f)
		{
			if(f.isFile())list.add(f);
			else if(f.isDirectory())
			{
				list.add(f);
				File[] fs=f.listFiles();
				for(File ff:fs)getFile(ff);
			}
		}
		@Override
		public void run()
		{
			getFile(file);
			for(File f:list)
			{
				try
				{
					BO bo=new BO(){
						@Override
						public boolean g()
						{
							return threads.size()>4;
						}
					};
					while(bo.g())Thread.sleep(1);
					int cmd=-1;
					if(f.isFile())cmd=1;
					if(f.isDirectory())cmd=3;
					FileService ser=new FileService(f,remote+f.getAbsolutePath().substring(local.length()),cmd);
					ser.setCallBack(this);
					ser.start();
					threads.add(ser);
				}
				catch (Throwable e)
				{}
			}
			upload=null;
		}
		@Override
		public void onProgress(FileService s,long p, long m)
		{
			int first=(int)(100l*p/m);
			int second=100*thind/list.size();
			runn.setProg(second,first,100);
		}
		@Override
		public void onFinish(FileService fs,boolean success)
		{
			threads.remove(fs);
			thind++;
			onProgress(fs,0,1);
			if(thind==list.size())util.toast(ctx,success?"上传成功":"上传失败");
		}
	}
	class uiRunnable implements Runnable
	{
		int m,f,s;
		//long time=0;
		@Override
		public void run()
		{
			prog.setMax(m);
			prog.setProgress(f);
			prog.setSecondaryProgress(s);
		}
		public void setProg(int f,int s,int m)
		{
			if(m==0)m=1;
			this.f=f;
			this.s=s;
			this.m=m;
			//if(System.currentTimeMillis()>time+100)
			{
				runOnUiThread(this);
				//time=System.currentTimeMillis();
			}
		}
	}
}
