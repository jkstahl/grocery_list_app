package com.example.grocerylist;

/**
 * Created by neoba on 12/28/2016.
 */

public class RecipeList extends TableMap {
    public static String RECIPE_LIST_TABLE = "RECIPE_LIST";
    public static String[] RECIPE_LIST_COLUMNS = {"_id", "LIST_ID", "RECIPE_ID", "DAY", "TIMESTAMP"};
    public static String[] RECIPE_LIST_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER", "INTEGER", "TEXT", "INTEGER"};

    public RecipeList(Integer listId, Integer recipeId, String day) {
        super();
        put("LIST_ID", listId);
        put("RECIPE_ID", recipeId);
        put("DAY", day);
    }

    public RecipeList() {
        super();
    }

    @Override
    public String getTableName() {
        return RECIPE_LIST_TABLE;
    }

    @Override
    public String[] getColumns() {
        return RECIPE_LIST_COLUMNS;
    }

    @Override
    public String[] getColumnTypes() {
        return RECIPE_LIST_TYPES;
    }
}
