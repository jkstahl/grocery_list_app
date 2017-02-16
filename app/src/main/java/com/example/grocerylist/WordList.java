package com.example.grocerylist;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by neoba on 2/13/2017.
 */

public class WordList {
    private static Set<String> foodWordSet;
    private static String[] foodWordList;
    private static Set<String> cookingVerbSet;

    public static void init(Context context) {
        Resources res = context.getResources();
        foodWordList = res.getStringArray(R.array.food_word_list);
        foodWordSet = new HashSet<>();
        for (int i=0; i<foodWordList.length; i++) {
            foodWordSet.add(foodWordList[i].toLowerCase());
        }

        cookingVerbSet = new HashSet<>();
        String[] cookingVerbs = res.getStringArray(R.array.cooking_verbs_list);
        for (int i=0; i<cookingVerbs.length; i++) {
            cookingVerbSet.add(cookingVerbs[i].toLowerCase());
        }
    }

    public static Set<String> getFoodWordSet() {
        return foodWordSet;
    }

    public static Set<String> getCookingVerbs() {
        return cookingVerbSet;
    }
}
