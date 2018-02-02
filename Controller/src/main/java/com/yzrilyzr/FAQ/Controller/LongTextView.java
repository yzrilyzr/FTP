package com.yzrilyzr.FAQ.Controller;
import android.view.inputmethod.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.yzrilyzr.ui.WidgetUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import com.yzrilyzr.myclass.util;
public class LongTextView extends View implements InputConnection
{

    private float lineNumWidth;
    public Paint pa;
    public float xOff=0,yOff=0,dxOff=0,dyOff=0,xVel=0,yVel=0;
    public float th;
    private float cursorX=0;
    public int currentLine=-1,cursorStart=0,cursorEnd=0,cursorC=0;
    public CopyOnWriteArrayList<String> stringLines;
    private boolean isEdit=false,isTouch=false;
    public boolean ViewMode=false;
    //private StringBuffer sb;

    @Override
    public CharSequence getTextBeforeCursor(int p1, int p2)
    {
        // TODO: Implement this method
        return null;
    }

    @Override
    public CharSequence getTextAfterCursor(int p1, int p2)
    {
        // TODO: Implement this method
        return null;
    }

    @Override
    public CharSequence getSelectedText(int p1)
    {
        // TODO: Implement this method
        return null;
    }

    @Override
    public int getCursorCapsMode(int p1)
    {
        // TODO: Implement this method
        return 0;
    }

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest p1, int p2)
    {
        // TODO: Implement this method
        return null;
    }

    @Override
    public boolean deleteSurroundingText(int p1, int p2)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean setComposingText(CharSequence p1, int p2)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean setComposingRegion(int p1, int p2)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean finishComposingText()
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean commitText(CharSequence p1, int p2)
    {
        // TODO: Implement this method
        String s=new StringBuffer()
		.append(getCurrentLine())
		.insert(cursorStart,p1)
		.toString();
        setCurrentLine(s);
        cursorStart+=p1.length();
        isEdit=true;
        return false;
    }

    @Override
    public boolean commitCompletion(CompletionInfo p1)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean commitCorrection(CorrectionInfo p1)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean setSelection(int p1, int p2)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean performEditorAction(int p1)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean performContextMenuAction(int p1)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean beginBatchEdit()
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean endBatchEdit()
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean sendKeyEvent(KeyEvent p1)
    {
        // TODO: Implement this method
        if(p1.getAction()==KeyEvent.ACTION_DOWN)
        {
            isEdit=true;
            int act=p1.getKeyCode();
            if(act==KeyEvent.KEYCODE_DPAD_RIGHT)cursorStart++;
            if(act==KeyEvent.KEYCODE_DPAD_LEFT)cursorStart--;
            if(act==KeyEvent.KEYCODE_DPAD_UP)
            {currentLine--;cursorX2Position();}
            if(act==KeyEvent.KEYCODE_DPAD_DOWN)
            {currentLine++;cursorX2Position();}
            if(act==KeyEvent.KEYCODE_DPAD_CENTER);
            if(act==KeyEvent.KEYCODE_DEL)
            {
                StringBuffer sb=new StringBuffer()
				.append(getCurrentLine());
                if(cursorStart==0)
                {
                    if(currentLine!=0)
                    {
                        stringLines.remove(currentLine);
                        currentLine--;
                        cursorStart=getCurrentLine().length();
                        String s2=getCurrentLine()+sb.toString();
                        setCurrentLine(s2);
                    }
                }
                else
                {
                    sb.delete(cursorStart-1,cursorStart);cursorStart--;
                    setCurrentLine(sb.toString());
                }
            }
            if(act==KeyEvent.KEYCODE_ENTER)
            {
                String s=getCurrentLine();
                setCurrentLine(s.substring(cursorStart,s.length()));
                stringLines.add(currentLine,s.substring(0,cursorStart));
                currentLine++;
                cursorStart=0;
            }
        }
        return true;
    }

    @Override
    public boolean clearMetaKeyStates(int p1)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean reportFullscreenMode(boolean p1)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean performPrivateCommand(String p1, Bundle p2)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean requestCursorUpdates(int p1)
    {
        // TODO: Implement this method
        return false;
    }
	public LongTextView(Context c,AttributeSet a){
		super(c,a);
		init();
	}
    public LongTextView(Context ctx)
    {
        super(ctx);
        init();
    }

	private void init()
	{
		stringLines=new CopyOnWriteArrayList<String>();
        //sb=new StringBuffer();
        pa=new Paint(Paint.ANTI_ALIAS_FLAG);
        pa.setTextSize(30);
        th=pa.getTextSize()*1.2f;
        pa.setStyle(Paint.Style.FILL);
        pa.setTypeface(Typeface.MONOSPACE);
	}
    private String getCurrentLine()
    {
        //String[] s=sb.toString().split("\n");
        if(currentLine<0)currentLine=0;
        if(currentLine>=stringLines.size())currentLine=stringLines.size() -1;
        return stringLines.get(currentLine);
    }
    private void setCurrentLine(String text)
    {
        //String[] s=sb.toString().split("\n");
        if(currentLine<0)currentLine=0;
        if(currentLine>=stringLines.size())currentLine=stringLines.size() -1;
        stringLines.set(currentLine,text);
    }
    public String getText()
    {
        StringBuilder s=new StringBuilder();
        for(String a:stringLines){
			s.append(a);
			s.append("\n");
		}
        return s.toString();
    }
	public void clear(){
		stringLines.clear();
	}
    public void setText(String text)
    {
        try
        {
            stringLines.clear();
            String[] sd=text.split("\n");
            for(String s:sd)stringLines.add(s);
            //sb=new StringBuffer();
            //sb.append(text);
        }
        catch(OutOfMemoryError e)
        {
            //Toast.makeText(getContext(),"内存不足",0).show();
        }
    }
    public void addText(String text)
    {
        try
        {
            String[] sd=text.split("\n");
            for(String s:sd)stringLines.add(s);
            //invalidate();
        }
        catch(OutOfMemoryError e)
        {
            //Toast.makeText(getContext(),"内存不足",0).show();
        }
    }
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        // TODO: Implement this method
        outAttrs.imeOptions = EditorInfo.TYPE_CLASS_TEXT;
        outAttrs.inputType = InputType.TYPE_CLASS_TEXT;
        return this;
    }
    private void cursorX2Position()
    {
        try
        {
            String curLineStr=getCurrentLine();
            float a=0;
            cursorStart=0;
            for(int i=0;i<curLineStr.length();i++)
            {
                a+=pa.measureText(curLineStr.charAt(i)+"");
                if(a>cursorX-lineNumWidth)break;
                else cursorStart++;
            }
        }
        catch(Throwable e)
        {System.out.println(e);}
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // TODO: Implement this method
        if(xOff>0)xOff=0;
        if(yOff>0)yOff=0;
		if(yOff<-th*stringLines.size())yOff=-th*stringLines.size();
        canvas.drawColor(0xff000000);
        try
        {
            //String[] stringLine=sb.toString().split("\n");
            if(currentLine<0)currentLine=0;
            if(currentLine>=stringLines.size())currentLine=stringLines.size()-1;
            lineNumWidth=pa.measureText(stringLines.size()+"");
            float yA=yOff%th;
            int w=getWidth(),h=getHeight();
            //line
            pa.setColor(0xbb666666);
            canvas.drawRect(0,currentLine*th+yOff,w,currentLine*th+th+yOff,pa);

            //cursor
            pa.setColor(cursorC<20||isEdit?0xffffffff:0x00000000);
            cursorX=pa.measureText(getCurrentLine().substring(0,cursorStart))+lineNumWidth;
            canvas.drawRect(cursorX+xOff,currentLine*th+yOff,cursorX+xOff+3,currentLine*th+th+yOff,pa);
            //text
            pa.setColor(0xffffffff);
            canvas.drawLine(lineNumWidth+xOff,0,lineNumWidth+xOff,h,pa);
            for(int i=(int)(-yOff/th),l=1;i<(-yOff+h)/th;i++,l++)
            {
                int ii=i;
                if(ii<0)break;
                if(ii>=stringLines.size())break;
                String s=stringLines.get(ii);
				pa.setColor(0xffcccccc);
			 	canvas.drawText((ii+1)+"",xOff,l*th+yA,pa);
				pa.setColor(0xffffffff);
                canvas.drawText(s,lineNumWidth+xOff,l*th+yA,pa);
				if(s.indexOf("[")!=-1){
				String gg=null;float ggw=0;
				pa.setColor(0xffffff00);
				canvas.drawText(gg=s.substring(s.indexOf("["),s.indexOf("]")+1),lineNumWidth+xOff,l*th+yA,pa);
				pa.setColor(0xff00ffff);ggw+=pa.measureText(gg);
				canvas.drawText(gg=s.substring(s.indexOf("<"),s.indexOf("(")),lineNumWidth+xOff+ggw,l*th+yA,pa);
				pa.setColor(0xff00ff00);ggw+=pa.measureText(gg);
				canvas.drawText(gg=s.substring(s.indexOf("("),s.indexOf(")")+1),lineNumWidth+xOff+ggw,l*th+yA,pa);
				pa.setColor(0xffff2222);ggw+=pa.measureText(gg);
				canvas.drawText(gg=s.substring(s.indexOf("@"),s.indexOf(">")),lineNumWidth+xOff+ggw,l*th+yA,pa);
				pa.setColor(0xff00ffff);ggw+=pa.measureText(gg);
				canvas.drawText(s.substring(s.indexOf(">"),s.indexOf(">")+1),lineNumWidth+xOff+ggw,l*th+yA,pa);
				}
            }
            if(!isTouch)
            {
                if(xVel>2)
                {xVel--;xOff+=xVel;}
                if(xVel<-2)
                {xVel++;xOff+=xVel;}
                if(yVel>2)
                {yVel--;yOff+=yVel;}
                if(yVel<-2)
                {yVel++;yOff+=yVel;}
            }
        }
        catch(Throwable e)
        {
            System.out.println(e);
        }
        cursorC++;
        if(cursorC>=40)
        {
            cursorC=0;
            isEdit=false;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO: Implement this method
		getParent().requestDisallowInterceptTouchEvent(true);
        float x=event.getX(),y=event.getY();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                dxOff=x;
                dyOff=y;
                isTouch=true;
                break;
            case MotionEvent.ACTION_UP:
                isTouch=false;
                if(Math.abs(yVel)<5&&Math.abs(xVel)<5&&!ViewMode)
                {
                    //openIME();
                    currentLine=(int)((-yOff+y)/th);
                    cursorX=x-xOff;
                    cursorX2Position();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                xVel=(x-dxOff);
                xOff+=xVel;
                dxOff=x;
                yVel=(y-dyOff);
                yOff+=yVel;
                dyOff=y;
                break;
        }
        return true;
    }
    public void hideIME()
    {
        ((InputMethodManager)getContext(). getSystemService(Context.INPUT_METHOD_SERVICE)). 
		hideSoftInputFromWindow(getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public void openIME()
    {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        InputMethodManager ime=((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        ime.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);

    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,getWidth()),WidgetUtils.measureHeight(heightMeasureSpec,util.dip2px(640)));
	}
	
}
