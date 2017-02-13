
package com.example.grocerylist;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by neoba on 1/18/2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ListPredictorTest {
    private static final Integer COMMON_QUANTITY = 1;
    ProductListDB db;
    private final int NUM_PRODUCTS = 30;
    private final int SEED = 52;
    Random r = new Random(SEED);
    Map<String, RandomType> checkData;
    private final String COMMON_TYPE="FrequentType";
    List<Product> outputList;

    private double getGaussianTimeStamp(double mean, double std) {
        return r.nextGaussian() * std + mean;
    }

    private double doubleGetUniformTimeStamp(double max) {
        return r.nextDouble() * max;
    }

    private List<String> getCommonList(int num, Random r, String commonType) {
        List<String> types = new ArrayList<>();

        for (int i = 0; i < num / 2 + 1; i++)
            types.add(commonType);
        while (types.size() < num) {
            int left = num - types.size();
            for (int i=0; i<r.nextInt(); i++)
                types.add("" + left + "abc");
        }
        Collections.shuffle(types);
        return types;
    }

    private List<Integer> getCommonList(int num, Random r, Integer commonQuantity) {
        List<Integer> types = new ArrayList<>();

        for (int i = 0; i < num / 2 + 1; i++)
            types.add(commonQuantity);
        while (types.size() < num) {
            int left = num - types.size();
            for (int i=0; i<r.nextInt(); i++)
                types.add(left);
        }
        Collections.shuffle(types);
        return types;
    }

    @Before
    public void setUp() throws Exception {
        checkData  = new HashMap<>();
        db = DatabaseHolder.getDatabase(RuntimeEnvironment.application);
        //db.createDummyList(db.getWritableDatabase());
        // Create products. For each create a set of samples with timestamps
        long startTime  = System.currentTimeMillis()/1000;
        for (int i=0; i<NUM_PRODUCTS; i++) {
            RandomType rt = new RandomType();
            String productName =  "Product " + i;
            int type = r.nextInt(4) / 3;
            rt.type = type;
            int averageStep = r.nextInt(30 * 24 * 60 * 60) + 1;
            rt.average = averageStep;
            double std = r.nextDouble() * ListPredictor.TOLERANCE * averageStep / 2;
            rt.std = std;
            long newStartTime = startTime ;
            checkData.put(productName, rt);
            int numSamples = r.nextInt(15);
            rt.numSamples = numSamples;



            List<String> types = getCommonList(numSamples, r, COMMON_TYPE);
            List<Integer> quantities = getCommonList(numSamples, r, COMMON_QUANTITY);
            List<String> units =  getCommonList(numSamples, r, COMMON_TYPE);

            double sum=0;
            for (int j=1; j<numSamples; j++) {  // sample products
                double timeDelta;
                if (type == 0) {
                    timeDelta = getGaussianTimeStamp(averageStep, std);
                    newStartTime -= timeDelta;
                } else {
                    timeDelta = doubleGetUniformTimeStamp(averageStep);
                    newStartTime -= timeDelta;
                }
                if (j == 1)
                    rt.lastTime = (int)newStartTime;
                else
                    sum += timeDelta;
                Product newProduct = new Product();

                newProduct.put("NAME", productName);
                newProduct.put("TYPE", types.get(j));
                newProduct.put("QUANTITY", quantities.get(j));
                newProduct.put("UNITS", units.get(j));
                db.addEntryToDatabase(newProduct);
                db.updateTimestamp((Integer) newProduct.get("_id"), newStartTime);
                //System.out.println(productName + " -> " + newStartTime + ", ");
            }
            rt.averageActual = sum / numSamples;
            System.out.print("Name: " + productName);
            System.out.println(rt);
        }

        // perform any db operations you want here
    }

    private boolean contains(List<Product> productList, String product) {
        boolean found = false;
        for (Product p : productList)
            if (p.get("NAME").equals(product))
                return found=true;
        return found;
    }

    @Test
    public void checkMissing() throws Exception {
        System.out.println("Testing basic prediction.");
        outputList = ListPredictor.predictList(RuntimeEnvironment.application);
        long epoch  = System.currentTimeMillis()/1000;
        for (Product p : outputList) {
            System.out.println(p);
            RandomType returnStats = checkData.get(p.get("NAME"));
            assertEquals(0, returnStats.type);
            assertTrue(returnStats.std / returnStats.average < .1);
            assertTrue(returnStats.numSamples >=3);
            assertTrue(returnStats.average + returnStats.lastTime > epoch);
        }

        for (String product : checkData.keySet()) {
            RandomType returnStats = checkData.get(product);
            if (returnStats.averageActual + returnStats.lastTime > epoch && returnStats.type == 0)
                assertTrue("Product not found in output. Product: " + product + returnStats, contains( outputList, product));
        }

    }

    private class RandomType {
        int type;
        int average;
        double std;
        int numSamples;
        int lastTime;
        public double averageActual;

        @Override
        public String toString() {
            String[] types = new String[]{"Gaussian", "Uniform"};
            return " Average: " + average + " Actual Average :" + averageActual + " STD: " + std + " Type: " + types[type] + " Last Time: " + lastTime;
        }

    }

}