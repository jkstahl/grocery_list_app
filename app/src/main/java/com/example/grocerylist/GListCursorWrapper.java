package com.example.grocerylist;

import android.database.Cursor;
import android.database.CursorWrapper;

public class GListCursorWrapper extends CursorWrapper {
	
	public GListCursorWrapper(Cursor cursor) {
		super(cursor);
	}
	
	public GList getGList() {
		if (isBeforeFirst() || isAfterLast())
			return null;
		GList glist = new GList();
		glist.setCursorData(this);
		return glist;
	}
	
}