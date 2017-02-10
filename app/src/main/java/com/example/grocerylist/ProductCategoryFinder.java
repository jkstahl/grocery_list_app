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
        name = name.toLowerCase();
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

        // Find if any are close
        // find if any of the products already selected are a subset
        cursor = db.getAllUniqueProducts();
        if (cursor.moveToFirst()) {
            do {
                String testName = cursor.getString(cursor.getColumnIndex("NAME"));
                if (name.contains(testName.toLowerCase())) {
                    String type  = cursor.getString(cursor.getColumnIndex("TYPE"));
                    Log.d(TAG, "Returning type: " + type);
                    return type;
                }
            } while (cursor.moveToNext());
        }

        return category;
    }

}
