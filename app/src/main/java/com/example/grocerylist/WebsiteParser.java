package com.example.grocerylist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line=null;
            int drillBitIndex=0;
            String fullText = "";
            while ((line = in.readLine()) != null) {
                //Log.d(TAG, line);
                //if (line.contains("Somali Spaghetti Sauce"))
                //    System.out.println(line);
                fullText += line;
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
            }
        } catch (Exception e) {
            e.printStackTrace();
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
