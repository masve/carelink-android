package com.dtu.mark.carelink_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.dtu.mark.carelink_android.USB.USBPower;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by mark on 12/09/14.
 */
public class CareLinkUSBService extends Service{

    public UsbManager mUsbManager;
    private static final String TAG = CareLinkUSBService.class.getSimpleName();
    private Context mContext;
    private UsbSerialDriver mSerialDevice;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mUsbDeviceConnection;
    private Handler mHandler = new Handler();
    private final String ping = "0x04";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate called");
        mContext = this.getBaseContext();
        mUsbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        getUsbDevice(mUsbManager);

        mHandler.removeCallbacks(pingDevice);
        mHandler.post(pingDevice);
    }

    private Runnable pingDevice = new Runnable() {
        public void run() {
            boolean connected = isConnected();
            if (connected) {

                openDevice();
                UsbSerialPort port = mSerialDevice.getPorts().get(0);
                try {
                    port.open(mUsbDeviceConnection);
                    port.write(ping.getBytes(), 1000);
                    byte buffer[] = new byte[64];
                    int numBytesRead = port.read(buffer, 1000);
                    Log.d(TAG, "Read " + numBytesRead + " bytes.");

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        port.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Could not close port");
                    }
                    closeDevice();
                }
            }
            mHandler.removeCallbacks(pingDevice);
            mHandler.postDelayed(pingDevice, 5000);
        }
    };

    public void getUsbDevice(UsbManager mUsbManager) {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice foundDevice = null;

        // For now we are only interested in one device
        while (deviceIterator.hasNext()) {
            foundDevice = deviceIterator.next();
            break;
        }

        if (foundDevice != null) {
            mUsbDevice = foundDevice;
        } else {
            Log.w(TAG, "Could not find usb device");
        }
    }


    private void acquireSerialDevice() {
        mSerialDevice = UsbSerialProber.getDefaultProber().probeDevice(mUsbDevice);
        if (mSerialDevice == null) {

            Log.i(TAG, "Unable to get the serial device, forcing USB PowerOn, and trying to get an updated USB Manager");

            try {
                USBPower.PowerOn();
            } catch (Exception e) {
                Log.w(TAG, "acquireSerialDevice: Unable to PowerOn", e);
            }

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Log.w(TAG, "Interrupted during sleep after Power On", e);
            }

            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Log.w(TAG, "Interrupted during sleep after getting updated USB Manager", e);
            }

            mSerialDevice = UsbSerialProber.getDefaultProber().probeDevice(mUsbDevice);
        }
    }

    private void openDevice() {
        mUsbDeviceConnection = mUsbManager.openDevice(mSerialDevice.getDevice());

        if (mUsbDeviceConnection == null) {
            Log.i(TAG, "Unable to open UsbDeviceConnection for device");
        }
    }

    private void closeDevice() {
        if (mUsbDeviceConnection == null) {
            Log.i(TAG, "No UsbDeviceConnection to close");
            return;
        }

        mUsbDeviceConnection.close();
    }

    private boolean isConnected() {
        acquireSerialDevice();
        return mSerialDevice != null;
    }
}
