package com.dtu.mark.carelink_android;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import android.view.View;
import android.widget.TextView;

import com.dtu.mark.carelink_android.USB.CareLinkUsb;
import com.dtu.mark.carelink_android.USB.UsbException;
import java.io.UnsupportedEncodingException;


public class MainActivity extends Activity {

    TextView log;
    CareLinkUsb stick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stick = new CareLinkUsb(this);

        log = (TextView) findViewById(R.id.log);
        log.setTypeface(Typeface.MONOSPACE);
        log.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onSendCommand(View view) {
        try {
            stick.open();

            byte command[] = {0x04, 0x00};

            appendToLog("info", "connection opened");
            appendToLog("info", "sending command...");

            byte[] result = stick.sendCommand(command);

//            UsbRequest request = stick.write(command);
//            byte[] result = stick.read(request);

            for(int i = 0; i < result.length; i++) {
                log.append("buffer["+i+"] " + result[i]+"\n");
            }

            String result3 = new String(result, "US-ASCII");

            appendToLog("info", "result returned");
            log.append("========result start=========\n");
            log.append(result3 + "\n");
            log.append("=========result end==========\n");

            stick.close();

            appendToLog("info", "connection closed");
            log.append("\n");
        } catch (UsbException e) {
            //e.printStackTrace();
            appendToLog("error", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            appendToLog("error", "could not decode result");
        }
    }

    public void appendToLog(String propName, String propValue) {
        log.append(propName + ": " + propValue + "\n");
    }


}
