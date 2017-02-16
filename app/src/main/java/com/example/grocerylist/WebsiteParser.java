package com.example.grocerylist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by neoba on 1/31/2017.
 */

public class WebsiteParser {
    private String TAG = "websiteparser";
    private Map<String,DrillBit> drillBits;
    private final int IMAGE_INDEX=0;
    private final int NAME_INDEX = 1;
    private final int INGREDIENTS_INDEX=2;
    private final int DIRECTIONS_INDEX=3;
    private final int SERVINGS_INDEX=4;


    public WebsiteParser() {
        drillBits = new HashMap<>();
        drillBits.put("allrecipes.com",new DrillBit(1,
                new String[]{
                        "\"recipeImageUrl\":\"([^\"]+)\"",
                        "<h1 class=\"recipe-summary__h1\" itemprop=\"name\">(.*?)</h1>",
                        "<span class=\"recipe-ingred_txt added\" data-id=\"[0-9]+\" data-nameid=\"[0-9]+\" itemprop=\"ingredients\">(.*?)</span>",
                        "<span class=\"recipe-directions__list--item\">(.*?)</span></li>"
        }));
        drillBits.put("weightwatchers.com",new DrillBit(2,
                new String[]{
                        "<meta itemprop=\"image\" content=\"(.*)\"></meta>",
                        "<h1 class=\"detail-masthead__title\" itemprop=\"name\">(.*)</h1>",
                        "<li class=\"detail-list__item\" itemprop=\"recipeIngredient\">.*?<span>(.*?)</span>",
                        "[ ]+<li class=\"detail-list__item-ordered\" >[ ]+(.*?)[ ]+</li>(?=.*</ol>)",
                        "<div class=\"detail-ico-list-item__value\" itemprop=\"recipeYield\">[ ]+([0-9]{1,2})"
                }));
    }

    public FormattedData parseSite(String webpage) {
        Pattern ingredientFinderPattern = (new ProductUnitExtractor()).getQuantityProductPattern();
        FormattedData returnData = new FormattedData();
        HttpURLConnection urlConnection=null;
        ProductUnitExtractor pue = new ProductUnitExtractor();

        boolean useDrillBit  = false;
        boolean foundSerings=false;

        String drillBit = null;
        List<Pattern> drillBitList = new ArrayList<Pattern>();
        List<String> drillLines = new LinkedList<>();
        int numDrillLines = 0;

        Pattern servingsPattern = Pattern.compile("(?i)(([0-9]{1,2}) Serving[s]?|Serving[s]?[:]? ([0-9]{1,2})|serves ([0-9]{1,2}))");
        for (String domain : drillBits.keySet()) {
            if (webpage.contains(domain)) {
                System.out.println("Found drill bit for domain: " + domain);
                useDrillBit = true;
                drillBit = domain;
                String[] bit =  drillBits.get(domain).drillString;
                for (int i=0; i<bit.length; i++)
                    drillBitList.add(Pattern.compile(bit[i]));
                numDrillLines = drillBits.get(domain).bufferLines;
            }
        }

        try {
            URL url = new URL(webpage);
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line=null;
            int drillBitIndex=0;
            String fullText = "";
            String fullTextLines = "";
            while ((line = in.readLine()) != null) {
                //Log.d(TAG, line);
                //if (line.contains("Somali Spaghetti Sauce"))
                //    System.out.println(line);
                fullText += line;
                fullTextLines += line + "\n";
            }
            if (useDrillBit) {
                Matcher m;
                int i = 0;

                for (Pattern drillPattern : drillBitList) {
                    m = drillPattern.matcher(fullText);
                    while (m.find()) {
                        System.out.println("FOUND: " + m.group(1));
                        String foundData = m.group(1).replaceAll("<([/]?[a-zA-Z0-9])+>", "");
                        switch (i) {
                            case IMAGE_INDEX:
                                try {
                                    URL url = new URL(foundData);
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    InputStream input = connection.getInputStream();
                                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                    returnData.recipe.put("THUMBNAIL", DbBitmapUtility.getBytes(myBitmap));
                                } catch (IOException e) {
                                    // Log exception
                                    Log.e(TAG, "Bad image url.");
                                }
                                break;
                            case NAME_INDEX:
                                returnData.recipe.put("NAME", foundData);
                                break;
                            case INGREDIENTS_INDEX:
                                Ingredients newIngredient = new Ingredients();
                                newIngredient.put("NAME", foundData);
                                returnData.ingredients.add(newIngredient);
                                break;
                            case DIRECTIONS_INDEX:
                                String currectDirections = (String) returnData.recipe.get("INSTRUCTIONS");
                                returnData.recipe.put("INSTRUCTIONS", currectDirections + foundData + "\n");
                                break;
                            case SERVINGS_INDEX:
                                try {
                                    returnData.recipe.put("SERVINGS", Integer.parseInt(m.group(1)));
                                } catch (Exception e) {

                                }
                                break;
                        }
                    }
                    i++;

                }
                // Find servings
                m=servingsPattern.matcher(fullText);
                if (m.find() && !foundSerings) {
                    System.out.println("FOUND SERVINGS: " + m.group(0) + " number:" + m.group(2));
                    try {
                        returnData.recipe.put("SERVINGS", Integer.parseInt(m.group(2)));
                        foundSerings=true;
                    } catch (Exception e ) {

                    }
                }

                // Find recipe name
                 /*else {
                String linePure = fullText.replaceAll("<([/]?[a-zA-Z0-9])+>", "");
                Matcher m = ingredientFinderPattern.matcher(linePure);
                if (m.find()) {
                    System.out.println(fullText);
                }
            }*/
            } else {
                //String linePure = fullText.replaceAll("<([/]?[a-zA-Z0-9])+>", "");
                String linePure = fullTextLines;
                BufferedReader br = new BufferedReader(new StringReader(linePure));
                Set<String> foodNouns = new HashSet<>();
                List<Integer> ingredientLines=new ArrayList<>();

                try {

                    String removeChars = "\\n\"<>,.";
                    int lineNum = 0;
                    while ((line = br.readLine()) != null) {
                        // Build a list of words from the
                        List<String> wordsInLine = new ArrayList<>();
                        String[] words = line.split("[ ]+");
                        Set<String> foodWords = WordList.getFoodWordSet();
                        for (int i=0; i<words.length; i++) {
                            words[i] = words[i].replaceAll("(["+ removeChars+"]|s$)", "").toLowerCase();
                            //words[i] = words[i].replaceAll("s$", ""); // remove plural
                            if (foodWords.contains(words[i])) {
                                wordsInLine.add(words[i]);
                            }
                        }
                        String patternString = ProductUnitExtractor.join(wordsInLine.toArray(new String[wordsInLine.size()]), ".*");
                        patternString = pue.getFractionOrNumberPatternString() + ".*" + patternString + "[\\w]*]";
                        //String line2 = pue.wordsToNumbers(line);
                        String line2 = line;
                        //System.out.println(patternString);
                        Pattern numberUnitFoodPattern = Pattern.compile(patternString);
                        Matcher m = pue.getQuantityProductPattern().matcher(line);
                        //if (m.find())
                        //    System.out.println("Found");
                        if (wordsInLine.size() > 0) {
                            //System.out.println(wordsInLine.toString());
                            // do a search with quantity and units.
                            m = numberUnitFoodPattern.matcher(line2);
                            if (m.find()) {
                                String found = line.substring(m.start(), m.end() + (line.length()-line2.length()));
                                System.out.println(found.trim());
                                String ingredientName = found.trim();
                                Ingredients newIngredient = new Ingredients();
                                newIngredient.put("NAME", ingredientName);
                                returnData.ingredients.add(newIngredient);
                                foodNouns.addAll(wordsInLine);
                                ingredientLines.add(lineNum);
                            }
                        }
                        lineNum++;
                    }


                    // look for directions
                    br = new BufferedReader(new StringReader(linePure));
                    lineNum = 0;
                    Set<String> cookingVerbsSet = WordList.getCookingVerbs();
                    Pattern titlePattern = Pattern.compile("<title>(.*)</title>");
                    while ((line = br.readLine()) != null) {
                        Matcher match = titlePattern.matcher(line);
                        if (match.find())
                            returnData.recipe.put("NAME", match.group(1).trim().replaceAll("<[\\w\\d]+>", ""));
                        if (!ingredientLines.contains(lineNum)) {
                            String[] words = line.split("[ ]+");
                            int foodCount = 0;
                            int verbCount = 0;
                            for (int i = 0; i < words.length; i++) {
                                words[i] = words[i].replaceAll("[" + removeChars + "]", "").toLowerCase();
                                if (foodNouns.contains(words[i]))
                                    foodCount++;
                                else if (cookingVerbsSet.contains(words[i]))
                                    verbCount++;

                            }
                            if (foodCount >= 1 && verbCount>=1) {
                                System.out.println(line);
                                String current = (String)returnData.recipe.get("INSTRUCTIONS");
                                Pattern instructionClipper = Pattern.compile("[\\w\\d.,!;:\\-#]*"++"[\\w\\d.,!;:\\-#]*");
                                returnData.recipe.put("INSTRUCTIONS", current );
                            }
                        }
                        lineNum++;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            } catch (Exception e) {
            e.printStackTrace();
            try {
                urlConnection.disconnect();
            } catch (Exception e2) {

            }
            return null;
        } finally {
            urlConnection.disconnect();
        }

        return returnData;
    }

    public class FormattedData {
        public Recipe recipe;
        public List<Ingredients> ingredients;


        public FormattedData() {
            ingredients = new ArrayList<>();
            recipe = new Recipe();
            recipe.put("DIRECTIONS", "");
        }
    }

    private class DrillBit {
        public String[] drillString;
        public int bufferLines;
        public DrillBit(int i, String[] strings) {
            drillString = strings;
            bufferLines = i;
        }
    }
}
