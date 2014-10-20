package com.dtu.mark.carelink_android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.dtu.mark.carelink_android.USB.CareLinkUsb;
import com.dtu.mark.carelink_android.USB.DataConverter;
import com.dtu.mark.carelink_android.USB.UsbException;
import com.dtu.mark.carelink_android.USB.UsbHandler;
import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyAsyncHandler;
import com.jockeyjs.JockeyHandler;
import com.jockeyjs.JockeyImpl;

import java.util.Map;


public class MainActivity extends Activity {

    public static String TAG = "MainActivity";
    public WebView wv;
    public Jockey jockey;
    public CareLinkUsb stick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            UsbHandler handler = UsbHandler.getInstance();
            stick = new CareLinkUsb(this);
            handler.setCareLinkUsb(stick);
        } catch (UsbException e) {
            e.printStackTrace();
        }

        wv = (WebView) findViewById(R.id.webview);

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

        jockey.on("command", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                String command = payload.get("message").toString();
                String id = payload.get("eventHandlerId").toString();
                Log.d(TAG, "Command initiated: " + command);
                doCommand(id, command);
            }
        });

        jockey.on("write", new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "Jockey called write");
                Log.d(TAG, "Received command: " + payload.get("message").toString() + " id: " + payload.get("eventHandlerId").toString());
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

        jockey.on("ping", new JockeyAsyncHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "JOCKEY SAYS HI!");
            }
        });
    }

    private void doCommand(String id, String commandStr) {
        try {
            byte[] command = DataConverter.hexStringArrayToByteArray(commandStr.split(","));

            byte[] result = stick.sendCommand(command);
            String resultStr = DataConverter.byteArrayToString(result);

            Log.d(TAG, "sending to Jockey: " + resultStr);

            jockey.send(id, wv, DataConverter.strToJSON(null, resultStr));
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
            jockey.send(id, wv, DataConverter.strToJSON(e.getMessage(), ""));
        }
    }

    private void doWrite(String id, String command) {
        try {
            byte[] data = DataConverter.hexStringArrayToByteArray(command.split(","));

            String dataStr = "";

            for (int i = 0; i < data.length; i++) {
                dataStr += data[i] + " ";
            }

            Log.d(TAG, dataStr);

            if (stick.write(data)) {
                jockey.send(id, wv, DataConverter.strToJSON(null, ""));
            }
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

            String resultStr = "";

            for (int i = 0; i < data.length; i++) {
                resultStr += data[i] + " ";
            }

            Log.d(TAG, resultStr);

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

}
