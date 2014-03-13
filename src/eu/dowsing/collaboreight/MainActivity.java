package eu.dowsing.collaboreight;

import eu.dowsing.collaboreight.R;

import eu.dowsing.collaboreight.painting.view.PaintingView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

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
        setContentView(R.layout.activity_main);
        
        // add painting view to layout
        this.paintingView = new PaintingView(this);
        RelativeLayout.LayoutParams pParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        pParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        pParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        
        ViewGroup l = (ViewGroup) this.findViewById(R.id.drawLayout);
        l.addView(paintingView, 0, pParams);
        
        final SeekBar offsetXBar = (SeekBar) findViewById(R.id.offsetXBar);
        final int OFFSET_MAX = 50;
        offsetXBar.setProgress(OFFSET_MAX);
        offsetXBar.setMax(OFFSET_MAX * 2);
        offsetXBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				paintingView.getModel().setOffsetX( (progress - OFFSET_MAX) * 50);
			}
		});
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
	            paintingView.getModel().setScale(0.5f);
	            return true;
	        case R.id.normalZoom:
	        	paintingView.getModel().setScale(1.5f);
	        	return true;
	        case R.id.clearData:
	        	paintingView.getModel().clearData();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
