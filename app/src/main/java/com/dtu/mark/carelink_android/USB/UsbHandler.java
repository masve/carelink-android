package com.dtu.mark.carelink_android.USB;

import java.util.HashMap;

/**
 * Created by marksv on 9/29/14.
 */
public class UsbHandler {

    private static UsbHandler instance;
    private CareLinkUsb stick;

    private UsbHandler() {

    }

    public static UsbHandler getInstance(){
        if (instance == null) {
            instance = new UsbHandler();
        }
        return instance;
    }

    public CareLinkUsb getCareLinkUsb() {
        return stick;
    }

    public void setCareLinkUsb(CareLinkUsb stick) {
            this.stick = stick;
    }
}
