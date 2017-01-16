package com.example.grocerylist;

public class Recipe extends TableMap implements TableElement {
	// table of recipes
	public static final String TABLE_RECIPE = "RECIPES";
	public static final String[] RECIPE_COLUMNS = {"_id", "NAME", "INSTRUCTIONS", "THUMBNAIL",  "TIMESTAMP"};
	public static final String[] RECIPE_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "TEXT", "BLOB", "INTEGER"};
	
	public Recipe(String name, String instructions) {
		this();
		put("NAME", name);
		put("INSTRUCTIONS", instructions);
		put("TIMESTAMP", 10);
	}
	
	public Recipe() {
		super();
	}
	
	@Override
	public String getTableName() {
		return TABLE_RECIPE;
	}
	
	@Override
	public String[] getColumns() {
		return RECIPE_COLUMNS;
	}
	
	@Override
	public String[] getColumnTypes() {
		return RECIPE_TYPES;
	}
	
}
