package com.example.grocerylist;

import android.content.ContentValues;
import android.content.Intent;

/**
 * Created by neoba on 1/19/2017.
 */
public class ProductPackager extends Product {
    public static final String TABLE_PRODUCTS = "PRODUCTS";
    public static final String[] PRODUCT_COLUMNS = {"_id", "NAME", "TYPE", "LIST_ID", "QUANTITY", "UNITS", "CHECK_OUT"};
    public static final String[] PRODUCT_COLUMN_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "TEXT", "INTEGER", "REAL", "TEXT", "BOOLEAN"};

    public ProductPackager(ContentValues valuesContainer) {
        super(valuesContainer);
    }

    public ProductPackager(Intent data) {
        super(data);
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
