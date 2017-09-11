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
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;

public class ListActivity extends BaseActivity
{
	myViewPager page;
	myToolBar toolbar;
	mySlidingMenu menu;
	ListView listMsg,listFri,listGro,listFor,listCld;
	ViewGroup[] bbutton=new ViewGroup[5];
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		Data.ctx=this;
		getWindow().setBackgroundDrawable(null);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		listMsg=new myListView(ctx);
		listFri=new myListView(ctx);
		listGro=new myListView(ctx);
		listFor=new myListView(ctx);
		listCld=new myListView(ctx);
		page=(myViewPager) findViewById(R.id.listViewPager);
		page.setPages(listMsg,listFri,listGro,listFor,listCld);
		((LinearLayout.LayoutParams)listFri.getLayoutParams()).weight=1f;
		((LinearLayout.LayoutParams)listMsg.getLayoutParams()).weight=1f;
		((LinearLayout.LayoutParams)listFor.getLayoutParams()).weight=1f;
		((LinearLayout.LayoutParams)listGro.getLayoutParams()).weight=1f;
		((LinearLayout.LayoutParams)listCld.getLayoutParams()).weight=1f;
		toolbar=(myToolBar) findViewById(R.id.listmyToolBar1);
		menu=(mySlidingMenu)findViewById(R.id.listmySlidingMenu1);
		myLoadingView lo=new myLoadingView(ctx);
		lo.paint.setColor(uidata.UI_COLOR_BACK);
		toolbar.addView(lo,2);
		((ImageView)findViewById(R.id.listmenuImageView1)).setImageDrawable(Data.DefaultHead);
		myTitleButton t=toolbar.getButton(0);
		t.setScaleType(ImageView.ScaleType.CENTER_CROP);
		t.setImageDrawable(Data.DefaultHead);
		ViewGroup vg=(ViewGroup) findViewById(R.id.listmyLinearLayout1);
		for(int i=0;i<5;i++)
		{
			ViewGroup v=(ViewGroup) vg.getChildAt(i);
			final int ii=i;
			bbutton[i]=v;
			v.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						// TODO: Implement this method
						for(ViewGroup v:bbutton)
							if(v==p1)v.getChildAt(0).setBackgroundColor(0xffaaaaaa);
							else v.getChildAt(0).setBackground(null);
						page.setCurrentItem(ii,true);
					}
				});
		}
		bbutton[0].getChildAt(0).setBackgroundColor(0xffaaaaaa);
		ListView lv=(ListView) findViewById(R.id.listmenuListView1);
		final String[] s="设置,界面设置,帮助,关于,退出登录".split(",");
		lv.setAdapter(new BaseAdapter(){
				@Override
				public int getCount()
				{
					// TODO: Implement this method
					return s.length;
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
					ViewGroup vg=(ViewGroup) LayoutInflater.from(ctx).inflate(R.layout.list_menu_entry,null);
					ImageView iv=(ImageView) vg.getChildAt(0);
					Bitmap ic=null;
					if(p1==0)ic=uidata.icon.settings;
					else if(p1==1)ic=BitmapFactory.decodeResource(getResources(),R.drawable.uiedit);
					else if(p1==2)ic=BitmapFactory.decodeResource(getResources(),R.drawable.help);
					else if(p1==3)ic=uidata.icon.info;
					else if(p1==4)ic=BitmapFactory.decodeResource(getResources(),R.drawable.exit_w);
					iv.setImageBitmap(ic);
					TextView te=(TextView) vg.getChildAt(1);
					te.setText(s[p1]);
					return vg;
				}
			});
		lv.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					if(p3==0)
					{}
					else if(p3==1)startActivity(new Intent(ctx,uiSettingsActivity.class));
					else if(p3==2)
					{}
					else if(p3==3)startActivity(new Intent(ctx,AboutActivity.class));
					else if(p3==4){
						stopService(new Intent(ctx,MsgService.class));
						for(BaseActivity a:BaseActivity.activities)a.finish();
						System.gc();
						startActivity(new Intent(ctx,LoginActivity.class));
						ClientService.isLogin=false;
					}
				}
			});
		ClientService.sendMsg(C.GUS,Data.myfaq+"");
		listMsg();
	}

	private void setMyInfo()
	{
		User me=Data.getMyself();
		((ImageView)findViewById(R.id.listmenuImageView1)).setImageDrawable(Data.getMyHeadDrawable());
		myTitleButton t=toolbar.getButton(0);
		t.setScaleType(ImageView.ScaleType.CENTER_CROP);
		t.setImageDrawable(Data.getMyHeadDrawable());
		((TextView)findViewById(R.id.listmenumyTextViewTitle1)).setText(me.nick);
		((TextView)findViewById(R.id.listmenumyTextView1)).setText(me.sign);
		toolbar.removeViewAt(2);
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
						if(u!=null&&u.faq==Data.myfaq)
						{
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
					else ni.setText(faq+"");
					hd.setImageDrawable(Data.getHeadDrawable(faq,false));
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
		Data.sortMsgByTime(msgu,1);
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
						hd.setImageDrawable(Data.DefaultHead);
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
						hd.setImageDrawable(Data.getHeadDrawable(o.from,o.isGroup));
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
