package com.example.grocerylist;

import java.util.List;

/**
 * Created by neoba on 2/3/2017.
 */
public interface WebpageLoaderCallback {
    public void webpadeLoadFinished(Recipe loadedRecipe, List<Ingredients> ingredientsList);
}
