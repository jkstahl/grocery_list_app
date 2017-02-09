package com.example.grocerylist;

/**
 * Created by neoba on 2/8/2017.
 */
public class ProductExamples extends TableMap {
    public static final String TABLE_NAME = "PRODUCT_EXAMPLES";
    public static final String[] PRODUCT_COLUMNS = {"_id", "NAME", "TYPE"};
    public static final String[] PRODUCT_COLUMN_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "TEXT"};

    @Override
    public String getTableName() {
        return TABLE_NAME;
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
