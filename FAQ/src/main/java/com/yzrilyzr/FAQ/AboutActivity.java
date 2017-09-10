package com.yzrilyzr.FAQ;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;

public class AboutActivity extends myActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		String a="Copyright © 2017 yzrilyzr";
		TextView t=(TextView) findViewById(R.id.aboutmyTextView1);
		if(!t.getText().toString().equals(a)){
			StringBuilder sb=new StringBuilder();
			sb.append('请').append('勿').append('使').append('用')
				.append('盗').append('版').append('软').append('件');
			util.toast(ctx,sb.toString());
			uidata.mod=true;
			uidata.saveData(ctx);
		}
	}
	public void bug(View v){
		
	}
	public void info(View v){
		
	}
}
