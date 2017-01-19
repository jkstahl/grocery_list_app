package com.example.grocerylist;



import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class GListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	protected ProductListDB productDatabase;
	private GListCursorWrapper mGListCursor;

	@Override
	public void onCreate(Bundle sis) {
		super.onCreate(sis);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.app_name);
		//getActivity().getApplicationContext().deleteDatabase(ProductListDB.DATABASE_NAME);
		productDatabase = DatabaseHolder.getDatabase(getActivity());
		//createDummyList();
		//mGListCursor = (GListCursor)productDatabase.getListsCursor();
		//this.setListAdapter(new GListAdapter(mGListCursor));
		getLoaderManager().initLoader(0, null, this);
		//this.getView().setPadding(3,3,3,3);
	}

	private void createDummyList() {
		Random r = new Random();

		int numProducts = 50;
		int numLists = 10;
		int numRecipes = 20;
		int numDays = 7;

		productDatabase.clearAllTables();
		for (int i=0; i<numLists; i++)
			productDatabase.addEntryToDatabase(new GList("My List " + (i + 1)));

		//for (int i=0; i<30; i++)
		//	productDatabase.addEntryToDatabase(new Product("My product " + (i + 1), "Type"));
		int listId = 3;
		List<GList> tempList = productDatabase.getLists();
		//List<Product> tempProds = productDatabase.getProducts();
		for (int i=0; i<numProducts; i++) {
			Product newList = new Product((Integer) tempList.get(r.nextInt(numLists)).get("_id"), "My product " + (r.nextInt(numProducts) + 1), "Unknown", i, "", false);
			productDatabase.addEntryToDatabase(newList);

		}
		Recipe[] ra = new Recipe[numRecipes];
		for (int i=0; i<numRecipes; i++) {
			Recipe re = new Recipe("Recipe " + i, "These are some instructions " + i);
			ra[i] = re;
			productDatabase.addEntryToDatabase(re);
		}
		String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
		for (int i=0; i<numDays; i++) {
			for (int j=0; j<12; j++) {
				RecipeList rl = new RecipeList((Integer) tempList.get(r.nextInt(numLists)).get("_id"), (Integer) ra[r.nextInt(numRecipes)].get("_id"), days[i]);
				productDatabase.addEntryToDatabase(rl);
			}
		}

		//productDatabase.getProductsFromList(tempList.get(3));
	}

	@Override
	public void onDestroy() {
		//mGListCursor.close();
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("management", "Clicked Item " + position);
		String listID = "" + ((GListCursorWrapper) l.getAdapter().getItem(position)).getGList().get("_id");
		String listName = "" + ((GListCursorWrapper) l.getAdapter().getItem(position)).getGList().get("NAME");
		Log.d("management", "ID is " + listID);
		openList(listID, listName);
		//DatabaseHolder.getDatabase(getActivity()).getProductsFromList(glist)
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu items for use in the action bar
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main_activity_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("ItemSelect", Integer.toString(item.getItemId()));
		switch (item.getItemId()) {
			case R.id.add_list:
				Log.d("AddListSelect", "Add a List selected.");

				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				Date date = new Date();
				String curDate = dateFormat.format(date);
				Log.d("AddListSelect", curDate);
				long rowId = productDatabase.addEntryToDatabase(new GList(curDate));
				Log.d("AddListSelect", productDatabase.getLists().toString());
				updateList();
				openList("" + rowId, curDate);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void updateList() {
		//mGListCursor = (GListCursor)productDatabase.getListsCursor();
		//((GListAdapter)this.getListAdapter()).swapCursor(mGListCursor);
		//mGListCursor.requery();
		//((GListAdapter)this.getListAdapter()).notifyDataSetChanged();
		getLoaderManager().restartLoader(0, null, this);
		Log.d("updateList", "Updating list");

	}

	public void openList(String rowId, String listName) {
		Intent i = new Intent(getActivity(), SwipeMainScreen.class);
		i.putExtra("ListID", rowId);
		i.putExtra("LIST_NAME", listName);
		startActivity(i);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new GListCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		GListAdapter adapter = new GListAdapter((GListCursorWrapper) data);
		setListAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		setListAdapter(null);
	}

	private class GListAdapter extends CursorAdapter {
		private GListCursorWrapper listCursor;

		public GListAdapter(GListCursorWrapper cursor) {
			super(getActivity(), cursor, 0);
			listCursor = cursor;
			
		}
		
		@Override
		public View newView (Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			return view;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			GList glist = listCursor.getGList();
			TextView gName = (TextView) view;
			String nameString = (String) glist.get("NAME");
			Log.d("bindView", nameString);
			gName.setText(nameString);
			
		}
	}

	private static class GListCursorLoader extends SQLiteCursorLoader {
		public GListCursorLoader(Context activity) {
			super(activity);
		}

		@Override
		protected Cursor loadCursor() {
			return DatabaseHolder.getDatabase(getContext()).getListsCursor();
		}
	}
}
