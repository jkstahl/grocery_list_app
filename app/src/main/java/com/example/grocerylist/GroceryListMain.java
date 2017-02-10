package com.example.grocerylist;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.Map;


/**

 */
public class GroceryListMain extends AbstractActivityHolder {
	private GListFragment listFragment;
	private final String TAG="grocerylistmain";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Class<?> activityClass;
		Map<String, ?> params;
		try {
			SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
			activityClass = Class.forName(prefs.getString("lastActivity", GroceryListMain.class.getName()));
			params = prefs.getAll();
		} catch(ClassNotFoundException ex) {
			activityClass = null;
			params = null;
		}
		Log.d(TAG, "Running class: " + activityClass.getName());
		if (activityClass != null && params != null && !activityClass.getName().contains(GroceryListMain.class.getName())) {
			Intent i = new Intent(this, activityClass);
			for (String key : params.keySet())
				i.putExtra(key, (String) params.get(key));
			startActivity(i);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("lastActivity", getClass().getName());
		editor.commit();
	}

	@Override
	protected Fragment createFragment() {
		listFragment = new GListFragment();
		return listFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Resumed main screen.");
		if (listFragment != null)
			listFragment.updateList();
	}

}
