package com.dtu.mark.carelink_android;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dtu.mark.carelink_android.Decoding.Stick.Commands;
import com.dtu.mark.carelink_android.Decoding.Stick.InterfaceStatsModel;
import com.dtu.mark.carelink_android.Decoding.Stick.ProductInfoModel;
import com.dtu.mark.carelink_android.Decoding.Stick.StatusModel;
import com.dtu.mark.carelink_android.USB.CareLinkUsb;
import com.dtu.mark.carelink_android.USB.DataConverter;
import com.dtu.mark.carelink_android.USB.UsbException;
import com.dtu.mark.carelink_android.USB.UsbHandler;


public class MainActivity extends Activity {

    public static String TAG = "MainActivity";
//    public WebView wv;
//    public Jockey jockey;
    public Button button;
    public TextView textView;
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
        button = (Button) findViewById(R.id.btn);

        textView = (TextView) findViewById(R.id.textView);
        textView.setTypeface(Typeface.MONOSPACE);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onClickBtn(View view) {
        app();
    }

    private void app() {
        doOpen("");

        log(TAG, "ProductInfo");
        byte[] productInfo = doCommand("", Commands.ProductInfo);
        ProductInfoModel productInfoModel = Commands.decodeProductInfo(productInfo);
        log(TAG, productInfoModel.toString());

        log(TAG, "===========================");

        log(TAG, "Status");
        byte[] status = doCommand("", Commands.Status);
        StatusModel statusModel = Commands.decodeStatus(status);
        log(TAG, statusModel.toString());

        log(TAG, "===========================");

        log(TAG, "RadioStats");
        byte[] radioStats = doCommand("", Commands.RadioStats);
        InterfaceStatsModel interfaceStatsModel = Commands.decodeInterfaceStats(radioStats);
        log(TAG, interfaceStatsModel.toString());

        log(TAG, "===========================");

        log(TAG, "SignalStrength");
        byte[] signalStrength = doCommand("", Commands.SignalStrength);
        int decodeSignalStrength = Commands.decodeSignal(signalStrength);
        log(TAG, ""+decodeSignalStrength);

        log(TAG, "===========================");

        log(TAG, "UsbStats");
        byte[] usbStats = doCommand("", Commands.UsbStats);
        InterfaceStatsModel interfaceStatsModel2 = Commands.decodeInterfaceStats(usbStats);
        log(TAG, interfaceStatsModel2.toString());

        doClose("");
    }

    private byte[] doCommand(String id, byte[] command) {
        try {
            byte[] result = stick.sendCommand(command);
            String resultStr = DataConverter.byteArrayToString(result);

            log(TAG, "command result received: " + resultStr);

            return result;
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private void doOpen(String id) {
        try {
            stick.open();
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void doClose(String id) {
        try {
            stick.close();
            log(TAG, "stick closed");
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void doWrite(String id, byte[] command) {
        try {
            String dataStr = "";

            for (int i = 0; i < command.length; i++) {
                dataStr += command[i] + " ";
            }

            log(TAG, dataStr);

            if (stick.write(command)) {
                log(TAG, "Data written!");
            }
            else
                throw new UsbException("could not queue the write request");
        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    private void doRead(String id) {
        try {
            byte[] data = stick.read();

            String resultStr = "";

            for (int i = 0; i < data.length; i++) {
                resultStr += data[i] + " ";
            }

            log(TAG, resultStr);

            String strData = DataConverter.byteArrayToString(data);

            log(TAG, "sending to Jockey: " + strData);

        } catch (UsbException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public void log(String TAG, String value) {
        Log.d(TAG, value);
        textView.append(value + "\n");
    }
}
