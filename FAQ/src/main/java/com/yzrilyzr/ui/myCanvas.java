package com.yzrilyzr.ui;
import android.view.*;
import android.content.*;
import android.util.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.SurfaceHolder.*;
import java.io.*;
import java.text.*;
import android.os.*;
import android.media.*;
import android.widget.*;
import android.net.*;
import java.util.*;

public class myCanvas extends SurfaceView implements SurfaceHolder.Callback,

View.OnTouchListener {


private boolean Rdraw=false;
		private SurfaceHolder holder = null;

		private Canvas canvas = null;

		private Canvas canvasTemp = null;

		private Bitmap bi = null;

		private Paint p = new Paint();

		private Path path = new Path();



		public myCanvas(Context context, AttributeSet attrs) {

				super(context, attrs);

				holder = getHolder();

				holder.addCallback(this);

				p.setColor(Color.RED);

				p.setTextSize(40);

				p.setStrokeWidth(5);

				p.setAntiAlias(true);

				p.setFlags(Paint.ANTI_ALIAS_FLAG);

				p.setStyle(Paint.Style.STROKE);

				setOnTouchListener(this);

			}

float rdx=0,rdy=0;
public void startRDraw(){
	Rdraw=true;
	rdx=(float)getWidth()/2;rdy=(float)getHeight()/2;
	path.moveTo(rdx,rdy);
	new Thread(new Runnable(){@Override public void run(){
		while(Rdraw){
			try{
			float r1=(float)(Math.random()*10.0);
			float r2=(float)(Math.random()*10.0);
			rdx=rdx-(new Random().nextBoolean()?r1:-r1);
			rdy=rdy-(new Random().nextBoolean()?r2:-r2);
			if((rdx<0||rdx>getWidth()||rdy<0||rdy>getHeight())){rdx=getWidth()/2;rdy=getHeight()/2;}
			//path.moveTo(rdx,rdy);
			path.lineTo(rdx,rdy);
			draw();
			
			}catch(Exception e){/*Rdraw=false;Log.i("MediaScanWork",e.toString());*/}
		}
	}}).start();
}
public void stopRDraw(){
	Rdraw=false;
}
		private final void draw() {

				try {

						canvas = holder.lockCanvas();

						if (holder != null) {

								canvasTemp.drawColor(Color.WHITE);

								canvasTemp.drawPath(path, p);

								canvas.drawBitmap(bi, 0, 0, null);
					
							}

					} catch (Exception e) {

						e.printStackTrace();

					} finally {

						if (holder != null) {

								holder.unlockCanvasAndPost(canvas);

							}

					}

			}



		private String getTime() {

				return new SimpleDateFormat("HHmmssSSS").format(new java.sql.Date(System

																		 .currentTimeMillis()));

			}



		// 保存圖片.

		// 这里参数没有用到，可以去掉

		public void saveCanvas() {

				FileOutputStream fos = null;

				try {

						String fileName = getTime();

						String filePath = Environment

							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

							+"/PIC"+ fileName + ".png";

						fos = new FileOutputStream(new File(filePath));

						bi.compress(Bitmap.CompressFormat.PNG, 100, fos);

						MediaScannerConnection.scanFile(getContext(),

							new String[] { filePath }, null,

							new MediaScannerConnection.OnScanCompletedListener() {

									@Override

									public void onScanCompleted(String path, Uri uri) {

											Log.v("MediaScanWork", "file " + path

												  + " was scanned seccessfully: " + uri);

										}

								});

						Toast.makeText(getContext(), "保存成功,文件名:" + fileName + ".png",

									   Toast.LENGTH_LONG).show();

					//	clear();

					} catch (IOException e) {

						e.printStackTrace();

					}

			}
public void setColor(int color){
	p.setColor(color);
}


		public void clear() {

				path.reset();

				draw();

			}



		@Override

		public void surfaceCreated(SurfaceHolder holder) {

				bi = Bitmap.createBitmap(getWidth(), getHeight(),

										 Bitmap.Config.ARGB_8888);

				canvasTemp = new Canvas(bi);

				draw();

			}



		@Override

		public void surfaceChanged(SurfaceHolder holder, int format, int width,

								   int height) {



			}



		@Override

		public void surfaceDestroyed(SurfaceHolder holder) {



			}



		@Override

		public boolean onTouch(View v, MotionEvent event) {
				getParent().requestDisallowInterceptTouchEvent(true);
				switch (event.getAction()) {

						case MotionEvent.ACTION_DOWN:



							path.moveTo(event.getX(), event.getY());

							draw();

							break;

						case MotionEvent.ACTION_MOVE: // 画出每次移动的轨迹

						case MotionEvent.ACTION_UP:

							path.lineTo(event.getX(), event.getY());

							draw();

							break;

					}

				return true;

			}

	}


