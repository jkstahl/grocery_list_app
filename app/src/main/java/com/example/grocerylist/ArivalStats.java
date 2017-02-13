package com.example.grocerylist;

/**
 * Created by neoba on 2/11/2017.
 */
public class ArivalStats {
    public int lastTime;
    public double average;
    public int shortestTime;
    public int size;
    public double std;

    public ArivalStats(int lastTime, double average, int size, int shortestTime, double std) {
        this.lastTime = lastTime;
        this.average = average;
        this.size = size;
        this.shortestTime = shortestTime;
        this.std = std;
    }
}
