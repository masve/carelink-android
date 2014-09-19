package com.dtu.mark.carelink_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dtu.mark.carelink_android.USB.CareLinkUsb;
import com.dtu.mark.carelink_android.USB.UsbException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
