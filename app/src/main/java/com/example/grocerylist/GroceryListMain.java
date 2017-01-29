package com.example.grocerylist;


import android.os.Bundle;
import android.support.v4.app.Fragment;


/**

 */
public class GroceryListMain extends AbstractActivityHolder {
	private GListFragment listFragment;


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
		if (listFragment != null)
			listFragment.updateList();
	}

}
