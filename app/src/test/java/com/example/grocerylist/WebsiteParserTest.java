
package com.example.grocerylist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
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
    public void allrecipes() throws Exception {

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
            assertEquals(fd.ingredients.get(i),ingredientExpect.get(i));


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

        // seaseme chicken.
        fd=wp.parseSite("http://allrecipes.com/recipe/61071/addictive-sesame-chicken/?internalSource=hub%20recipe&referringContentType=search%20results&clickId=cardslot%204");
        recipeExpect = new Recipe();
        recipeExpect.put("NAME", "Addictive Sesame Chicken");
        ingredientExpect = new ArrayList<Ingredients>();
        ingredientExpect.add(new Ingredients("2 tablespoons soy sauce"));
        ingredientExpect.add(new Ingredients("1 tablespoon dry sherry"));
        ingredientExpect.add(new Ingredients("1 dash sesame oil"));
        ingredientExpect.add(new Ingredients("2 tablespoons all-purpose flour"));
        ingredientExpect.add(new Ingredients("2 tablespoons cornstarch"));
        ingredientExpect.add(new Ingredients("2 tablespoons water"));
        ingredientExpect.add(new Ingredients("1/4 teaspoon baking powder"));
        ingredientExpect.add(new Ingredients("1/4 teaspoon baking soda"));
        ingredientExpect.add(new Ingredients("1 teaspoon canola oil"));
        ingredientExpect.add(new Ingredients("4 (5 ounce) skinless, boneless chicken breast halves, cut into 1-inch cubes"));
        ingredientExpect.add(new Ingredients("1 quart vegetable oil for frying"));
        ingredientExpect.add(new Ingredients("1/2 cup water"));
        ingredientExpect.add(new Ingredients("1 cup chicken broth"));
        ingredientExpect.add(new Ingredients("1/4 cup distilled white vinegar"));
        ingredientExpect.add(new Ingredients("1/4 cup cornstarch"));
        ingredientExpect.add(new Ingredients("1 cup white sugar"));
        ingredientExpect.add(new Ingredients("2 tablespoons soy sauce"));
        ingredientExpect.add(new Ingredients("2 tablespoons sesame oil"));
        ingredientExpect.add(new Ingredients("1 teaspoon red chile paste (such as Thai Kitchen&#174;)"));
        ingredientExpect.add(new Ingredients("1 clove garlic, minced"));
        ingredientExpect.add(new Ingredients("2 tablespoons toasted sesame seeds"));
        for (int i=0; i<fd.ingredients.size(); i++)
            assertEquals(fd.ingredients.get(i), ingredientExpect.get(i));

        recipeExpect.put("INSTRUCTIONS",
                "Combine the 2 tablespoons soy sauce, the dry sherry, dash of sesame oil, flour, 2 tablespoons cornstarch, 2 tablespoons water, baking powder, baking soda, and canola oil in a large bowl. Mix well; stir in the chicken. Cover and refrigerate for 20 minutes.\n" +
                        "Heat oil in a deep-fryer or large saucepan to 375 degrees F (190 degrees C).\n" +
                        "Combine the 1/2 cup water, chicken broth, vinegar, 1/4 cup cornstarch, sugar, 2 tablespoons soy sauce, 2 tablespoons sesame oil, red chili paste, and garlic in a small saucepan. Bring to a boil, stirring constantly. Turn heat to low and keep warm, stirring occasionally.\n" +
                        "Fry the marinated chicken in batches until cooked through and golden brown, 3 to 5 minutes. Drain on paper towels.\n" +
                        "Transfer the chicken to a large platter, top with sauce, and sprinkle with sesame seeds.\n");
        recipeExpect.put("SERVINGS", 4);
        assertEquals(recipeExpect,fd.recipe);
    }

    @Test
    public void weightWatchers() throws Exception {

        //WebsiteParser wp = new WebsiteParser();
        WebsiteParser wp = new WebsiteParser();
        WebsiteParser.FormattedData fd = wp.parseSite("https://www.weightwatchers.com/us/recipe/baked-ziti-turkey-sausage-1/5626a601a6d5b396106ff961");
        // Check parsed data is correct from website.
        Recipe recipeExpect = new Recipe();
        List<Ingredients> ingredientExpect = new ArrayList<Ingredients>();
        ingredientExpect.add(new Ingredients("3/4 pound(s) uncooked turkey sausage(s), spicy-variety, casings removed"));
        ingredientExpect.add(new Ingredients("1 medium uncooked onion(s), chopped"));
        ingredientExpect.add(new Ingredients("1 medium green pepper(s), chopped"));
        ingredientExpect.add(new Ingredients("28 oz canned diced tomatoes"));
        ingredientExpect.add(new Ingredients("10 oz frozen green peas, thawed"));
        ingredientExpect.add(new Ingredients("2 Tbsp canned tomato paste"));
        ingredientExpect.add(new Ingredients("1 tsp dried oregano"));
        ingredientExpect.add(new Ingredients("1 tsp dried basil"));
        ingredientExpect.add(new Ingredients("1/2 tsp dried thyme"));
        ingredientExpect.add(new Ingredients("1/2 tsp fennel seed"));
        ingredientExpect.add(new Ingredients("1/2 tsp table salt, or to taste"));
        ingredientExpect.add(new Ingredients("1/2 tsp black pepper, freshly ground"));
        ingredientExpect.add(new Ingredients("12 oz uncooked whole wheat pasta, ziti, cooked according to package directions"));
        ingredientExpect.add(new Ingredients("6 oz shredded part-skim mozzarella cheese"));
        for (int i=0; i<fd.ingredients.size(); i++)
            assertEquals(fd.ingredients.get(i), ingredientExpect.get(i));
        recipeExpect.put("NAME", "Baked Ziti with Turkey Sausage");
        recipeExpect.put("INSTRUCTIONS",
                "Position the rack in the center of the oven and preheat the oven to 350°F.\n" +
                        "Crumble the sausage meat into a large saucepan and brown over medium heat, stirring often, about 4 minutes.\n" +
                        "Drain off any fat, then add the onion and bell pepper. Cook, stirring often, until softened, about 3 minutes.\n" +
                        "Stir in the tomatoes, peas, tomato paste, oregano, basil, thyme, fennel seeds, salt and pepper. Bring to a simmer, then reduce the heat and cook uncovered 5 minutes, stirring often.\n" +
                        "Stir in the cooked pasta and half the cheese. Spread evenly into a 9- X 13-inch baking pan. Top evenly with the remaining cheese.\n" +
                        "Bake until the cheese has melted and the casserole is bubbling, about 20 minutes. Let stand 10 minutes at room temperature before slicing into 8 pieces. Yields 1 piece per serving.\n");
        recipeExpect.put("SERVINGS", 8);
        assertEquals(recipeExpect,fd.recipe);

        fd = wp.parseSite("https://www.weightwatchers.com/us/recipe/cashew-chicken-1/5626a5eca6d5b396106fe961");
        ingredientExpect = new ArrayList<Ingredients>();
        ingredientExpect.add(new Ingredients("2 tsp peanut oil"));
        ingredientExpect.add(new Ingredients("2 clove(s), medium garlic clove(s), minced"));
        ingredientExpect.add(new Ingredients("1 pound(s) uncooked boneless skinless chicken breast(s), cut into 1-inch cubes"));
        ingredientExpect.add(new Ingredients("1/2 tsp table salt, or more to taste"));
        ingredientExpect.add(new Ingredients("1/4 tsp black pepper, or more to taste"));
        ingredientExpect.add(new Ingredients("1 1/2 cup(s) fat-free reduced sodium chicken broth, divided"));
        ingredientExpect.add(new Ingredients("2 Tbsp low sodium soy sauce, or more to taste"));
        ingredientExpect.add(new Ingredients("2 rib(s), medium uncooked celery, chopped"));
        ingredientExpect.add(new Ingredients("8 oz canned bamboo shoots, drained"));
        ingredientExpect.add(new Ingredients("8 oz canned water chestnut(s), sliced, drained"));
        ingredientExpect.add(new Ingredients("1 1/2 Tbsp cornstarch"));
        ingredientExpect.add(new Ingredients("2 cup(s) cooked white rice, kept hot"));
        ingredientExpect.add(new Ingredients("1 3/4 oz unsalted dry-roasted cashews, chopped (about 6 Tbsp)"));
        for (int i=0; i<fd.ingredients.size(); i++)
            assertEquals(fd.ingredients.get(i), ingredientExpect.get(i));
        recipeExpect.put("NAME", "Cashew Chicken");
        recipeExpect.put("INSTRUCTIONS",
                "Heat oil in a large skillet over medium-high heat. Add garlic and cook for 30 seconds. Season chicken on both sides with salt and pepper and add to skillet. Cook until browned on all sides, stirring frequently, about 4 minutes.\n" +
                        "Add 1 cup of broth, soy sauce, celery, bamboo shoots and water chestnuts to chicken and bring to a simmer. Reduce heat to low, cover and simmer until chicken is cooked through, about 5 minutes.\n" +
                        "Dissolve cornstarch in remaining 1/2 cup of broth; add to skillet and simmer until sauce thickens, stirring constantly, about 1 minute.\n" +
                        "To serve, divide rice among 4 shallow dishes. Spoon chicken mixture onto rice and sprinkle with cashews. Yields about 1 cup of chicken, 1/2 cup of rice and 1 1/2 tablespoons of cashews per serving.\n");
        recipeExpect.put("SERVINGS", 4);
        assertEquals(recipeExpect,fd.recipe);
    }

    @Before
    public void initialize() {
        WordList.init(RuntimeEnvironment.application);
    }

    @Test
    public void generic() throws Exception {



        //WebsiteParser wp = new WebsiteParser();
        WebsiteParser wp = new WebsiteParser();
        WebsiteParser.FormattedData fd = wp.parseSite("http://www.mccormick.com/recipes/main-dishes/ultimate-macaroni-and-cheese");
        // Check parsed data is correct from website.
        Recipe recipeExpect = new Recipe();
        List<Ingredients> ingredientExpect = new ArrayList<Ingredients>();
        ingredientExpect.add(new Ingredients("1 package elbow macaroni"));
        ingredientExpect.add(new Ingredients("6 tablespoons butter"));
        ingredientExpect.add(new Ingredients("6 tablespoons flour"));
        ingredientExpect.add(new Ingredients("2 tablespoons McCormick&#174; Mustard"));
        ingredientExpect.add(new Ingredients("1 1/2 teaspoons McCormick&#174; Black Pepper"));
        ingredientExpect.add(new Ingredients("1 teaspoon McCormick&#174; Garlic Powder"));
        ingredientExpect.add(new Ingredients("1 teaspoon salt"));
        ingredientExpect.add(new Ingredients("4 cups milk"));
        ingredientExpect.add(new Ingredients("6 cups shredded sharp Cheddar cheese"));
        ingredientExpect.add(new Ingredients("1 1/2 cups panko bread crumbs"));
        ingredientExpect.add(new Ingredients("1 teaspoon McCormick&#174; Paprika"));
        for (int i=0; i<fd.ingredients.size(); i++)
            assertEquals(ingredientExpect.get(i), fd.ingredients.get(i));
        recipeExpect.put("NAME", "Ultimate Macaroni & Cheese Recipe | McCormick");
        recipeExpect.put("INSTRUCTIONS",
                "Position the rack in the center of the oven and preheat the oven to 350°F.\n" +
                        "Crumble the sausage meat into a large saucepan and brown over medium heat, stirring often, about 4 minutes.\n" +
                        "Drain off any fat, then add the onion and bell pepper. Cook, stirring often, until softened, about 3 minutes.\n" +
                        "Stir in the tomatoes, peas, tomato paste, oregano, basil, thyme, fennel seeds, salt and pepper. Bring to a simmer, then reduce the heat and cook uncovered 5 minutes, stirring often.\n" +
                        "Stir in the cooked pasta and half the cheese. Spread evenly into a 9- X 13-inch baking pan. Top evenly with the remaining cheese.\n" +
                        "Bake until the cheese has melted and the casserole is bubbling, about 20 minutes. Let stand 10 minutes at room temperature before slicing into 8 pieces. Yields 1 piece per serving.\n");
        recipeExpect.put("SERVINGS", 8);
        assertEquals(recipeExpect,fd.recipe);

    }
}