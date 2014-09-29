package com.dtu.mark.carelink_android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.dtu.mark.carelink_android.Services.WebViewHandler;
import com.dtu.mark.carelink_android.USB.CareLinkUsb;
import com.dtu.mark.carelink_android.USB.UsbException;
import com.dtu.mark.carelink_android.USB.UsbHandler;


public class MainActivity extends Activity {

    public static String TAG = "MainActivity";

    public static TextView log;
    //public CareLinkUsb stick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //stick = CareLinkUsb.getInstance(this);

        registerReceiver(logUpdate, new IntentFilter("UPDATE_LOG"));

        log = (TextView) findViewById(R.id.log);
        log.setTypeface(Typeface.MONOSPACE);
        log.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(logUpdate);
    }

    public void onInitCommand(View view) {
        try {
            if (!isMyServiceRunning()) {

                UsbHandler handler = UsbHandler.getInstance();

                //if (handler.getCareLinkUsb() != null){
                    CareLinkUsb stick = new CareLinkUsb(this);
                    handler.setCareLinkUsb(stick);
                //}

                Intent intent = new Intent(this, WebViewHandler.class);
                //intent.putExtra("stick", (android.os.Parcelable) stick);

                startService(intent);
            }
        } catch (UsbException e) {
            e.printStackTrace();
            Log.d(TAG, "error here");
        }


    }

    private BroadcastReceiver logUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getExtras().getString("dmsg");
            Log.d("WebViewHandler", "MainActivity received a message: " + msg);
            appendToLog(msg);
        }
    };

    public static void appendToLog(String propName, String propValue) {
        log.append(propName + ": " + propValue + "\n");
    }

    public static void appendToLog(String propValue) {
        log.append(propValue + "\n");
    }

    //Check to see if service is running
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (WebViewHandler.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
