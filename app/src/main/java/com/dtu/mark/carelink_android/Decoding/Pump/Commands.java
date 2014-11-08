package com.dtu.mark.carelink_android.Decoding.Pump;

import com.dtu.mark.carelink_android.Decoding.Lib;

import java.util.HashMap;

/**
 * Created by mark on 08/11/14.
 */
public class Commands {
    public static void bolus(BolusModel bolus) {
        int params = 5;
    }

    private static int fmt_bolus_opts(BolusModel bolus) {
        int strokes = bolus.getStrokes() * (int)bolus.getUnits();
//        byte[] params = floatToBytes(strokes);
//        if (bolus.getStrokes() > 10) {
//            byte[] params2 = {Lib.highByte(floatToBytes(strokes)), Lib.lowByte(strokes)};
//        }
        return strokes;
    }

    private static byte[] floatToBytes(float value) {
        int bits = Float.floatToIntBits(value);
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(bits & 0xFF);
        bytes[1] = (byte)((bits >> 8) & 0xFF);
        bytes[2] = (byte)((bits >> 16) & 0xFF);
        bytes[3] = (byte)((bits >> 24) & 0xFF);

        return bytes;
    }
}
