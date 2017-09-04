package com.yzrilyzr.FAQ;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.yzrilyzr.FAQ.Main.T;
import com.yzrilyzr.ui.myAlertDialog;
import com.yzrilyzr.ui.myDialogInterface;
import com.yzrilyzr.ui.myRoundDrawable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SysMsgActivity extends BaseActivity
{
	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sysmsg);
		lv=(ListView) findViewById(R.id.sysmsgListView1);
		setList();
	}
	private void setList()
	{
		final ArrayList<MessageObj> sml=new ArrayList<MessageObj>();
		for(MessageObj m:Data.msgs)
			if(m.type==T.VMS)sml.add(m);
		lv.setAdapter(new BaseAdapter(){
				@Override
				public int getCount()
				{
					// TODO: Implement this method
					return sml.size();
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
					ViewGroup vg=(ViewGroup)LayoutInflater.from(ctx).inflate(R.layout.list_entry,null);
					ImageView hd=(ImageView) vg.findViewById(R.id.listentryImageView1);
					TextView ni=(TextView) vg.findViewById(R.id.listentryTextView1);
					TextView ms=(TextView) vg.findViewById(R.id.listentryTextView2);
					TextView ts=(TextView) vg.findViewById(R.id.listentryTextView3);
					MessageObj o=sml.get(p1);
					ms.setText(o.msg);
					ts.setText(new SimpleDateFormat("HH:mm").format(new Date(o.time)));
					User u=Data.getUser(o.from);
					if(u!=null)ni.setText(u.nick);
					else ni.setText(o.from+"");
					byte[] b=Data.getHead(o.from,o.isGroup);
					if(b!=null)
					{
						Bitmap bm=BitmapFactory.decodeByteArray(b,0,b.length);
						hd.setImageDrawable(new myRoundDrawable(bm));
					}
					return vg;
				}
			});
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					final MessageObj m=sml.get(p3);
					new myAlertDialog(ctx)
						.setTitle("系统消息")
						.setMessage(String.format("同意来自 %d 的请求:%s",m.from,m.msg))
						.setPositiveButton("同意",new myDialogInterface(){
							public void click(View v,int i)
							{
								if(!m.isGroup)ClientService.sendMsg(C.AFD,m.from+"");
							}
						})
						.setNegativeButton("拒绝",null)
						.setNeutralButton("关闭",null)
						.show();
				}});
	}
}
