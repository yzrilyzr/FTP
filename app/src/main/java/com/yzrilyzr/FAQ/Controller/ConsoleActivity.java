package com.yzrilyzr.FAQ.Controller;

import android.os.Bundle;
import android.view.View;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.C;
import android.widget.EditText;
import com.yzrilyzr.myclass.AndroidBug5497Workaround;

public class ConsoleActivity extends BaseActivity
{
	LongTextView lte;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);
		AndroidBug5497Workaround.assistActivity(this);
		lte=(LongTextView)findViewById(R.id.consoleLongTextView1);
	}
	@Override
	public void rev(byte cmd, String msg)
	{
		if(cmd==C.LOG)lte.addText(msg);
		else if(cmd==C.CLV)lte.setText("");
	}
	
	public void send(View v){
		EditText e=(EditText)findViewById(R.id.consoleEditText1);
		String s=e.getText().toString();
		ClientService.sendMsg(C.EXE,s);
		e.setText("");
	}
}
