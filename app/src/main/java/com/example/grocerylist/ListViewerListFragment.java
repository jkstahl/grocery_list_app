package com.example.grocerylist;



import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ListViewerListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {
	private ProductListDB productDatabase;
	private ProductInListAdapter adapter;
	private String thisID;


	@Override
	public void onCreate(Bundle sis) {
		super.onCreate(sis);
		setHasOptionsMenu(true);
		Log.d("debug", "created list fragment");
		thisID = this.getActivity().getIntent().getStringExtra("ListID");
		productDatabase = DatabaseHolder.getDatabase(getActivity());

		getLoaderManager().initLoader(1, null, this);

	}


	public void updateList() {
		getLoaderManager().restartLoader(0, null, this);

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));



	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("actions", "Clicked Item " + position);
		//Log.d("actions", "ID is " + ((ListOProdsCursorWrapper) l.getAdapter().getItem(position)).getGList().get("_id"));
		//DatabaseHolder.getDatabase(getActivity()).getProductsFromList(glist)
		Product wp = (Product) ((WorkingProductCursorWrapper) adapter.getItem(position)).getWorkingProduct();
		ProductPackager pp = new ProductPackager(wp.getValuesContainer());
		Log.d("actions", "working product name " + ((String)wp.get("NAME")));
		Intent i = pp.getIntent(getActivity(), ProductEditActivity.class);

		startActivityForResult(i, ProductEditActivity.ACTIVITY_ID);
		//openList(listID);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("return", "Result return. " + requestCode);
		switch(requestCode) {
			case (ProductEditActivity.ACTIVITY_ID) : {
				if (resultCode == ProductEditActivity.OK) {
					Log.d("return", "Returned from Product edit activity with OK to change.");
					ProductPackager pp = new ProductPackager(data);
					ContentValues cv = pp.getValuesContainer();
					productDatabase.updateTable(Product.TABLE_PRODUCTS, cv, "" + cv.get("_id"));
					updateList();
					//productDatabase.updateProductInList();
				} else if (resultCode == ProductEditActivity.DELETE) {
					Log.d("return", "Returned from Product edit activity with delete.");
					productDatabase.deleteProductByID(Integer.parseInt(data.getStringExtra("_id")));
					updateList();
				}


				break;
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu items for use in the action bar
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main_activity_menu, menu);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new GListCursorLoader(getActivity(), productDatabase, thisID);
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		ProductInListAdapter adapter = new ProductInListAdapter((WorkingProductCursorWrapper) data);
		this.adapter=adapter;
		setListAdapter(adapter);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
		setListAdapter(null);
	}



	private class CheckoutListener implements View.OnClickListener {

		private int productId;
		
		public CheckoutListener(int productId) {
			this.productId = productId;
		}


		@Override
		public void onClick(View v) {

			DatabaseHolder.getDatabase(getActivity()).changeCheckout("" + this.productId, ((CheckBox) v).isChecked());
			updateList();
		}
	}

	private class ProductInListAdapter extends CursorAdapter {
		private WorkingProductCursorWrapper listCursor;

		public ProductInListAdapter(WorkingProductCursorWrapper cursor) {
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
			View view = inflater.inflate(R.layout.list_prod_item, parent, false);

			//View view = getActivity().getLayoutInflater().inflate(R.layout.list_prod_item, null);

			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Product wp =listCursor.getWorkingProduct();
			//GList glist = listCursor.getGList();
			//TextView gName = (TextView) view;
			//String nameString = (String) glist.get("NAME");
			//Log.d("bindView", nameString);
			//gName.setText(nameString);
			TextView tv = (TextView) view.findViewById(R.id.g_list_name);
			tv.setText((String) wp.get("NAME"));
			tv = (TextView) view.findViewById(R.id.text_listi_quantity);
			tv.setText("" + wp.get("QUANTITY"));
			tv = (TextView) view.findViewById(R.id.text_listi_units);
			tv.setText("" + wp.get("UNITS"));

			CheckBox cb = (CheckBox) view.findViewById(R.id.check_out_box);
			Log.d("management", "checkout id " + wp.get("_id") + " is " + wp.get("CHECK_OUT"));
			//cb.setSelected(p.get("CHECK_OUT").equals("TRUE"));
			cb.setOnCheckedChangeListener(null);
			cb.setChecked((Boolean) wp.get("CHECK_OUT"));

			//cb.setOnCheckedChangeListener(new CheckoutListener((Integer) wp.get("_id")));
			cb.setOnClickListener(new CheckoutListener((Integer) wp.get("_id")));
		}
	}

	private static class NewCurse extends CursorLoader {

		public NewCurse(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
			super(context, uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	private static class GListCursorLoader extends SQLiteCursorLoader {
		private String listId;
		private ProductListDB productDatabase;

		public GListCursorLoader(Context activity, ProductListDB db, String thisID) {
			super(activity);
			listId = thisID;
			productDatabase = db;
		}

		@Override
		protected Cursor loadCursor() {
			return productDatabase.getProdsFromList(listId);
		}
	}
	
}
