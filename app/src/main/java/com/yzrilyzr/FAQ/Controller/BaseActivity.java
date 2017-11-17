package com.yzrilyzr.FAQ.Controller;

import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.myclass.myActivity;
import android.os.Bundle;
import java.util.ArrayList;

public class BaseActivity extends myActivity implements ClientService.Listener
{
	public static ArrayList<BaseActivity> activities=new ArrayList<BaseActivity>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		ClientService.addListener(this);
		activities.add(this);
	}	
	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		ClientService.removeListener(this);
		activities.remove(this);
	}
	
	@Override
	public void rev(byte cmd, String msg)
	{
		// TODO: Implement this method
	}
}
