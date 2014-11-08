package com.dtu.mark.carelink_android.Decoding.Pump;

/**
 * Created by mark on 08/11/14.
 */
public class BolusModel {
    private int strokes;
    private float units;

    public BolusModel(int strokes, float units) {
        this.strokes = strokes;
        this.units = units;
    }

    public int getStrokes() {
        return strokes;
    }

    public float getUnits() {
        return units;
    }

    @Override
    public String toString() {
        return "BolusModel{" +
                "strokes=" + strokes +
                ", units=" + units +
                '}';
    }
}
