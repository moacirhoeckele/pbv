package com.volvo.wis.pbv.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class FontAwesomeUtil  extends AppCompatTextView {

    public FontAwesomeUtil(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontAwesomeUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontAwesomeUtil(Context context) {
        super(context);
        init();
    }

    private void init() {

        //Font name should not contain "/".
        //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fa-regular-400.ttf");
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fa-solid-900.ttf");
        setTypeface(tf);
    }
}
