package com.example.grocerylist;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;


/**
 * Created by neoba on 12/28/2016.
 */

public class SwipeMainScreen extends FragmentActivity implements RecipeViewerFragment.OnRefreshCallback {
    InsideListPager mInsideListPager;
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.swipe_main);

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
                    }
                });

        final ActionBar actionBar = getActionBar();
        setTitle(getIntent().getStringExtra("LIST_NAME"));

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
    }

    @Override
    public void refreshProductList() {
        mInsideListPager.addFragment.updateList();
    }
}

