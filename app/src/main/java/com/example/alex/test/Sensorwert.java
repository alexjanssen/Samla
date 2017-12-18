package com.example.alex.test;

/**
 * Created by Blackilli on 14.11.17.
 */

public class Sensorwert {
    private long timestamp;
    private double[] values;

    public Sensorwert(long timestamp, double[] values) {
        this.timestamp = timestamp;
        this.values = values.clone();
        if (values.length < 3) this.values = new double[]{values[0], 0.0, 0.0};
    }

    public Sensorwert(long timestamp, float[] values) {
        this.timestamp = timestamp;
        this.values = new double[]{0.0, 0.0, 0.0};
        try {
            for (int i = 0; i < 3; i++)
                this.values[i] = values[i];
        } catch (IndexOutOfBoundsException ex) {}
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public long getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
