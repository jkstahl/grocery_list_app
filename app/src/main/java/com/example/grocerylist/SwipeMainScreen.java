package com.example.grocerylist;


import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.Map;


/**
 * Created by neoba on 12/28/2016.
 */

public class SwipeMainScreen extends FragmentActivity implements RecipeViewerFragment.OnRefreshCallback {
    InsideListPager mInsideListPager;
    ViewPager mViewPager;
    private String TAG="swipemainscreen";

    public void hideKeyboard() {

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_main_old);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mInsideListPager = new InsideListPager( getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mInsideListPager);



        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                        // Hide keyboard
                        Log.d(TAG, "Tab selected: "+position);
                        mViewPager.getWindowToken();
                        if (position == 1)
                            hideKeyboard();


                        if (position == 0 && mInsideListPager.addFragment != null)
                            mInsideListPager.addFragment.updateList();
                    }
                });



        setTitle(getIntent().getStringExtra("LIST_NAME"));
        //TabLayout tl = (TabLayout) findViewById(R.id.tabs);
        //tl.setupWithViewPager(mViewPager);

        final ActionBar actionBar = getActionBar();
        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText("List").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Recipes").setTabListener(tabListener));

        try {
            if (getIntent().hasExtra("LAST_TAB"))
                mViewPager.setCurrentItem(Integer.parseInt(getIntent().getStringExtra("LAST_TAB")));
        } catch (Exception e) {
            Log.e(TAG, "Error getting last tab.");
        }
    }

    @Override
    public void refreshProductList() {
        //mInsideListPager.getListFragment().updateList();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        for (String key : getIntent().getExtras().keySet())
            editor.putString(key, (String) getIntent().getExtras().getString(key));
        editor.putString("LAST_TAB", "" + mViewPager.getCurrentItem());
        editor.commit();
    }
}

