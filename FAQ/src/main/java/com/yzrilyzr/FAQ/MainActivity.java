package com.yzrilyzr.FAQ;

import android.widget.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myToolBar;
import java.util.ArrayList;
import java.util.Collections;
import com.yzrilyzr.FAQ.Main.T;

public class MainActivity extends BaseActivity
{
	User to;
	EditText et;
	LinearLayout ll;
	Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		AndroidBug5497Workaround.assistActivity(this);
		to=(User) ToStrObj.s2o(getIntent().getStringExtra("user"));
		setTitle(to.nick);
		ll=(LinearLayout) findViewById(R.id.mainLinearLayout1);
		et=(EditText) findViewById(R.id.mainEditText1);
		bt=(Button) findViewById(R.id.mainButton1);
		bt.setEnabled(false);
		myToolBar mt=(myToolBar) findViewById(R.id.mainmyToolBar1);
		mt.setTitle(to.nick);
		et.setOnFocusChangeListener(new OnFocusChangeListener(){
				@Override
				public void onFocusChange(View p1, boolean p2)
				{
					// TODO: Implement this method
					new Handler().postDelayed(new Runnable(){
							@Override
							public void run()
							{
								// TODO: Implement this method
								((ScrollView)ll.getParent()).fullScroll(View.FOCUS_DOWN);
							}
						},300);
				}
			});
		et.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
					if(p1.length()==0)bt.setEnabled(false);
					else bt.setEnabled(true);
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
				}
			});
			init();
    }

	private void init()
	{
		ArrayList<MessageObj> msg=new ArrayList<MessageObj>();
		for(MessageObj ms:Data.msgs)
			if(ms.from==to.faq||ms.to==to.faq)msg.add(ms);
		Data.sortMsgByTime(msg,-1);
		for(MessageObj m:msg){
			addMsg(Data.getUser(m.from),m);
		}
	}

	@Override
	public void rev(byte cmd, final String msg)
	{
		// TODO: Implement this method
		if(cmd==C.MSG)
		{
			MessageObj m=(MessageObj)ToStrObj.s2o(msg);
			if(m.from==to.faq)
				runOnUiThread(new Runnable(){
						@Override
						public void run()
						{
							// TODO: Implement this method
							MessageObj m=(MessageObj) ToStrObj.s2o(msg);
							addMsg(Data.getUser(m.from),m);
						}
					});
		}
	}
	public void send(View v)
	{
		String m=et.getText().toString();
		et.setText("");
		User u=Data.getMyself();
		MessageObj obj=new MessageObj(u.faq,to.faq,T.MSG,false,m).setTime();
		addMsg(u,obj);
		Data.msgBuffer.add(obj);
		Data.msgs.add(obj);
		MessageObj obj2=(MessageObj) ToStrObj.s2o( obj.o2s());
		obj2.from=obj.to;
		obj2.to=obj.from;
		Data.msglist.put(obj.to+"",obj2);
		if(!ClientService.sendMsg(C.MSG,obj.o2s()))util.toast(this,"发送失败");
	}

	@Override
	public void exit(View v)
	{
		// TODO: Implement this method
		et.setEnabled(false);
		super.exit(v);
	}

	private void addMsg(User us,MessageObj msg)
	{
		ViewGroup vg=(ViewGroup) LayoutInflater.from(this).inflate(R.layout.msg_entry,null);
		((LinearLayout)vg.findViewById(R.id.msgentryLinearLayout1)).setGravity(us.faq==Data.myfaq?Gravity.RIGHT:Gravity.LEFT);
		TextView ni=(TextView) vg.findViewById(R.id.msgentryTextView1);
		ni.setVisibility(msg.isGroup?0:8);
		ni.setText(us.nick);
		TextView tv=(TextView) vg.findViewById(R.id.msgentryTextView2);
		tv.setText(msg.msg);
		Bitmap b=BitmapFactory.decodeResource(getResources(),R.drawable.aio_user_bg_nor);
		byte[] nc=b.getNinePatchChunk();
		if(NinePatch.isNinePatchChunk(nc))
		{
			NinePatchDrawable n=new NinePatchDrawable(getResources(),b,nc,
													  new Rect(tv.getPaddingLeft(),tv.getPaddingTop(),tv.getPaddingRight(),tv.getPaddingBottom())
													  ,null);
			//n.setAutoMirrored(!isSend);
			tv.setBackground(n);
		}
		ImageView iv1=(ImageView) vg.findViewById(R.id.msgentryImageView1);
		ImageView iv2=(ImageView) vg.findViewById(R.id.msgentryImageView2);
		if(us.faq==Data.myfaq)iv2.setImageDrawable(Data.getMyHeadDrawable());
		else iv1.setImageDrawable(Data.getHeadDrawable(us.faq,msg.isGroup));
		ll.addView(vg);
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					((ScrollView)ll.getParent()).fullScroll(View.FOCUS_DOWN);
				}
			},300);
	}

}
