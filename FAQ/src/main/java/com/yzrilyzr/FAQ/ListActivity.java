package com.yzrilyzr.FAQ;

import android.widget.*;
import com.yzrilyzr.ui.*;
import java.util.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.FAQ.Main.T;
import com.yzrilyzr.myclass.util;
import java.text.SimpleDateFormat;

public class ListActivity extends BaseActivity
{
	myViewPager page;
	myToolBar toolbar;
	mySlidingMenu menu;
	ListView listMsg,listFri,listGro,listFor;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		Data.ctx=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		listMsg=new myListView(ctx);
		listFri=new myListView(ctx);
		listGro=new myListView(ctx);
		listFor=new myListView(ctx);
		page=(myViewPager) findViewById(R.id.listViewPager);
		page.setPages(listMsg,listFri,listGro,listFor);
		((LinearLayout.LayoutParams)listFri.getLayoutParams()).weight=1f;
		((LinearLayout.LayoutParams)listMsg.getLayoutParams()).weight=1f;
		((LinearLayout.LayoutParams)listFor.getLayoutParams()).weight=1f;
		((LinearLayout.LayoutParams)listGro.getLayoutParams()).weight=1f;
		toolbar=(myToolBar) findViewById(R.id.listmyToolBar1);
		menu=(mySlidingMenu)findViewById(R.id.listmySlidingMenu1);
		myLoadingView lo=new myLoadingView(ctx);
		lo.paint.setColor(uidata.UI_COLOR_BACK);
		toolbar.addView(lo,2);
		Data.getMyself();
		//Data.msglist.put("1303895279",new MessageObj(1303895279,Data.me.faq,(byte)0,false,"t"));
		listMsg();
	}

	private void setMyInfo()
	{
		User me=Data.getMyself();
		byte[] hd=Data.getMyHead();
		myRoundDrawable hd2=new myRoundDrawable(BitmapFactory.decodeByteArray(hd,0,hd.length));
		((ImageView)findViewById(R.id.listImageView1)).setImageDrawable(hd2);
		((TextView)findViewById(R.id.listTextView1)).setText(me.nick);
		((TextView)findViewById(R.id.listTextView2)).setText(me.sign);
		toolbar.removeViewAt(2);
		myTitleButton t=toolbar.getButton(0);
		t.setScaleType(ImageView.ScaleType.CENTER_CROP);
		t.setImageDrawable(hd2);
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
	@Override
	public void rev(final byte cmd, final String msg)
	{
		// TODO: Implement this method
		runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					if(cmd==C.MSG)listMsg();
					else if(cmd==C.GUS)
					{
						if("-1".equals(msg))return;
						User u=(User) ToStrObj.s2o(msg);
						if(u!=null&&u.faq==Data.myfaq){
							setMyInfo();
							listFri();
						}
					}
				}
			});
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
	public void page1(View v)
	{
		page.setCurrentItem(0,true);
	}
	public void page2(View v)
	{
		page.setCurrentItem(1,true);
	}
	public void page3(View v)
	{
		page.setCurrentItem(2,true);
	}
	public void page4(View v)
	{
		page.setCurrentItem(3,true);
	}
	private void listFri()
	{
		listFri.setAdapter(new BaseAdapter(){
				@Override
				public int getCount()
				{
					// TODO: Implement this method
					return Data.getMyself().friends.size();
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
					ts.setVisibility(8);
					int faq=Data.getMyself().friends.get(p1);
					User u=Data.getUser(faq);
					if(u!=null)
					{
						ni.setText(u.nick);
						ms.setText(u.sign);
					}
					else
					{
						ni.setText(faq+"");
						ClientService.sendMsg(C.GUS,faq+"");
					}
					byte[] b=Data.getHead(faq,false);
					if(b!=null)
					{
						Bitmap bm=BitmapFactory.decodeByteArray(b,0,b.length);
						hd.setImageDrawable(new myRoundDrawable(bm));
					}
					return vg;
				}
			});
		listFri.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					Intent in=new Intent(ListActivity.this,MainActivity.class);
					User u=Data.getUser(Data.getMyself().friends.get(p3));
					if(u==null)return;
					in.putExtra("user",u.o2s());
					startActivity(in);
				}
			});
	}
	private void listMsg()
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
		listMsg.setAdapter(new BaseAdapter(){

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
						User u=Data.getUser(o.from);
						if(u!=null)ni.setText(u.nick);
						else
						{
							ni.setText(o.from+"");
							ClientService.sendMsg(C.GUS,o.from+"");
						}
						byte[] b=Data.getHead(o.from,o.isGroup);
						if(b!=null)
						{
							Bitmap bm=BitmapFactory.decodeByteArray(b,0,b.length);
							hd.setImageDrawable(new myRoundDrawable(bm));
						}
					}
					return vg;
				}
			});
		listMsg.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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
						User u=Data.getUser(o.from);
						if(u==null)return;
						in.putExtra("user",u.o2s());
						startActivity(in);
					}
				}
			});
	}

}
