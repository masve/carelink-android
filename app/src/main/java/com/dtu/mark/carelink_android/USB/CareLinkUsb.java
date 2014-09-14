package com.dtu.mark.carelink_android.USB;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

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

        // assigning endpoint in and out
        if (iFace.getEndpoint(0).getDirection() == 0) {
            epOUT = iFace.getEndpoint(0);
            epIN = iFace.getEndpoint(1);
        } else {
            epOUT = iFace.getEndpoint(1);
            epIN = iFace.getEndpoint(0);
        }

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
    public ByteBuffer sendCommand(Byte command) throws UsbException {
        if (mUsbDeviceConnection == null) {
            throw new UsbException("no connection available");
        }

        int bufferMaxLength = epOUT.getMaxPacketSize();
        ByteBuffer buffer = ByteBuffer.allocate(bufferMaxLength);
        UsbRequest request = new UsbRequest();
        request.initialize(mUsbDeviceConnection, epOUT);
        buffer.put(command);

        boolean retval = request.queue(buffer, bufferMaxLength);
        if (mUsbDeviceConnection.requestWait() == request) {
            UsbRequest inRequest = new UsbRequest();
            inRequest.initialize(mUsbDeviceConnection, epIN);
            if (inRequest.queue(buffer,bufferMaxLength) == true) {
                mUsbDeviceConnection.requestWait();
                return buffer;
            }
        }
        return null;
    }
}
