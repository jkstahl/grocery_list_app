package com.example.grocerylist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neoba on 12/27/2016.
 */

public abstract class Packager extends TableMap {
    private Map<String, View> dataViews;

    //public String columns = new String[] {};
    //String types = new String[] {};
    public Packager(ContentValues dataValues) {
        super();
        dataViews = new HashMap<String, View>();

        String[] c = getColumns();
        for (int i=0; i<c.length; i++) {
            String key = c[i];

            if (!containsKey(key))
                Log.e("packager", "Incompatible data types.  Could not find key " + key);
            put(key, dataValues.get(key));

        }

    }

    public Packager(Intent intent) {
        dataViews = new HashMap<String, View>();
        String[] c = getColumns();
        for (int i=0; i<c.length; i++) {
            String key = c[i];
            if (!intent.hasExtra(key))
                Log.e("packager", "Could not find key in intent " + key);
            String typeName = getColumnTypes()[i];
            putRealValue(key, intent.getStringExtra(key), typeName);
        }
    }

    private void putRealValue(String key, String val, String typeName) {
        put(key, val);
        if (typeName.contains("TEXT"))
            put(key, val);
        else if (typeName.contains("INTEGER"))
            put(key, Integer.parseInt(val));
        else if (typeName.contains("REAL"))
            put(key, Float.parseFloat(val));
        else if (typeName.contains("BOOLEAN")) {
            valuesContainer.put(key, (Boolean) (val.equals("true")));
        } else if (typeName.contains("BLOB")) {
            Log.d("packager", "Putting blob " + val);
            put(key, val.getBytes());
        }
    }

    public Packager() {
        super();
        dataViews = new HashMap<String, View>();

    }

    public Intent getIntentFromView() {
        String[] c = getColumns();
        Intent returnIntent = new Intent();
        for (int i=0; i<c.length; i++) {
            Log.d("packager", c[i] + ": " +  getViewText(c[i]) + " original " + get(c[i]));
            if (dataViews.containsKey(c[i]) && !isEmpty(getViewText(c[i])))
                returnIntent.putExtra(c[i], "" + getViewText(c[i]));
            else
                returnIntent.putExtra(c[i], "" + get(c[i]));
        }

        return returnIntent;
    }

    public Intent getIntent(Activity a, Class<?> cls) {
        String[] c = getColumns();
        Intent returnIntent = new Intent(a, cls);
        for (int i=0; i<c.length; i++) {
            Object val = get(c[i]);
            if (val instanceof byte[]) {
                try {
                    Log.d("packager", "Converting to String.");
                    String str = new String((byte[]) val, "UTF-8");
                    returnIntent.putExtra(c[i], str);
                } catch (Exception e) {
                    Log.e("packager", "Problem converting byte array to string");
                }
            } else {
                returnIntent.putExtra(c[i], "" + get(c[i]));
            }
        }

        return returnIntent;
    }



    private String getViewText(String key) {
        View dataView = dataViews.get(key);
        if (dataView instanceof EditText) {
            return ((EditText) dataView).getText().toString();
        } else if (dataView instanceof TextView) {
            return ((TextView) dataView).getText().toString();
        } else if (dataView instanceof Spinner) {
            return ((Spinner) dataView).getSelectedItem().toString();
        } else {
            Log.e("packager", "Unknown view type.");
            return "";
        }


    }

    private boolean isEmpty(String editText) {
        return editText.trim().length() == 0;
    }

    public void setView(String key, View v) {
        dataViews.put(key, v);

    }

    public void commitChangesToViews() {
        String[] c = getColumns();
        for (int i=0; i<c.length; i++) {
            Log.d("packager", c[i] + ": " +  getViewText(c[i]) + " original " + get(c[i]));
            if (dataViews.containsKey(c[i]) && !isEmpty(getViewText(c[i])))
                putRealValue(c[i], "" + getViewText(c[i]), getColumnTypes()[i]);
        }
    }
}
