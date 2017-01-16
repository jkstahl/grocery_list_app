package com.example.grocerylist;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class AbstractActivityHolder extends FragmentActivity {
	protected abstract Fragment createFragment();
	/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grocery_list_main);
		this.se
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.activity_container);
		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction().add(R.id.activity_container, fragment).commit();
		}
	} */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grocery_list_main);
		if (savedInstanceState == null) {

			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_container);
			fragment = createFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.activity_container, fragment).commit();
		}
	}
}
