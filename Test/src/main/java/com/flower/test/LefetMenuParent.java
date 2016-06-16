package com.flower.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by flower on 2016/6/15.
 */
public class LefetMenuParent extends RelativeLayout {
    public LefetMenuParent(Context context) {
        super(context);
    }

    public LefetMenuParent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LefetMenuParent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onTouchEvent(event);
    }
}
