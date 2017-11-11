package com.yzrilyzr.FAQ.Server;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.Toast;
import com.yzrilyzr.FAQ.Main.Data;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;

public class StartActivity extends Activity
{
	static Class<?> main;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		try
		{
			if(main==null){
			File f=new File(Data.datafile);
			if(!f.exists())f.mkdirs();
			File u=new File(Data.datafile+"/update.apk");
			if(!u.exists()){
				PackageManager pm=getPackageManager();
				PackageInfo in=pm.getPackageInfo(getPackageName(),PackageInfo.INSTALL_LOCATION_AUTO);
				u=new File(in.applicationInfo.publicSourceDir);
			}
			DexClassLoader d=hookLoadedApkInActivityThread(u,getDir("dex",MODE_PRIVATE).getAbsolutePath());
			main=d.loadClass("com.yzrilyzr.FAQ.Server.MainActivity");
			}
		}
		catch (Throwable e)
		{
		}
		startActivity(new Intent(this,main));
		finish();
	}
	public static Map<String, Object> sLoadedApk = new HashMap<String, Object>();

	public static DexClassLoader hookLoadedApkInActivityThread(File apkFile,String odexPath) throws Exception
	{
		// 1、获取到当前的ActivityThread对象
		Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
		//2、 获取 mPackages 静态成员变量, 这里缓存了dex包的信息
		Field mPackagesField = activityThreadClass.getDeclaredField("mPackages");
		mPackagesField.setAccessible(true);
		Map mPackages = (Map) mPackagesField.get(currentActivityThread);
		// 方法签名：public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai,CompatibilityInfo compatInfo)
		//3、获取getPackageInfoNoCheck方法
		Class<?> compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
		Method getPackageInfoNoCheckMethod = activityThreadClass.getDeclaredMethod("getPackageInfoNoCheck",
		ApplicationInfo.class, compatibilityInfoClass);
		Field defaultCompatibilityInfoField = compatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
		defaultCompatibilityInfoField.setAccessible(true);
		Object defaultCompatibilityInfo = defaultCompatibilityInfoField.get(null);
		//4、获取applicationInfo信息
		ApplicationInfo applicationInfo = generateApplicationInfo(apkFile);
		Object loadedApk = getPackageInfoNoCheckMethod.invoke(currentActivityThread, applicationInfo, defaultCompatibilityInfo);
		//5、创建DexClassLoader
		DexClassLoader classLoader = new DexClassLoader(apkFile.getPath(), odexPath, null, ClassLoader.getSystemClassLoader());
		Field mClassLoaderField = loadedApk.getClass().getDeclaredField("mClassLoader");
		mClassLoaderField.setAccessible(true);
		//6、替换掉loadedApk
		mClassLoaderField.set(loadedApk, classLoader);
		// 由于是弱引用, 为了防止被GC，我们必须在某个地方存一份
		sLoadedApk.put(applicationInfo.packageName, loadedApk);
		WeakReference weakReference = new WeakReference(loadedApk);
		mPackages.put(applicationInfo.packageName, weakReference);
		return classLoader;
	}
	public static ApplicationInfo generateApplicationInfo(File apkFile) throws Exception
	{
		// 获取PackageParser类
		Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
		// 获取PackageParser$Package类
		Class<?> packageParser$PackageClass = Class.forName("android.content.pm.PackageParser$Package");
		Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
		Method generateApplicationInfoMethod = packageParserClass.getDeclaredMethod("generateApplicationInfo",
		packageParser$PackageClass,
		int.class,
		packageUserStateClass);
		// 创建出一个PackageParser对象供使用
		Object packageParser = packageParserClass.newInstance();
		// 调用 PackageParser.parsePackage 解析apk的信息
		Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
		// 得到第一个参数 ：PackageParser.Package 对象
		Object packageObj = parsePackageMethod.invoke(packageParser, apkFile, 0);
		//得到第三个参数：PackageUserState对象
		Object defaultPackageUserState = packageUserStateClass.newInstance();
		// 反射generateApplicationInfo得到ApplicationInfo对象
		ApplicationInfo applicationInfo = (ApplicationInfo) generateApplicationInfoMethod.invoke(packageParser,
		packageObj, 0, defaultPackageUserState);
		String apkPath = apkFile.getPath();
		applicationInfo.sourceDir = apkPath;
		applicationInfo.publicSourceDir = apkPath;
		return applicationInfo;
	}
}

