package com.dtu.mark.carelink_android.Services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dtu.mark.carelink_android.MainActivity;
import com.dtu.mark.carelink_android.USB.CareLinkUsb;
import com.dtu.mark.carelink_android.USB.UsbException;
import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyAsyncHandler;
import com.jockeyjs.JockeyHandler;
import com.jockeyjs.JockeyImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Created by marksv on 9/26/14.
 */
public class WebViewHandler extends Service {

    private String message = "";

    private WebView wv;

    private Jockey jockey;

    private static final String TAG = "WebViewHandler";

    CareLinkUsb stick;

    Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Reference: http://stackoverflow.com/questions/18865035/android-using-webview-outside-an-activity-context
         *
         * Initialize webview
         */

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;

        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        wv = new WebView(this);
        wv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        view.addView(wv);


        windowManager.addView(view, params);

        /**
         * Initialize JockeyJS and load index.html with javascript
         */

        jockey = JockeyImpl.getDefault();

        jockey.configure(wv);

        jockey.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "page finished loading!");
            }
        });


        setJockeyEvents();

        wv.loadUrl("file:///android_asset/index.html");

//        stick = CareLinkUsb.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Getting stick from intent");
        stick = (CareLinkUsb) intent.getParcelableExtra("stick");
        Log.d(TAG, "Did get the stick from intent");

        mHandler.removeCallbacks(init);
        mHandler.postDelayed(init, 1000);

        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initSend() {
        jockey.send("send-opcode", wv);
        Log.d(TAG, "opcode init sent");
    }


    private Runnable init = new Runnable() {
        @Override
        public void run() {
            initSend();

            mHandler.removeCallbacks(init);
        }
    };

    private void doWrite(Object byteArrayCommand) {
        try {
            // Convert object back to bytearray
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(byteArrayCommand);

            byte[] data = out.toByteArray();

            for(int i = 0; i < data.length; i++) {
                Log.d(TAG, "data["+i+"] = " + data[i]);
            }

            stick.write(data);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void doOpen() {
        try {
            stick.open();
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void doRead() {
        try {
            byte[] data = stick.read();
            jockey.send("doRead", wv, data);
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void doClose() {
        try {
            stick.close();
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void doSendMessage() {
        Intent i = new Intent("UPDATE_LOG");
        i.putExtra("dmsg", message);
        sendBroadcast(i);
    }

    /**
     * This is basically the interface, or the events from JockeyJS that Java listens to.
     * This is how JockeyJS/Javascript will invoke functions in implemented in Java.
     */
    private void setJockeyEvents() {

        jockey.on("open", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called open");
                doOpen();
                Log.d(TAG, "Device opened");
            }
        });

        jockey.on("write", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called write");
                doWrite(payload.get("command"));
            }
        });

        jockey.on("read", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called read");
                doRead();
            }
        });

        jockey.on("close", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called close");
                doClose();
            }
        });

        jockey.on("message", new JockeyHandler() {

            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called message");
                message = payload.get("message").toString();
                Log.d(TAG, "message received: " + message);
                doSendMessage();
            }
        });

        jockey.on("ping", new JockeyAsyncHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "JOCKEY SAYS HI!");
            }
        });
    }


}