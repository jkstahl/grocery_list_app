package com.example.grocerylist;

public class RecipeOProducts extends TableMap {
	// list of all products associated with their recipes
	public static final String TABLE_RECIPE_PRODUCTS = "RECIPE_PRODUCTS";
	public static final String[] PRODUCT_RECIPE_COLUMNS = {"_id", "RECIPE_ID", "DAY", "TIMESTAMP"};
	public static final String[] PRODUCT_RECIPE_COLUMN_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER", "STRING", "INTEGER"};
	
	public RecipeOProducts(Integer recipeId) {
		this();

		put("RECIPE_ID", recipeId);
		put("DAY", "Monday");
		put("TIMESTAMP", 10);
	}

	public RecipeOProducts() {
		super();
	}

	@Override
	public String getTableName() {
		return TABLE_RECIPE_PRODUCTS;
	}
	
	@Override
	public String[] getColumns() {
		return PRODUCT_RECIPE_COLUMNS;
	}
	
	@Override
	public String[] getColumnTypes() {
		return PRODUCT_RECIPE_COLUMN_TYPES;
	}
	
}
