package com.yzrilyzr.FAQ;

import com.yzrilyzr.ui.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import com.yzrilyzr.FAQ.Main.T;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

public class ListActivity extends BaseActivity
{
	ListView list;
	myToolBar toolbar;
	mySlidingMenu menu;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		Data.ctx=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		list=(ListView) findViewById(R.id.listListView1);
		toolbar=(myToolBar) findViewById(R.id.listmyToolBar1);
		menu=(mySlidingMenu)findViewById(R.id.listmySlidingMenu1);
		myLoadingView lo=new myLoadingView(ctx);
		lo.paint.setColor(uidata.UI_COLOR_BACK);
		toolbar.addView(lo,2);
		byte[] hd=Data.getHead(Data.me.faq+"",false);
		myRoundDrawable hd2=new myRoundDrawable(BitmapFactory.decodeByteArray(hd,0,hd.length));
		((ImageView)findViewById(R.id.listImageView1)).setImageDrawable(hd2);
		((TextView)findViewById(R.id.listTextView1)).setText(Data.me.nick);
		((TextView)findViewById(R.id.listTextView2)).setText(Data.me.sign);
		toolbar.removeViewAt(2);
		myTitleButton t=toolbar.getButton(0);
		t.setScaleType(ImageView.ScaleType.CENTER_CROP);
		t.setImageDrawable(hd2);
		//Data.msglist.put("1303895279",new MessageObj(1303895279,Data.me.faq,(byte)0,false,"t"));
		list();
	}
	public void add(View v)
	{
		new myAlertDialog(ctx)
			.setTitle("添加")
			.setItems("好友,群组,创建群组".split(","),new myDialogInterface(){
				public void click(View v,final int ii)
				{
					if(ii==0||ii==1)
					{
						final myEditText e=new myEditText(ctx);
						e.setHint("请输入FAQ号");
						e.setInputType(InputType.TYPE_CLASS_NUMBER);
						new myAlertDialog(ctx)
							.setTitle("查找")
							.setView(e)
							.setPositiveButton("查找",new myDialogInterface(){
								public void click(View v,int i)
								{
									try
									{
										Intent in=new Intent(ctx,ProfileActivity.class);
										in.putExtra(ii==0?"faq":"gro",Integer.parseInt(e.getText().toString()));
										startActivity(in);
									}
									catch(Throwable e)
									{
										util.toast(ctx,"您输入的FAQ号有误");
									}
								}})
							.setNegativeButton("取消",null)
							.show();
						e.getLayoutParams().width=-1;
					}
				}
			})
			.setNegativeButton("关闭",null)
			.show();
	}
	public void logout()
	{
		new myAlertDialog(this)
			.setTitle("强制下线提示")
			.setCancelable(false)
			.setMessage("您的帐号在其他客户端登录，您已被强制下线")
			.setPositiveButton("确定",new myDialogInterface(){
				public void click(View v,int i)
				{
					Intent e=new Intent(ctx,LoginActivity.class);
					e.putExtra("al",false);
					startActivity(e);
					finish();
				}})
			.show();
	}
	@Override
	public void rev(byte cmd, String msg)
	{
		// TODO: Implement this method
		if(cmd==C.MSG)
		{
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						list();
					}
				});
		}
	}
	public void menu(View v)
	{
		menu.openMenu();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO: Implement this method
		if(event.getAction()==KeyEvent.ACTION_DOWN)
		{
			if(keyCode==KeyEvent.KEYCODE_MENU)menu.toggle();
			if(keyCode==KeyEvent.KEYCODE_BACK)
			{
				if(menu.getIsOpen())menu.closeMenu();
				else return super.onKeyDown(keyCode, event);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void list()
	{
		final ArrayList<MessageObj> msgu=new ArrayList<MessageObj>();
		Iterator it=Data.msglist.entrySet().iterator();
		while(it.hasNext())
		{
			MessageObj e=(MessageObj)((Map.Entry) it.next()).getValue();
			msgu.add(e);
		}
		Collections.sort(msgu,new Comparator<MessageObj>(){
				@Override
				public int compare(MessageObj p1, MessageObj p2)
				{
					// TODO: Implement this method
					if(p1.time>p2.time)return -1;
					else return 1;
				}
			});
		list.setAdapter(new BaseAdapter(){

				@Override
				public int getCount()
				{
					// TODO: Implement this method
					return msgu.size();
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
					ViewGroup vg=(ViewGroup)LayoutInflater.from(ListActivity.this).inflate(R.layout.list_entry,null);
					ImageView hd=(ImageView) vg.findViewById(R.id.listentryImageView1);
					TextView ni=(TextView) vg.findViewById(R.id.listentryTextView1);
					TextView ms=(TextView) vg.findViewById(R.id.listentryTextView2);
					TextView ts=(TextView) vg.findViewById(R.id.listentryTextView3);
					MessageObj o=msgu.get(p1);
					ms.setText(o.msg);
					ts.setText(new SimpleDateFormat("HH:mm").format(new Date(o.time)));
					if(o.type==T.VMS)
					{
						hd.setImageDrawable(new myRoundDrawable(ctx,R.drawable.launch));
						ni.setText("系统消息");
					}
					else
					{
						User u=Data.users.get(o.from+"");
						if(u!=null)ni.setText(u.nick);
						else ni.setText(o.from+"");
						byte[] b=Data.getHead(o.from+"",o.isGroup);
						if(b!=null)
						{
							Bitmap bm=BitmapFactory.decodeByteArray(b,0,b.length);
							hd.setImageDrawable(new myRoundDrawable(bm));
						}
					}
					return vg;
				}
			});
		list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					MessageObj o=msgu.get(p3);
					if(o.type==T.VMS)
						startActivity(new Intent(ctx,SysMsgActivity.class));
					else
					{
						Intent in=new Intent(ListActivity.this,MainActivity.class);
						User u=Data.users.get(o.from+"");
						if(u==null)return;
						in.putExtra("user",u.o2s());
						startActivity(in);
					}
				}
			});
	}

}
