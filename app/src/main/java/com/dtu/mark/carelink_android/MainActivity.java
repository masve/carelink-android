package com.dtu.mark.carelink_android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import android.view.View;
import android.widget.TextView;

import com.dtu.mark.carelink_android.Services.WebViewHandler;
import com.dtu.mark.carelink_android.USB.CareLinkUsb;


public class MainActivity extends Activity {

    public static TextView log;
    public CareLinkUsb stick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stick = CareLinkUsb.getInstance(this);

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
        startService(new Intent(this, WebViewHandler.class));
    }

    private BroadcastReceiver logUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            appendToLog(intent.getExtras().getString("dmsg"));
        }
    };

    public static void appendToLog(String propName, String propValue) {
        log.append(propName + ": " + propValue + "\n");
    }

    public static void appendToLog(String propValue) {
        log.append(propValue + "\n");
    }


}
