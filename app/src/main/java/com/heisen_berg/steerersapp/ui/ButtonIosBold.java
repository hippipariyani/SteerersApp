package com.heisen_berg.steerersapp.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by heisen-berg on 1/22/2018.
*/

public class ButtonIosBold extends AppCompatButton {

    public ButtonIosBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ButtonIosBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonIosBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/ios_font_bold.otf");
            setTypeface(tf);
        }
    }
}