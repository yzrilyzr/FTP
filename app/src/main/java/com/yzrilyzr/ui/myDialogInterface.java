package com.yzrilyzr.ui;
import android.view.*;

import android.app.Dialog;
import android.widget.AdapterView;

public class myDialogInterface extends myTouchListener implements View.OnClickListener,AdapterView.OnItemClickListener
{
    protected Dialog dia=null;
    
    public myDialogInterface()
    {
    }
    public void setDialog(Dialog d)
    {dia=d;}
    @Override
    public void onDown(View v,MotionEvent m)
    {
        v.setBackgroundColor(uidata.UI_COLOR_MAIN);
    }
    @Override
    public void onUp(View v,MotionEvent m)
    {
        v.setBackgroundColor(0x00000000);
    }
    @Override
    public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
    {
        // TODO: Implement this method
        click(p2,p3);
        p2.setBackgroundColor(uidata.UI_COLOR_MAINHL);
        if(!isPrevent)dia.dismiss();
    }

    public void preventDefault()
    {
        isPrevent=true;
    }
    boolean isPrevent=false;
    @Override
    public void onClick(View p1)
    {
        // TODO: Implement this method
        click(p1,-1);
        if(!isPrevent)dia.dismiss();
    }
    public void click(View p1,int p3)
    {}

}
	
