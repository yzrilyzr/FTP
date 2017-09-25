package com.yzrilyzr.FAQ;
import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
import com.yzrilyzr.FAQ.BG.BGdraw;
import com.yzrilyzr.FAQ.BG.Shapes;

public class WallPaperService extends WallpaperService 
{
	BGdraw bg=null;
	Engine eng=null;
	@Override
	public WallpaperService.Engine onCreateEngine()
	{
		// TODO: Implement this method
		return eng;
	}

	@Override
	public void onCreate()
	{
		// TODO: Implement this method
		super.onCreate();
		bg=new Shapes();
		eng=new Engine(){
			@Override
			public void onSurfaceCreated(SurfaceHolder holder)
			{
				// TODO: Implement this method
				super.onSurfaceCreated(holder);
				bg.onCreate(holder);
			}

			@Override
			public void onVisibilityChanged(boolean visible)
			{
				// TODO: Implement this method
				super.onVisibilityChanged(visible);
				bg.onVisibilityChanged(visible);
			}

			@Override
			public void onTouchEvent(MotionEvent event)
			{
				// TODO: Implement this method
				super.onTouchEvent(event);
				bg.onTouchEvent(event);
			}

			@Override
			public void onSurfaceDestroyed(SurfaceHolder holder)
			{
				// TODO: Implement this method
				super.onSurfaceDestroyed(holder);
				bg.onDestory();
			}

			@Override
			public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
			{
				// TODO: Implement this method
				super.onSurfaceChanged(holder, format, width, height);
				bg.onChange(holder,format,width,height);
			}

			@Override
			public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset)
			{
				// TODO: Implement this method
				super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
				bg.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
			}
		};
	}

}
