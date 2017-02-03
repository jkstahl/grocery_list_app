package com.example.grocerylist;

/**
 * Created by neoba on 12/31/2016.
 */

public class Ingredients extends Packager {

    // table of all products
    public static final String TABLE_PRODUCTS = "INGREDIENTS";
    public static final String[] PRODUCT_COLUMNS = {"_id", "RECIPE_ID","NAME", "USE_IN_LIST", "TIMESTAMP"};
    public static final String[] PRODUCT_COLUMN_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER", "TEXT", "BOOLEAN", "INTEGER"};

    public Ingredients(Integer recipeId, String name, Float quantity, String units) {
        this();
        put("NAME", name);
        put("RECIPE_ID", recipeId);
        put("QUANTITY", quantity);
        put("UNITS", units);
        put("USE_IN_LIST", true);
        put("TIMESTAMP", 10);
    }

    public Ingredients(String name) {
        super();
        put("NAME", name);
    }

    public Ingredients() {super();}

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
