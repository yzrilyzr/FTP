package com.yzrilyzr.FAQ;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.yzrilyzr.ui.myProgressBar;
import com.yzrilyzr.ui.myToolBar;
import android.webkit.WebSettings;

public class BrowserActivity extends BaseActivity
{
WebView webv;
myProgressBar prog;
myToolBar tool;
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
					if(progress == 100){     
						prog.setVisibility(View.GONE); 
						tool.setTitle(view.getTitle());
					}
				}
		});
		webv.getSettings().setUseWideViewPort(true);
		webv.getSettings().setLoadWithOverviewMode(true);
		//webv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.);
		webv.setWebViewClient(new WebViewClient());
		String ur=getIntent().getStringExtra("url");
		if(ur!=null)webv.loadUrl(ur);
	}
	
}
