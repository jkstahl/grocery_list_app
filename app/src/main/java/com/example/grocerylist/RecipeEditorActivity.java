package com.example.grocerylist;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by neoba on 1/1/2017.
 */

public class RecipeEditorActivity extends AbstractActivityHolder {
    public static final int ACTIVITY_ID = 3;
    public static final int OK = 0;
    public static final int CANCEL=1;


    @Override
    protected Fragment createFragment() {
        return new RecipeEditorFragment();
    }

    @Override
    public void onBackPressed(){
        setResult(CANCEL, new Intent());
        finish();
        super.onBackPressed();
    }

}
