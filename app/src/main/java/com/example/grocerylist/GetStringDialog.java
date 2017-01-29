package com.example.grocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by neoba on 1/26/2017.
 */

public class GetStringDialog extends FragmentActivity {
    public static final int ACTIVITY_ID=5;
    public static final int RESULT_OK=0;
    public static final int RESULT_CANCEL=1;
    private EditText newString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_string_dialog);

        newString = (EditText) findViewById(R.id.new_string);
        ((TextView) findViewById(R.id.prompt)).setText(getIntent().getStringExtra("PROMPT"));
        ((Button)findViewById(R.id.ok_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("RETURN_STRING", newString.getText().toString());
                setResult(RESULT_OK, i);
                finish();
            }
        });

        ((Button)findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(RESULT_CANCEL, i);
                finish();
            }
        });



    }

}
