package com.yzrilyzr.FAQ;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.yzrilyzr.FAQ.R;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myAlertDialog;
import com.yzrilyzr.ui.myDialogInterface;
import com.yzrilyzr.ui.myLoadingView;
import com.yzrilyzr.ui.myProgressBar;
import com.yzrilyzr.ui.myToolBar;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class BrowserActivity extends BaseActivity
{
	WebView webv;
	myProgressBar prog;
	myToolBar tool;
	boolean sho=true;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		webv=(WebView) findViewById(R.id.browserWebView1);
		prog=(myProgressBar) findViewById(R.id.browsermyProgressBar1);
		tool=(myToolBar) findViewById(R.id.browsermyToolBar1);
		webv.getSettings().setJavaScriptEnabled(true);
		webv.setWebChromeClient(new WebChromeClient(){
				public void onProgressChanged(WebView view, int progress)   
				{
					prog.setVisibility(View.VISIBLE); 
					prog.setProgress(progress);     
					if(progress == 100)
					{     
						prog.setVisibility(View.GONE); 
						tool.setTitle(view.getTitle());
					}
				}
				public boolean onJsAlert(WebView view,String url,String message,final JsResult result)
				{
					if(sho)new myAlertDialog(ctx)
							.setTitle("来自网页的提示")
							.setMessage(message)
							.setPositiveButton("确定",new myDialogInterface(){
								public void click(View v,int i)
								{
									result.confirm();
								}
							})
							.setNeutralButton("不要再显示",new myDialogInterface(){
								public void click(View v,int i)
								{
									sho=false;
									result.cancel();
								}
							})
							.setNegativeButton("取消",new myDialogInterface(){
								public void click(View v,int i)
								{
									result.cancel();
								}
							})
							.show();
					else result.cancel();
					return true;
				}
				public View getVideoLoadingProgressView()
				{
					return new myLoadingView(ctx);
				}
			});
		webv.getSettings().setUseWideViewPort(true);
		webv.getSettings().setLoadWithOverviewMode(true);
		//webv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.);
		webv.setWebViewClient(new WebViewClient());
		webv.setDownloadListener(new DownloadListener(){
				@Override
				public void onDownloadStart(final String p1, String p2, String p3, String p4, long p5)
				{
					// TODO: Implement this method
					new myAlertDialog(ctx)
						.setTitle("下载文件")
						.setMessage(URLDecoder.decode(p1.substring(p1.lastIndexOf("/")+1)))
						.setPositiveButton("下载",new myDialogInterface(){
							public void click(View v,int i)
							{
								new Thread(new DownloaderTask(p1)).start();
							}
						})
						.setNegativeButton("取消",null)
						.show();

				}
			});
		String ur=getIntent().getStringExtra("url");
		if(ur!=null)webv.loadUrl(ur);
	}
	public void back(View v)
	{if(webv.canGoBack())webv.goBack();}
	public void forw(View v)
	{if(webv.canGoForward())webv.goForward();}
	public void ref(View v)
	{webv.reload();}
	public void stop(View v)
	{webv.stopLoading();}
	private class DownloaderTask implements Runnable
	{
		String url;
		public DownloaderTask(String url)
		{ 
			this.url=url;
		}
		@Override
		public void run()
		{
			// TODO: Implement this method
			util.toast(ctx,"正在下载");
            String fileName=url.substring(url.lastIndexOf("/")+1);   
            fileName=URLDecoder.decode(fileName);   
			File file=new File(util.mainDir+"/download");   
            if(!file.exists())file.mkdirs();
            try
			{     
                HttpClient client = new DefaultHttpClient();     
                HttpGet get = new HttpGet(url);     
                HttpResponse response = client.execute(get);   
                if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode())
				{   
                    HttpEntity entity = response.getEntity();   
                    InputStream input = entity.getContent();
					byte[] bu=new byte[10240];
					int ii=0;
					BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(util.mainDir+"/download/"+fileName));
					while((ii=input.read(bu))!=-1)
					{
						os.write(bu,0,ii);
						os.flush();
					}
					os.close();
                    input.close();
					util.toast(ctx,"下载完成，在"+util.mainDir+"/download/"+fileName);  
                }
				else
				{   
                    util.toast(ctx,"无法连接服务器");
                }   
            }
			catch (Exception e)
			{     
            }   
		}
	}
}
