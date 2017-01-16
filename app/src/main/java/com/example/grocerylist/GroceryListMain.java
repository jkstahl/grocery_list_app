package com.example.grocerylist;


import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
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

}
