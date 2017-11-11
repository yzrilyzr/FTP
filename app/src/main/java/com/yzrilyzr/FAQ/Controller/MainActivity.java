package com.yzrilyzr.FAQ.Controller;

import android.os.Bundle;
import android.view.View;
import com.yzrilyzr.FAQ.Controller.R;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import java.io.IOException;

public class MainActivity extends myActivity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		uidata.readData(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		try
		{
			ClientService.connect();
			util.toast(ctx,"连接成功");
		}
		catch (IOException e)
		{}
    }
	public void updateServer(View v){
		
	}
}
