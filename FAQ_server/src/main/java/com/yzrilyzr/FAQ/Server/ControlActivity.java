package com.yzrilyzr.FAQ.Server;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ControlActivity extends Activity
{/*
	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		lv=new ListView(this);
		setContentView(lv);
		lv.getLayoutParams().width=-1;
		lv.getLayoutParams().height=-1;
		setList();
	}
	public void setList()
	{
		setTitle("连接控制:("+Data.onlineClient.size()+"位用户在线)");
		int ind=lv.getFirstVisiblePosition();
		final ArrayList<String> a2=new ArrayList<String>();
		Iterator iter=Data.blacklist.entrySet().iterator();
		while(iter.hasNext())
		{
			ConcurrentHashMap.Entry e=(ConcurrentHashMap.Entry) iter.next();
			a2.add((String)e.getValue()+(String)e.getKey());
		}
		final int dos=Data.onlineClient.size();
		lv.setAdapter(new BaseAdapter(){
				@Override
				public int getCount()
				{
					// TODO: Implement this method
					return dos+Data.blacklist.size();
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
					ViewGroup vg=(ViewGroup) LayoutInflater.from(ControlActivity.this).inflate(R.layout.user_entry,null);
					ImageView iv=(ImageView) vg.findViewById(R.id.userentryImageView1);
					TextView t1=(TextView) vg.findViewById(R.id.userentryTextView1);
					TextView t2=(TextView) vg.findViewById(R.id.userentryTextView2);
					if(p1<dos)
					{
						BaseService c=Data.onlineClient.get(p1);
						StringBuilder sb=new StringBuilder();
						sb.append(c.IP);
						sb.append(" ");
						sb.append(c.LOCATION);
						t1.append(sb);
						sb=new StringBuilder();
						if(c instanceof ClientService){
							ClientService cc=(ClientService)c;
						if(cc.user!=null)
						{
							sb.append(cc.user.faq);
							sb.append("(");
							sb.append(cc.user.nick);
							sb.append(")");
							iv.setImageBitmap(BitmapFactory.decodeFile(Data.datafile+"/head/"+cc.user.faq+".user.png"));
						}
						}
						else sb.append(c.TAG);
						t2.setText(sb);
					}
					else
					{
						String a=a2.get(p1-dos);
						t1.append(a.substring(1));
						if(a.substring(0,1).equals("1"))t2.append("暂时封禁");
						else t2.append("永久封禁");
					}
					return vg;
				}
			});
		lv.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, final int p3, long p4)
				{
					// TODO: Implement this method
					setList();
					if(p3<dos)
					{
						final BaseService c=Data.onlineClient.get(p3);
						AlertDialog.Builder b=new AlertDialog.Builder(ControlActivity.this);
						b.setItems("断开(会自动重连),封禁IP(重启服务端后恢复),封禁IP(永久)".split(","),new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									// TODO: Implement this method
									
									if(p2==1)Data.blacklist.put(c.IP,"1");
									if(p2==2)
									{
										Data.blacklist.put(c.IP,"2");
										Data.saveBlackList();
									}
									Data.onlineClient.remove(c);
									c.isActive=false;
									c.running=false;
									setList();
								}
							});
						b.setNegativeButton("关闭",null);
						b.show();
					}
					else
					{
						AlertDialog.Builder b=new AlertDialog.Builder(ControlActivity.this);
						b.setPositiveButton("解除封禁",new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									// TODO: Implement this method
									String s=Data.blacklist.remove(a2.get(p3-dos).substring(1));
									if("2".equals(s))Data.saveBlackList();
									setList();
								}
							}).setNegativeButton("取消",null).show();
					}
				}
			});
			lv.setSelection(ind);
	}*/
}
