package com.example.grocerylist;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by neoba on 1/18/2017.
 */
public class WebsiteParserTest {

    @Test
    public void basicWebsite() throws Exception {

        WebsiteParser wp = new WebsiteParser();
        //ProductUnitExtractor.QuantityUnitPackage qup = pue.getUnitsProductFromString("one carrot");
        wp.parseSite("http://allrecipes.com/recipe/246489/somali-spaghetti-sauce/?internalSource=staff%20pick&referringContentType=home%20page&clickId=cardslot%203");
        wp.parseSite("http://allrecipes.com/recipe/20045/cabbage-rolls-ii/?clickId=right%20rail%200&internalSource=rr_feed_recipe&referringId=20045&referringContentType=recipe");
    }


}