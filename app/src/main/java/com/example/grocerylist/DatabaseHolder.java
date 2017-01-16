package com.example.grocerylist;

import android.content.Context;

public class DatabaseHolder {
	private static ProductListDB mAppDatabase;
	private Context mAppContext;
	
	private DatabaseHolder() {
		
	}
	
	public static ProductListDB getDatabase(Context appContext) {
		if (mAppDatabase == null) {
			mAppDatabase = new ProductListDB(appContext.getApplicationContext());
		}
		return mAppDatabase;
	}
	
}
