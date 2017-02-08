package com.example.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class ProductListDB extends SQLiteOpenHelper {

	
	public static final String DATABASE_NAME = "grocery_list_2013.db";
	public static final int DATABASE_VERSION = 30;
    private List<TableMap> tables;
    private final String TAG="database";
    // If we update add new columns and tables if needed to database.
	
	public ProductListDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		tables = new ArrayList<TableMap>();
        tables.add(new Product());
        tables.add(new GList());
        tables.add(new Recipe());
        tables.add(new RecipeList());
        tables.add(new Ingredients());
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        for (TableMap table : tables)
            db.execSQL(table.getTableCreatString());
        //db.execSQL((new Product()).getTableCreatString());
        //db.execSQL((new GList()).getTableCreatString());
        //db.execSQL((new Recipe()).getTableCreatString());
        //db.execSQL((new RecipeList()).getTableCreatString());
        //db.execSQL((new ListOProducts()).getTableCreatString());
        //db.execSQL((new RecipeOProducts()).getTableCreatString());
        //db.execSQL((new Ingredients()).getTableCreatString());
        //createDummyList(db);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("management", "Upgrading database.");
		
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + Product.TABLE_PRODUCTS);
        //db.execSQL("DROP TABLE IF EXISTS " + GList.TABLE_LIST);
        //db.execSQL("DROP TABLE IF EXISTS " + ListOProducts.TABLE_LIST_OF_PRODUCTS);
        //db.execSQL("DROP TABLE IF EXISTS " + RecipeOProducts.TABLE_RECIPE_PRODUCTS);
        //db.execSQL("DROP TABLE IF EXISTS " + Recipe.TABLE_RECIPE);
        //db.execSQL("DROP TABLE IF EXISTS " + RecipeList.RECIPE_LIST_TABLE);
        //db.execSQL("DROP TABLE IF EXISTS " + Ingredients.TABLE_PRODUCTS);

        // Create tables again
        //onCreate(db);
        Cursor c = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table'", null);
        List<String> tableNames = new ArrayList<String>();

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                String tableName = c.getString(c.getColumnIndex("name"));
                Log.d(TAG, "Found table: " + tableName);
                tableNames.add(tableName);
                boolean inTableList = false;
                TableMap updateTable = null;
                for (TableMap table : tables) {
                    if (table.getTableName().equals(tableName)) {
                        updateTable = table;
                        inTableList = true;
                    }
                }

                if (inTableList) {
                    Log.d(TAG, "Found table in list");
                    //checkTableFields(db, tableName, updateTable);
                } else {
                    String type = c.getString(c.getColumnIndex("type"));
                    int rootPage = c.getInt(c.getColumnIndex("rootpage"));
                    String sql = c.getString(c.getColumnIndex("sql"));
                    //if (rootPage != 3)
                    // Table does not exist in our data strictures.  Remove.
                    //    db.execSQL("DROP TABLE IF EXISTS " + tableName);
                }
                c.moveToNext();
            }
        }
        c.close();

        // check database for all definition tables and if it doesn't exist create.
        for (TableMap table: tables) {
            String defTableName = table.getTableName();

            //if (!tableNames.contains(defTableName)) {
            //    db.execSQL(table.getTableCreatString());
            //}
            copyTable(db, defTableName, table);

        }

	}

    private void copyTable(SQLiteDatabase db, String tableName, TableMap updateTable) {
        Cursor dbCursor = db.query(tableName, null, null, null, null, null, null);
        List<String> colSet = new ArrayList<>(Arrays.asList(dbCursor.getColumnNames()));
        List<String> defColumnNames = new ArrayList<>(Arrays.asList(updateTable.getColumns()));
        Log.d(TAG, "DB column set: " + colSet.toString());
        Log.d(TAG, "Definitions column set: " + defColumnNames.toString());
        colSet.retainAll(defColumnNames);
        Log.d(TAG, "DB column definition intersect: " + colSet.toString());
        // create new table
        String tableCreateString = updateTable.getTableCreatString();
        String tableCreateStringCopy=tableCreateString.replace(updateTable.getTableName(), updateTable.getTableName()+"_COPY");
        Log.d(TAG, tableCreateStringCopy);
        db.execSQL(tableCreateStringCopy);
        if (!colSet.isEmpty()){
            // copy over old table
            int colDif = defColumnNames.size()-colSet.size();
            for(int i=0; i<colDif;i++)
                colSet.add("NULL");
            String copyString = "INSERT INTO " + updateTable.getTableName() + "_COPY" + " SELECT " + StringUtils.join(colSet, ", ")+ " FROM " + updateTable.getTableName();
            Log.d(TAG, copyString);
            db.execSQL(copyString);
        }
        // delete old table
        String dropTable = "DROP TABLE IF EXISTS " + updateTable.getTableName();
        Log.d(TAG, dropTable);
        db.execSQL(dropTable);
        // rename new table to old
        String renameTable = "ALTER TABLE " + updateTable.getTableName()+"_COPY" + " RENAME TO " + updateTable.getTableName();
        Log.d(TAG, renameTable);
        db.execSQL(renameTable);
    }

    private void checkTableFields(SQLiteDatabase db, String tableName, TableMap updateTable) {
        Cursor dbCursor = db.query(tableName, null, null, null, null, null, null);
        List<String> dbColumnNames = new ArrayList<String>(Arrays.asList(dbCursor.getColumnNames()));
        List<String> defColumnNames = new ArrayList<String>(Arrays.asList(updateTable.getColumns()));

        // check all columns in database and remove if not present
        for (String colName : dbColumnNames) {
            if (!defColumnNames.contains(colName)) {
                Log.d(TAG, "Database table not in the definitions." + colName);
                //db.execSQL("ALTER TABLE " + tableName + " DROP COLUMN " + colName);
                //  Drop column from table.  Can't use SQL statement to do this.  Need to copy table without column, delete old, copy back.
            }
        }

        // check all colums in definitions agains database and add if not present.
        int i = 0;
        for (String colName : defColumnNames){
            if (!dbColumnNames.contains(colName)) {
                Log.d(TAG, "Column " + colName + " not in database.  Adding.");
                String addColString = "ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + updateTable.getColumnTypes()[i];
                Log.d(TAG, addColString);
                db.execSQL(addColString);
            }
            i++;
        }

    }


    private void createDummyList(SQLiteDatabase productDatabase) {
        Random r = new Random();

        int numProducts = 50;
        int numLists = 10;
        int numRecipes = 20;
        int numDays = 7;

        //clearAllTables();
        List<GList> tempList = new ArrayList<GList>();
        for (int i=0; i<numLists; i++) {
            GList newList = new GList("My List " + (i + 1));
            addEntryToDatabase(newList, productDatabase);
            tempList.add(newList);
        }

        //for (int i=0; i<30; i++)
        //	productDatabase.addEntryToDatabase(new Product("My product " + (i + 1), "Type"));
        int listId = 3;

        //List<Product> tempProds = productDatabase.getProducts();
        for (int i=0; i<numProducts; i++) {
            Product newList = new Product((Integer) tempList.get(r.nextInt(numLists)).get("_id"), "My product " + (r.nextInt(numProducts) + 1), "Unknown", i, "-1", false);
            addEntryToDatabase(newList, productDatabase);

        }
        Recipe[] ra = new Recipe[numRecipes];
        for (int i=0; i<numRecipes; i++) {
            Recipe re = new Recipe("Recipe " + i, "These are some instructions " + i);
            ra[i] = re;
            addEntryToDatabase(re, productDatabase);
        }
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i=0; i<numDays; i++) {
            for (int j=0; j<12; j++) {
                RecipeList rl = new RecipeList((Integer) tempList.get(r.nextInt(numLists)).get("_id"), (Integer) ra[r.nextInt(numRecipes)].get("_id"), days[i]);
                addEntryToDatabase(rl, productDatabase);
            }
        }

        //productDatabase.getProductsFromList(tempList.get(3));
    }

	
	private <T> List<T> getAllGeneric(String tableName, Class<T> thisClass) throws InstantiationException, IllegalAccessException {
        List<T> contactList = new ArrayList<T>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + tableName;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	TableElement tt = (TableElement) thisClass.newInstance();
            	tt.setCursorData(cursor);
                // Adding contact to list
                contactList.add((T) tt);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return contactList;
	}

    
    public List<Product> getProducts() {
        
        // Select All Query
        List<Product> retProd=null;
        try {
        retProd = getAllGeneric(Product.TABLE_PRODUCTS, Product.class);
        } catch (Exception e) {
        	Log.d("management", "Error creating class in get products function.");
        	e.printStackTrace();
        }
        
        // return contact list
        return retProd;
    }

    
    public Cursor getListsCursor() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor newCursor = db.rawQuery("SELECT * FROM " + GList.TABLE_LIST, null);
    	Log.d("management", Arrays.toString(newCursor.getColumnNames()));
    	return new GListCursorWrapper(newCursor);
    }
    
    /**
     * Get a list of products of a given grocery list given the list id.
     */
    public Cursor getProdsFromList(GList glist) {

        return getProdsFromList("" + glist.get("_id"), "CHECK_OUT ASC");
    }

    
    public Cursor getProdsFromList(String getID, String sort) {
    	//Cursor cursor = db.rawQuery(SQLStatement, null);
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor cursor = db.query(Product.TABLE_PRODUCTS,
                Product.PRODUCT_COLUMNS,
    			"LIST_ID=" + getID,
    			null, null, null, sort);
        
        Log.d("management", "Generated list of products from list based on SQL " /*+ SQLStatement*/);
        
        return new WorkingProductCursorWrapper(cursor);
    	
    }


    
    public List<GList> getLists() {
        
        // Select All Query
        List<GList> retProd=null;
        try {
        retProd = getAllGeneric(GList.TABLE_LIST, GList.class);
        } catch (Exception e) {
        	Log.d("error", "Error creating class in get products function.");
        	e.printStackTrace();
        }
        
        // return contact list
        return retProd;
    }

    
    
    /**
     * Return all of the recipes
     * @return array list of recipes
     */
    public List<Recipe> getRecipes() {
        
        // Select All Query
        List<Recipe> retProd=null;
        try {
        retProd = getAllGeneric(Recipe.TABLE_RECIPE, Recipe.class);
        } catch (Exception e) {
        	Log.d("management", "Error creating class in get products function.");
        	e.printStackTrace();
        }
        
        // return contact list
        return retProd;
    }

    public Integer addEntryToDatabase(TableMap entry ) {
        SQLiteDatabase db = this.getWritableDatabase();

        return addEntryToDatabase(entry, db);
    }

    public Integer addEntryToDatabase(TableMap entry, SQLiteDatabase db ) {

        long epoch = System.currentTimeMillis();
        entry.put("TIMESTAMP", epoch/1000);
        Integer rowId = new Integer ( (int) db.insert(entry.getTableName(), null, entry.getValuesContainer()));
        entry.put("_id", rowId);
        return rowId;
    }


	public void clearAllTables() {
        Log.d("management", "Clearing entried in DB" /*+ SQLStatement*/);
		SQLiteDatabase db = this.getWritableDatabase();
        for (TableMap table : tables )
            db.delete(table.getTableName(), null, null);
		//db.delete(Product.TABLE_PRODUCTS, null, null);
		//db.delete(GList.TABLE_LIST, null, null);
		//db.delete(Recipe.TABLE_RECIPE, null, null);
		//db.delete(ListOProducts.TABLE_LIST_OF_PRODUCTS, null, null);
		//db.delete(RecipeOProducts.TABLE_RECIPE_PRODUCTS, null, null);
		
		db.close();
	}

	public void changeCheckout(String id, boolean newValue, long checkTime) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d(TAG, "Checkout time: " + checkTime);
		String boolString = "TRUE";
		if (!newValue)
			boolString = "FALSE";
		ContentValues values = new ContentValues();
		values.put("CHECK_OUT", newValue);
        values.put("CHECKOUT_TIME", checkTime);
		Log.d("management", "Setting id " + id + " to " + newValue);
		db.update(Product.TABLE_PRODUCTS, values, "_id=" + id, null);
		//db.close();
	}

    public List<String> getFilteredProducts(String constraint) {
        SQLiteDatabase db = this.getWritableDatabase();

//        Cursor cursor = db.query(true, Product.TABLE_PRODUCTS,
//                new String[]{"NAME"},
//                "NAME LIKE '%" + constraint + "%'",
//                null, null, null, null, null);
        Cursor cursor = db.query(true, Product.TABLE_PRODUCTS,
                new String[]{"NAME"},
                "NAME LIKE ?",
                new String[] {"%"+constraint+"%"}, null, null, null, null);

        List<String> filteredProducts = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                // Adding contact to list
                filteredProducts.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return filteredProducts;
    }

    public long getProductIdByName(String s) {
        long productId=0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Product.TABLE_PRODUCTS,
                new String[]{"_id"},
                "NAME = '" + s + "'",
                null, null, null, null);
        if (cursor.moveToFirst()) { // If there is an entry with the name return the id
            Log.d("management", "Found product.");
            productId = cursor.getLong(0);
        }
        return productId;
    }

    public void deleteProductByID(Object id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("database", "Deleting id " + id);
        int result = db.delete(Product.TABLE_PRODUCTS, "_id=" + id, null );
        Log.d("database", "Deleted " + result + " records.");
    }

    public void updateTable(String tableListOfProducts, ContentValues cv, String id) {
        Log.d("database", "Updating database " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = db.update(tableListOfProducts, cv,  "_id=?",new String[]{id});
        Log.d("database", "Updating " + updated + "records");
    }

    private void printAll(Cursor reciepeCursor) {
        if (reciepeCursor.moveToFirst()) {
            do {
                for (int i = 0; i < reciepeCursor.getColumnCount(); i++) {
                    Log.d("database", reciepeCursor.getColumnName(i) + ": " + reciepeCursor.getString(i));
                }
            } while (reciepeCursor.moveToNext());
        }
    }

    public Cursor getRecipesFromList(String listId) {
        Log.d("database", "Creating recipe list cursor");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor reciepeCursor = db.query(Recipe.TABLE_RECIPE+ "," +RecipeList.RECIPE_LIST_TABLE,
                new String[]{"RECIPES._id", "NAME", "DAY", "INSTRUCTIONS", "RECIPE_LIST._id AS RECIPE_LIST_ID, THUMBNAIL"},
                "RECIPE_LIST.LIST_ID=" + listId + " AND RECIPES._id=RECIPE_LIST.RECIPE_ID",null, null, null, null);

        RecipeListCursorWrapper rcl = new RecipeListCursorWrapper(reciepeCursor);
        return rcl;
    }

    public Cursor getAllRecipes(final String filterString) {
        Log.d("database", "Creating recipe list cursor");
        SQLiteDatabase db = this.getWritableDatabase();

        /*
        Cursor reciepeCursor = db.query(Recipe.TABLE_RECIPE +" LEFT JOIN " + RecipeList.RECIPE_LIST_TABLE,
                new String[]{Recipe.TABLE_RECIPE + "._id", "NAME", "INSTRUCTIONS", "COUNT(RECIPE_LIST._id) AS RECICOUNT"},
                Recipe.TABLE_RECIPE + "._id = " + RecipeList.RECIPE_LIST_TABLE + ".RECIPE_ID",null, "NAME", null,null);
        */
        final String[] queryArgs = new String[] { "%" + filterString + "%"};

        Cursor reciepeCursor = db.rawQuery("SELECT RECIPES._id, NAME, INSTRUCTIONS, COUNT(RECIPE_LIST._id) AS RECCOUNT, RECIPE_LIST.TIMESTAMP, THUMBNAIL FROM RECIPES LEFT JOIN RECIPE_LIST ON RECIPES._id = RECIPE_LIST.RECIPE_ID WHERE NAME LIKE ? GROUP BY NAME ORDER BY RECCOUNT DESC, RECIPE_LIST.TIMESTAMP DESC", queryArgs);
        //printAll(reciepeCursor);
        return reciepeCursor;

    }

    public Cursor getIngredientsForRecipe(String listId) {
        Log.d("database", "Creating recipe list cursor");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor recipeCursor = db.query(Ingredients.TABLE_PRODUCTS,
                new String[]{"INGREDIENTS._id", "RECIPE_ID", "INGREDIENTS.NAME", "USE_IN_LIST"},
                "INGREDIENTS.RECIPE_ID=" + listId,null, null, null,null);
        //Cursor recipeCursor = db.rawQuery("SELECT INGREDIENTS._id, RECIPE_ID, INGREDIENTS.NAME, QUANTITY, UNITS FROM INGREDIENTS",null); //JOIN RECIPES ON INGREDIENTS.RECIPE_ID=" + listId + "", null);
        this.printAll(recipeCursor);
        Log.d("database", "Listid " + listId);
        return recipeCursor;
    }

    public void updateOrAddEntry(TableMap ing) {

        SQLiteDatabase db = this.getWritableDatabase();
        if (ing.get("_id") != null && (Integer)ing.get("_id") > 0)
            db.update(ing.getTableName(), ing.getValuesContainer(),"_id=" + ing.get("_id"),null);
        else
            addEntryToDatabase(ing, db);

    }

    public void deleteIngredientsWithId(String recipeListId) {

        Log.d("database", "Deleting old products with recipe id " + recipeListId);
        if (recipeListId != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            int numDeleted = db.delete(Product.TABLE_PRODUCTS, "RECIPE_LIST_ID = " + recipeListId, null);
            Log.d("database", "Deleting " + numDeleted + " records with list id " + recipeListId);
        }
    }

    public void deleteRecipeListItem(String recipeListId) {
        Log.d("database", "Deleting recipe list with id " + recipeListId);

        SQLiteDatabase db = this.getWritableDatabase();
        deleteIngredientsWithId(recipeListId);
        int numDeleted = db.delete(RecipeList.RECIPE_LIST_TABLE, "_id = " + recipeListId, null);
        Log.d("database", "Deleting " + numDeleted + " records with list id " + recipeListId);
    }

    public void deleteIngredientsFromRecipe(Recipe recipe) {
        Log.d("database", "Deleting  ingredient with id " + recipe.get("_id"));
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(Ingredients.TABLE_PRODUCTS, "RECIPE_ID = " + recipe.get("_id"), null);
        Log.d("database", "Deleting " + numDeleted + " records with list id " + recipe.get("_id"));
    }

    public Cursor getRecipeFromId(String stringExtra) {
        Log.d("database", "Getting recipe with id " + stringExtra);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor recipeCursor = db.query(Recipe.TABLE_RECIPE,
                Recipe.RECIPE_COLUMNS,
                "_id = " + stringExtra, null, null, null, null, null);
        recipeCursor.moveToFirst();
        return recipeCursor;
    }

    public void deleteMultipleProducts(String whereString) {
        Log.d("database", "Deleting  ingredient with id " + whereString);
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(Product.TABLE_PRODUCTS, whereString, null);
        Log.d("database", "Deleting " + numDeleted);
    }

    public void updateListName(String listId, String newListName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("NAME", newListName);
        db.update(GList.TABLE_LIST,cv,"_id=" + listId, null);
    }

    public void removeList(int listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // remove list
        db.delete(GList.TABLE_LIST, "_id="+listId,null);
        // remove products
        db.delete(Product.TABLE_PRODUCTS, "LIST_ID="+listId,null);
        // remove recipes
        db.delete(RecipeList.RECIPE_LIST_TABLE, "LIST_ID="+listId, null);
    }


}
