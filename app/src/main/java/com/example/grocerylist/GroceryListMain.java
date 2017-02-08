package com.example.grocerylist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;


/**

 */
public class GroceryListMain extends AbstractActivityHolder {
	private GListFragment listFragment;
	private final String TAG="grocerylistmain";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
