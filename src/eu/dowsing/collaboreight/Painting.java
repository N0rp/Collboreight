package eu.dowsing.collaboreight;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class Painting {
	
    private Bitmap  mBitmap;
    private Canvas  mCanvas;

    /** <code>true</code> if recently rescaled and not redrawn yet **/
    private boolean wasRescaled = false;
//    private float scale = 1;
    private List<ScaledPath> paths = new LinkedList<ScaledPath>();
    private float mScaleFactor = 1.f;
	private double offsetX = 0;
	private double offsetY = 0;
	

	private Paint mPaint;
	private Paint rectPaint;
    private Paint   mBitmapPaint;
	
    private static int pathId = 0;
	
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }
	
	public Painting(Paint mPaint){
        this.mPaint = mPaint;
        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(Color.BLUE);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}
	
	public void commitPath(ScaledPath path){
		drawPath(mCanvas, path, mPaint, getScale());
        // store the drawn object
        
        paths.add(new ScaledPath(path, (pathId++)+""));
	}
	
	private void drawPath(Canvas canvas, ScaledPath path, Paint paint, float scale){
		Log.d(this.getClass()+"", "drawPath with scale "+scale);
		Log.d(this.getClass()+"", "Draw "+path);
		Path scaled = path.getCurrentScaled(scale);
		Log.d(this.getClass()+"", "Draw "+path);
		Log.d(this.getClass()+"", "Draw rescaled "+scaled);
		
		canvas.drawPath(scaled, mPaint);
	}
	

    
    public void setScale(View view, float scale){
        Log.d(this.getClass()+"", "Scale is "+mScaleFactor);
    	this.mScaleFactor = scale;
    	this.wasRescaled = true;
    	view.invalidate();
    }
    
    public float getScale(){
    	return this.mScaleFactor;
    }
    
    protected void draw(Canvas canvas, ScaledPath tempPath) {
//    	canvas.save();
    	Log.d(this.getClass()+"", "onDraw");

    	
//    	System.out.println("On draw");
        canvas.drawColor(Color.LTGRAY);
       // canvas.drawLine(mX, mY, Mx1, My1, mPaint);
       // canvas.drawLine(mX, mY, x, y, mPaint);
        
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        if(tempPath != null){
        	drawPath(canvas, tempPath, mPaint, getScale());
        }
//        canvas.drawPath(mPath, mPaint);
        if(wasRescaled){
        	// draw all paths again with the new scale factor
        	wasRescaled = false;
        	// clear the data from the main canvas
        	mCanvas.drawColor(Color.DKGRAY);
//        	Log.d(this.getClass()+"", "Was rescaled, now drawing "+paths.size()+" paths");
        	for(ScaledPath path : paths){
//            	mCanvas.drawPath(path, mPaint);
        		drawPath(mCanvas, path, mPaint, getScale());
        	}
            canvas.drawRect(50, 50, 100, 100, rectPaint);
        }
        
//        canvas.restore();
    	
    }
}
