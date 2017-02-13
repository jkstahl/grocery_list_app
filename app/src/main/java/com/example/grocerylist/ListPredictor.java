package com.example.grocerylist;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neoba on 2/10/2017.
 */



public class ListPredictor {
    public static final double TOLERANCE =  0.1;

    private static void addValue(Map<String, Map<Object, Integer>> valueMap, String product,Object value) {

        if (valueMap.containsKey(product)) {
            Map<Object, Integer> integrator=valueMap.get(product);
            if (integrator.containsKey(value)) {
                integrator.put(value, integrator.get(value) + 1);
            } else {
                integrator.put(value, 1);
            }

        } else {
            Map<Object, Integer> integrator = new HashMap<>();
            integrator.put(value, 1);
            valueMap.put(product, integrator);
        }
    }

    private static void getProductsFromTracker(Map<String, Map<Object, Integer>> tracker, Map<String, Product> productMap, String col) {

        for (String productName : tracker.keySet()) {
            Map<Object, Integer> integrator = tracker.get(productName);
            int largestVal = 0;
            Object largest = null;
            for (Map.Entry<Object, Integer> entry : integrator.entrySet()) {
                if (entry.getValue() > largestVal) {
                    largestVal = entry.getValue();
                    largest = entry.getKey();
                }
            }
            Product p = productMap.get(productName);
            p.put(col, largest);

        }
    }

    public static List<Product> predictList(Context context) {
        List<Product> returnList = new ArrayList<>();
        ProductListDB db = DatabaseHolder.getDatabase(context);

        // make a map for all products in the data base to a list of their creation time in seconds since epoch.
        Cursor c = db.getAllProductRecords();
        Map<String, List<Integer>> productFreqMap = new HashMap<>();
        Map<String, Map<Object, Integer>> typeTracker = new HashMap<>();
        Map<String, Map<Object, Integer>> quantityTracker = new HashMap<>();
        Map<String, Map<Object, Integer>> unitsTracker = new HashMap<>();
        Map<String, Product> productToAdd = new HashMap<>();
        if (c.moveToFirst()) {
            do {
                String  productName = c.getString(c.getColumnIndex("NAME"));
                Integer timestamp = c.getInt(c.getColumnIndex("TIMESTAMP"));

                addValue(typeTracker, productName, c.getString(c.getColumnIndex("TYPE")));
                addValue(quantityTracker, productName, c.getDouble(c.getColumnIndex("QUANTITY")));
                addValue(unitsTracker, productName, c.getString(c.getColumnIndex("UNITS")));

                if (!productToAdd.containsKey(productName)) {
                    productToAdd.put(productName, new Product());
                    productToAdd.get(productName).put("NAME", productName);
                }

                List<Integer> newList;
                if (productFreqMap.containsKey(productName)) {
                    newList = productFreqMap.get(productName);
                } else {
                    newList = new ArrayList<>();
                    productFreqMap.put(productName, newList);
                }
                newList.add(timestamp);
            } while (c.moveToNext());
        }
        c.close();

        // consolidate trackers to the largest values.
        getProductsFromTracker(typeTracker, productToAdd, "TYPE");
        getProductsFromTracker(quantityTracker, productToAdd, "QUANTITY");
        getProductsFromTracker(unitsTracker, productToAdd, "UNITS");


        // Create statistics from all products in the map
        Map <String, ArivalStats> arrivalStatsMap = new HashMap<>();
        for (String product : productFreqMap.keySet()) {
            List<Integer> timestamps = productFreqMap.get(product);
            if (timestamps.size() >= 3) {  // if there isnt enough data then ignore this one.
                Collections.sort(timestamps);
                int lastTime = timestamps.get(0),
                        deltaSum = 0;
                List<Integer> deltaList = new ArrayList<>();
                for (int i=1; i<timestamps.size(); i++){
                    int delta = timestamps.get(i) - lastTime;
                    lastTime = timestamps.get(i);
                    deltaSum += delta;
                    deltaList.add(delta);
                }
                Collections.sort(deltaList);
                double variance = 0.0;
                double average = ((double) deltaSum / deltaList.size());
                for (Integer delta : deltaList) {
                    variance += Math.pow((0.0 + delta)-average, 2);
                }
                double std = Math.sqrt(variance);
//                System.out.println("Product: " + product +
//                        " Average: " + average +
//                        " STD: " + std +
//                        " Tolerance: " + (std/average) +
//                        " Delta: " + ((System.currentTimeMillis()/1000) -lastTime));
                ArivalStats newStats = new ArivalStats(lastTime, average, timestamps.size(), deltaList.get(0), std);
                arrivalStatsMap.put(product, newStats);
            }
        }
        long epoch = System.currentTimeMillis()/1000;
        for (String product : arrivalStatsMap.keySet()) {
            ArivalStats as = arrivalStatsMap.get(product);
            System.out.println("Product: " + product +
                    " Average: " + as.average +
                    " STD: " + as.std +
                    " Tolerance: " + (as.std/as.average) +
                    " Delta: " + (epoch -(as.lastTime+as.average)) +
                    " Last Time: " + as.lastTime);
            if ((as.std) / as.average <= .1 && epoch <= as.lastTime + as.average) {
                returnList.add(productToAdd.get(product));
            }
        }

        return returnList;
    }

}
