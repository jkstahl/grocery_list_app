package com.example.grocerylist;

import android.content.ContentValues;
import android.content.Intent;

public class Recipe extends Packager implements TableElement {
	// table of recipes
	public static final String TABLE_RECIPE = "RECIPES";
	public static final String[] RECIPE_COLUMNS = {"_id", "NAME", "INSTRUCTIONS", "THUMBNAIL", "DESCRIPTION", "SERVINGS", "URL", "TIMESTAMP"};
	public static final String[] RECIPE_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "TEXT", "BLOB", "TEXT", "INTEGER", "TEXT","INTEGER"};

	public Recipe(String name, String instructions) {
		this();
		put("NAME", name);
		put("INSTRUCTIONS", instructions);
		put("TIMESTAMP", 10);
	}
	
	public Recipe() {
		super();
	}

	public Recipe(ContentValues dataValues) {
		super(dataValues);
	}

	public Recipe(Intent intent) {
		super(intent);
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof Recipe) {
			Recipe r = (Recipe) o;
			return r.get("NAME").equals(this.get("NAME")) &&
					r.get("INSTRUCTIONS").equals(this.get("INSTRUCTIONS")) &&
					r.get("SERVINGS") == this.get("SERVINGS");
		}
		return false;
	}
	
}
