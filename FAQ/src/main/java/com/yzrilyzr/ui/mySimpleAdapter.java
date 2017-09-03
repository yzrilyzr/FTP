package com.yzrilyzr.ui;
import android.view.*;
import android.widget.*;
import java.util.*;

import com.yzrilyzr.FAQ.R;

public class mySimpleAdapter extends SimpleAdapter
{
	LayoutInflater mInflater;
	String[] from;
	List<? extends Map<String,?>> list;
		public mySimpleAdapter(android.content.Context context, java.util.List<? extends java.util.Map<java.lang.String, ?>> data, int resource, java.lang.String[] f, int[] t) {
			super(context,data,resource,f,t);
			from=f;
			list=data;
			mInflater=LayoutInflater.from(context);
		}
		static class ViewHolder {
				TextView text1,text2;
				ImageView icon;
			}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				if (convertView == null) {
						convertView = mInflater.inflate(R.layout.layout_entry,parent, false);
						holder = new ViewHolder();
						holder.text1 = (TextView) convertView.findViewById(R.id.fileentryTextView1);
						holder.text2 = (TextView) convertView.findViewById(R.id.fileentryTextView2);
						holder.icon = (ImageView) convertView.findViewById(R.id.fileentryImageView1);
						convertView.setTag(holder);
					} else {
						holder = (ViewHolder) convertView.getTag();
					}
				Map m=list.get(position);
				holder.text1.setText(""+m.get(from[1]));
				holder.text2.setText(""+m.get(from[2]));
				holder.icon.setImageResource(m.get(from[0]));
				
				return convertView;
			}
}
