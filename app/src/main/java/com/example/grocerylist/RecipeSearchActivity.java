package com.example.grocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public class RecipeSearchActivity extends AbstractActivityHolder {
    public static final int ACTIVITY_ID = 1;
    public static final int OK=0;
    public static final int CANCEL=1;
    public static final int NEW=2;

    @Override
    protected Fragment createFragment() {
        Fragment fragment = new RecipeSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed(){
        setResult(CANCEL, new Intent());
        finish();
        super.onBackPressed();
    }

}
