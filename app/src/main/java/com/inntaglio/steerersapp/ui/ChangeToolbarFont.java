package com.inntaglio.steerersapp.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by heisen-berg on 21/02/18.
 */

public class ChangeToolbarFont {
    public static void changeToolbarFont(Toolbar toolbar, Activity context) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/ios_font_bold.otf");
                    tv.setTypeface(tf);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tv.setLetterSpacing(0.02f);
                    }
                    break;
                }
            }
        }
    }
}
