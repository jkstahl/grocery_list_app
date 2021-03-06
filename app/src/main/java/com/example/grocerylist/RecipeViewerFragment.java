package com.example.grocerylist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neoba on 12/28/2016.
 */
public class RecipeViewerFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private RecipeAdderAdapter recipeAdderAdapter;
    private DaysTracker dayTracker;
    private String listId;
    private ListView recipeList;
    //private ProductListDB productDatabase;
    private OnRefreshCallback callbackRefresh;
    private ProductUnitExtractor unitExtractor;
    private final String TAG = "recipeviewer";

    private void refreshRecipeList() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void startSearchIntent(String clickDay, String recipeListId) {
        Intent i = new Intent(getActivity(), RecipeSearchActivity.class);
        i.putExtra("DAY", clickDay);
        i.putExtra("LIST_ID", "" + listId);
        i.putExtra("RECIPE_LIST_ID", recipeListId);
        startActivityForResult(i, RecipeSearchActivity.ACTIVITY_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unitExtractor = new ProductUnitExtractor();
        dayTracker = new DaysTracker();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.recipe_adder_layout, container, false);
        recipeList = (ListView) rootView.findViewById(R.id.recipe_adder_list);

        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickDay  = dayTracker.getDayFromPosition(position);
                // Add search for days that already have recipes assigned
                Map<String, RecipeListPackager> recipeMap = ((RecipeAdderAdapter)recipeList.getAdapter()).getActiveRecipes();
                if (!recipeMap.containsKey(clickDay)) {
                    RecipeListPackager rlp = recipeMap.get(clickDay);
                    if (rlp == null)
                        startSearchIntent(clickDay, null);
                    else
                        startSearchIntent(clickDay, "" + rlp.get("RECIPE_LIST_ID"));
                } else {
                    RecipeListPackager rlp = recipeMap.get(clickDay);
                    //Intent i = rlp.getIntent(getActivity(), RecipeEditorActivity.class);
                    Intent i = new Intent(getActivity(), RecipeEditorActivity.class);
                    i.putExtra("RECIPE_ID", ""+rlp.get("_id"));
                    i.putExtra("DAY", clickDay);
                    i.putExtra("RECIPE_LIST_ID", (String) "" + rlp.get("RECIPE_LIST_ID"));
                    Log.d(TAG, "Sending Recipe list ID: " + rlp.get("RECIPE_LIST_ID"));
                    startActivityForResult(i, RecipeEditorActivity.ACTIVITY_ID);
                }
            }
        });



        listId = getActivity().getIntent().getStringExtra("ListID");
        //productDatabase = DatabaseHolder.getDatabase(getActivity());
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("action", "Returned to recipe viewer " + resultCode);
        switch (requestCode) {
            case RecipeSearchActivity.ACTIVITY_ID:
                if (resultCode == RecipeSearchActivity.OK) {
                    // Delete old ingredients if this is a selected recipe
                    String recipeId = data.getStringExtra("RECIPE_ID");
                    String day = data.getStringExtra("DAY");
                    RecipeList rp = new RecipeList(Integer.parseInt(listId), Integer.parseInt(recipeId), day);
                    String newRecipeListId = "" + DatabaseHolder.getDatabase(getActivity()).addEntryToDatabase(rp);
                    // Add ingredients to the grocery list.
                    String recipeListId = data.getStringExtra("RECIPE_LIST_ID");
                    Log.d(TAG, "Recieved list id: " + recipeListId);
                    DatabaseHolder.getDatabase(getActivity()).deleteIngredientsWithId(recipeListId);
                    DatabaseHolder.getDatabase(getActivity()).deleteRecipeListItem(recipeListId);
                    //callbackRefresh.refreshProductList();
                    addIngredientsToList(recipeId, newRecipeListId);
                    refreshRecipeList();
                } else if (resultCode == RecipeSearchActivity.NEW) {
                    // Add new creation functionality.
                    //String newRecipeName = data.getStringExtra("NEW_NAME");
                    Intent i = new Intent(getActivity(), RecipeEditorActivity.class);
                    i.putExtra("NEW_RECIPE_NAME", data.getStringExtra("NEW_RECIPE_NAME"));
                    i.putExtra("DAY", data.getStringExtra("DAY"));
                    startActivityForResult(i, RecipeEditorActivity.ACTIVITY_ID);
                }
                break;
            case RecipeEditorActivity.ACTIVITY_ID:
                if (resultCode == RecipeEditorActivity.OK) {
                    Log.d("result", "OK selected");
                    String recipeListId;
                    if (data.hasExtra("NEW_RECIPE_NAME")) {
                        RecipeList rl  = new RecipeList(Integer.parseInt(listId), Integer.parseInt(data.getStringExtra("RECIPE_ID")), data.getStringExtra("DAY"));
                        recipeListId =  "" + DatabaseHolder.getDatabase(getActivity()).addEntryToDatabase(rl);
                    } else { // this was an edit to a recipe
                        recipeListId = data.getStringExtra("RECIPE_LIST_ID");
                        //productDatabase.getIngredientsFromRecipeListId(recipeListId);
                    }
                    String recipeId = data.getStringExtra("RECIPE_ID");
                    DatabaseHolder.getDatabase(getActivity()).deleteIngredientsWithId(recipeListId);
                    // Add ingredients as products to list.
                    addIngredientsToList(recipeId, recipeListId);
                    refreshRecipeList();
                }else {
                    Log.d("result", "CANCEL selected");
                }
                break;
        }
    }

    private List<Integer> getIngredientsToAdd(String[] recipeIngredients, boolean[] isSelected, Product[] newProductList){
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_multiple_choice, recipeIngredients);
        final List<Integer> selectedList = new ArrayList<>();
        ListView lv = new ListView(getActivity());
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adp);
        final Product[] np = newProductList;

        for (int i=0; i<isSelected.length; i++)
            if (isSelected[i])
                selectedList.add(i);

        AlertDialog.Builder bldr = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogStyle);
        bldr.setTitle("Select Ingredients to Add");
        //bldr.setMessage("Select Ingredients to Add");
        //bldr.setView(lv);
        bldr.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Ok clicked.");
                        for (Integer index : selectedList) {
                            //Product newProduct = new Product(Integer.parseInt(listId), formattedProoduct.product, "Uncategorized", (float) formattedProoduct.quantity, formattedProoduct.units, false, recipeListId);
                            DatabaseHolder.getDatabase(getActivity()).addEntryToDatabase(np[index]);
                        }
                        callbackRefresh.refreshProductList();
                    }
                });
        bldr.setMultiChoiceItems(recipeIngredients, isSelected ,new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedList.add(which);
                } else {
                    selectedList.remove(Integer.valueOf(which));
                }
            }
        });
        bldr.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel clicked.");
                    }
                });

        final Dialog dlg = bldr.create();
        dlg.show();
        return selectedList;
    }

    private void addIngredientsToList(String recipeId, String recipeListId) {
        Cursor ingredientForRecipeCursor = DatabaseHolder.getDatabase(getActivity()).getIngredientsForRecipe(recipeId);
        String[] ingredientsList = new String[ingredientForRecipeCursor.getCount()];
        boolean[] selectedArray = new boolean[ingredientForRecipeCursor.getCount()];
        Product[] newProductList = new Product[ingredientForRecipeCursor.getCount()];

        if (ingredientForRecipeCursor.moveToFirst()) {
            Log.d("ingredientsadd", "Found ingredients for recipe " + recipeId + ". Adding to list.");
            int i =0;
            do {
                int useInList = (int) ingredientForRecipeCursor.getInt(ingredientForRecipeCursor.getColumnIndex("USE_IN_LIST"));
                    String ingredientName = ingredientForRecipeCursor.getString(ingredientForRecipeCursor.getColumnIndex("NAME"));
                    Log.d("ingredientsadd", "Adding " + ingredientName + " to list.");
                    ProductUnitExtractor.QuantityUnitPackage formattedProoduct = unitExtractor.getUnitsProductFromString(ingredientName);
                    selectedArray[i] = false;
                if (useInList != 0) {
                    selectedArray[i] = true;
                    // figure out category
                    //Product newProduct = new Product(Integer.parseInt(listId), formattedProoduct.product, "Uncategorized", (float) formattedProoduct.quantity, formattedProoduct.units, false, recipeListId);
                    //productDatabase.addEntryToDatabase(newProduct);
                }
                String category = ProductCategoryFinder.getCategoryFromProductName(getActivity(), formattedProoduct.product);
                Product newProduct = new Product(Integer.parseInt(listId), formattedProoduct.product, category, (float) formattedProoduct.quantity, formattedProoduct.units, false, recipeListId);
                newProductList[i] = newProduct;
                ingredientsList[i] = formattedProoduct.product;
                i++;
            } while (ingredientForRecipeCursor.moveToNext());

            List<Integer> selected =  getIngredientsToAdd(ingredientsList, selectedArray, newProductList);
            //for (Integer index : selected) {
                //Product newProduct = new Product(Integer.parseInt(listId), formattedProoduct.product, "Uncategorized", (float) formattedProoduct.quantity, formattedProoduct.units, false, recipeListId);
            //    productDatabase.addEntryToDatabase(newProductList[index]);
            //}
            Log.d(TAG, "Added dialogue done.");
        }
    }

    private void deleteRecipeFromList(String recipeListId) {
        DatabaseHolder.getDatabase(getActivity()).deleteRecipeListItem(recipeListId);
        callbackRefresh.refreshProductList();
        refreshRecipeList();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new GListCursorLoader(getActivity(), DatabaseHolder.getDatabase(getActivity()), listId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Map<String, RecipeListPackager> recipeListMap = new HashMap<String, RecipeListPackager>();
        data.moveToFirst();
        if (data.getCount() != 0) {
            do {
                RecipeListPackager rl = ((RecipeListCursorWrapper) data).getRecipeList();
                recipeListMap.put((String) rl.get("DAY"), rl);
            } while (data.moveToNext());
        }
        RecipeAdderAdapter adapter = new RecipeAdderAdapter(getActivity(), 0, recipeListMap);
        this.recipeAdderAdapter=adapter;
        recipeList.setAdapter(recipeAdderAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recipeList.setAdapter(null);
    }

    public interface OnRefreshCallback {
        public void refreshProductList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbackRefresh = (OnRefreshCallback) activity;
    }

    private class RecipeAdderAdapter extends ArrayAdapter<String> {
        Map<String, RecipeListPackager> recipeList;

        public RecipeAdderAdapter(Context context, int resource,  Map<String, RecipeListPackager> recipeList) {
            super(context, resource);
            this.recipeList = recipeList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_list_item, parent, false);
            }

            ImageButton searchButton = (ImageButton) convertView.findViewById(R.id.recipe_search);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecipeListPackager rlp = recipeList.get(dayTracker.getDayFromPosition(position));
                    if (rlp != null)
                        startSearchIntent(dayTracker.getDayFromPosition(position), ""+ rlp.get("RECIPE_LIST_ID"));
                    else
                        startSearchIntent(dayTracker.getDayFromPosition(position), null);
                }
            });
            String dayString = dayTracker.getDayFromPosition(position);
            ((TextView) convertView.findViewById(R.id.day_label)).setText(dayString);
            RecipeListPackager recipeItem = recipeList.get(dayTracker.getDayFromPosition(position));
            ImageView imageView = (ImageView) convertView.findViewById(R.id.recipe_image);
            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.delete_recipe_button);

            if (recipeList.containsKey(dayString)) {
                imageView.setVisibility(ImageView.VISIBLE);
                deleteButton.setVisibility(Button.VISIBLE);
                searchButton.setVisibility(Button.VISIBLE);
                ((TextView) convertView.findViewById(R.id.recipe_name_label)).setText((String) recipeItem.get("NAME"));

                byte[] imageRaw = (byte[])recipeItem.get("THUMBNAIL");
                //if (imageRaw != null && imageRaw.length > 1) {
                // TODO why is bitmap rotated.
                Bitmap imageBitmap = DbBitmapUtility.getImage(getActivity(), imageRaw);
                imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(imageBitmap, DbBitmapUtility.THUMBNAIL_WIDTH, DbBitmapUtility.THUMBNAIL_HEIGHT));


                Log.d("recipelistitem", "Day: " + dayString + " Item: " + recipeItem.get("NAME") + " ID: " + recipeItem.get("RECIPE_LIST_ID"));
                // Delete recipe if there is one there.

                deleteButton.setTag("" + recipeItem.get("RECIPE_LIST_ID"));
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String recipeListId = "" + v.getTag();
                        // Delete all ingredients and list item
                        deleteRecipeFromList(recipeListId);
                    }
                });
            } else {
                ((TextView) convertView.findViewById(R.id.recipe_name_label)).setText("Click to add");
                imageView.setVisibility(ImageView.INVISIBLE);
                deleteButton.setVisibility(Button.INVISIBLE);
                searchButton.setVisibility(Button.INVISIBLE);
            }

                return convertView;
        }

        @Override
        public int getCount() {
            return dayTracker.getDaysLength();
        }

        public Map<String,RecipeListPackager> getActiveRecipes() {
            return recipeList;
        }
    }

    private static class GListCursorLoader extends SQLiteCursorLoader {
        private String listId;
        //private ProductListDB productDatabase;

        public GListCursorLoader(Context activity, ProductListDB db, String thisID) {
            super(activity);
            listId = thisID;
            //productDatabase = db;
        }

        @Override
        protected Cursor loadCursor() {
            return DatabaseHolder.getDatabase(null).getRecipesFromList(listId);
        }
    }
}
