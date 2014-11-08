package com.dtu.mark.carelink_android.Decoding.Stick;

import com.dtu.mark.carelink_android.Decoding.Lib;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by mark on 08/11/14.
 */
public class Commands {

    public static final byte[] ProductInfo = {0x04, 0x00};
    public static final byte[] UsbStats = {0x05, 0x01};
    public static final byte[] SignalStrength = {0x06, 0x00};
    public static final byte[] RadioStats = {0x05, 0x00};
    public static final byte[] Status = {0x03, 0x00};

    /* DECODING PRODUCT INFO */

    public static ProductInfoModel decodeProductInfo(byte[] data) {
        data = Arrays.copyOfRange(data, 3, data.length);

        String serial = bytesToHex(Arrays.copyOfRange(data, 0, 3));
        String product = data[3] + "." + data[4];
        String rf = rfLookup(data[5]);
        String description = new String(Arrays.copyOfRange(data, 6, 16));
        String firmware = data[16] + "." + data[17];
        HashMap<Integer, String> interfaces = decodeInterfaces(Arrays.copyOfRange(data, 18, data.length));

        return new ProductInfoModel(serial, product, rf, description, firmware, interfaces);
    }

    public static String bytesToHex(byte[] in) {
        StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private static String rfLookup(int i) {
        switch (i) {
            case 255:
                return "916.5Mhz";
            case 1:
                return "868.35Mhz";
            case 0:
                return "916.5Mhz";
            default:
                return i + " UNKNOWN";
        }
    }

    private static HashMap<Integer, String> decodeInterfaces(byte[] L) {
        int n = L[0];
        byte[] tail = Arrays.copyOfRange(L, 1, L.length);
        int i, k, v;
//        String interfaces = "";
        HashMap<Integer, String> interfaces = new HashMap<Integer, String>();

        for (int x = 0; x < n; x++) {
            i = x * 2;
            k = tail[i];
            v = tail[i + 1];

//            interfaces += "[ " + k + ": " + iface(v) + " ] ";
            interfaces.put(k, iface(v));
        }
        return interfaces;
    }

    private static String iface(int v) {
        switch (v) {
            case 3:
                return "USB";
            case 1:
                return "Paradigm RF";
            default:
                return "UNKNOWN";
        }
    }

    /* DECODING STATUS */

    public static StatusModel decodeStatus(byte[] data) {
        byte[] radio = Arrays.copyOfRange(data, 3, 6);
        byte iface = radio[2];

        String ok = String.valueOf(data[0] == 1 && radio[0] == 0 && radio[1] == 2);
        String ack = String.valueOf(data[0]);
        String success = String.valueOf(data[1]);
        String status = String.valueOf(data[2]);
        String ifacestr = describeStatus(iface);
        String debug_iface = bytesToHex(radio);
        String size = Lib.bangInt(data[6], data[7]) + "";
        String unknown_b = bytesToHex(Arrays.copyOfRange(data, 8, 12));
        String unknown_c = bytesToHex(Arrays.copyOfRange(data, 12, 16));
        String unknown_d = bytesToHex(Arrays.copyOfRange(data, 16, 20));
        String unknown_e = bytesToHex(Arrays.copyOfRange(data, 20, 24));
        String unknown_f = bytesToHex(Arrays.copyOfRange(data, 24, 28));
        String unknown_g = bytesToHex(Arrays.copyOfRange(data, 28, 32));
        String unknown_h = bytesToHex(Arrays.copyOfRange(data, 32, data.length));

        return new StatusModel(ok, ack, success, status, ifacestr, debug_iface, size, unknown_b, unknown_c, unknown_d, unknown_e, unknown_f, unknown_g, unknown_h);
    }

    private static class StatusMap {
        boolean received = false;
        boolean receiving = false;
        boolean transmitting = false;
        boolean error = false;
        boolean receiveOverflow = false;
        boolean transmitOverflow = false;

        @Override
        public String toString() {
            return "{" +
                    "received=" + received +
                    ", receiving=" + receiving +
                    ", transmitting=" + transmitting +
                    ", error=" + error +
                    ", receiveOverflow=" + receiveOverflow +
                    ", transmitOverflow=" + transmitOverflow +
                    '}';
        }
    }

    public static String describeStatus(byte iface) {
        StatusMap statusMap = new StatusMap();

        if ((iface & 0x01) >= 1) {
            statusMap.received = true;
        }
        if ((iface & 0x02) >= 1) {
            statusMap.receiving = true;
        }
        if ((iface & 0x04) >= 1) {
            statusMap.transmitting = true;
        }
        if ((iface & 0x08) >= 1) {
            statusMap.error = true;
        }
        if ((iface & 0x10) >= 1) {
            statusMap.receiveOverflow = true;
        }
        if ((iface & 0x20) >= 1) {
            statusMap.transmitOverflow = true;
        }

        return statusMap.toString();
    }

    /* DECODING INTERFACE STATS*/

    public static InterfaceStatsModel decodeInterfaceStats(byte[] data) {
        data = Arrays.copyOfRange(data, 3, data.length);
        int received = Lib.bangLong(data[4], data[5], data[6], data[7]);
        int transmit = Lib.bangLong(data[8], data[9], data[10], data[11]);

        return new InterfaceStatsModel(data[0], data[1], data[2], data[3], received, transmit);
    }

    /* DECODING SIGNAL */

    public static int decodeSignal(byte[] data) {
        return data[3];
    }

    /* OTHER STUFF */

//    public static byte[] describeParams(byte[] params) {
//        int count = params.length;
//        return new byte[] {0x80 | Lib.highByte(count), Lib.lowByte(count)};
//    }

}
