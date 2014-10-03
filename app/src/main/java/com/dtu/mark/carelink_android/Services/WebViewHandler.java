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
import com.dtu.mark.carelink_android.USB.DataConverter;
import com.dtu.mark.carelink_android.USB.UsbException;
import com.dtu.mark.carelink_android.USB.UsbHandler;
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

    private UsbHandler usbHandler;

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

        wv = new WebView(this);
        windowManager.addView(wv, params);

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

                mHandler.removeCallbacks(init);
                mHandler.postDelayed(init, 1000);
            }
        });


        setJockeyEvents();

        wv.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * Get the dongle instance from the UsbHandler
         */
        usbHandler = UsbHandler.getInstance();
        stick = usbHandler.getCareLinkUsb();

        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            stick.close();
            wv.destroy();
            stopSelf();
        } catch (UsbException e) {
            e.printStackTrace();
            Log.e(TAG, "something happened when closing the usb connection");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initSend() {
        jockey.send("open", wv);
        Log.d(TAG, "command procedure initialized");
    }


    private Runnable init = new Runnable() {
        @Override
        public void run() {
            initSend();

            mHandler.removeCallbacks(init);
        }
    };

    private void doWrite(String id, String command) {
        try {
            byte[] data = DataConverter.hexStringArrayToByteArray(command.split(","));

            for (int i = 0; i < data.length; i++) {
                Log.d(TAG, "data[" + i + "] = " + data[i]);
            }

            if (stick.write(data))
                jockey.send(id, wv, DataConverter.strToJSON(null, ""));
            else
                throw new UsbException("could not queue the write request");
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
            jockey.send(id, wv, DataConverter.strToJSON(e.getMessage(), ""));
        }
    }

    private void doOpen(String id) {
        try {
            stick.open();
            jockey.send(id, wv, DataConverter.strToJSON(null, ""));
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
            jockey.send(id, wv, DataConverter.strToJSON(e.getMessage(), ""));
        }
    }

    private void doRead(String id) {
        try {
            byte[] data = stick.read();

            for (int i = 0; i < data.length; i++) {
                Log.d(TAG, "data[" + i + "] = " + data[i]);
            }

            String strData = DataConverter.byteArrayToString(data);

            Log.d(TAG, "sending to Jockey: " + strData);

            jockey.send(id, wv, DataConverter.strToJSON(null, strData));
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
            jockey.send(id, wv, DataConverter.strToJSON(e.getMessage(), ""));
        }
    }

    private void doClose(String id) {
        try {
            stick.close();
            jockey.send(id, wv, DataConverter.strToJSON(null, ""));
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
            jockey.send(id, wv, DataConverter.strToJSON(e.getMessage(), ""));
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
                doOpen(payload.get("eventHandlerId").toString());
                Log.d(TAG, "Device opened");
            }
        });

        jockey.on("write", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called write");
                Log.d(TAG, "Received command: " + payload.get("command").toString());
                doWrite(payload.get("eventHandlerId").toString(), payload.get("message").toString());
            }
        });

        jockey.on("read", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called read");
                doRead(payload.get("eventHandlerId").toString());
            }
        });

        jockey.on("close", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called close");
                doClose(payload.get("eventHandlerId").toString());
                Log.d(TAG, "Device closed");
            }
        });

        jockey.on("message", new JockeyHandler() {

            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called message");
                message = payload.get("message").toString();
                Log.d(TAG, "Message from Jockey: " + message);
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