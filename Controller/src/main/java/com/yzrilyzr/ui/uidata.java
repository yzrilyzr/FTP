package com.yzrilyzr.ui;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myIconDrawer.DrawType;
public class uidata
{
	public static float UI_TEXTSIZE_DEFAULT=5f;
	public static float UI_TEXTSIZE_TITLE=7f;
	public static int UI_PADDING_DEFAULT=5;
	public static boolean UI_USETYPEFACE=true;
	public static boolean UI_USESHADOW=true;
	public static Typeface UI_TYPEFACE=null;
	public static int UI_TEXTCOLOR_MAIN=0xffffffff;
	public static int UI_TEXTCOLOR_BACK=0xff000000;
	public static int UI_TEXTCOLOR_HL=0xffaaaaaa;
	public static int UI_COLOR_MAIN=0xff3f51b5;
	public static int UI_COLOR_BACK=0xffc5cae9;
	public static int UI_COLOR_MAINHL=0xffe8eaf6;
	public static float UI_DENSITY=1.0F;
	public static float UI_RADIUS=3.0f;
	public static boolean isInit=false;
	public static boolean mod=false;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
	public static final void saveData(Context ctx)
	{
		UI_DENSITY=ctx.getResources().getDisplayMetrics().density;
		util.getSPWrite(ctx)
			.putBoolean("typeface",UI_USETYPEFACE)
			.putBoolean("mod",mod)
			.putBoolean("shadow",UI_USESHADOW)
			.putInt("maincolor",UI_COLOR_MAIN)
			.putInt("backcolor",UI_COLOR_BACK)
			.putInt("highcolor",UI_COLOR_MAINHL)
			.putInt("maintext",UI_TEXTCOLOR_MAIN)
			.putInt("backtext",UI_TEXTCOLOR_BACK)
			.putInt("hightext",UI_TEXTCOLOR_HL)
			.putInt("padding",(int)(UI_PADDING_DEFAULT/UI_DENSITY))
			.putFloat("sizetext",UI_TEXTSIZE_DEFAULT/UI_DENSITY)
			.putFloat("titletext",UI_TEXTSIZE_TITLE/UI_DENSITY)
			.putFloat("radius",UI_RADIUS/UI_DENSITY)
			.putBoolean("init",true)
			.commit();
	}

	public static final void readData(Context ctx)
	{
		SharedPreferences sp=util.getSPRead(ctx);
		isInit=sp.getBoolean("init",false);
		UI_TYPEFACE=Typeface.MONOSPACE;//.createFromAsset(ctx.getAssets(),"font.ttf");
		UI_DENSITY=ctx.getResources().getDisplayMetrics().density;
		UI_USETYPEFACE=sp.getBoolean("typeface",UI_USETYPEFACE);
		mod=sp.getBoolean("mod",mod);
		UI_USESHADOW=sp.getBoolean("typeface",UI_USESHADOW);
		UI_COLOR_MAIN=sp.getInt("maincolor",UI_COLOR_MAIN);
		UI_COLOR_BACK=sp.getInt("backcolor",UI_COLOR_BACK);
		UI_COLOR_MAINHL=sp.getInt("highcolor",UI_COLOR_MAINHL);
		UI_TEXTCOLOR_MAIN=sp.getInt("maintext",UI_TEXTCOLOR_MAIN);
		UI_TEXTCOLOR_BACK=sp.getInt("backtext",UI_TEXTCOLOR_BACK);
		UI_TEXTCOLOR_HL=sp.getInt("hightext",UI_TEXTCOLOR_HL);
		UI_PADDING_DEFAULT=(int)(sp.getInt("padding",UI_PADDING_DEFAULT)*UI_DENSITY);
		UI_TEXTSIZE_DEFAULT=sp.getFloat("sizetext",UI_TEXTSIZE_DEFAULT)*UI_DENSITY;
		UI_TEXTSIZE_TITLE=sp.getFloat("titletext",UI_TEXTSIZE_TITLE)*UI_DENSITY;
		UI_RADIUS=sp.getFloat("radius",UI_RADIUS)*UI_DENSITY;
		if(!isInit)saveData(ctx);
	}
    public static void initIcon(Context ctx)
    {

        myIconDrawer m=new myIconDrawer(util.dip2px(50));
        m.draw(DrawType.CIRCLE, false, m.hw, m.hw, m.ww * 7);
        m.draw(DrawType.CIRCLE, true, m.hw, m.ww * 6, m.ww);
        m.draw(DrawType.RECT, true, m.ww * 9, m.ww * 9, m.ww * 11, m.ww * 15);

        icon.info=m.getBitmap();
        m.clear();

        m.move(m.hw,m.qw);
        m.line(m.qw*3,m.hw);
        m.line(m.hw,m.qw*3);
        m.move(m.qw, m.hw);
        m.line(m.qw*3, m.hw);
        m.draw(DrawType.PATH, false);

        icon.next=m.getBitmap();
        m.clear();

        m.move(m.hw,m.qw);
        m.line(m.qw,m.hw);
        m.line(m.hw,m.qw*3);
        m.move(m.qw, m.hw);
        m.line(m.qw*3, m.hw);
        m.draw(DrawType.PATH, false);

        icon.prev=m.getBitmap();
        m.clear();

        m.draw(DrawType.CIRCLE,true,m.hw,m.hw,m.ww);
        m.draw(DrawType.CIRCLE,true,m.hw,m.qw,m.ww);
        m.draw(DrawType.CIRCLE,true,m.hw,m.qw*3,m.ww);

        icon.more_overflow=m.getBitmap();
        m.clear();

        Paint p=m.getPaint();
        float i=p.getStrokeWidth();
        p.setStrokeWidth(m.ww*4);
        m.draw(DrawType.CIRCLE,false,m.hw,m.hw,m.qw);
        p.setStrokeWidth(i);
        i=m.ww;
        m.move(i*8,i*2);
        m.line(i*12,i*2);
        m.line(i*12,i*4);
        m.line(i*8,i*4);
        m.line(i*8,i*2);

        m.move(i*8,i*18);
        m.line(i*12,i*18);
        m.line(i*12,i*16);
        m.line(i*8,i*16);
        m.line(i*8,i*18);

        m.move(i*2,i*8);
        m.line(i*2,i*12);
        m.line(i*4,i*12);
        m.line(i*4,i*8);
        m.line(i*2,i*8);

        m.move(i*16,i*8);
        m.line(i*18,i*8);
        m.line(i*18,i*12);
        m.line(i*16,i*12);
        m.line(i*16,i*8);

        m.move(i*3,i*6);
        m.line(i*4,i*7);
        m.line(i*7,i*4);
        m.line(i*6,i*3);
        m.line(i*3,i*6);

        m.move(i*14,i*3);
        m.line(i*13,i*4);
        m.line(i*16,i*7);
        m.line(i*17,i*6);
        m.line(i*14,i*3);

        m.move(i*6,i*17);
        m.line(i*7,i*16);
        m.line(i*4,i*13);
        m.line(i*3,i*14);
        m.line(i*6,i*17);

        m.move(i*14,i*17);
        m.line(i*13,i*16);
        m.line(i*16,i*13);
        m.line(i*17,i*14);
        m.line(i*14,i*17);
        m.draw(DrawType.PATH,true);

        icon.settings=m.getBitmap();
        m.clear();

        m.move(m.ww*4,m.ww*4);
        m.line(m.ww*16,m.ww*16);
        m.move(m.ww*16,m.ww*4);
        m.line(m.ww*4,m.ww*16);
        m.draw(DrawType.PATH,false);

        icon.wrong=m.getBitmap();
        m.clear();

        m.draw(DrawType.LINE,false,m.ww*14,m.ww*3,m.ww*4.5f,m.ww*3);
        m.draw(DrawType.LINE,false,m.ww*4,m.ww*3.5f,m.ww*4,m.ww*14);
        m.draw(DrawType.ARC,false,m.ww*4,m.ww*3,m.ww*5,m.ww*4,180,90);
        m.draw(DrawType.ROUNDRECT,false,m.ww*6,m.ww*5,m.ww*16,m.ww*17,m.ww/2,m.ww/2);

        icon.copy=m.getBitmap();
        m.clear();
        m.draw(DrawType.ARC,false,m.ww*4,m.ww*4,m.ww*5,m.ww*5,180,90);
        m.draw(DrawType.ARC,false,m.ww*4,m.ww*16,m.ww*5,m.ww*17,90,90);
        m.draw(DrawType.ARC,false,m.ww*15,m.ww*4,m.ww*16,m.ww*5,270,90);
        m.draw(DrawType.ARC,false,m.ww*15,m.ww*16,m.ww*16,m.ww*17,0,90);
        m.draw(DrawType.LINE,false,m.ww*4,m.ww*4.5f,m.ww*4,m.ww*16.5f);
        m.draw(DrawType.LINE,false,m.ww*16,m.ww*4.5f,m.ww*16,m.ww*16.5f);
        m.draw(DrawType.LINE,false,m.ww*4.5f,m.ww*17,m.ww*15.5f,m.ww*17);
        m.draw(DrawType.RECT,true,m.ww*6,m.ww*4.5f,m.ww*14,m.ww*7);
        m.draw(DrawType.CIRCLE,false,m.ww*10,m.ww*4,m.ww);
        m.draw(DrawType.LINE,false,m.ww*4.5f,m.ww*4,m.ww*9,m.ww*4);
        m.draw(DrawType.LINE,false,m.ww*11,m.ww*4,m.ww*15.5f,m.ww*4);


        icon.paste=m.getBitmap();
        m.clear();
        m.draw(DrawType.CIRCLE,false,m.ww*6,m.ww*6,m.ww*2);
        m.draw(DrawType.CIRCLE,false,m.ww*6,m.ww*14,m.ww*2);
        m.draw(DrawType.CIRCLE,false,m.hw,m.hw,m.ww/5);
        m.draw(DrawType.LINE,false,m.ww*7.5f,m.ww*7.5f,m.ww*9.7f,m.ww*9.7f);
        m.draw(DrawType.LINE,false,m.ww*7.5f,m.ww*12.5f,m.ww*9.7f,m.ww*10.3f);
        m.draw(DrawType.LINE,false,m.ww*10.3f,m.ww*10.3f,m.ww*16,m.ww*16);
        m.draw(DrawType.LINE,false,m.ww*11,m.ww*9,m.ww*16,m.ww*4);


        icon.cut=m.getBitmap();
        m.clear();
        m.draw(DrawType.ARC,false,m.ww*4,m.ww*4,m.ww*5,m.ww*5,180,90);
        m.draw(DrawType.ARC,false,m.ww*4,m.ww*15,m.ww*5,m.ww*16,90,90);
        m.draw(DrawType.ARC,false,m.ww*15,m.ww*4,m.ww*16,m.ww*5,270,90);
        m.draw(DrawType.ARC,false,m.ww*15,m.ww*15,m.ww*16,m.ww*16,0,90);
        m.draw(DrawType.RECT,false,m.ww*7,m.ww*7,m.ww*13,m.ww*13);
        m.draw(DrawType.POINT,false,m.hw,m.ww*4);
        m.draw(DrawType.POINT,false,m.hw,m.ww*16);
        m.draw(DrawType.POINT,false,m.ww*4,m.hw);
        m.draw(DrawType.POINT,false,m.ww*16,m.hw);
        m.draw(DrawType.POINT,false,m.ww*7,m.ww*4);
        m.draw(DrawType.POINT,false,m.ww*13,m.ww*4);
        m.draw(DrawType.POINT,false,m.ww*7,m.ww*16);
        m.draw(DrawType.POINT,false,m.ww*13,m.ww*16);
        m.draw(DrawType.POINT,false,m.ww*4,m.ww*7);
        m.draw(DrawType.POINT,false,m.ww*4,m.ww*13);
        m.draw(DrawType.POINT,false,m.ww*16,m.ww*7);
        m.draw(DrawType.POINT,false,m.ww*16,m.ww*13);


        icon.selectall=m.getBitmap();
        m.clear();
        m.move(m.ww*3,m.ww*11);
        m.line(m.ww*7,m.ww*15);
        m.line(m.ww*7,m.ww*15);
        m.line(m.ww*17,m.ww*5);
        m.draw(DrawType.PATH,false);
        icon.correct=m.getBitmap();

    }
	public static class icon
	{
		public static Bitmap 
		next,
		prev,
		more_overflow,
		info,
		settings,
		wrong,
		copy,
		paste,
		cut,
		selectall,
		correct
		;
	}
	public static Bitmap getIconByName(String name)
	{
		switch(name)
		{
			case "next":return icon.next;
			case "prev":return icon.prev;
			case "more_overflow":return icon.more_overflow;
			case "info":return icon.info;
			case "settings":return icon.settings;
			case "wrong":return icon.wrong;
			case "copy":return icon.copy;
			case "paste":return icon.paste;
			case "cut":return icon.cut;
			case "selectall":return icon.selectall;
			case "correct":return icon.correct;
			default:return null;
		}
	}
}
