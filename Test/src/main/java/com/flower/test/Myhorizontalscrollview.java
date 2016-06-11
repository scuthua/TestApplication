package com.flower.test;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

import java.util.UnknownFormatConversionException;

/**
 * Created by flower on 2016/3/27.
 */
public class Myhorizontalscrollview extends HorizontalScrollView {

    private final int mScreenWidth;
    private boolean once = true;
    private int MenuRightPadding = 50;
    private LinearLayout mWarpper;
    private Button mButton;
    private ViewGroup mContent;
    private int mButtonWidth;


    public Myhorizontalscrollview(Context context) {
        this(context, null);
    }

    public Myhorizontalscrollview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Myhorizontalscrollview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        DisplayMetrics outMetris = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetris);
        mScreenWidth = outMetris.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (once) {
            mWarpper = (LinearLayout) getChildAt(0);
            mContent = (ViewGroup) mWarpper.getChildAt(0);
            mButton = (Button) mWarpper.getChildAt(1);
            mContent.getLayoutParams().width = mScreenWidth;
//            mButtonWidth = mButton.getLayoutParams().width = mScreenWidth - MenuRightPadding;
            once = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(0, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                int scroll = getScrollX();
                mButtonWidth=mButton.getLayoutParams().width;
                if (scroll > mButtonWidth / 2) {
                    smoothScrollTo(mButtonWidth, 0);
                    return true;
                } else {
                    smoothScrollTo(0, 0);
                    return false;
                }

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 用于保证在滑动中  文字部分不动
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        ViewHelper.setTranslationX(mContent,l);
    }
}
