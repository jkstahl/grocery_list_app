package com.example.grocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ProductEditActivity extends Activity {
    public static final int OK=0;
    public static final int CANCEL=1;
    public static final int DELETE=2;
    public static final int ACTIVITY_ID=0;

    private EditText productName;
    private EditText productQuantity;
    private Spinner productUnits;
    private EditText productType;
    private String id;
    ProductPackager pp;
    public static String[] unitTypes = {"", "ounces", "pounds", "cups", "quarts", "liters"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);

        Intent inputIntent = getIntent();
        pp = new ProductPackager(inputIntent);
        productName = ((EditText) findViewById(R.id.product_name_view));
        productName.setText(inputIntent.getStringExtra("NAME"));

        productQuantity = ((EditText) findViewById(R.id.text_listi_quantity));
        productQuantity.setText("" + inputIntent.getStringExtra("QUANTITY"));

        productUnits = ((Spinner) findViewById(R.id.selector_pedit_units));
        unitTypes = ProductUnitExtractor.getAllUnits();
        ArrayAdapter<String> adp2=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, unitTypes);
        productUnits.setAdapter(adp2);
        for (int i=0; i<unitTypes.length; i++)
            if (inputIntent.getStringExtra("UNITS").equals(unitTypes[i]))
                productUnits.setSelection(i);

        productType = ((EditText) findViewById(R.id.edit_pedit_type));
        productType.setText(inputIntent.getStringExtra("TYPE"));

        pp.setView("NAME", productName);
        pp.setView("TYPE", productType);
        pp.setView("QUANTITY", productQuantity);
        pp.setView("UNITS", productUnits);

        id = inputIntent.getStringExtra("_id");

        Button buttonCancel = ((Button) findViewById(R.id.button_pedit_cancel));
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("action", "cancel button clicked.");
                Intent i = new Intent();
                setResult(CANCEL, i);
                finish();
            }
        });

        Button buttonOk = ((Button) findViewById(R.id.button_pedit_ok));
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("action", "ok button clicked.");
                Intent i = pp.getIntentFromView();
                setResult(OK, i);
                finish();
            }
        });

        Button buttonDelete = ((Button) findViewById(R.id.button_pedit_delete));
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("action", "delete button clicked.");
                Intent i = new Intent();
                i.putExtra("_id", id);
                setResult(DELETE, i);
                finish();
            }
        });

        /*
        this.wp = ((WorkingProduct)getIntent().getSerializableExtra("workingProduct"));
        Intent i = new Intent();
        i.putExtra("changeProduct", wp);
        setResult(DELETE, i);
        finish();*/
    }

    @Override
    public void onBackPressed(){
        setResult(CANCEL, new Intent());
        finish();
        super.onBackPressed();
    }

}
