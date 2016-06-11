package com.flower.test.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by flower on 2016/6/11.
 */
public class Item extends LinearLayout {

    private Button btn;
    private TextView textView;

    public Item(Context context) {
        super(context);
    }

    public Item(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
