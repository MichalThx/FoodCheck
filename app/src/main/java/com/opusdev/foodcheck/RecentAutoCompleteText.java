package com.opusdev.foodcheck;

import android.content.Context;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;


public class RecentAutoCompleteText extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
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
