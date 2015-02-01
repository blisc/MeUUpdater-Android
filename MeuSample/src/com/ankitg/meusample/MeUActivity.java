package com.ankitg.meusample;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ankitguglani.MeU;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.larswerkman.holocolorpicker.ColorPicker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MeUActivity extends Activity implements OnClickListener {

	Button btn_connect;
//	Button btn_disconnect;
	Button btn_refresh;
	Button btn_send_text;
	ImageButton btn_send_image1;
	ImageButton btn_send_image2;
	ImageButton btn_send_image3;
	ImageButton btn_send_image4;
	ImageButton btn_send_image5;
	ImageButton btn_send_image6;
	ImageButton btn_send_image7;
	ImageButton btn_send_image8;
	ImageButton btn_send_image9;
	ImageButton btn_send_image10;
	ImageButton btn_send_image11;
	ImageButton btn_send_image12;
	TextView tv_bt_status;
	EditText et_text;
	Spinner spn_bluetooth;
	ColorPicker picker;

    private static MeU instance = null;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "538415636022";
    TextView mDisplay;
    GoogleCloudMessaging gcm;
    //AtomicInteger msgId = new AtomicInteger();
    //SharedPreferences prefs;
    Context context;

    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu);

        context = getApplicationContext();

        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);

        if (regid.isEmpty()) {
            registerInBackground();
        }

        init();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    public static MeU getInstance() {
        if(instance == null) {
            instance = new MeU();
        }
        return instance;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MeUActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
       }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void ... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            /*@Override
            protected void onPostExecute(String msg) {
                mDisplay.append(msg + "\n");
            }*/
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() {
        String url = "http://104.131.64.14:1337/user/create?registration_id=" + regid;
        HttpGet mRequest = new HttpGet(url);
        DefaultHttpClient client = new DefaultHttpClient();

        try {
            HttpResponse response = client.execute(mRequest);
        } catch (IOException e) {
            mRequest.abort();
        }
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        //Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void init() {
        btn_connect = (Button)findViewById(R.id.BTN_main_connect);
//        btn_disconnect = (Button)findViewById(R.id.BTN_main_disconnect);
        btn_refresh = (Button)findViewById(R.id.BTN_main_refresh);
        btn_send_text = (Button)findViewById(R.id.BTN_main_sendText);
        btn_send_image1 = (ImageButton)findViewById(R.id.BTN_main_image1);
        btn_send_image2 = (ImageButton)findViewById(R.id.BTN_main_image2);
        btn_send_image3 = (ImageButton)findViewById(R.id.BTN_main_image3);
        btn_send_image4 = (ImageButton)findViewById(R.id.BTN_main_image4);
        btn_send_image5 = (ImageButton)findViewById(R.id.BTN_main_image5);
        btn_send_image6 = (ImageButton)findViewById(R.id.BTN_main_image6);
        btn_send_image7 = (ImageButton)findViewById(R.id.BTN_main_image7);
        btn_send_image8 = (ImageButton)findViewById(R.id.BTN_main_image8);
        btn_send_image9 = (ImageButton)findViewById(R.id.BTN_main_image9);
        btn_send_image10 = (ImageButton)findViewById(R.id.BTN_main_image10);
        btn_send_image11 = (ImageButton)findViewById(R.id.BTN_main_image11);
        btn_send_image12 = (ImageButton)findViewById(R.id.BTN_main_image12);
        tv_bt_status = (TextView)findViewById(R.id.TV_main_bluetooth_status);
        et_text = (EditText)findViewById(R.id.ET_main_text);
        spn_bluetooth = (Spinner)findViewById(R.id.SPN_main_bluetooth_list);
        picker = (ColorPicker)findViewById(R.id.Picker_main_colorpicker);
        
        btn_connect.setOnClickListener(this);
//        btn_disconnect.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_send_image1.setOnClickListener(this);
        btn_send_image2.setOnClickListener(this);
        btn_send_image3.setOnClickListener(this);
        btn_send_image4.setOnClickListener(this);
        btn_send_image5.setOnClickListener(this);
        btn_send_image6.setOnClickListener(this);
        btn_send_image7.setOnClickListener(this);
        btn_send_image8.setOnClickListener(this);
        btn_send_image9.setOnClickListener(this);
        btn_send_image10.setOnClickListener(this);
        btn_send_image11.setOnClickListener(this);
        btn_send_image12.setOnClickListener(this);
        btn_send_text.setOnClickListener(this);
        
        picker.setShowOldCenterColor(false);
        populateBtDevices();
        updateStatus();
    }
    
    private void updateStatus()
    {
        MeU MeU = getInstance();
    	tv_bt_status.setText(MeU.btStatus());
    }
    
    private void populateBtDevices(){
        MeU MeU = getInstance();
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item, MeU.listMeUs(getApplicationContext()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_bluetooth.setAdapter(dataAdapter);
        updateStatus();
    }
    
    private void connect()
    {
    	if(spn_bluetooth.getSelectedItem() != null)
    	{
    		MeU MeU = getInstance();
            MeU.setupBluetooth(getApplicationContext(), spn_bluetooth.getSelectedItem().toString());
    	}
    	updateStatus();
    }
    
//	private void disconnnect() {
//		MeU.disconnectBt();
//		updateStatus();		
//	}
    
    private void sendText()
    {
        //String debug1 = et_text.getText().toString();
        //String debug2 = Integer.toHexString(picker.getColor()).substring(2);
        MeU MeU = getInstance();
    	MeU.sendText(et_text.getText().toString(), Integer.toHexString(picker.getColor()).substring(2));
    }
    
    private void sendImage(int resource)
    {
        MeU MeU = getInstance();
    	Bitmap bmp = BitmapFactory.decodeResource(getResources(), resource);
    	MeU.sendImage(bmp);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.BTN_main_connect:
				connect();
				break;
								
			case R.id.BTN_main_sendText:
				sendText();
				break;
				
			case R.id.BTN_main_image1:
				sendImage(R.drawable.nascent);
				break;
				
			case R.id.BTN_main_image2:
				sendImage(R.drawable.wwto);
				break;
				
			case R.id.BTN_main_image3:
				sendImage(R.drawable.heart);
				break;

			case R.id.BTN_main_image4:
				sendImage(R.drawable.star);
				break;

			case R.id.BTN_main_image5:
				sendImage(R.drawable.skull);
				break;

			case R.id.BTN_main_image6:
				sendImage(R.drawable.megaman);
				break;

			case R.id.BTN_main_image7:
				sendImage(R.drawable.hearts);
				break;

			case R.id.BTN_main_image8:
				sendImage(R.drawable.mushroom_red);
				break;

			case R.id.BTN_main_image9:
				sendImage(R.drawable.mushroom_green);
				break;

			case R.id.BTN_main_image10:
				sendImage(R.drawable.goomba);
				break;
				
			case R.id.BTN_main_image11:
				sendImage(R.drawable.fireflower);
				break;

			case R.id.BTN_main_image12:
				sendImage(R.drawable.gremlin);
				break;
			
			case R.id.BTN_main_refresh:
				populateBtDevices();
				break;
				
//			case R.id.BTN_main_disconnect:
//				disconnnect();
//				break;
		}
	}
}
