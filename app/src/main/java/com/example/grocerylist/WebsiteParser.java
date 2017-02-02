package com.example.grocerylist;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by neoba on 1/31/2017.
 */

public class WebsiteParser {
    private final String TAG = "websiteparser";
    private Map<String,String[]> drillBits;
    private final int IMAGE_INDEX=0;
    private final int NAME_INDEX = 1;
    private final int INGREDIENTS_INDEX=2;
    private final int DIRECTIONS_INDEX=3;

    public WebsiteParser() {
        drillBits = new HashMap<>();
        drillBits.put("allrecipes.com",
                new String[]{
                        "<span class=\"recipe-ingred_txt added\" data-id=\"[0-9]+\" data-nameid=\"[0-9]+\" itemprop=\"ingredients\">(.*)</span>",
                        "<span class=\"recipe-directions__list--item\">(.*)</span></li>",
                        "\"recipeTitle\":\"([^\"]+)\"",
                        "\"recipeImageUrl\":\"([^\"]+)\""
        });
    }

    public FormattedData parseSite(String webpage) {
        FormattedData returnData = new FormattedData();
        HttpURLConnection urlConnection=null;

        boolean useDrillBit  = false;
        String drillBit = null;
        List<Pattern> drillBitList = new ArrayList<Pattern>();
        Pattern servingsPattern = Pattern.compile("(?i)(([0-9]{1,2}) Serving[s]?|Serving[s]?[:]? ([0-9]{1,2}))");
        for (String domain : drillBits.keySet()) {
            if (webpage.contains(domain)) {
                System.out.println("Found drill bit for domain: " + domain);
                useDrillBit = true;
                drillBit = domain;
                String[] bit =  drillBits.get(domain);
                for (int i=0; i<bit.length; i++)
                    drillBitList.add(Pattern.compile(bit[i]));
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
            while ((line = in.readLine()) != null) {
                //Log.d(TAG, line);
                //if (line.contains("Cabbage Rolls II"))
                //    System.out.println(line);

                if (useDrillBit) {
                    Matcher m;
                    int i = 0;
                    for (Pattern drillPattern : drillBitList) {
                        m = drillPattern.matcher(line);
                        if (m.find()) {
                            System.out.println("FOUND: " + m.group(1));
                            String foundData = m.group(1);
                            switch (i) {
                                case IMAGE_INDEX:
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
                                    returnData.recipe.put("DIRECTIONS", foundData);
                                    break;
                            }
                        }
                        i++;
                    }

                    // Find servings
                    m=servingsPattern.matcher(line);
                    if (m.find())
                        System.out.println("FOUND SERVINGS: " + m.group(0) + " number:" + m.group(2));

                    // Find recipe name
                }
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
        }
    }
}
