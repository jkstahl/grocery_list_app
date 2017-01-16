package com.example.grocerylist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neoba on 12/30/2016.
 */

public class RecipeSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    public static final int ACTIVITY_ID = 2;

    private ListView recipeList;
    private EditText recipeSearchBox;
    private ProductListDB productDatabase;
    private RecipeSearchFragment thisFrag;
    private final int LOADER_ID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.recipe_search, container, false);
        Intent contextIntent = getActivity().getIntent();


        recipeList = (ListView) rootView.findViewById(R.id.list_recipe_search);

        productDatabase = DatabaseHolder.getDatabase(getActivity());
        // add all recipes to the list

        thisFrag = this;
        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecipeListAdapter ra = (RecipeListAdapter) recipeList.getAdapter();
                Cursor cursor = ra.getCursor();

                String recipeId = "" + cursor.getInt(cursor.getColumnIndex("_id"));
                Intent returnIntent = getActivity().getIntent();
                returnIntent.putExtra("RECIPE_ID", recipeId);
                getActivity().setResult(RecipeSearchActivity.OK, returnIntent);
                getActivity().finish();
            }
        });
        recipeSearchBox = (EditText) rootView.findViewById(R.id.edit_search_recipe);
        recipeSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getLoaderManager().restartLoader(LOADER_ID, null, thisFrag);
            }
        });

        // set loader here
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipe_search_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("ItemSelect", Integer.toString(item.getItemId()));
        switch (item.getItemId()) {
            case R.id.new_menu_item:
                Intent i = getActivity().getIntent().putExtra("NEW_RECIPE_NAME", recipeSearchBox.getText().toString());
                getActivity().setResult(RecipeSearchActivity.NEW, i);
                getActivity().finish();
                return true;
            case R.id.cancel_menu_item:
                getActivity().setResult(RecipeSearchActivity.CANCEL);
                getActivity().finish();
                return true;

        }
        return false;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new GListCursorLoader(getActivity(), productDatabase, recipeSearchBox);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        RecipeListAdapter adapter = new RecipeListAdapter(data);
        recipeList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recipeList.setAdapter(null);
    }

    private class RecipeListAdapter extends CursorAdapter {
        private Cursor listCursor;

        public RecipeListAdapter(Cursor cursor) {
            super(getActivity(), cursor, 0);
            listCursor = cursor;

        }
        @Override
        public boolean areAllItemsEnabled()
        {
            return true;
        }

        @Override
        public boolean isEnabled(int arg0)
        {
            return true;
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.recipe_list_item, parent, false);



            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //View view = getActivity().getLayoutInflater().inflate(R.layout.list_prod_item, null);
            TextView recipeName = (TextView) view.findViewById(R.id.recipe_name_label);
            recipeName.setText(cursor.getString(cursor.getColumnIndex("NAME")));
        }


    }

    private static class GListCursorLoader extends SQLiteCursorLoader {
        private String listId;
        private ProductListDB productDatabase;
        private EditText recipeSearchBox;

        public GListCursorLoader(Context activity, ProductListDB db, EditText searchBox) {
            super(activity);
            recipeSearchBox = searchBox;
            productDatabase = db;

        }

        @Override
        protected Cursor loadCursor() {
            Log.d("loadcursor", "Cursor loading");
            return productDatabase.getAllRecipes(recipeSearchBox.getText().toString().trim());
        }
    }
}
