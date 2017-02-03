package com.example.grocerylist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by neoba on 1/18/2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class WebsiteParserTest {

    @Test
    public void basicWebsite() throws Exception {

        //WebsiteParser wp = new WebsiteParser();
        WebsiteParser wp = new WebsiteParser();
        //ProductUnitExtractor.QuantityUnitPackage qup = pue.getUnitsProductFromString("one carrot");
        WebsiteParser.FormattedData fd = wp.parseSite("http://allrecipes.com/recipe/246489/somali-spaghetti-sauce/?internalSource=staff%20pick&referringContentType=home%20page&clickId=cardslot%203");
        // Check parsed data is correct from website.
        Recipe recipeExpect = new Recipe();
        recipeExpect.put("NAME", "Somali Spaghetti Sauce");
        recipeExpect.put("INSTRUCTIONS",
                         "Place cilantro and garlic in a food processor; pulse until finely chopped.\n" +
                         "Toast cumin seeds in a small pot over low heat until fragrant, 2 to 3 minutes. Allow to cool, 5 minutes. Grind into a powder.\n" +
                         "Transfer ground cumin to a small bowl. Add Himalayan pink salt, turmeric, paprika, Italian seasoning, seasoning blend, and black pepper to make seasoning mix.\n" +
                         "Heat oil in a large pot over medium heat. Add ground beef; cook and stir until browned, about 5 minutes. Stir in 1/2 of the seasoning mix. Add onion; cook and stir until softened, about 5 minutes. Add potato and carrots. Cook, covered, stirring occasionally, until slightly softened, about 5 minutes.\n" +
                         "Stir tomatoes into the pot and bring sauce to a boil. Reduce heat to medium-low; stir in cilantro-garlic mixture and remaining 1/2 of the seasoning mix. Simmer, stirring occasionally, until flavors combine, 30 to 40 minutes. Thin sauce with water if it seems too thick.\n" +
                         "Bring a large pot of lightly salted water to a boil. Cook angel hair in the boiling water, stirring occasionally until tender yet firm to the bite, 4 to 5 minutes. Drain. Serve sauce over pasta.\n");
        recipeExpect.put("SERVINGS", 6);
        assertEquals(recipeExpect,fd.recipe);
        // check ingredients list
        List<Ingredients> ingredientExpect = new ArrayList<Ingredients>();
        ingredientExpect.add(new Ingredients("2 tablespoons chopped fresh cilantro"));
        ingredientExpect.add(new Ingredients("4 cloves garlic"));
        ingredientExpect.add(new Ingredients("1 teaspoon cumin seeds"));
        ingredientExpect.add(new Ingredients("1 teaspoon Himalayan pink salt"));
        ingredientExpect.add(new Ingredients("1 teaspoon ground turmeric"));
        ingredientExpect.add(new Ingredients("1 teaspoon paprika"));
        ingredientExpect.add(new Ingredients("1 teaspoon Italian seasoning"));
        ingredientExpect.add(new Ingredients("1 teaspoon salt-free seasoning blend (such as Mrs. Dash&#174;)"));
        ingredientExpect.add(new Ingredients("1/2 teaspoon ground black pepper"));
        ingredientExpect.add(new Ingredients("1/4 cup olive oil"));
        ingredientExpect.add(new Ingredients("1 pound lean ground beef"));
        ingredientExpect.add(new Ingredients("1 onion, diced"));
        ingredientExpect.add(new Ingredients("1 large potato, peeled and cubed"));
        ingredientExpect.add(new Ingredients("2 carrots, diced"));
        ingredientExpect.add(new Ingredients("2 (14 ounce) cans diced tomatoes"));
        ingredientExpect.add(new Ingredients("2 tablespoons water, or to taste (optional)"));
        ingredientExpect.add(new Ingredients("1 (16 ounce) package angel hair pasta"));
        for (int i=0; i<fd.ingredients.size(); i++)
            assertEquals(ingredientExpect.get(i), fd.ingredients.get(i));


        fd=wp.parseSite("http://allrecipes.com/recipe/20045/cabbage-rolls-ii/?clickId=right%20rail%200&internalSource=rr_feed_recipe&referringId=20045&referringContentType=recipe");
        recipeExpect = new Recipe();
        recipeExpect.put("NAME", "Cabbage Rolls II");
        recipeExpect.put("INSTRUCTIONS",
                         "Bring a large pot of water to a boil. Boil cabbage leaves 2 minutes; drain.\n" +
                         "In large bowl, combine 1 cup cooked rice, egg, milk, onion, ground beef, salt, and pepper. Place about 1/4 cup of meat mixture in center of each cabbage leaf, and roll up, tucking in ends. Place rolls in slow cooker.\n" +
                         "In a small bowl, mix together tomato sauce, brown sugar, lemon juice, and Worcestershire sauce. Pour over cabbage rolls.\n" +
                         "Cover, and cook on Low 8 to 9 hours.\n");
        recipeExpect.put("SERVINGS", 6);
        assertEquals(recipeExpect,fd.recipe);
        // check ingredients list.

        fd=wp.parseSite("http://allrecipes.com/recipe/19489/maryland-crab-cakes-ii/?clickId=right%20rail%204&internalSource=rr_feed_recipe&referringId=19489&referringContentType=recipe");
        recipeExpect = new Recipe();
        recipeExpect.put("NAME", "Maryland Crab Cakes II");
        recipeExpect.put("INSTRUCTIONS",
                "Preheat oven broiler.\n" +
                        "Mix together crabmeat, bread crumbs, parsley, salt and pepper.\n" +
                        "Beat together egg, mayonnaise, hot sauce and mustard. Combine with other ingredients and mix well. Form into patties and place on a lightly greased broiler pan or baking sheet.\n" +
                        "Broil for 10 to 15 minutes, until lightly brown.\n");
        recipeExpect.put("SERVINGS", 4);
        assertEquals(recipeExpect,fd.recipe);
    }


}