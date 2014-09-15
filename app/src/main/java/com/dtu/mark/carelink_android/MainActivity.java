package com.dtu.mark.carelink_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
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

//            String op = "0x04";
//
//            int op2 = Integer.decode(op);
//            byte command = (byte)op2;

            //byte[] command = new byte[] {0x04, 0x01};

            byte command[] = {0x04, 0x00};

            appendToLog("info", "connection opened");
            appendToLog("info", "sending command...");

            byte[] result = stick.sendCommand(command);


//            byte[] result2 = result.array();

//            for(int i = 0; i < result2.length; i++) {
//                log.append("buffer["+i+"] " + something2[i]+"\n");
//            }


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

//    public void onClickConnect(View view) {
//        usb();
//    }

//    public void usb() {
//        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//
//        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//
//        UsbDevice device = null;
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//        while (deviceIterator.hasNext()) {
//            device = deviceIterator.next();
//        }
//
//        if (device == null) {
//            appendToLog("error", "device not found");
//            return;
//        }
//
//        // logging a whole bunch of device info
//
//        appendToLog("name", device.getDeviceName().toString());
//        appendToLog("vendor-id", device.getVendorId() + "");
//        appendToLog("product-id", device.getProductId() + "");
//        appendToLog("interface count", device.getInterfaceCount() + "");
//
//        UsbInterface iface = device.getInterface(0);
//        UsbEndpoint epIN, epOUT;
//
//        // assigning endpoint in and out
//        if (iface.getEndpoint(0).getDirection() == 0) {
//            epOUT = iface.getEndpoint(0);
//            epIN = iface.getEndpoint(1);
//        } else {
//            epOUT = iface.getEndpoint(1);
//            epIN = iface.getEndpoint(0);
//        }
//
//        epOUT.getAddress();
//        epOUT.getMaxPacketSize();
//
//        appendToLog("epOUT - address", epOUT.getAddress() + "");
//        appendToLog("epOUT - maxpackagesize", epOUT.getMaxPacketSize() + "");
//
//        appendToLog("epIN - address", epIN.getAddress() + "");
//        appendToLog("epIN - maxpackagesize", epIN.getMaxPacketSize() + "");
//
//        // Opening connection to device - real stuff starts here
//        UsbDeviceConnection connection = manager.openDevice(device);
//        if (connection == null) {
//            appendToLog("error", "no connection available");
//            return;
//        }
//
//        appendToLog("serial number", connection.getSerial());
//
//        // preparing opcode for transfer
//        byte[] outBuffer = new byte[]{0x04};
//
//        for (int i = 0; i < outBuffer.length; i++) {
//            appendToLog("out buffer" + i, outBuffer[i] + "");
//        }
//
//        // transfer to comlink
//        connection.bulkTransfer(epOUT, outBuffer, outBuffer.length, 1000);
//
//        // setup for receive
//        byte[] inBuffer = new byte[64];
//        StringBuilder str = new StringBuilder();
//
//        // receive on endpoint into buffer
//        int dataLength = connection.bulkTransfer(epIN, inBuffer, 64, 1000);
//
//        appendToLog("in datalength", dataLength + "");
//
//        for (int i = 0; i < inBuffer.length; i++) {
//            appendToLog("in buffer" + i, inBuffer[i] + "");
//        }
//
////        if (dataLength >= 0) {
////            for (int i = 2; i < 64; i++) {
////                //appendToLog("loop", i+"");
////                if (inBuffer[i] != 0) {
////                    str.append((char) inBuffer[i]);
////                } else {
////                    //appendToLog("inter",str.toString());
////                    break;
////                }
////            }
////        }
//
//        String s1 = Arrays.toString(inBuffer);
//        String s2 = new String(inBuffer);
//
//        appendToLog("decoded=\n", s2);
//        log.append("\n");
//
////
////            log.append("sent " + counter);
////            counter++;
////            counter = (byte) (counter % 16);
////        }
//
//    }

    public void appendToLog(String propName, String propValue) {
        log.append(propName + ": " + propValue + "\n");
    }


}
