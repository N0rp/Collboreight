package eu.dowsing.collaboreight.painting.view;

import java.util.LinkedList;
import java.util.List;

import eu.dowsing.collaboreight.painting.control.PaintingChangedListener;
import eu.dowsing.collaboreight.painting.model.Painting;
import eu.dowsing.collaboreight.painting.model.ScaledPath;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Displays the painting. 
 * @author richardg
 *
 */
public class PaintingView extends View {

//    private static final float MINP = 0.25f;
//    private static final float MAXP = 0.75f;
	
    private Bitmap  mBitmap;
    private Canvas  mCanvas;

	private Paint pathPaint;
	private Paint tempPaint;
	private Paint rectPaint;
    private Paint   mBitmapPaint;
   
	private TextView txt;
	
	private Painting painting;
	/** <code>true</code> if recently rescaled and not redrawn yet **/
	private boolean wasResized = false;
	
	private List<ScaledPath> newPaths = new LinkedList<ScaledPath>();

    public PaintingView(Context context) {
        super(context);
        initPaint();
        initPainting(context);
        Log.d(getClass()+"", "onCreate");
    }
    
    public Painting getModel(){
    	return this.painting;
    }
    
    private void initPainting(Context context){
    	painting = new Painting(context, this);
        painting.addPaintingChangedListener(new PaintingChangedListener() {
			
			@Override
			public void onRedrawPainting() {
				wasResized = true;
			}

			@Override
			public void onPathCommited(ScaledPath path) {
				newPaths.add(path);
			}
		});
    }
    
    private void initPaint(){
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    	
        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(Color.BLUE);
        
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setDither(true);
        pathPaint.setColor(0xFFFF0000);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
       // mPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeWidth(10);
        
        tempPaint = new Paint(pathPaint);
        tempPaint.setColor(Color.GREEN);
    }


    @Override
   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(this.getClass()+"", "OnSizeChange to "+w+":"+h);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	Log.d(this.getClass()+"", "onDraw");

        canvas.drawColor(Color.LTGRAY);
       // canvas.drawLine(mX, mY, Mx1, My1, mPaint);
       // canvas.drawLine(mX, mY, x, y, mPaint);
        
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        ScaledPath tempPath = painting.getCurrentPath();
        if(tempPath != null){
        	// draw the users path temporarily
    		canvas.drawPath(tempPath.getActualPath(), tempPaint);
        }else{
        	Log.d(getClass()+"", "path is null");
        }
        
        // draw a ll new paths
        while(!newPaths.isEmpty()){
        	ScaledPath newPath = newPaths.remove(0);
        	drawPath(mCanvas, newPath, pathPaint, painting.getScale(), painting.getOffsetX(), painting.getOffsetY());
        }
        
//        canvas.drawPath(mPath, mPaint);
        if(wasResized){
        	// draw all paths again with the new scale factor
        	wasResized = false;
        	// clear the data from the main canvas
        	mCanvas.drawColor(Color.DKGRAY);
//        	Log.d(this.getClass()+"", "Was rescaled, now drawing "+paths.size()+" paths");
        	List<ScaledPath> paths = painting.getAllPaths();
        	for(ScaledPath path : paths){
//            	mCanvas.drawPath(path, mPaint);
        		drawPath(mCanvas, path, pathPaint, painting.getScale(), painting.getOffsetX(), painting.getOffsetY());
        	}
            canvas.drawRect(50, 50, 100, 100, rectPaint);
        }
    }
	
	private void drawPath(final Canvas canvas, ScaledPath path, Paint paint, float scale, float offsetX, float offsetY){
//		Log.d(this.getClass()+"", "drawPath with scale "+scale);
//		Log.d(this.getClass()+"", "Draw "+path);
		Path scaled = path.getCurrentScaled(scale, offsetX, offsetY);
//		Log.d(this.getClass()+"", "Draw rescaled "+scaled);
		
		canvas.drawPath(scaled, paint);
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return painting.handleTouch(event);
    }
    


}
