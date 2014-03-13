package eu.dowsing.collaboreight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class MyView extends View {

    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;

    
    /** Contains the path the user has touched **/
    private ScaledPath    mPath;

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;
   
	
	private ScaleGestureDetector mScaleDetector;
	
	private Painting painting;

    public MyView(Context c, Paint mPaint) {
        super(c);
        painting = new Painting(mPaint);
        mPath = new ScaledPath();
        

        mScaleDetector = new ScaleGestureDetector(c, new ScaleListener());
    }

    public void setScale(float scale){
    	painting.setScale(this, scale);
    }


    @Override
   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(this.getClass()+"", "OnSizeChange to "+w+":"+h);
        painting.onSizeChanged(w, h, oldw, oldh);
    }
    
    
    private boolean isScaleGesture = false;

    @Override
    protected void onDraw(Canvas canvas) {
    	if(mScaleDetector.isInProgress()){
    		painting.draw(canvas, null);
    	}else{
    		painting.draw(canvas, mPath);
    	}
    	
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.setScale(painting.getScale());
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
//        invalidate();
    }
    
    
    private void touch_up() {
    	
    	if(isScaleGesture){	        
    		isScaleGesture = false;
    	}else{
	        mPath.lineTo(mX, mY);
	        // commit the path to our final painting
	        painting.commitPath(mPath);
    	}
    	// kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        mScaleDetector.onTouchEvent(event);
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
           //   Mx1=(int) event.getX();
             //  My1= (int) event.getY();
               invalidate();
                break;
        }
        return true;
    }
    


	private class ScaleListener 
	        extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	float temp = painting.getScale() * detector.getScaleFactor();
	    	//Don't let the object get too small or too large.
	        temp = Math.max(0.1f, Math.min(temp, 5.0f));
	    	
	        painting.setScale(MyView.this, temp);
	        isScaleGesture = true;
	        return true;
	    }
	}
}
