package com.example.grocerylist;

import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;

public class Product extends Packager implements TableElement, Serializable {

	// table of all products
	public static final String TABLE_PRODUCTS = "PRODUCTS";
	public static final String[] PRODUCT_COLUMNS = {"_id", "NAME", "TYPE", "LIST_ID", "QUANTITY", "UNITS", "CHECK_OUT", "CHECKOUT_TIME", "RECIPE_LIST_ID", "TIMESTAMP"};
	public static final String[] PRODUCT_COLUMN_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "TEXT", "INTEGER", "REAL", "TEXT", "BOOLEAN", "INTEGER","INTEGER", "INTEGER"};

	public Product(Integer listId, String name, String type, float quantity, String units, Boolean checkOut) {
		this();
		put("NAME", name);
		put("TYPE", type);
		put("LIST_ID", listId);
		put("QUANTITY", quantity);
		put("UNITS", units);
		put("CHECK_OUT", checkOut);
		put("RECIPE_LIST_ID", -1);
		put("TIMESTAMP", 10);
	}

	public Product() {super();}

	public Product(int i, String ingredientName, String uncategorized, float i1, String s, boolean b, String recipeId) {
		this(i, ingredientName, uncategorized, i1, s, b);
		Log.d("product", "Recipe list id is " + recipeId);
		put("RECIPE_LIST_ID", Integer.parseInt(recipeId));
	}

	public Product(ContentValues dataValues) {
		super(dataValues);
	}

	public Product(Intent intent) {
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
