package com.yzrilyzr.FAQ.Server;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;

public class BroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context p1, Intent p2)
	{
		if (p2.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            
        }
	}
	
}
