package com.yzrilyzr.myclass;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.util.*;
import android.widget.*;
import com.yzrilyzr.ui.*;
import java.io.*;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.lang.reflect.Method;
import java.util.List;
import android.os.Handler;

public class util
{
    public static final String ERROR="ERROR;";

	public static String mainDir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/FAQ";
    public static void toast(final Context c,final String s)
    {
        Toast.makeText(c,s,0).show();
    }
    public static void alert(final Context c,final String s)
    {
		new Handler(c.getMainLooper()).post(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					new myAlertDialog(c)
						.setTitle("提示")
						.setMessage(s)
						.setPositiveButton("确定",null)
						.setNegativeButton("复制",new myDialogInterface(){
							@Override public void click(View p,int p3)
							{
								copy(c,s);
							}
						})
						.show();
				}
			
	});
	}
    public static void copy(Context c,String s)
    {
        ((ClipboardManager)c.getSystemService(c.CLIPBOARD_SERVICE)).setText(s);
    }
    public static String paste(Context c)
    {
        return ((ClipboardManager)c.getSystemService(c.CLIPBOARD_SERVICE)).getText().toString();
    }
    public static void writeBmp(Bitmap b,String path)
    {
        try
        {
            b.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(path));
        }
        catch (FileNotFoundException e)
        {}
    }
    public static boolean write(String f,String s)
    {
        try
        {
            BufferedWriter bw=new BufferedWriter(new FileWriter(f));
            bw.write(s);
            bw.close();
        }
        catch(IOException e)
        {
            return false;
        }
        return true;
    }
    public static void call(Class currentClass,String mname,Class<?>[] paramClasses,Object instance,Object[] callParams)
    {
        try
        {
            Method m=currentClass.getClass().getMethod(mname,paramClasses);
            m.invoke(instance,callParams);
        }
        catch(Throwable e)
        {}
    }

    public static String read(String f,boolean withN)
    {
        StringBuffer sb=new StringBuffer();
        try
        {
            String n=withN?"\n":"";
            BufferedReader br=new BufferedReader(new FileReader(f));
            String b="";
            while((b=br.readLine())!=null)sb.append(b+n);
        }
        catch(IOException e)
        {
            return ERROR;
        }
        return sb.toString();
    }
    public static void check(Context c,Throwable t)
    {
        alert(c,"===Exception===\n"+t+"\n===Message===\n"+t.getMessage()+"\n===StackTrace===\n"+getStackTrace(t));
    }
    public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try
        {
            t.printStackTrace(pw);
            return sw.toString();
        }
        finally
        {
            pw.close();
        }
    }
    public static String getAppVersion(Context c)
    {
        PackageManager pm = c.getPackageManager();
        try
        {
            PackageInfo info = pm.getPackageInfo(c.getPackageName(), 0);
            return info.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return ERROR;
        }
    }




    public static float density=0;
    public static final String sdcard=Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String info="yzr's tool v1.0.Don't delete this info";

    public static String toNor(String keyword)
    {
        if (!keyword.equals(""))
        {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr)
            {
                if (keyword.contains(key))
                {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static String readwithN(String path)throws IOException
    {
        StringBuffer sb=new StringBuffer();
        String st="";
        BufferedReader br=new BufferedReader(new FileReader(path));
        while((st=br.readLine())!=null)
        {sb.append(st+"\n");}
        br.close();
        return sb.toString();
    }
    public static void hideSoftKeyboard(EditText editText, Context context)
    {
        if (editText != null && context != null)
        {
            InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public static void showSoftKeyboard(EditText editText, Context context)
    {
        if (editText != null && context != null)
        {
            InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        }
    }
    public static String stringToUnicode(String string)
    {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++)
        {
            String u=Integer.toHexString(string.charAt(i));
            String[] p=new String[]{"0000","000","00","0",""};
            // 转换为unicode
            unicode.append("\\u"+p[u.length()]+u);
        }
        return unicode.toString();
    }
    public static String unicodeToString(String unicode)
    {
        StringBuffer sb=new StringBuffer();
        String[] b=unicode.split("");
        for(int i=0;i<b.length;i++)
        {
            if(b[i].equals("\\")&&b[i+1].equals("u"))
            {
                int data = Integer.parseInt(b[i+2]+b[i+3]+b[i+4]+b[i+5],16);
                sb.append((char) data);
                i+=5;
            }
            else
            {sb.append(b[i]);}
        }
        return sb.toString();
    }
    public static SharedPreferences getSPRead(Context ctx)
    { 
        return ctx. getSharedPreferences("data",Activity.MODE_PRIVATE);
    } 
    public static SharedPreferences.Editor getSPWrite(Context ctx)
    { 
        return ctx. getSharedPreferences("data",Activity.MODE_PRIVATE).edit();
    } 

    public static Typeface getTypefaceFA(Context ctx,String path)
    {
        AssetManager mgr=ctx.getAssets();
        Typeface tf=Typeface.createFromAsset(mgr,path);
        return tf;
    }

    public static View 外观(View view,int width,int height,int margin)
    {
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(width,height);
        lp.setMargins(margin,margin,margin,margin);
        view.setLayoutParams(lp);
        return view;
    }
    public static View 色(View view,int a,int r,int g,int b)
    {
        view.setBackgroundColor(Color.argb(a,r,g,b));
        return view;
    }


    public static int int2Str(String str)
    {
        return Integer.parseInt(str);
    }
    public static void runAppByPkgName(Context ctx,String packagename)
    {
        PackageInfo packageinfo = null;
        try
        {
            packageinfo = ctx.getPackageManager().getPackageInfo(packagename, 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            toast(ctx,"找不到包");
        }
        if (packageinfo == null)
        {return;}
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        List<ResolveInfo> resolveinfoList = ctx.getPackageManager()
            .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null)
        {
            String packageName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            ctx.startActivity(intent);
        }
    }
    public static int getStatusBarHeight(Context context) {

		int statusHeight = -1;
		try {
			Class
				clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
										  .get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

    public static String[] concat(String[] a,String[] b)
    {
        String[] tmp=new String[a.length+b.length];
        for(int i=0;i<a.length;i++)
        {
            tmp[i]=a[i];
        }
        for(int i=0;i<b.length;i++)
        {
            tmp[i+a.length]=b[i];
        }
        return tmp;
    }

    public static String[] push(String[] before,String now)
    {
        String[] tmp=new String[before.length+1];
        for(int i=0;i<before.length;i++)
        {
            tmp[i]=before[i];
        }
        tmp[tmp.length-1]=now.toString();
        return tmp;
    }
    public static String[] deleteItem(String[] array,String item)
    {
        String[] tmp=new String[array.length-1];
        int index=0;
        for(int i=0;i<array.length;i++)
        {
            if(!array[i].equals(item))
            {tmp[index]=array[i];index++;}
        }
        return tmp;
    }
    public static boolean searchItem(String[] array,String item)
    {
        for(int i=0;i<array.length;i++)
        {
            if(array[i].equals(item))return true;
        }
        return false;
    }
    public int changeTextSize(Context ctx,int size)
    {
        Resources r = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                                               size, r.getDisplayMetrics());
	}
    public static int px2dip(int pxValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static int dip2px(float dipValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (int)(dipValue * scale + 0.5f);
	}
    public static Rect getScreenRect()
    {
        DisplayMetrics displayMetric = new DisplayMetrics();
        displayMetric = Resources.getSystem().getDisplayMetrics();
        Rect rect = new Rect(0, 0, displayMetric.widthPixels, displayMetric.heightPixels);
        return rect;
    }

    public static int getScreenWidth()
    {
        return getScreenRect().width();
    }

    public static int getScreenHeight()
    {
        return getScreenRect().height();
    }

    public static int random(int min,int max)
    {
        return (int)Math.floor(Math.random()*(max-min))+min;
    }


}
