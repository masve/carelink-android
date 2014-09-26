package com.dtu.mark.carelink_android.USB;

import android.hardware.usb.UsbRequest;

/**
 * Created by marksv on 9/26/14.
 */
public interface ICareLinkUsb {
    /**
     * Opens a connection to the connected CareLink stick
     * @throws UsbException
     */
    void open() throws UsbException;

    /**
     * Closes the current connection to the CareLink stick
     * @throws UsbException
     */
    void close() throws UsbException;

    /**
     * Gets the response from the connected device.
     * @return Returns the response in a byte[].
     * @throws UsbException
     */
    byte[] read() throws UsbException;

    /**
     * Write a command to the connected device.
     * @param command Byte[] containing the opcode for the command.
     * @return Returns the response in a byte[].
     * @throws UsbException
     */
    void write(byte[] command) throws UsbException;

    /**
     * Wrapper for CareLinkUsb.write() and CareLinkUsb.read()
     * @param command Byte[] containing the opcode for the command.
     * @return Returns a reference to the UsbRequest put in the output queue.
     * @throws UsbException
     */
    byte[] sendCommand(byte[] command) throws UsbException;
}
