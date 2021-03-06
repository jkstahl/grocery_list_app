package com.example.grocerylist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by neoba on 1/1/2017.
 */
public class RecipeEditorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, WebpageLoaderCallback {
    private final String TAG="recipeeditor";

    private Recipe recipe;
    private String recipeId;
    private ListView ingredientsList;
    private boolean newRecipe = false;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap recipeImageBitmap=null;
    private ProductUnitExtractor unitExtractor;
    private RecipeEditorFragment thisActivity=null;
    private ImageButton editButton;
    private Menu menu;
    private ProgressDialog progress;

    // Adjustable widgets.
    private TextView recipeName;
    private EditText instructions;
    private EditText description;
    private EditText servings;
    private EditText urlEdit=null;
    private Button importButton;

    private boolean editMode=true;

    public void setMenuVisibility() {
        if (menu != null) {
            ((MenuItem) menu.findItem(R.id.menu_item_ok)).setVisible(editMode);
            ((MenuItem) menu.findItem(R.id.menu_item_cancel)).setVisible(editMode);
            ((MenuItem) menu.findItem(R.id.done_menu_item)).setVisible(!editMode);
            ((MenuItem) menu.findItem(R.id.edit_menu_item)).setVisible(!editMode);
        }
    }

    public void setEditAble(){
        recipeName.setFocusable(editMode);
        instructions.setFocusable(editMode);
        description.setFocusable(editMode);
        servings.setFocusable(editMode);
        urlEdit.setEnabled(editMode);
        importButton.setEnabled(editMode);

        recipeName.setFocusableInTouchMode(editMode);
        instructions.setFocusableInTouchMode(editMode);
        description.setFocusableInTouchMode(editMode);
        servings.setFocusableInTouchMode(editMode);

        // menu
        setMenuVisibility();


        if (editMode) {
            editButton.setVisibility(ImageButton.INVISIBLE);

        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        thisActivity=this;

        ProductListDB productDatabase = DatabaseHolder.getDatabase(getActivity());
        unitExtractor = new ProductUnitExtractor();

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.recipe_edit, container, false);
        Intent contextIntent = getActivity().getIntent();
        newRecipe = contextIntent.hasExtra("NEW_RECIPE_NAME");
        if (newRecipe) {
            recipe = new Recipe();
            editMode = true;
        } else {
            recipe = new Recipe();
            Cursor recipeCursor = productDatabase.getRecipeFromId((String) contextIntent.getStringExtra("RECIPE_ID"));
            recipe.setCursorData(recipeCursor);
            //recipe = new Recipe(contextIntent);
            editMode=false;
        }

        ImageView recipeImage = (ImageView) rootView.findViewById(R.id.recipe_image);
        recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Image clicked.");
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        //TODO Validate images smaller and larger than the default
        Object image = recipe.get("THUMBNAIL");
        //if (image instanceof byte[] && ((byte[]) image).length>1)
        if (image instanceof String)
            image = new byte[1];
        recipeImage.setImageBitmap(DbBitmapUtility.getImage(getActivity(), (byte[]) image ));

        recipeName = (TextView) rootView.findViewById(R.id.recipe_name_label);
        instructions = (EditText) rootView.findViewById(R.id.edit_instructions);
        ingredientsList = (ListView) rootView.findViewById(R.id.ingredient_list);
        description = (EditText) rootView.findViewById(R.id.description_edit_text);
        servings = (EditText) rootView.findViewById(R.id.servings_edit_text);

        if( newRecipe ) {
            String tempName = contextIntent.getStringExtra("NEW_RECIPE_NAME");
            recipeName.setText(tempName);
            servings.setText("1");
            recipeId = "-1";
        }    else {
            recipeName.setText((String) recipe.get("NAME"));
            instructions.setText((String) recipe.get("INSTRUCTIONS"));
            description.setText ((String) recipe.get("DESCRIPTION"));
            servings.setText("" + recipe.get("SERVINGS"));
            this.recipeId = contextIntent.getStringExtra("RECIPE_ID");
        }


        Log.d(TAG, "Id is " + recipeId);

        urlEdit = (EditText) rootView.findViewById(R.id.url_edit_text);
        if (recipe.get("URL") != null && !recipe.get("URL").equals("")){
            urlEdit.setText((String) recipe.get("URL"));
        }
        importButton = ((Button) rootView.findViewById(R.id.url_import_button));
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebpageLoader wl = new WebpageLoader(getActivity(), thisActivity);

                progress = new ProgressDialog(getActivity());
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                wl.execute(urlEdit.getText().toString());
            }
        });

        ((Button) rootView.findViewById(R.id.url_open_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlEdit.getText().toString().trim()));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error opening web page.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                }
            }
        });

        ((ImageButton) rootView.findViewById(R.id.recipe_edit_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            setEditMode(true);
            }
        });
editButton = (ImageButton) rootView.findViewById(R.id.recipe_edit_button);

        recipe.setView("NAME", recipeName);
        recipe.setView("INSTRUCTIONS", instructions);
        recipe.setView("DESCRIPTION", description);
        recipe.setView("SERVINGS", servings);
        recipe.setView("URL", urlEdit);

        //Set up ingredients list
        setEditAble();
        getLoaderManager().initLoader(2, null, this);
        return rootView;
    }

    public void setImage(Bitmap thumbNail) {
        try {
            // Log.d(TAG, String.valueOf(bitmap));
            //Bitmap thumbNail = ThumbnailUtils.extractThumbnail(bitmap, DbBitmapUtility.IMAGE_WIDTH, DbBitmapUtility.IMAGE_HIEGHT );
            //thumbNail = DbBitmapUtility.getBitmap(thumbNail, path);
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.recipe_image);
            imageView.setImageBitmap(thumbNail);
            recipeImageBitmap = thumbNail;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                String path = uri.getPath();
                Log.d(TAG, "Image path: " + path);
                Bitmap thumbNail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                setImage(thumbNail);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipe_edit_menu, menu);
        this.menu = menu;
        setMenuVisibility();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("ItemSelect", Integer.toString(item.getItemId()));
        switch (item.getItemId()) {
            case R.id.menu_item_ok:
                Log.d("AddListSelect", "Add a List selected.");
                if (recipeName.getText().toString().trim().equals("")) {
                    Log.d(TAG, "Empty name field");
                    Toast.makeText(getActivity(), "Recipe name must not be empty", Toast.LENGTH_SHORT).show();
                    recipeName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
                }    else {
                    // Add recipe if this is for a new recipe.
                    Intent returnIntent = getActivity().getIntent();
                    ProductListDB productDatabase = DatabaseHolder.getDatabase(getActivity());
                    if (newRecipe) {
                        productDatabase.addEntryToDatabase(recipe);
                        returnIntent.putExtra("RECIPE_ID", "" + recipe.get("_id"));
                    }

                    List<Ingredients> ingredientList = ((IngredientsAdapter) ingredientsList.getAdapter()).getIngredientList();
                    // Delete ingredients from recipe ID
                    productDatabase.deleteIngredientsFromRecipe(recipe);
                    for (Ingredients ing : ingredientList) {

                        // format ingredients unsing unit extractor
                        if (newRecipe) { // put the recipe id in if this is a new recipe becase we just added the recipe.
                            ing.put("RECIPE_ID", recipe.get("_id"));
                            returnIntent.putExtra("_id", "" + recipe.get("_id"));
                        }
                        Log.d(TAG, "Ingredient added " + ing.get("NAME"));
                        productDatabase.addEntryToDatabase(ing);

                    }

                    // recipe in db after this point
                    // get thumbnail into database
                    if (recipeImageBitmap != null) { // Put thumbnail into the database
                        Log.d(TAG, "Adding thumbnail.");
                        byte[] tempImage = DbBitmapUtility.getBytes(recipeImageBitmap);
                        recipe.put("THUMBNAIL", tempImage);
                    }
                    recipe.commitChangesToViews();
                    productDatabase.updateOrAddEntry(recipe);
                    getActivity().setResult(RecipeEditorActivity.OK, returnIntent);
                    getActivity().finish();
                }
                return true;
            case R.id.edit_menu_item:
                setEditMode(true);
                return true;
            case R.id.done_menu_item:
            case R.id.menu_item_cancel:
                getActivity().setResult(RecipeEditorActivity.CANCEL, getActivity().getIntent());
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new GListCursorLoader(getActivity(), DatabaseHolder.getDatabase(getActivity()), recipeId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ingredientsList.setAdapter(new IngredientsAdapter(getActivity(), 0, data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ingredientsList.setAdapter(null);
    }

    private boolean isEmpty(String testString) {
        return testString == null || testString.trim().equals("");
    }

    @Override
    public void webpadeLoadFinished(Recipe loadedRecipe, List<Ingredients> ingredientsList) {
        try {
            progress.dismiss();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (loadedRecipe != null) {
            Log.d(TAG, "Callback function called");
            if (!isEmpty((String) loadedRecipe.get("NAME")))
                recipeName.setText((String) loadedRecipe.get("NAME"));
            if (!isEmpty((String) loadedRecipe.get("INSTRUCTIONS")))
                instructions.setText((String) loadedRecipe.get("INSTRUCTIONS"));
            if (!isEmpty((String) loadedRecipe.get("DESCRIPTION")))
                description.setText((String) loadedRecipe.get("DESCRIPTION"));
            if (loadedRecipe.get("SERVINGS") != null)
                servings.setText("" + loadedRecipe.get("SERVINGS"));

            if (loadedRecipe.get("THUMBNAIL") != null && !(loadedRecipe.get("THUMBNAIL") instanceof String) && ((byte[]) loadedRecipe.get("THUMBNAIL")).length > 1)
                setImage(DbBitmapUtility.getImage(getActivity(), ((byte[]) loadedRecipe.get("THUMBNAIL"))));
            if (!newRecipe) { // set recipe ID.
                for (Ingredients ing : ingredientsList)
                    ing.put("RECIPE_ID", Integer.parseInt(recipeId));
            }
            ((IngredientsAdapter) this.ingredientsList.getAdapter()).setIngredientsList(ingredientsList);
            ((IngredientsAdapter) this.ingredientsList.getAdapter()).notifyDataSetChanged();
        }else {
            Toast.makeText(getActivity(), "Failed to parse web page.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        setEditAble();
        getLoaderManager().restartLoader(2, null, thisActivity);
    }

    private class IngredientsAdapter extends ArrayAdapter<Object> {
        private List<Ingredients> ingredientArrayList;
        private final int ADD_TYPE=0;
        private final int SHOW_TYPE=0;
        private final int EDIT_TYPE=1;
        private int editPosition=-1;

        public IngredientsAdapter(Context context, int resource, Cursor cursor) {
            super(context, resource);
            ingredientArrayList = new LinkedList<Ingredients>();
            if(cursor.moveToFirst()) {
                do {
                    Ingredients ing = new Ingredients();
                    ing.setCursorData(cursor);
                    ingredientArrayList.add(ing);
                } while (cursor.moveToNext());
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int arg0) {
            return true;
        }

        @Override
        public int getCount(){

            if (editPosition == -1 && editMode)
                return ingredientArrayList.size() + 1;
            else
                return ingredientArrayList.size();
        }

        private void addNew(EditText editName){
            if (editName.getText().toString().trim().equals("")){
                Toast.makeText(getActivity(), "You must enter an ingredient name.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editPosition == -1) {
                ingredientArrayList.add(new Ingredients(Integer.parseInt(recipeId), editName.getText().toString(), (float) 1.0, "Each"));
            }else {
                Ingredients ing = ingredientArrayList.get(editPosition);
                ing.put("NAME", editName.getText().toString());
                editPosition = -1;
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            int itemType = getItemViewType(position);
            int itemTag=0;
            if (convertView != null)
                itemTag = (Integer) convertView.getTag();

            if (convertView == null || itemTag != itemType) {
                switch (getItemViewType(position)) {
                    case SHOW_TYPE:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_list_item, parent, false);
                        // Create tag map for handling multiple types
                        //convertView.setTag(new HashMap<String, Object>());
                        convertView.setTag(SHOW_TYPE);
                        break;
                    case EDIT_TYPE:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_edit_list_item, parent, false);
                        convertView.setTag(EDIT_TYPE);
                        break;
                }
            }
            switch (getItemViewType(position)) {
                case SHOW_TYPE:
                    EditTextListenerRemover name = (EditTextListenerRemover) convertView.findViewById(R.id.ingredient_name_check);
                    name.clearTextChangedListeners();
                    name.setText((String) ingredientArrayList.get(position).get("NAME"));
                    IngredientTextWatcher tw =new IngredientTextWatcher(ingredientArrayList.get(position));
                    name.addTextChangedListener(tw);

                    ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.ingredient_delete_button);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ingredientArrayList.remove(position);
                            notifyDataSetChanged();
                        }
                    });




                    final CheckBox ingredientAdd = (CheckBox) convertView.findViewById(R.id.list_add_checkbox);
                    ingredientAdd.setOnClickListener(null);
                    ingredientAdd.setChecked((Boolean) ingredientArrayList.get(position).get("USE_IN_LIST"));

                    ingredientAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Use in list set to: " + ((CheckBox) v).isChecked());
                            ingredientArrayList.get(position).put("USE_IN_LIST", ((CheckBox) v).isChecked());
                        }
                    });

                    // set editmode attributes
                    name.setFocusable(editMode);
                    name.setFocusableInTouchMode(editMode);

                    if (editMode) {
                        deleteButton.setVisibility(Button.VISIBLE);
                        ingredientAdd.setVisibility(CheckBox.VISIBLE);

                    } else {
                        deleteButton.setVisibility(Button.INVISIBLE);
                        ingredientAdd.setVisibility(CheckBox.INVISIBLE);
                    }

                    break;
                case EDIT_TYPE:
                    final EditText editName = (EditText)convertView.findViewById(R.id.ingredient_name_edit);
                    editName.setText("");

                    editName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE){
                                addNew(editName);
                                return true;
                            }
                            return false;
                        }
                    });

                    ImageButton clickAdd = (ImageButton)  convertView.findViewById(R.id.ingredients_edit_add_button);
                    clickAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addNew(editName);

                        }
                    });

                    ImageButton cancelButton = (ImageButton) convertView.findViewById(R.id.ingredients_edit_cancel_button);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editPosition != -1) {
                                editPosition = -1;
                                notifyDataSetChanged();
                            }
                        }
                    });

                    break;
            }

            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= ingredientArrayList.size() || position == editPosition)
                return EDIT_TYPE;
            else
                return SHOW_TYPE;
        }

        public List<Ingredients> getIngredientList() {
            return ingredientArrayList;
        }

        public void setIngredientsList(List<Ingredients> ingredientsList) {
            this.ingredientArrayList = ingredientsList;
        }
    }

    private class IngredientTextWatcher implements TextWatcher {
        private Ingredients thisIngredients;
        public IngredientTextWatcher(Ingredients ing) {
            thisIngredients = ing;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, s.toString() + " was: " + thisIngredients.get("NAME"));
            thisIngredients.put("NAME", s.toString());

        }

    }

    private static class GListCursorLoader extends SQLiteCursorLoader {
        private String recipeId;
        private ProductListDB productDatabase;

        public GListCursorLoader(Context activity, ProductListDB db, String recipeId) {
            super(activity);
            this.recipeId = recipeId;
            productDatabase = db;
        }

        @Override
        protected Cursor loadCursor() {
            return productDatabase.getIngredientsForRecipe(recipeId);
        }
    }

    private class WebpageLoader extends AsyncTask<String, Void ,WebsiteParser.FormattedData> {
        private String TAG="webpageloader";
        private WebpageLoaderCallback wc;

        public WebpageLoader(Context context, WebpageLoaderCallback wc) {
            super();
            this.wc = wc;

        }

        @Override
        protected WebsiteParser.FormattedData doInBackground(String... params) {
            Log.d(TAG, params[0]);
            WebsiteParser wp = new WebsiteParser();
            WebsiteParser.FormattedData result = wp.parseSite(params[0]);
            return result;

        }

        @Override
        protected void onPostExecute(WebsiteParser.FormattedData result) {
            super.onPostExecute(result);
            if (result != null)
                wc.webpadeLoadFinished(result.recipe, result.ingredients);
            else
                wc.webpadeLoadFinished(null, null);
        }

    }
}
