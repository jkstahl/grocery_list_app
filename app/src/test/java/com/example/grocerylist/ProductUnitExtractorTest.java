package com.example.grocerylist;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.grocerylist.ProductUnitExtractor;

/**
 * Created by neoba on 1/18/2017.
 */
public class ProductUnitExtractorTest {

    @Test
    public void testGetUnitsProductFromString() throws Exception {

        ProductUnitExtractor pue = new ProductUnitExtractor();
        //ProductUnitExtractor.QuantityUnitPackage qup = pue.getUnitsProductFromString("one carrot");
        String defaultUnit = "";
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("one carrot"), pue.getQuantityUnitsClass("carrot", 1, defaultUnit));
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("twenTy carrots"), pue.getQuantityUnitsClass("carrots", 20, defaultUnit));
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("1 Ounce milk"), pue.getQuantityUnitsClass("milk", 1, "ounce"));
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("32 liters coke"), pue.getQuantityUnitsClass("coke", 32, "liter"));
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("carrot 2   rolls"), pue.getQuantityUnitsClass("carrot", 2, "roll"));
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("two pounds flower"), pue.getQuantityUnitsClass("flower", 2, "pound"));
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("2.5 grams flower"), pue.getQuantityUnitsClass("flower", 2.5, "gram"));
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("2.7 ounce flower"), pue.getQuantityUnitsClass("flower", 2.7, "ounce"));
        assertEquals("Extracted product or quantity not correct", pue.getUnitsProductFromString("flower"), pue.getQuantityUnitsClass("flower", 1, defaultUnit));

    }

    @Test
    public void testCaseInsensative() throws Exception {
        ProductUnitExtractor pue = new ProductUnitExtractor();
        //ProductUnitExtractor.QuantityUnitPackage qup = pue.getUnitsProductFromString("one carrot");
        String defaultUnit = "";
        assertEquals("Case sensitivity test failed", pue.getQuantityUnitsClass("carrot", 1, defaultUnit), pue.getUnitsProductFromString("One carrot"));
        assertEquals("Case sensitivity test failed", pue.getQuantityUnitsClass("cArrot", 1, defaultUnit), pue.getUnitsProductFromString("onE cArrot"));
        assertEquals("Case sensitivity test failed", pue.getQuantityUnitsClass("carrot", 21, defaultUnit), pue.getUnitsProductFromString("Twenty One carrot"));
        assertEquals("Case sensitivity test failed", pue.getQuantityUnitsClass("carrot", 1, "ounce"), pue.getUnitsProductFromString("OnE ounces carrot"));
        assertEquals("Case sensitivity test failed", pue.getQuantityUnitsClass("carrot", 1, "pound"), pue.getUnitsProductFromString("One pounD carrot"));
        assertEquals("Case sensitivity test failed", pue.getQuantityUnitsClass("seeds", 12.2, "gram"), pue.getUnitsProductFromString("12.2 grams seeds"));
        assertEquals("Case sensitivity test failed", pue.getQuantityUnitsClass("carrot", 51, "pack"), pue.getUnitsProductFromString("51Pack carrot"));
    }

    @Test
    public void testCaseQuantityUnitProduct() throws Exception {
        ProductUnitExtractor pue = new ProductUnitExtractor();
        //ProductUnitExtractor.QuantityUnitPackage qup = pue.getUnitsProductFromString("one carrot");
        String defaultUnit = "";
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("flower", 1, "pound"), pue.getUnitsProductFromString("one pound flower"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("oatmeal", 5, "pint"), pue.getUnitsProductFromString("five pints oatmeal"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("tylenol", 2.45, "mililiter"), pue.getUnitsProductFromString("2.45ml tylenol"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("tablets sodium", 2.45, "miligram"), pue.getUnitsProductFromString("1.245 2.45 mg tablets sodium"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("pancakes", 2, "quart"), pue.getUnitsProductFromString("pancakes 2 quarts"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("crystals", 21, "each"), pue.getUnitsProductFromString("twenty-one each crystals"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("grape soda", 15, "teaspoon"), pue.getUnitsProductFromString("fifteen tsp. grape soda"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("grape soda", 15, "teaspoon"), pue.getUnitsProductFromString("i want fifteen tsp. grape soda"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("cola", 2, "liter"), pue.getUnitsProductFromString("2 liters of cola"));



    }
    @Test
    public void testCaseQuantityProduct() throws Exception {
        ProductUnitExtractor pue = new ProductUnitExtractor();
        //ProductUnitExtractor.QuantityUnitPackage qup = pue.getUnitsProductFromString("one carrot");
        String defaultUnit = "";
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("cocanuts", 2, defaultUnit), pue.getUnitsProductFromString("2 cocanuts"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("2cola", 1, defaultUnit), pue.getUnitsProductFromString("2cola"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("cabbage", 2.4, defaultUnit), pue.getUnitsProductFromString("2.4   cabbage"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("cranberries", 49, defaultUnit), pue.getUnitsProductFromString("forty nine cranberries"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("cola", 2, defaultUnit), pue.getUnitsProductFromString("   2 cola"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("cherry coke", 6, defaultUnit), pue.getUnitsProductFromString("six cherry coke"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("skinless boneless chicken breasts", 3, defaultUnit), pue.getUnitsProductFromString("3 skinless, boneless chicken breasts"));
    }

    @Test
    public void testCaseFractions() throws Exception {
        ProductUnitExtractor pue = new ProductUnitExtractor();
        //ProductUnitExtractor.QuantityUnitPackage qup = pue.getUnitsProductFromString("one carrot");
        String defaultUnit = "";
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("sugar", .25, "cup"), pue.getUnitsProductFromString("1/4 cups of sugar"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("carrot", .5, defaultUnit), pue.getUnitsProductFromString("1 / 2  carrot"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("carrot", 1.5, defaultUnit), pue.getUnitsProductFromString("1 1/2  carrot"));
        assertEquals("Quantity unit product test failed.", pue.getQuantityUnitsClass("apples", 11.5, "ounce"), pue.getUnitsProductFromString("11 1/2 ounce apples"));
    }

}