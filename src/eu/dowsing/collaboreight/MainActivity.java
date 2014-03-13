package eu.dowsing.collaboreight;

import com.example.collaboreight.R;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class MainActivity extends Activity {

	/** Called when the activity is first created. */
    Paint mPaint;
    float Mx1,My1;
    float x,y;
    
    private MyView view1;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPaint = new Paint();
       // setContentView(R.layout.main);
        this.view1 =new MyView(this, mPaint);
        setContentView(view1);
        
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
       // mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.halfZoom:
	            view1.setScale(0.5f);
	            return true;
	        case R.id.normalZoom:
	        	view1.setScale(1f);
	        	return true;
	        case R.id.redraw:
	        	view1.invalidate();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
