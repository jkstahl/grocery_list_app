package com.example.grocerylist;

import android.content.ContentValues;
import android.content.Intent;

/**
 * Created by neoba on 12/27/2016.
 */

public class ProductPackager extends Packager {

    public static final String TABLE_PRODUCTS = "PRODUCTS";
    public static final String[] PRODUCT_COLUMNS = {"_id", "NAME", "TYPE", "LIST_ID", "QUANTITY", "UNITS", "CHECK_OUT"};
    public static final String[] PRODUCT_COLUMN_TYPES = {"INTEGER", "TEXT", "TEXT", "INTEGER", "REAL", "TEXT", "BOOLEAN"};

    public ProductPackager(ContentValues dataValues) {
        super(dataValues);
    }

    public ProductPackager(Intent intent) {
        super(intent);
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
