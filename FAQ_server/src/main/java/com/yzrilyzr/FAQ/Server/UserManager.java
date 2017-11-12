package com.yzrilyzr.FAQ.Server;

import android.widget.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UserManager extends Activity
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
		final ArrayList<User> usa=new ArrayList<User>();
		Iterator iter=Data.users.entrySet().iterator();
		while(iter.hasNext())
		{
			HashMap.Entry enty=(HashMap.Entry)iter.next();
			User us=(User)enty.getValue();
			usa.add(us);
		}
		lv.setAdapter(new BaseAdapter(){
				@Override
				public int getCount()
				{
					// TODO: Implement this method
					return usa.size()+1;
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
					if(p1==0)
				  	{
						TextView t=new TextView(UserManager.this);
						t.setText("添加用户");
						t.setPadding(20,20,20,20);
						return t;
					}
					else
					{
						ViewGroup vg=(ViewGroup) LayoutInflater.from(UserManager.this).inflate(R.layout.user_entry,null);
						ImageView iv=(ImageView) vg.findViewById(R.id.userentryImageView1);
						TextView t1=(TextView) vg.findViewById(R.id.userentryTextView1);
						TextView t2=(TextView) vg.findViewById(R.id.userentryTextView2);
						User u=usa.get(p1-1);
						t1.setText(u.nick);
						t2.setText(u.faq+" "+u.pwd);
						iv.setImageBitmap(BitmapFactory.decodeFile(Data.datafile+"/head/"+u.faq+".user.png"));
						return vg;
					}
				}
			});
		lv.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					AlertDialog.Builder b=new AlertDialog.Builder(UserManager.this);
					ViewGroup v2=(ViewGroup)LayoutInflater.from(UserManager.this).inflate(R.layout.usermanager,null);
					ViewGroup v=(ViewGroup) v2.getChildAt(0);
					final EditText aa=(EditText) v.getChildAt(0);
					final EditText bb=(EditText) v.getChildAt(1);
					final EditText cc=(EditText) v.getChildAt(2);
					final EditText dd=(EditText) v.getChildAt(3);
					final EditText ee=(EditText) v.getChildAt(4);
					final EditText ff=(EditText) v.getChildAt(5);
					final User u;
					if(p3==0)
					{
						b.setTitle("添加用户");
						u=new User();
						u.faq=-1;
						Data.users.put(u.faq+"",u);
					}
					else
					{
						u=usa.get(p3-1);
						aa.setText(u.faq+"");
						bb.setText(u.pwd);
						cc.setText(u.nick);
						dd.setText(u.sign);
						ee.setText(u.ip);
						ff.setText(u.email);
						/*for(int i:u.friends){
							EditText ed=new EditText(UserManager.this);
							ed.setText(i+"");
							v.addView(ed,-1,-2);
						}*//*
						b.setTitle("管理用户");
					}
					b.setView(v2)
						.setNegativeButton("取消",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								int ind=lv.getFirstVisiblePosition();
								setList();
								lv.setSelection(ind);
							}
						})
						.setNeutralButton("删除",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								Data.users.remove(u.faq+"");
								int ind=lv.getFirstVisiblePosition();
								Data.saveUserData();
								setList();
								lv.setSelection(ind);
							}
						})
						.setPositiveButton("保存",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								Data.users.remove(u.faq+"");
								u.faq=Integer.parseInt(aa.getText().toString());
								u.pwd=bb.getText().toString();
								u.nick=cc.getText().toString();
								u.sign=dd.getText().toString();
								u.ip=ee.getText().toString();
								u.email=ff.getText().toString();
								Data.users.put(u.faq+"",u);
								int ind=lv.getFirstVisiblePosition();
								Data.saveUserData();
								setList();
								lv.setSelection(ind);
							}
						});
				b.show();
			}
	});
	}*/
}
