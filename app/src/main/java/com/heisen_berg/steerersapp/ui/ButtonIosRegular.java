package com.heisen_berg.steerersapp.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by heisen-berg on 1/22/2018.
*/

public class ButtonIosRegular extends AppCompatButton {

    public ButtonIosRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ButtonIosRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonIosRegular(Context context) {
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