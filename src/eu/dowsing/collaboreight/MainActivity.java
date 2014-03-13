package eu.dowsing.collaboreight;

import com.example.collaboreight.R;

import eu.dowsing.collaboreight.painting.view.PaintingView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The main activity.
 * @author richardg
 *
 */
public class MainActivity extends Activity {

    
    float Mx1,My1;
    float x,y;
    
    private PaintingView paintingView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       // setContentView(R.layout.main);
        this.paintingView = new PaintingView(this);
        setContentView(paintingView);
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
	            paintingView.setContentScale(0.5f);
	            return true;
	        case R.id.normalZoom:
	        	paintingView.setContentScale(1f);
	        	return true;
	        case R.id.redraw:
	        	paintingView.invalidate();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
