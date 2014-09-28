package com.dtu.mark.carelink_android.USB;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Parcelable;
import android.util.Log;

import android.content.Intent;
import android.content.IntentFilter;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by mark on 14/09/14.
 */
public class CareLinkUsb {

    private static final int VENDOR_ID = 2593;
    private static final int PRODUCT_ID = 32769;
    private static final int MAX_PACKAGE_SIZE = 64;

    private static final String TAG = "CareLinkUsb";
    //    private static CareLinkUsb instance;
    private Context context;
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbInterface mInterface;
    private UsbEndpoint epIN, epOUT;
    private UsbRequest currentRequest;

    public CareLinkUsb(Context context) throws UsbException {
        this.context = context;
        initCareLink();
    }

    private void initCareLink() throws UsbException {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Log.d(TAG, "Enumerating connected devices...");

        // Getting the CareLink UsbDevice object
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            mUsbDevice = deviceIterator.next();
            if (mUsbDevice.getVendorId() == VENDOR_ID && mUsbDevice.getProductId() == PRODUCT_ID) {
                break;
            }
        }

        if (mUsbDevice == null) {
            throw new UsbException("Device not found");
        }

        Log.d(TAG, "Device found\nVendorId: " + mUsbDevice.getVendorId() + "\nProductId: " + mUsbDevice.getProductId());
    }

    /**
     * Opens a connection to the connected CareLink stick
     *
     * @throws UsbException
     */
    public void open() throws UsbException {
        // Assigning interface
        mInterface = mUsbDevice.getInterface(0);

        // Assigning endpoint in and out
        epOUT = mInterface.getEndpoint(0);
        epIN = mInterface.getEndpoint(1);

        // Open connection
        Log.d(TAG, "Opening connection...");
        mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
        Log.d(TAG, "Opened connection to\nVendorId: " + mUsbDevice.getVendorId() + "\nProductId: " + mUsbDevice.getProductId());

        if (mUsbDeviceConnection == null) {
            throw new UsbException("open: no connection available");
        }
    }

    /**
     * Closes the current connection to the CareLink stick
     *
     * @throws UsbException
     */
    public void close() throws UsbException {
        if (mUsbDeviceConnection == null) {
            throw new UsbException("close: no connection available");
        }
        mUsbDeviceConnection.releaseInterface(mInterface);
        mUsbDeviceConnection.close();
    }

    /**
     * Gets the response from the connected device.
     *
     * @return Returns the response in a byte[].
     * @throws UsbException
     */
    public byte[] read() throws UsbException {
        if (mUsbDeviceConnection == null) {
            throw new UsbException("read: no connection available");
        }

        if (currentRequest == null) {
            throw new UsbException("there is nothing to read");
        }

        ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKAGE_SIZE);

        // Receive data from device
        if (currentRequest.equals(mUsbDeviceConnection.requestWait())) {
            UsbRequest inRequest = new UsbRequest();
            inRequest.initialize(mUsbDeviceConnection, epIN);
            if (inRequest.queue(buffer, MAX_PACKAGE_SIZE)) {
                mUsbDeviceConnection.requestWait();
                return buffer.array();
            }
        }
        return null;
    }

    /**
     * Write a command to the connected device.
     *
     * @param command Byte[] containing the opcode for the command.
     * @return Returns the response in a byte[].
     * @throws UsbException
     */
    public void write(byte[] command) throws UsbException {
        if (mUsbDeviceConnection == null) {
            throw new UsbException("write: no connection available");
        }

        ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKAGE_SIZE);

        currentRequest = new UsbRequest();
        currentRequest.initialize(mUsbDeviceConnection, epOUT);

        buffer.put(command);
        currentRequest.queue(buffer, MAX_PACKAGE_SIZE);
    }

    /**
     * Wrapper for CareLinkUsb.write() and CareLinkUsb.read()
     *
     * @param command Byte[] containing the opcode for the command.
     * @return Returns a reference to the UsbRequest put in the output queue.
     * @throws UsbException
     */
    public byte[] sendCommand(byte[] command) throws UsbException {
        write(command);
        return read();
    }


}
