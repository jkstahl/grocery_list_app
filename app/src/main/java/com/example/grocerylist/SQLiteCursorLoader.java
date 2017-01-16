package com.example.grocerylist;


import android.content.Context;

import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Joshua on 9/14/2015.
 */
public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {
    private Cursor cursorSave;

    public SQLiteCursorLoader(Context context) {
        super(context);
    }

    protected abstract Cursor loadCursor();

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = loadCursor();
        if (cursor != null) {
            cursor.getCount();
        }
        return  cursor;
    }

    @Override
    public void deliverResult(Cursor data) {
        Cursor oldCursor  = cursorSave;
        cursorSave = data;

        if (isStarted())
            super.deliverResult(data);

        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (cursorSave != null) {
            deliverResult(cursorSave);
        }
        if (takeContentChanged() || cursorSave == null) {
            forceLoad();
        }

    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();
        if (cursorSave != null && !cursorSave.isClosed()) {
            cursorSave.close();
        }
        cursorSave = null;
    }
}
