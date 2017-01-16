package com.example.grocerylist;

public class GList extends TableMap implements TableElement {
	// table of lists of products
	public static final String TABLE_LIST = "LISTS";
	public static final String[] LIST_COLUMNS = {"_id", "NAME", "TIMESTAMP"};
	public static final String[] LIST_TYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "INTEGER"};

	public GList(String name) {
		this();
		put("NAME", name);
		put("TIMESTAMP", 10);
	}

	public GList() {
		super();
	}


	@Override
	public String getTableName() {
		return TABLE_LIST;
	}
	
	@Override
	public String[] getColumns() {
		return LIST_COLUMNS;
	}
	
	@Override
	public String[] getColumnTypes() {
		return LIST_TYPES;
	}

	
	
	
}
