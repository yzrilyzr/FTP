package com.yzrilyzr.FAQ.Controller;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yzrilyzr.FAQ.Controller.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MsgAdapter extends BaseAdapter
{
	Context ctx;
	List<Entry> ent;
	public MsgAdapter(Context C,List<Entry> entry){
		ctx=C;
		ent=entry;
	}
	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return ent.size();
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
		Entry e=ent.get(p1);
		if(e!=null){
		hd.setImageDrawable(e.head);
		ni.setText(e.txt);
		ms.setText(e.stxt);
		if(e.time!=0)ts.setText(new SimpleDateFormat("HH:mm").format(new Date(e.time)));
		}
		return vg;
	}
	public static class Entry{
		public String txt,stxt;
		public long time;
		public Drawable head;
	}
}
