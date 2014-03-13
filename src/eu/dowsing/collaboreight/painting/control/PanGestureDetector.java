package eu.dowsing.collaboreight.painting.control;

import java.util.LinkedList;
import java.util.List;

import android.view.MotionEvent;

/**
 * Detects three finger pan gestures.
 * 
 * @author richardg
 *
 */
public class PanGestureDetector {
//	private static final String DEBUG_TAG = "PanGestures";
	
	private int START_TOUCH_POSITION_X = 0;
	private int CURRENT_TOUCH_POSITION_X = 0;
	
	private boolean isPan = false;
	
	private List<PanGestureListener> listeners = new LinkedList<PanGestureListener>();
	
	public void addListener(PanGestureListener listener){
		this.listeners.add(listener);
	}
	
	private void notifyPanGestureListeners(float moveX, float moveY){
		for(PanGestureListener listener : listeners){
			listener.onPanGesture(moveX, moveY);
		}
	}
	
	public boolean isPan(){
		return this.isPan;
	}
	
	public void handleTouch(MotionEvent m){
		int action = m.getActionMasked();
		
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                START_TOUCH_POSITION_X = (int) m.getX(0);
                break;
                
            case MotionEvent.ACTION_UP:                 
                CURRENT_TOUCH_POSITION_X = 0;
                break;  
                
            case MotionEvent.ACTION_POINTER_DOWN:
                START_TOUCH_POSITION_X = (int) m.getX(0);
                break;
                
            case MotionEvent.ACTION_MOVE:
        		if(m.getPointerCount() == 3){
        			isPan = true;
	                CURRENT_TOUCH_POSITION_X = (int) m.getX(0);
	                notifyPanGestureListeners(START_TOUCH_POSITION_X - CURRENT_TOUCH_POSITION_X, 0);
	                
        		}else{
        			clear();
        		}
        		break;
            default:
            	clear();
        }
	}
	
	private void clear(){
    	isPan = false;
    	
        START_TOUCH_POSITION_X = 0;
        CURRENT_TOUCH_POSITION_X = 0;
		
	}

}
