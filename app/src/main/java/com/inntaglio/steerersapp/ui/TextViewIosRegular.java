package com.inntaglio.steerersapp.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by heisen-berg on 1/22/2018.
*/

public class TextViewIosRegular extends AppCompatTextView {

    public TextViewIosRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewIosRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewIosRegular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/ios_font_regular.otf");
            setTypeface(tf);
        }
    }
}