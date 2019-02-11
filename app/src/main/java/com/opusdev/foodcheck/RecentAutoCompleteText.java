package com.opusdev.foodcheck;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;


public class RecentAutoCompleteText extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public RecentAutoCompleteText(Context context) {
        super(context);
    }

    public RecentAutoCompleteText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }
}
