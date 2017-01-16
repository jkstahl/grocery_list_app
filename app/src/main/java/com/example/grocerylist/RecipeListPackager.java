package com.example.grocerylist;

import android.content.ContentValues;
import android.content.Intent;


/**
 * Created by neoba on 12/29/2016.
 */

public class RecipeListPackager extends Packager {
    public static final String TABLE_PRODUCTS = "RECIPES";
    public static final String[] PRODUCT_COLUMNS = {"_id", "NAME", "DAY", "INSTRUCTIONS", "RECIPE_LIST_ID", "THUMBNAIL"};
    public static final String[] PRODUCT_COLUMN_TYPES = {"INTEGER", "TEXT", "TEXT", "TEXT", "INTEGER", "BLOB"};

    public RecipeListPackager(ContentValues dataValues) {
        super(dataValues);
    }

    public RecipeListPackager(Intent intent) {
        super(intent);
    }

    public RecipeListPackager() {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_PRODUCTS;
    }

    @Override
    public String[] getColumns() {
        return PRODUCT_COLUMNS;
    }

    @Override
    public String[] getColumnTypes() {
        return PRODUCT_COLUMN_TYPES;
    }
}
