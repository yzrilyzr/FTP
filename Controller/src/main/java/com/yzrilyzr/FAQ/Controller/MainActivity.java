package com.yzrilyzr.FAQ.Controller;

import android.os.Bundle;
import android.view.View;
import com.yzrilyzr.FAQ.Controller.R;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import java.net.SocketTimeoutException;
import android.content.Intent;
import com.yzrilyzr.FAQ.Main.C;

public class MainActivity extends myActivity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
	public void control(View v){
		startActivity(new Intent(this,ControlActivity.class));
	}
	public void console(View v){
		startActivity(new Intent(this,ConsoleActivity.class));
	}
	public void fileExp(View v){
		startActivity(new Intent(this,FileActivity.class));
	}
}
