package com.example.grocerylist;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public abstract class TableMap implements TableElement, Serializable {

	ContentValues valuesContainer;

	public TableMap() {
		valuesContainer = new ContentValues();
		for (int i = 0; i < getColumns().length; i++) {
			if (!getColumns()[i].equals("_id"))
			put(getColumns()[i], "");
		}
	}

	public TableMap(Cursor cursor) {
		this();
		this.setCursorData(cursor);
	}
	
	public Object get(String key) {
		return valuesContainer.get(key);
	}

	public void put(String key, Object val) {
		//if (valuesContainer.containsKey(key))
			if (val instanceof String )
				valuesContainer.put(key, (String) val);
			else if (val instanceof Integer)
				valuesContainer.put(key, (Integer) val);
			else if (val instanceof Double)
				valuesContainer.put(key, (Double) val);
			else if (val instanceof Float)
				valuesContainer.put(key, (Float) val);
			else if (val instanceof Short)
				valuesContainer.put(key, (Short) val);
			else if (val instanceof Boolean)
				valuesContainer.put(key, (Boolean) val);
			else if (val instanceof Long)
				valuesContainer.put(key, (Long) val);
			else if (val instanceof byte[])
				valuesContainer.put(key, (byte[]) val);
			else {
				Log.d("error", "Error: Unexpected type passed to put function in TableMap. " + key);
			}
		//else {
		//	Log.d("error", "Error putting value in, key: " + key + " value: " + val);
		//}
			
	}

	public boolean containsKey(String key) {
		return valuesContainer.containsKey(key);
	}

	public ContentValues getValuesContainer() {
		return valuesContainer;
	}
	
	public void setCursorData(Cursor c) {
		for (int i=0; i < c.getColumnCount(); i++) {
			String colName = c.getColumnName(i);
			//if (valuesContainer.containsKey(colName)) {
				String typeName = getColumnTypes()[Arrays.asList(getColumns()).indexOf(c.getColumnName(i))];
				if (typeName.contains("TEXT"))
					valuesContainer.put(colName, c.getString(i));
				else if (typeName.contains("INTEGER"))
					valuesContainer.put(colName, c.getInt(i));
				else if (typeName.contains("BLOB"))
					valuesContainer.put(colName, c.getBlob(i));
				else if (typeName.contains("REAL"))
					valuesContainer.put(colName, c.getFloat(i));
				else if (typeName.contains("BOOLEAN")) {
					int val = c.getInt(i);
					valuesContainer.put(colName, (Boolean) (val == 1));
				}

			//} else {
			//	Log.d("error", "Error: collumn name not in map, " + colName + " " + getTableName());
			//}
		}
	}
	
	public abstract String getTableName();
	
	public abstract String[] getColumns();
	
	public abstract String[] getColumnTypes();
	
	public String getTableCreatString() {
		String returnString = "CREATE TABLE " + getTableName() + " (";
		String[] columnNames = getColumns();
		String[] columnTypes = getColumnTypes();
		for (int i=0; i<columnNames.length; i++) 
			returnString += (columnNames[i] + " " + columnTypes[i] + ",");
		
		returnString = returnString.substring(0, returnString.length()-1) + ")";
		Log.d("management", "Table creation string is " + returnString);
		return returnString;
	}
	
@Override
	public String toString() {
		String returnString = "";
		for (int i=0; i<getColumns().length; i++) {
			returnString += (getColumns()[i] + " -> " + get(getColumns()[i])+"\n") ;
		}
		return returnString;
}

	/**
	 * Test the object to see if they are equal.
	 */
	public boolean equals(Object testObj) {
		if (!(testObj instanceof TableMap))
			return false;
		
		TableMap testMap = (TableMap) testObj;
		
		for (String test : getColumns()) {
			if (testMap.get(test) == null || this.get(test) == null) {
				if (testMap.get(test) != this.get(test))
					return false;
			} else if (!testMap.get(test).equals(this.get(test)))
				return false;
		}
		
		return true;
	}
}
