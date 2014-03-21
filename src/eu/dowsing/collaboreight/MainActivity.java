package eu.dowsing.collaboreight;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import eu.dowsing.collaboreight.R;

import eu.dowsing.collaboreight.painting.view.PaintingView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
    
    private void initSmack(){
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // init smack library
        SmackAndroid.init(this);
        
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
        
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();
        new ConnectToXmpp().execute();
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
		    case R.id.menuPen:
	        	
	        	return true;
		    case R.id.menuMagicPen:
	        	
	        	return true;
		    case R.id.menuSelect:
	        	
	        	return true;
	        case R.id.normalZoom:
	        	paintingView.getModel().setScale(1.0f);
	        	return true;
	        case R.id.halfZoom:
	            paintingView.getModel().setScale(0.5f);
	            return true;
	        case R.id.clearData:
	        	paintingView.getModel().clearData();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private class ConnectToXmpp extends AsyncTask<Void, Void, Void> {

		private static final String server_host = "xabber.de";
		private static final int server_port = 5222;
		
		private static final String server_user = "fyinconvenience@xabber.de";
		private static final String server_pw = "fyinconvenience@xabber.de";
		
	    @Override
	    protected Void doInBackground(Void... params) {
	    	
//	    	SmackAndroid.init(MainActivity.this);
	         SASLAuthentication.supportSASLMechanism("PLAIN");
	        ConnectionConfiguration config = new ConnectionConfiguration(server_host, server_port);
	        config.setSASLAuthenticationEnabled(true);
            config.setDebuggerEnabled(true);// Enable xmpp debugging at Logcat
	        
	        // set the android locaiton of the trust store
	    	config.setTruststorePath("/system/etc/security/cacerts.bks");
	    	config.setTruststorePassword("changeit");
	    	config.setTruststoreType("bks");

	        XMPPConnection conn2 = new XMPPConnection(config);
	    try {
	    	conn2.connect();
//            conn2.login(server_user, server_pw);
	        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
	    } catch (XMPPException e) {
	        e.printStackTrace();
	    } 

	        return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	    	Log.d("MainActivity", "connected with results "+result);
	    }

	}

}
