package com.dtu.mark.carelink_android.Decoding.Stick;

import java.util.HashMap;

/**
 * Created by mark on 08/11/14.
 */
public class ProductInfoModel {
    private String serial;
    private String product;
    private String rf;
    private String description;
    private String firmware;
    private HashMap<Integer, String> interfaces;

    public ProductInfoModel(String serial, String product, String rf, String description, String firmware, HashMap<Integer, String> interfaces) {
        this.serial = serial;
        this.product = product;
        this.rf = rf;
        this.description = description;
        this.firmware = firmware;
        this.interfaces = interfaces;
    }

    public String getSerial() {
        return serial;
    }

    public String getProduct() {
        return product;
    }

    public String getRf() {
        return rf;
    }

    public String getDescription() {
        return description;
    }

    public String getFirmware() {
        return firmware;
    }

    public HashMap<Integer, String> getInterfaces() {
        return interfaces;
    }

    @Override
    public String toString() {
        return "ProductInfoModel{" +
                "serial='" + serial + '\'' +
                ", product='" + product + '\'' +
                ", rf='" + rf + '\'' +
                ", description='" + description + '\'' +
                ", firmware='" + firmware + '\'' +
                ", interfaces=" + interfaces.toString() +
                '}';
    }
}
