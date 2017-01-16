package com.example.grocerylist;

import android.database.Cursor;
import android.database.CursorWrapper;

public class WorkingProductCursorWrapper extends CursorWrapper {

	public WorkingProductCursorWrapper(Cursor cursor) {
		super(cursor);
	}
	
	public Product getWorkingProduct() {
		if (isBeforeFirst() || isAfterLast())
			return null;
		Product glist = new Product();
		glist.setCursorData(this);
		return glist;
	}
	
}