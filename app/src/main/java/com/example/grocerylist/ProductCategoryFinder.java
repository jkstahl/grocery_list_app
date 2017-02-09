package com.example.grocerylist;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;


/**
 * Created by neoba on 2/8/2017.
 */

public class ProductCategoryFinder {
    private static ProductListDB db=null;
    private static final String TAG="productcategoryfinder";

    public static String getCategoryFromProductName(Context c, String name) {
        String category = "Uncategorized";

        if (db == null)
            db = DatabaseHolder.getDatabase(c);

        // Check past  uses of this name first
        Cursor cursor = db.getMostCommonCategory(name);
        if (cursor.moveToFirst()) {
            String returnCategory = cursor.getString(cursor.getColumnIndex("TYPE"));
            Log.d(TAG, returnCategory);
            cursor.close();
            return returnCategory;
        }
        cursor.close();

        // Next check the preloaded common.
        // Check past  uses of this name first
        /*
        cursor = db.getMostCommonCategoryFromExamples(name);
        if (cursor.moveToFirst()) {
            String returnCategory = cursor.getString(cursor.getColumnIndex("TYPE"));
            Log.d(TAG, returnCategory);
            return returnCategory;
        }*/


        // Find if any are close

        return category;
    }

}
