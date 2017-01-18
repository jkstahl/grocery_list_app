package com.example.grocerylist;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by neoba on 1/17/2017.
 */

public class ProductUnitExtractor {
    private Pattern numberWordParserSearch;
    private Pattern quantityProductPattern;
    private String[] unitsUsable;
    private String[] unitsNotUsable;
    private Pattern quantityUnitsPattern;
    private Map<String, Integer> wordToNumberMap;
    private Pattern numberWordSearch;
    private final String TAG="unitextractor";

    public ProductUnitExtractor() {
        unitsUsable = new String[] {"each", "ounce", "pound", "quart", "pint","cup", "teaspoon", "tablespoon", "liter", "gram", "miligram", "liter", "mililiter", "roll", "can","slice","stick"};
        unitsNotUsable = new String[] {"ea[ .]", "oz[ .]", "lbs[ .]", "pt[ .]", "qt[ .]", "l[ .]", "ml[ .]", "g[ .]", "mg[ .]"};

        // initialize some of the pattern info
        //Add plural searching
        String[] usableUnitsPlural = new String[unitsUsable.length];
        for (int i=0; i<unitsUsable.length; i++)
            usableUnitsPlural[i] = unitsUsable[i] + "[s]?";

        String unitSearch = join(usableUnitsPlural, "|");
        unitSearch += ("|" + join(unitsNotUsable, "|"));
        String quantityUnitsString = "(?i)([0-9]+[.][0-9]+|[0-9]+|[.][0-9]+|[0-9]+[.])\\s*(" + unitSearch+ ")";
        quantityUnitsPattern = Pattern.compile(quantityUnitsString);
        String quantityProductString = "^(?i)([0-9]+[.][0-9]+|[0-9]+|[.][0-9]+|[0-9]+[.])\\s+([a-zA-Z. ]+)";
        quantityProductPattern = Pattern.compile(quantityProductString);

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
        String numberWordPatternString = "^(" + join(numberWords, " *|") + " *)+";
        numberWordSearch = Pattern.compile(numberWordPatternString);
        String numberWordParserPatternString = "(" + join(numberWords, " *|") + " *)";
        numberWordParserSearch = Pattern.compile(numberWordParserPatternString);

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
        // TODO words to numbers
        // TODO a = 1 or a couple = 2
        // TODO handle the word of
        // Check that number is real
        // handle plurals.
        QuantityUnitPackage returnPackage = new QuantityUnitPackage();
        // trim the space
        String workingString = productString.trim();
        //remove all non alpha or numeric characters.
        workingString = workingString.replaceAll("[^a-zA-Z0-9. ]", "");
        returnPackage.product=workingString;

        String saveWorkingString = workingString;
        workingString = wordsToNumbers(workingString);
        Matcher match = quantityUnitsPattern.matcher(workingString);

        if (match.find()) {
            // found so let get the location
            int startFind = match.start();
            int endFind = match.end();
            String quantity = match.group(1);
            String units = match.group(2);
            returnPackage.quantity = Double.parseDouble(quantity);
            returnPackage.units = units;

            int productStart = 0;
            int productEnd = startFind;
            if (startFind - 0 <= (workingString.length() - 1)- endFind) {
                productStart = endFind;
                productEnd = workingString.length();
            }
            String product = workingString.substring(productStart, productEnd);
            returnPackage.product = product.trim();
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
        Matcher m = numberWordSearch.matcher(workingString);
        String numberSequence="";
        if (m.find()) {
            numberSequence = m.group().trim();

        } else {
            return workingString;
        }
        Log.d(TAG, numberSequence);
        m = numberWordParserSearch.matcher(numberSequence);
        int number = 0;
        while (m.find()) {
            String numberWord = m.group().trim();
            if (wordToNumberMap.containsKey(numberWord))
                number += wordToNumberMap.get(numberWord);
        }
        workingString = workingString.replace(numberSequence, "" + number );
        return  workingString;
    }

    public class QuantityUnitPackage {
        public String product=null;
        public double quantity = 1.0;
        public String units="";

    }
}
