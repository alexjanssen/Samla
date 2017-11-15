package com.example.alex.test;

/**
 * Created by Blackilli on 14.11.17.
 */

public class Sensorwert {
    private long timestamp;
    private float[] values;

    public Sensorwert(long timestamp, float[] values) {
        this.timestamp = timestamp;
        this.values = values.clone();
        if (values.length < 3) this.values = new float[]{values[0], 0.0f, 0.0f};
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public long getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
