package com.flower.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by flower on 2016/3/26.
 */
public class MylistView extends ListView {
    private Context context;
    private int selectedItem;
    private int previousPositionX;
    private boolean isDeleteShow = false;
    private boolean selcetedItemChange;
    private int mScreenWidth;
    private RelativeLayout.LayoutParams params;

    private View btn;
    private ViewGroup viewGroup;
    private OnItemDeleteListener onItemDeleteListener;


    public MylistView(Context context) {
        super(context);
        init(context);
    }

    public MylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MylistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        DisplayMetrics outMetris = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetris);
        mScreenWidth = outMetris.widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int presentPositionX;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*
                判断当前按下的item和之前的是不是一样
                 */
                int temp = selectedItem;
                selectedItem = pointToPosition((int) ev.getX(), (int) ev.getY());
                if (temp != selectedItem) {
                    selcetedItemChange = true;
                } else {
                    selcetedItemChange = false;
                }
                /*
                当选择了不同的item要将已经显示的btn隐藏删除
                 */
                if (selcetedItemChange && btn != null) {
                    btnHide(btn);
                }
                previousPositionX = (int) ev.getX();
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                SlideAdd(ev);
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_UP:
                presentPositionX = (int) ev.getX();
                /*
                当我们点击的还是原来的item但不是在btn上，我们应该让btn消失。当滑动太小的时候也让button消失
                 */
                if (isDeleteShow && (Math.abs(presentPositionX - previousPositionX) < 100)) {
                    btnHide(btn);
                } else {
                    super.onTouchEvent(ev);
                }
        }
        return true;
    }
    private void SlideAdd(MotionEvent ev) {
    /*
    当界面上没有button并且滑动大于30的时候才开始处理button的显示
     */
        if (!isDeleteShow && Math.abs(ev.getX() - previousPositionX) > 30) {
            btn = LayoutInflater.from(getContext()).inflate(R.layout.btn, null);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewGroup.removeView(btn);
                    isDeleteShow = false;
                    btn = null;
                    onItemDeleteListener.onItemDelete(selectedItem);
                }
            });
            viewGroup = (ViewGroup) getChildAt(selectedItem - getFirstVisiblePosition());
            params = new RelativeLayout.LayoutParams(ViewGroup
                    .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            btn.setLayoutParams(params);
            viewGroup.addView(btn);
            isDeleteShow = true;
        }
        /*
        通过mScreenWidth 和滑动的距离设置btn的margin。动态设置margin是通过params设置的，这点和padding不一样
        设置滑动距离小于300是为了不让button离开有边界太远，这是是我自己的情况，不一定适用于所有设备。
         */
        if (Math.abs(ev.getX() - previousPositionX) > 30 && Math.abs(ev.getX() -
                previousPositionX) < 300) {
            if (ev.getX() - previousPositionX < 0) {
                int left = (int) (mScreenWidth + ev.getX() - previousPositionX);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.setMarginStart(left);
                params.setMarginEnd(0);
                btn.setLayoutParams(params);
            } else {
                /*
                set的方法在左右滑动的时候会被覆盖，但是add的方法不会，所以这里需要removeRule
                 */
                int right=(int) (mScreenWidth - (ev.getX() - previousPositionX));
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.setMarginEnd(right);
                params.setMarginStart(0);
                btn.setLayoutParams(params);
            }
        }
    }

    /**
     * 开放接口的方法，先定义带抽象方法的一个接口，然后在这个类中声明一个接口，在对外开放一个setXXX的方法，让调用这个类的类去具体化这个方法
     */
    public interface OnItemDeleteListener {
        public void onItemDelete(int selectedItem);//这个的参数是我们这个类提供的
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }


    private void btnShow(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_show));
    }

    private void btnHide(View v) {
//        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_hide));
        btn.setVisibility(GONE);
        viewGroup.removeView(btn);
        isDeleteShow = false;
    }
}
