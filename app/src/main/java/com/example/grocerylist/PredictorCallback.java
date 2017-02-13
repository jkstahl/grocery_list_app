package com.example.grocerylist;

import java.util.Map;

/**
 * Created by neoba on 2/13/2017.
 */
public interface PredictorCallback {
    public void predictionDoneCallback(Map<String, Product> prediction);
}
