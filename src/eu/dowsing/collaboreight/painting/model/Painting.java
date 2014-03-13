package eu.dowsing.collaboreight.painting.model;

import java.util.LinkedList;
import java.util.List;

import eu.dowsing.collaboreight.painting.control.PaintingChangedListener;
import eu.dowsing.collaboreight.painting.control.PanGestureDetector;
import eu.dowsing.collaboreight.painting.control.PanGestureListener;
import eu.dowsing.collaboreight.painting.view.PaintingView;

import android.content.Context;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.MotionEvent.PointerCoords;

/**
 * Model of the painting.
 * @author richardg
 *
 */
public class Painting {

	private static final String DEBUG_TAG = "Painting";
    
    /** Contains the scaled path the user has touched **/
    private ScaledPath    scaledPath;

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

    private List<ScaledPath> paths = new LinkedList<ScaledPath>();
    private float mScaleFactor = 1.f;
    
	private float offsetX = 0;
	private float offsetY = 0;

    
    /** if <code>true</code> scale gesture was encountered. **/
    private boolean isScaleGesture = false;
    
    private static int pathId = 0;
    
    private PaintingView view;
    private ScaleGestureDetector mScaleDetector;

	private PanGestureDetector panDetector = new PanGestureDetector();
    
    private List<PaintingChangedListener> listeners = new LinkedList<PaintingChangedListener>();
    
    public Painting(Context context, final PaintingView view){
    	this.view = view;
    	mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        scaledPath = new ScaledPath();
        panDetector.addListener(new PanGestureListener() {
			
			@Override
			public void onPanGesture(float moveX, float moveY) {
				Log.w(DEBUG_TAG, "Pan offset is X: "+moveX);
				setOffsetX(moveX);
				setOffsetY(moveY);
				
				view.invalidate();
			}
		});
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
	
	/**
	 * Return current user drawn path if there is one.
	 * 
	 * @return the current path or <code>null</code> if there is none.
	 */
	public ScaledPath getCurrentPath(){
		if(scaledPath.isEmpty() || isScaleGesture){
			return null;
		}else{
			return scaledPath;
		}
	}
	
	public float getOffsetX(){
		Log.d(DEBUG_TAG, "Get offsetX: "+this.offsetX);
		return this.offsetX;
	}
	
	public float getOffsetY(){
		return this.offsetY;
	}
	
	public List<ScaledPath> getAllPaths(){
		return this.paths;
	}
    
    public void setScale(float scale){
//        Log.d(this.getClass()+"", "Scale is "+mScaleFactor);
    	this.mScaleFactor = scale;
    	// notify listeners
    	notifyPaintingChangedListenersAboutRedraw();
    	view.invalidate();
    }
    
    public float getScale(){
    	return this.mScaleFactor;
    }
    
    public void setOffsetX(float offsetX){
    	this.offsetX = offsetX;
    	notifyPaintingChangedListenersAboutRedraw();
    	view.invalidate();
    }
    
    public void setOffsetY(float offsetY){
    	this.offsetY = offsetY;
    	notifyPaintingChangedListenersAboutRedraw();
    	view.invalidate();
    }

    public boolean handleTouch(MotionEvent m){
		mScaleDetector.onTouchEvent(m);
		panDetector.handleTouch(m);
		
	    //Number of touches
	    int pointerCount = m.getPointerCount();
		Log.d(getClass()+"", "pointer count is "+pointerCount);
	    if(pointerCount == 1){
	    	handleSingleTouch(m);
	    }
	    
	    return true;
	}

	private void touch_start(float x, float y) {
        scaledPath.reset();
        scaledPath.setScale(getScale());
        scaledPath.setOffset(offsetX, offsetY);
        scaledPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            scaledPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    
    private void touch_up() {
    	
    	if(isScaleGesture){	        
    		isScaleGesture = false;
    	}else{
	        scaledPath.lineTo(mX, mY);
	        // commit the path to our final painting
	        commitPath(scaledPath);
    	}
    	// kill this so we don't double draw
        scaledPath.reset();
    }
    
	private void handleSingleTouch(MotionEvent event){
		float x = event.getX();
	    float y = event.getY();
	    Log.d(getClass()+"", "handle single touch");
	    
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
	}

	private class ScaleListener 
	        extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	if(!panDetector.isPan()){
		    	float temp = getScale() * detector.getScaleFactor();
		    	//Don't let the object get too small or too large.
		        temp = Math.max(0.1f, Math.min(temp, 5.0f));
		    	
		        setScale(temp);
		        isScaleGesture = true;
		        return true;
	    	}else{
	    		return false;
	    	}
	    }
	}
}
