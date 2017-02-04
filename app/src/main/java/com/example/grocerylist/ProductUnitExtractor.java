package com.example.grocerylist;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by neoba on 1/17/2017.
 */

public class ProductUnitExtractor {
    private Pattern fractionParser;
    private Pattern numberWordParserSearch;
    private Pattern quantityProductPattern;
    private static String[] unitsUsable;
    private String[] unitsNotUsable;
    private Pattern quantityUnitsPattern;
    private Map<String, Integer> wordToNumberMap;
    private Map<String, String[]> unitsToCommonMap;
    private Map<String, String> commonToUnitsMap;
    private Pattern numberWordSearch;
    private final String TAG="unitextractor";

    public ProductUnitExtractor() {
        //unitsUsable = new String[] {"each", "ounce", "pound", "quart", "pint","cup", "teaspoon", "tablespoon", "liter", "gram", "miligram", "liter", "mililiter", "roll", "can","slice","stick", "pack"};
        //unitsNotUsable = new String[] {"ea[ .]", "oz[ .]", "lbs[ .]", "pt[ .]", "qt[ .]", "l[ .]", "ml[ .]", "g[ .]", "mg[ .]"};

        // initialize some of the pattern info




        // Initialize number word to number
        wordToNumberMap = new HashMap<String, Integer>();
            wordToNumberMap.put("one", 1);
            wordToNumberMap.put("two", 2);
            wordToNumberMap.put("three", 3);
            wordToNumberMap.put("four", 4);
            wordToNumberMap.put("five", 5);
            wordToNumberMap.put("six", 6);
            wordToNumberMap.put("seven", 7);
            wordToNumberMap.put("eight", 8);
            wordToNumberMap.put("nine", 9);
            wordToNumberMap.put("ten", 10);
            wordToNumberMap.put("eleven", 11);
            wordToNumberMap.put("twelve", 12);
            wordToNumberMap.put("thirteen", 13);
            wordToNumberMap.put("fourteen", 14);
            wordToNumberMap.put("fifteen", 15);
            wordToNumberMap.put("sixteen", 16);
            wordToNumberMap.put("seventeen", 17);
            wordToNumberMap.put("eighteen", 18);
            wordToNumberMap.put("nineteen", 19);
            wordToNumberMap.put("twenty", 20);
            wordToNumberMap.put("thirty", 30);
            wordToNumberMap.put("forty", 40);
            wordToNumberMap.put("fifty", 50);
            wordToNumberMap.put("sixty", 60);
            wordToNumberMap.put("seventy", 70);
            wordToNumberMap.put("eighty", 80);
            wordToNumberMap.put("ninety", 90);
            wordToNumberMap.put("onehundred", 100);

        // build key array
        wordToNumberMap.keySet();
        String[] numberWords = new String[wordToNumberMap.size()];
        int i = 0;
        for (String numberWord : wordToNumberMap.keySet()) {
            numberWords[i]= numberWord;
            i+=1;
        }

        // build the pattern
        String fractionRegex = "[0-9]+[ ]?/[ ]?[0-9]+";
        String numberWordPatternString = "(?i)(^| )(" + join(numberWords, " *|") + " *|"+fractionRegex+")+ ";
        numberWordSearch = Pattern.compile(numberWordPatternString);
        String numberWordParserPatternString = "(?i)(" + join(numberWords, " *|") + " *)";
        numberWordParserSearch = Pattern.compile(numberWordParserPatternString);
        String fractionParserString = fractionRegex;
        fractionParser = Pattern.compile(fractionParserString);

        // build map of units that we should be using.
        unitsToCommonMap = new HashMap<String, String[]>();
        unitsToCommonMap.put("each", new String[] {"ea", "ea."});
        unitsToCommonMap.put("ounce",new String[] {"oz", "oz."});
        unitsToCommonMap.put("pound",new String[] {"lbs", "lbs."});
        unitsToCommonMap.put("quart", new String[] {"qt", "qt."});
        unitsToCommonMap.put("pint", new String[] {"pt", "pt."});
        unitsToCommonMap.put("cup",new String[] {});
        unitsToCommonMap.put("teaspoon",new String[] {"tsp", "tsp."});
        unitsToCommonMap.put("tablespoon",new String[] {"tbsp", "tbsp."});
        unitsToCommonMap.put("liter",new String[] {"l", "l."});
        unitsToCommonMap.put("gram",new String[] {"g", "g."});
        unitsToCommonMap.put("miligram",new String[] {"mg", "mg."});
        unitsToCommonMap.put("mililiter",new String[] {"ml", "ml."});
        unitsToCommonMap.put("roll",new String[] {});
        unitsToCommonMap.put("can",new String[] {});
        unitsToCommonMap.put("slice",new String[] {"sl", "sl."});
        unitsToCommonMap.put("stick",new String[] {});
        unitsToCommonMap.put("pack",new String[] {});
        unitsToCommonMap.put("box",new String[] {"boxes"});

        List<String> unitsUsableList = new LinkedList<String>();
        List<String> unitsNotUsableList = new LinkedList<String>();
        commonToUnitsMap = new HashMap<String , String>();
        for (String common : unitsToCommonMap.keySet()){
            unitsUsableList.add(common);
            String[] abreve = unitsToCommonMap.get(common);
            commonToUnitsMap.put(common + "s", common);
            for(int j=0; j<abreve.length; j++){
                commonToUnitsMap.put(abreve[j], common);
                unitsNotUsableList.add(abreve[j]);
            }
        }

        //Add plural searching
        unitsUsable = (String[]) unitsUsableList.toArray(new String[unitsUsableList.size()]);
        unitsNotUsable = (String[]) unitsNotUsableList.toArray(new String[unitsNotUsableList.size()]);

        String[] usableUnitsPlural = new String[unitsUsable.length];
        for (int i2=0; i2<unitsUsable.length; i2++)
            usableUnitsPlural[i2] = unitsUsable[i2] + "[s]?";

        String unitSearch = join(usableUnitsPlural, "|");
        unitSearch += ("|" + join(unitsNotUsable, "[ ]+|"));
        String numberRegex = "[0-9]+[.][0-9]+|[0-9]+|[.][0-9]+|[0-9]+[.]|[0-9]+/[0-9]+";
        String quantityUnitsString = "(?i)("+ numberRegex +")\\s*(" + unitSearch+ "[ ]+)";
        quantityUnitsPattern = Pattern.compile(quantityUnitsString);
        String quantityProductString = "^(?i)(" + numberRegex +")\\s+([a-zA-Z. ]+)";
        quantityProductPattern = Pattern.compile(quantityProductString);
    }

    public static String[] getAllUnits() {
        return unitsUsable;
    }

    private String join(String[] array, String joinString) {
        String returnString = "";
        if (array.length > 0)
            returnString += array[0];
        for (int i=1; i<array.length; i++){
            returnString += (joinString + array[i]);
        }

        return returnString;
    }
    
    

    public QuantityUnitPackage getUnitsProductFromString(String productString) {
        // Case insensative
        // TODO a = 1 or a couple = 2
        // handle the word of
        // TODO units only
        // Only use common units
        // Check that number is real
        // handle plurals.
        QuantityUnitPackage returnPackage = new QuantityUnitPackage();
        // trim the space
        String workingString = productString.trim();
        //remove all non alpha or numeric characters.
        workingString = workingString.replaceAll("[^a-zA-Z0-9. /']", "");
        returnPackage.product=workingString;

        String saveWorkingString = workingString;
        workingString = wordsToNumbers(workingString);
        Matcher match = quantityUnitsPattern.matcher(workingString);

        if (match.find()) {
            // found so let get the location
            int startFind = match.start();
            int endFind = match.end();
            String quantity = match.group(1).trim();
            String units = match.group(2).trim().toLowerCase();
            returnPackage.quantity = Double.parseDouble(quantity);
            returnPackage.units = units;
            if (!unitsToCommonMap.containsKey(units))
                returnPackage.units = commonToUnitsMap.get(units);

            int productStart = 0;
            int productEnd = startFind;
            if (startFind - 0 <= (workingString.length() - 1)- endFind) {
                productStart = endFind;
                productEnd = workingString.length();
            }
            String product = workingString.substring(productStart, productEnd);
            returnPackage.product = product.trim().replaceFirst("^of ", "");
        } else { // not in the format of quantity unit product.  Need to see if just quantity product.
            match = quantityProductPattern.matcher(workingString);

            if (match.find()) {
                returnPackage.quantity = Double.parseDouble(match.group(1));
                returnPackage.product = match.group(2).trim();

            } else {
                workingString = saveWorkingString;
            }
        }

        return returnPackage;
    }

    private String wordsToNumbers(String workingString) {
        // TODO check for 1 1/4 type number
        Matcher m = numberWordSearch.matcher(workingString);
        String numberSequence="";
        if (m.find()) {
            numberSequence = m.group().trim();

        } else {
            return workingString;
        }
        //Log.d(TAG, numberSequence);

        // check for fractions
        m = fractionParser.matcher(numberSequence);
        double number =0;
        if (m.find()) {
            String[] numerDenomer = m.group().split("[ ]?/[ ]?");
            double numerator = Double.parseDouble(numerDenomer[0].trim());
            double denominator = Double.parseDouble(numerDenomer[1].trim());
            if (denominator == 0)
                number = 0;
            else
                number = numerator/denominator;
        }else {
            m = numberWordParserSearch.matcher(numberSequence);
            while (m.find()) {
                String numberWord = m.group().trim();
                String numberLower = numberWord.toLowerCase();
                if (wordToNumberMap.containsKey(numberLower))
                    number += wordToNumberMap.get(numberLower);
            }
        }
        workingString = workingString.replace(numberSequence, "" + number );

        return  workingString;
    }

    public QuantityUnitPackage getQuantityUnitsClass(String carrot, double i, String each) {
        return  new QuantityUnitPackage(carrot, i, each);
    }

    public class QuantityUnitPackage {
        public String product=null;
        public double quantity = 1.0;
        public String units="";

        public QuantityUnitPackage() {

        }

        public QuantityUnitPackage(String product, double quantity, String units) {
            this.product = product;
            this.quantity = quantity;
            this.units = units;
        }

        @Override
        public boolean equals(Object o) {
            QuantityUnitPackage q = (QuantityUnitPackage) o;
            return q.product.equals(this.product) && q.quantity == this.quantity && q.units.equals(this.units);
        }

        @Override
        public String toString() {
            return "Product: " + this.product + "\nQuantity: " + this.quantity + "\nUnits: " + this.units;
        }
    }
}
