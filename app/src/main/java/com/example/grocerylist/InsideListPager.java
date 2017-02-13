package com.example.grocerylist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class InsideListPager extends FragmentPagerAdapter {
    ListViewerAddFragment addFragment;

    public InsideListPager(FragmentManager fm) {
        super(fm);
    }

    public ListViewerAddFragment getListFragment() {
        if (addFragment == null) {
            addFragment = new ListViewerAddFragment();
            return addFragment;
        } else {
            return addFragment;
        }
    }

    @Override
    public android.support.v4.app.Fragment getItem(int i) {
        Fragment fragment;
        switch (i) {
            case 0:
                fragment = getListFragment();
                break;
            case 1:
                fragment = new RecipeViewerFragment();
                break;
            default:
                return null;
        }

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "List";
            case 1:
                return "Recipes";
        }
        return "None";
    }


}
