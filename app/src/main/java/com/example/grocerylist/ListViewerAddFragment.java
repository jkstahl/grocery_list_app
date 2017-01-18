package com.example.grocerylist;


import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class ListViewerAddFragment extends Fragment {
	private ProductListDB productDatabase;
	private int listId;
	private ListViewerListFragment listViewer;
	private final String TAG="listvieweradd";

	protected Fragment createFragment() {

		listViewer = new ListViewerListFragment();
		productDatabase = DatabaseHolder.getDatabase(null);
		listId = Integer.parseInt(this.getActivity().getIntent().getStringExtra("ListID"));
		Log.d("debug", "List id is "+listId);
		return listViewer;
	}

	private void addProduct(String newProduct) {
		Log.d("action", "Done clicked in new item input");
		// TODO Extract quantity and units from name string
		// get id of the product
		ProductUnitExtractor pue = new ProductUnitExtractor();
		ProductUnitExtractor.QuantityUnitPackage qup = pue.getUnitsProductFromString(newProduct);
		Log.d(TAG, qup.product);

		Log.d("debug", "Product id received " + newProduct);
		Product newList =  new Product(listId, qup.product, "Uncategorized", (float) qup.quantity, qup.units, false);
		// add product to the database
		productDatabase.addEntryToDatabase(newList);
		listViewer.updateList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.list_frame_global);
		if (savedInstanceState == null) {
			Log.d("listviewer", "Creating fragment.");
			//Fragment fragment = getFragmentManager().findFragmentById(R.id.activity_container);
			Fragment fragment = createFragment();
			getFragmentManager().beginTransaction().add(R.id.list_fragment, fragment).commit();
		}
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_frame_global, container, false);
		listId = Integer.parseInt(this.getActivity().getIntent().getStringExtra("ListID"));

		final AutoCompleteTextView addNewItemTextView = (AutoCompleteTextView)rootView.findViewById(R.id.new_item_input);
		addNewItemTextView.setAdapter(new AutoCompleteAdapter(getActivity()));


		addNewItemTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//sendMessage();
					String newProductString = addNewItemTextView.getText().toString();
					if (!newProductString.trim().equals("")) {
						addProduct(newProductString);
					}
					addNewItemTextView.setText("");

					handled = true;
				}
				return handled;
			}
		});

		addNewItemTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("addnewtext", "clicked " + addNewItemTextView.getText().toString());
				addProduct(addNewItemTextView.getText().toString());
			}
		});

		return rootView;
	}

	public void updateList() {
		listViewer.updateList();
	}

	private class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

		private LayoutInflater mInflater;


		public AutoCompleteAdapter(final Context context) {
			super(context, -1);
			mInflater = LayoutInflater.from(context);

		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final TextView tv;
			if (convertView != null) {
				tv = (TextView) convertView;
			} else {
				tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
			}

			tv.setText(getItem(position));
			return tv;
		}



		@Override
		public Filter getFilter() {
			Filter myFilter = new Filter() {
				@Override
				protected FilterResults performFiltering(final CharSequence constraint) {
					List<String> filteredProducts = null;
					if (constraint != null) {
						filteredProducts = productDatabase.getFilteredProducts((String) constraint);
					}
					if (filteredProducts == null) {
						filteredProducts = new ArrayList<String>();
					}

					final FilterResults filterResults = new FilterResults();
					filterResults.values = filteredProducts;
					filterResults.count = filteredProducts.size();

					return filterResults;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(final CharSequence contraint, final FilterResults results) {
					clear();
					for (String address : (List<String>) results.values) {
						add(address);
					}
					if (results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}

				@Override
				public CharSequence convertResultToString(final Object resultValue) {
					return resultValue == null ? "" : (String) resultValue;
				}
			};
			return myFilter;
		}
	}

}
