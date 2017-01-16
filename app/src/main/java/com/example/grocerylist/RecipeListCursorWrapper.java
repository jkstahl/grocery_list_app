package com.example.grocerylist;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by neoba on 12/29/2016.
 */

public class RecipeListCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public RecipeListCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public RecipeListPackager getRecipeList() {

        RecipeListPackager rl = new RecipeListPackager();
        rl.setCursorData(this);
        return rl;
    }
}
