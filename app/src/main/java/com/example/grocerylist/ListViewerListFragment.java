package com.example.grocerylist;



import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ListViewerListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {
	//private ProductListDB productDatabase;
	private ProductInListAdapter adapter;
	private String thisID;
	private final String TAG= "listviewer";
	private final int LOADER_ID=1;


	@Override
	public void onCreate(Bundle sis) {
		super.onCreate(sis);

		Log.d("debug", "created list fragment");
		thisID = this.getActivity().getIntent().getStringExtra("ListID");
		//productDatabase = DatabaseHolder.getDatabase(getActivity());
		getLoaderManager().initLoader(LOADER_ID, null, this);
		//this.setRetainInstance(true);

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("lifecycle", "Resume listviewer");
		updateList();

	}


	public void updateList() {
		getLoaderManager().restartLoader(LOADER_ID, null, this);

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//this.getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
		getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				getListView().setItemChecked(position, true);
				return true;
			}
		});

		getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

			@Override
			public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {

				Log.d(TAG, "Action Item Clicked.");
				switch (arg1.getItemId()) {
					case R.id.delete_product_menu_item:
						ProductInListAdapter adapter = (ProductInListAdapter)getListAdapter();
						String whereString = "";
						String orString = " or ";
						for (int i = adapter.items.size() - 1; i >= 0; i--) {

							if (getListView().isItemChecked(i) && !(adapter.items.get(i) instanceof String)) {
								Product wp = (Product) adapter.items.get(i);
								whereString += "_id=" + wp.get("_id")  + orString;
							}
						}
						if (whereString.length() > 0)
							whereString = whereString.substring(0, whereString.length()-orString.length());
						Log.d(TAG, whereString);
						DatabaseHolder.getDatabase(getActivity()).deleteMultipleProducts(whereString);
						arg0.finish();
                        updateList();
						return true;
				}
				return false;
			}

			/*
             *
             */
			@Override
			public boolean onCreateActionMode(ActionMode arg0, Menu menu) {
				getActivity().getMenuInflater().inflate(
						R.menu.product_selection_menu, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode arg0) {
			}

			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode actionMode,
												  int position, long id, boolean arg3) {
            /*
             * Change Title bar to number of selection
             */

				int checkedItems = getListView().getCheckedItemCount();
				actionMode.setTitle(String.valueOf(checkedItems) + " Selected");

			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("actions", "Clicked Item " + position);
		if (adapter.items.get(position) instanceof Product) {
			//Log.d("actions", "ID is " + ((ListOProdsCursorWrapper) l.getAdapter().getItem(position)).getGList().get("_id"));
			//DatabaseHolder.getDatabase(getActivity()).getProductsFromList(glist)
			Product wp = (Product) adapter.items.get(position);
			ProductPackager pp = new ProductPackager(wp.getValuesContainer());
			Log.d("actions", "working product name " + ((String) wp.get("NAME")));
			Intent i = pp.getIntent(getActivity(), ProductEditActivity.class);

			startActivityForResult(i, ProductEditActivity.ACTIVITY_ID);
		}
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
					DatabaseHolder.getDatabase(getActivity()).updateTable(Product.TABLE_PRODUCTS, cv, "" + cv.get("_id"));
					updateList();
					//productDatabase.updateProductInList();
				} else if (resultCode == ProductEditActivity.DELETE) {
					Log.d("return", "Returned from Product edit activity with delete.");
					DatabaseHolder.getDatabase(getActivity()).deleteProductByID(Integer.parseInt(data.getStringExtra("_id")));
					updateList();
				}


				break;
			}
		}
	}



	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new GListCursorLoader(getActivity(), DatabaseHolder.getDatabase(getActivity()), thisID);
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		ProductInListAdapter adapter = new ProductInListAdapter(getActivity(), 0, (WorkingProductCursorWrapper) data);
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
			long epoch = (long) System.currentTimeMillis();
			DatabaseHolder.getDatabase(getActivity()).changeCheckout("" + this.productId, ((CheckBox) v).isChecked(), epoch);


			updateList();
		}
	}

	private class ProductInListAdapter extends ArrayAdapter<Object> {
		private static final int PRODUCT_TYPE = 0;
		private static final int CATEGORY_TYPE = 1;
		private static final int PRODUCT_NAME_MAX_SIZE = 30;

		private WorkingProductCursorWrapper listCursor;
		List<Object> items;

		@Override
		public int getCount(){

			return items.size();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (items.get(position) instanceof String)
				return CATEGORY_TYPE;
			else
				return PRODUCT_TYPE;
		}

		public ProductInListAdapter(Context context, int resource, WorkingProductCursorWrapper cursor) {
			super(context, resource);
			listCursor = cursor;
			String sortColumn = "TYPE";
			items = new ArrayList<>();

			if (cursor.moveToFirst()) {
				String lastCat = null;
				int lastCheck = 0;
				do {
					String category = cursor.getString(cursor.getColumnIndex(sortColumn));
					int checked = cursor.getInt(cursor.getColumnIndex("CHECK_OUT"));
					if (checked != lastCheck) {
						items.add("Checked Off");
						lastCheck = checked;
					} else if (category != null && !category.equals(lastCat) && checked == 0) {
						items.add(category);
						lastCat = category;
					}
					items.add(listCursor.getWorkingProduct());

				} while (cursor.moveToNext());
			}
			Log.d(TAG, "Items List: " + items.toString());
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			if (items.get(position) instanceof String)
				return false;

			return true;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			int itemType = getItemViewType(position);
			int itemTag = 0;
			if (convertView != null)
				itemTag = (Integer) convertView.getTag();

			if (convertView == null || itemTag != itemType) {
				//LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				switch (itemType) {
					case PRODUCT_TYPE:
						convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_prod_item, parent, false);
						convertView.setTag(PRODUCT_TYPE);
						break;
					case CATEGORY_TYPE:
						convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_category, parent, false);
						convertView.setTag(CATEGORY_TYPE);
						break;
				}
			}
			switch (itemType) {
				case PRODUCT_TYPE:
					Product wp = (Product) items.get(position);

					TextView tv = (TextView) convertView.findViewById(R.id.g_list_name);
					String productName = (String) wp.get("NAME");
					if (productName.length() > PRODUCT_NAME_MAX_SIZE)
						productName = productName.substring(0, PRODUCT_NAME_MAX_SIZE) + "...";
					tv.setText(productName);
					tv = (TextView) convertView.findViewById(R.id.text_listi_quantity);
					float quantity = (Float) wp.get("QUANTITY");
					String units = (String) wp.get("UNITS");
					TextView unitsView = (TextView) convertView.findViewById(R.id.text_listi_units);
					if (quantity == 1 && (units.equals("each") || units.equals(""))) {
						tv.setText("");
						unitsView.setText("");
					} else {
						tv.setText("" + quantity);
						unitsView.setText(units);
					}

					CheckBox cb = (CheckBox) convertView.findViewById(R.id.check_out_box);
					Log.d("management", "checkout id " + wp.get("_id") + " is " + wp.get("CHECK_OUT"));
					//cb.setSelected(p.get("CHECK_OUT").equals("TRUE"));
					cb.setOnCheckedChangeListener(null);
					cb.setChecked((Boolean) wp.get("CHECK_OUT"));

					//cb.setOnCheckedChangeListener(new CheckoutListener((Integer) wp.get("_id")));
					cb.setOnClickListener(new CheckoutListener((Integer) wp.get("_id")));
					break;
				case CATEGORY_TYPE:
					String categoryName = (String) items.get(position);
					TextView categoryLabel = (TextView) convertView.findViewById(R.id.category_label);
					categoryLabel.setText(categoryName);
					break;

			}
			return convertView;
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
			String filter="TYPE";
			return productDatabase.getProdsFromList(listId, "CHECK_OUT ASC, " + filter + " ASC, NAME ASC");
		}
	}
	
}
