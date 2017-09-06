package com.yzrilyzr.ui;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.yzrilyzr.FAQ.R;

public class myAlertDialog
{	
	private View view;
	private myTextView title,message;
	private myTextView positive,negative,neutral;
	public ListView listview;
	private LinearLayout cview;
	private AlertDialog.Builder adb;
	protected myAlertDialog mad;
	protected Dialog dia;
	private Context ctx;
	private myDialogInterface c1,c2,c3,c4;
	public myAlertDialog(Context c)
	{
		mad=this;
		ctx=c;
		view=LayoutInflater.from(c).inflate(R.layout.layout_alertdialog,null);
		title=(myTextView)view.findViewById(R.id.layoutalertdialogTitle);
		message=(myTextView)view.findViewById(R.id.layoutalertdialogMessage);
		cview=(LinearLayout)view.findViewById(R.id.layoutalertdialogView);
		positive=(myTextView)view.findViewById(R.id.layoutalertdialogpocl);
		negative=(myTextView)view.findViewById(R.id.layoutalertdialognocl);
		neutral=(myTextView)view.findViewById(R.id.layoutalertdialogneuocl);
		listview=(ListView)view.findViewById(R.id.layoutalertdialogListView1);
		adb=new AlertDialog.Builder(c);
		adb.setView(view);
	}
	public myAlertDialog dismiss()
	{
		dia.dismiss();
		return mad;
	}
	public ViewGroup getCView()
	{return cview;}
	public myAlertDialog setItems(String[] items,myDialogInterface ocl)
	{
		listview.setVisibility(0);
		//listview.setDivider(new ColorDrawable(uidata.UI_COLOR_MAIN));
		//listview.setDividerHeight(2);
		//listview.setBackgroundColor(uidata.UI_COLOR_BACK);
		cview.setVisibility(8);
		ListAdapter adapter = new ArrayAdapter<String>(ctx,R.layout.layout_alertdialog_items,R.id.layoutalertdialogitemsmyTextView1,items);
		listview.setAdapter(adapter);
		((myListView)listview).setScrollView(true);
		//setListViewHeightBasedOnChildren(this.listview);
		if(ocl==null)ocl=new myDialogInterface();
		listview.setOnItemClickListener(ocl);
		c4=ocl;
		refreshDialog();
		return this;
	}

	public myAlertDialog setNegativeButton(String s,myDialogInterface ocl)
	{
		if(s==null)
		{negative.setVisibility(8);return this;}
		negative.setVisibility(0);
		if(ocl==null)ocl=new myDialogInterface();
		negative.setOnClickListener(ocl);
		negative.setOnTouchListener(ocl);
		negative.setText(s);
		c2=ocl;
		refreshDialog();
		return this;
	}
	public myAlertDialog setCancelable(boolean b)
	{
		adb.setCancelable(b);
		return this;
	}
	public myAlertDialog setNeutralButton(String s,myDialogInterface ocl)
	{
		if(s==null)
		{neutral.setVisibility(8);return this;}
		neutral.setVisibility(0);
		if(ocl==null)ocl=new myDialogInterface();
		neutral.setOnClickListener(ocl);
		neutral.setOnTouchListener(ocl);
		neutral.setText(s);
		c3=ocl;
		refreshDialog();
		return this;
	}
	public myAlertDialog setPositiveButton(String s,myDialogInterface ocl)
	{
		if(s==null)
		{positive.setVisibility(8);return this;}
		positive.setVisibility(0);
		if(ocl==null)ocl=new myDialogInterface();
		positive.setOnClickListener(ocl);
		positive.setOnTouchListener(ocl);
		positive.setText(s);
		c1=ocl;
		refreshDialog();
		return this;
	}
	public myAlertDialog setView(View v)
	{
		//title.setVisibility(8);
		cview.setVisibility(0);
		message.setVisibility(8);
		listview.setVisibility(8);
		cview.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
		cview.addView(v,new LinearLayout.LayoutParams(-1,-1));
		return this;
	}
	public myAlertDialog setTitle(String s)
	{
		title.setVisibility(0);
		title.setText(s);
		return this;
	}
	public myAlertDialog setMessage(String s)
	{
		message.setVisibility(0);
		message.setText(s);
		return this;
	}
	private void refreshDialog()
	{
		if(c1!=null)c1.setDialog(dia);
		if(c2!=null)c2.setDialog(dia);
		if(c3!=null)c3.setDialog(dia);
		if(c4!=null)c4.setDialog(dia);

	}
	public myAlertDialog show()
	{
		dia=adb.show();
		refreshDialog();
		return this;
	}
}
