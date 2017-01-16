package com.example.grocerylist;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by neoba on 1/14/2017.
 */

public class EditTextListenerRemover extends EditText {
    private TextWatcher tw=null;

    public EditTextListenerRemover(Context ctx)
    {
        super(ctx);
    }

    public EditTextListenerRemover(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
    }

    public EditTextListenerRemover(Context ctx, AttributeSet attrs, int defStyle)
    {
        super(ctx, attrs, defStyle);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher)
    {
        if (tw != null)
            removeTextChangedListener(tw);
        super.addTextChangedListener(watcher);
        tw = watcher;
    }

    public void clearTextChangedListeners()
    {
        if(tw != null)
        {
            removeTextChangedListener(tw);
        }
    }

}
