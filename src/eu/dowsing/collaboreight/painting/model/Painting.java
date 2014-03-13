package eu.dowsing.collaboreight.painting.model;

import java.util.LinkedList;
import java.util.List;

import eu.dowsing.collaboreight.painting.control.PaintingChangedListener;
import eu.dowsing.collaboreight.painting.view.PaintingView;

import android.content.Context;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class Painting {

    
    /** Contains the path the user has touched **/
    private ScaledPath    mPath;

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

    /** <code>true</code> if recently rescaled and not redrawn yet **/
    private boolean wasRescaled = false;
//    private float scale = 1;
    private List<ScaledPath> paths = new LinkedList<ScaledPath>();
    private float mScaleFactor = 1.f;
	private double offsetX = 0;
	private double offsetY = 0;

    
    /** if <code>true</code> scale gesture was encountered. **/
    private boolean isScaleGesture = false;
    
    private static int pathId = 0;
    
    private PaintingView view;
    private ScaleGestureDetector mScaleDetector;
    
    private List<PaintingChangedListener> listeners = new LinkedList<PaintingChangedListener>();
    
    public Painting(Context context, PaintingView view){
    	this.view = view;
    	mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mPath = new ScaledPath();
    }
	
    public void addPaintingChangedListener(PaintingChangedListener listener){
    	this.listeners.add(listener);
    }
    
    private void notifyPaintingChangedListenersAboutRedraw(){
    	for(PaintingChangedListener listener : this.listeners){
    		listener.onRedrawPainting();
    	}
    }
    
    private void notifyPaintingChangedListenersAboutCommitedPath(ScaledPath path){
    	for(PaintingChangedListener listener : this.listeners){
    		listener.onPathCommited(path);
    	}
    }
    
	public void commitPath(ScaledPath path){
        // store the drawn object
		ScaledPath temp = new ScaledPath(path, (pathId++)+"");
        paths.add(temp);
        notifyPaintingChangedListenersAboutCommitedPath(temp);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
        float y = event.getY();

        mScaleDetector.onTouchEvent(event);
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                view.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                view.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                view.invalidate();
                break;
        }	
        
        return true;
	}
	
	/**
	 * Return current user drawn path if there is one.
	 * 
	 * @return the current path or <code>null</code> if there is none.
	 */
	public ScaledPath getCurrentPath(){
		if(mPath.isEmpty() || isScaleGesture){
			return null;
		}else{
			return mPath;
		}
	}
	
	public List<ScaledPath> getAllPaths(){
		return this.paths;
	}
    
    public void setScale(float scale){
//        Log.d(this.getClass()+"", "Scale is "+mScaleFactor);
    	this.mScaleFactor = scale;
    	this.wasRescaled = true;
    	// notify listeners
    	notifyPaintingChangedListenersAboutRedraw();
    	view.invalidate();
    }
    
    public float getScale(){
    	return this.mScaleFactor;
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.setScale(getScale());
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
    }
    
    private void touch_up() {
    	
    	if(isScaleGesture){	        
    		isScaleGesture = false;
    	}else{
	        mPath.lineTo(mX, mY);
	        // commit the path to our final painting
	        commitPath(mPath);
    	}
    	// kill this so we don't double draw
        mPath.reset();
    }
    
	private class ScaleListener 
	        extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	float temp = getScale() * detector.getScaleFactor();
	    	//Don't let the object get too small or too large.
	        temp = Math.max(0.1f, Math.min(temp, 5.0f));
	    	
	        setScale(temp);
	        isScaleGesture = true;
	        return true;
	    }
	}
}
