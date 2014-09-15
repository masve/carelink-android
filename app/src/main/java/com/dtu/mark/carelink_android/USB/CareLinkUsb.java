package com.dtu.mark.carelink_android.USB;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by mark on 14/09/14.
 */
public class CareLinkUsb {

    Context context;

    UsbManager mUsbManager;
    UsbDevice mUsbDevice;
    UsbDeviceConnection mUsbDeviceConnection;
    UsbInterface iFace;
    UsbEndpoint epIN, epOUT;


    public CareLinkUsb(Activity activity) {
        context = activity.getBaseContext();
    }

    public void open() throws UsbException {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            mUsbDevice = deviceIterator.next();
        }

        if (mUsbDevice == null) {
            throw new UsbException("device not found");
        }

        iFace = mUsbDevice.getInterface(0);
        //mUsbDeviceConnection.claimInterface(iFace, true);


//        if (iFace.getEndpoint(0).getDirection() == UsbConstants.USB_DIR_OUT) {
//            epOUT = iFace.getEndpoint(0);
//            epIN = iFace.getEndpoint(1);
//        } else {
//            epOUT = iFace.getEndpoint(1);
//            epIN = iFace.getEndpoint(0);
//        }



        // Assigning endpoint in and out
        epOUT = iFace.getEndpoint(0);
        epIN = iFace.getEndpoint(1);

        mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);

        if (mUsbDeviceConnection == null) {
            throw new UsbException("no connection available");
        }
    }
    public void close() throws UsbException {
        if (mUsbDeviceConnection == null) {
            throw new UsbException("no connection available");
        }
        mUsbDeviceConnection.releaseInterface(iFace);
        mUsbDeviceConnection.close();
    }

    /**
     * http://stackoverflow.com/questions/12345953/android-usb-host-asynchronous-interrupt-transfer
     * @param command
     * @return
     * @throws UsbException
     */
    public byte[] sendCommand(byte[] command) throws UsbException {
        if (mUsbDeviceConnection == null) {
            throw new UsbException("no connection available");
        }

        // MaxPacketSize = 64
        int bufferMaxLength = epOUT.getMaxPacketSize();
        ByteBuffer buffer = ByteBuffer.allocate(bufferMaxLength);
        UsbRequest outRequest = new UsbRequest();
        outRequest.initialize(mUsbDeviceConnection, epOUT);

//        for(int i = 0; i < command.length; i++) {
//            buffer.put(command[i]);
//
//        }

//        buffer.put(command, 0, command.length);

        // Putting op in buffer
        buffer.put(command);
        // Queue outbound request
        outRequest.queue(buffer, bufferMaxLength);

        // Receive data from device
        if (outRequest.equals(mUsbDeviceConnection.requestWait())) {
            UsbRequest inRequest = new UsbRequest();
            inRequest.initialize(mUsbDeviceConnection, epIN);
            if (inRequest.queue(buffer, bufferMaxLength)) {
                mUsbDeviceConnection.requestWait();
                return buffer.array();
            }
        }
        return null;
    }
}
