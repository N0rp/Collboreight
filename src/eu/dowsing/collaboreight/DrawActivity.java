package eu.dowsing.collaboreight;

import java.net.UnknownHostException;
import java.util.Locale;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.xbill.DNS.TXTRecord;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import eu.dowsing.collaboreight.painting.view.PaintingView;
import eu.dowsing.collaboreight.settings.SettingsActivity;

/**
 * The main activity.
 * @author richardg
 *
 */
public class DrawActivity extends Activity {
	
    public static final String PREFERENCES_NAME = "CollaborightPrefs";
    public static final int PREFERENCES_MODE = MODE_PRIVATE;

    
    public static final String PREF_ACCOUNT_SERVER = "pref_account_server";
    public static final String PREF_ACCOUNT_PORT = "pref_account_port";
    public static final String PREF_ACCOUNT_USER = "pref_account_user";
    public static final String PREF_ACCOUNT_PW = "pref_account_pw";
	
	private XMPPConnection conn2;
    
    // 18:11 Halt Hanau
    // 18:26 Halt Ffm Süd
    
    // 17:55 bis 18:53 hanau nach Langen 
    // 17:58 bis 18:33 ffm süd nach Langen
    
	private TextView lblUser;
    
    float Mx1,My1;
    float x,y;
    
    private PaintingView paintingView;
    

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy");
        if(conn2 != null && conn2.isConnected()){
        	conn2.disconnect();
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	
        lblUser = (TextView)findViewById(R.id.lblXmppAccountUser);
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

        
        Log.d("MainActivity", "getAccountNames: "+getAccountNames());
        initDrawer();

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
//        new ConnectToXmpp().execute();
        ConnectivityManager connMgr = (ConnectivityManager) 
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        	lblUser.setText("Connecting...");
        	ConnectToXmpp c = new ConnectToXmpp();
        	c.execute();
        } else {
            lblUser.setText("No network connection available.");
        }
    }
    
    
    
    
    private void initDrawer(){
    	mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                            "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
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
	        case R.id.action_settings:
	        	Intent i = new Intent(this, SettingsActivity.class);
	        	//Then start the activity
	        	startActivity(i);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private final static String ACCOUNT_NAME = "Collaboright";
	private final static String ACCOUNT_TYPE = "XMPP";
	
	private String[] getAccountNames() {
		AccountManager mAccountManager = AccountManager.get(this);
	    Account[] accounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
	    if(accounts.length == 0){
		    Account newAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
//			mAccountManager.addAccountExplicitly(account, password, userdata)
	    }
	    String[] names = new String[accounts.length];
	    for (int i = 0; i < names.length; i++) {
	        names[i] = accounts[i].name;
	    }
	    return names;

	}
	
	private class ConnectToXmpp extends AsyncTask<Void, Void, Void> {

		private static final String server_host = "xabber.de";
		private static final int server_port = 5222;
		
		private static final String server_user = "fyinconvenience@xabber.de";
		private static final String server_pw = "fyinconvenience@xabber.de";
		
		private void updateLabel(final String text){

	    	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
			    	lblUser.setText(text);
				}
			});
	    	
		}
		
	    @Override
	    protected Void doInBackground(Void... params) {
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DrawActivity.this);
	    	String server = prefs.getString(PREF_ACCOUNT_SERVER, "Not Jabber");
	    	int port = prefs.getInt(PREF_ACCOUNT_PORT, 5222);
	    	String user = prefs.getString(PREF_ACCOUNT_USER, "Not Test");
	    	String password = prefs.getString(PREF_ACCOUNT_PW, "Not Password");
	    	
	    	connectAndLogin(server, port, user, password);
	        return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	    	Log.d("MainActivity", "connected with results "+result);
	    	String user = conn2.getUser();
	    	updateLabel("Connected as user "+user);
//	    	createChatListeners();
	    }
	    
	    private void connect(String server, int port){
	    	SmackAndroid.init(DrawActivity.this);
//	         SASLAuthentication.supportSASLMechanism("PLAIN");
	        ConnectionConfiguration config = new ConnectionConfiguration(server, port);
	        config.setSASLAuthenticationEnabled(true);
           config.setDebuggerEnabled(true);// Enable xmpp debugging at Logcat
	        
	        // set the android locaiton of the trust store
//	    	config.setTruststorePath("/system/etc/security/cacerts.bks");
//	    	config.setTruststorePassword("changeit");
//	    	config.setTruststoreType("bks");

	        conn2 = new XMPPConnection(config);
		    try {
		    	conn2.connect();
		    } catch (XMPPException e) {
		        e.printStackTrace();
		        Log.e("Xmpp", "Connection Failed with server: "+server);
		        updateLabel("Connection failed to server "+server);
		    }
	    }
	    
	    private void connectAndLogin(String server, int port, String user, String password){
	    	Log.i("Xmpp", "Logging in to server: "+server+" with user: "+user);
	    	updateLabel("Connecting to server "+server+" as user "+user);
	    	// then you use
//	    	prefs.getBoolean(R.id.p, true);
	    	connect(server, port);
		    

    		try {
    			if(conn2.isConnected()){
    				Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
						conn2.login(user, password);
						Log.i("Xmpp", "Xmpp login successful");
    			}
			} catch (XMPPException e) {
				e.printStackTrace();
				updateLabel("Connection failed to server "+server+" as user "+user);
			}
		    
	    }


	    
	    private void createChatListeners(){
	    	ChatManager chatManager = conn2.getChatManager();
	        chatManager.addChatListener(new ChatManagerListener() {

	            @Override
	            public void chatCreated(final Chat chat, boolean createdLocally) {
	                System.out.println("Chat was created with: " + chat.getParticipant());
	                try {
	                    chat.sendMessage("ping");
	                } catch (XMPPException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }

	                if (!createdLocally) {
	                    // add message listener
	                    chat.addMessageListener(new MessageListener() {
							
							@Override
							public void processMessage(Chat arg0, Message arg1) {
								try {
									chat.sendMessage("pong");
								} catch (XMPPException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
	                }
	            }
	        });
	    }
	}

}
