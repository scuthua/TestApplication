package com.flower.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by flower on 2016/3/26.
 */
public class MylistView extends ListView {
    /**
     * 左滑模式LEFT_SLIDE是0，右滑模式是RIGHT_SLIDE，其他的数字都是左右滑动都有。
     * 这里slideMode是int，默认值是0即默认模式是左滑。
     */
    public static int LEFT_SLIDE = 0;
    public static int RIGHT_SLIDE = 1;
    private int slideMode = 0;
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

    /**
     * client通过设置setSlideMode决定滑动模式
     */
    public void setSlideMode(int slideMode) {
        this.slideMode = slideMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int presentPositionX;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /**
                 *判断当前按下的item和之前的是不是一样
                 */
                int temp = selectedItem;
                selectedItem = pointToPosition((int) ev.getX(), (int) ev.getY());
                if (temp != selectedItem) {
                    selcetedItemChange = true;
                } else {
                    selcetedItemChange = false;
                }
                /**
                 *当选择了不同的item要将已经显示的btn隐藏删除
                 */
                if (selcetedItemChange && btn != null) {
                    btnHide(btn);
                }
                previousPositionX = (int) ev.getX();
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                SlideAdd(ev, slideMode);
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_UP:
                presentPositionX = (int) ev.getX();
                /**
                 *当我们点击的还是原来的item但不是在btn上，我们应该让btn消失。当滑动太小的时候也让button消失
                 */
                if (isDeleteShow && (Math.abs(presentPositionX - previousPositionX) < 100)) {
                    btnHide(btn);
                } else if (isDeleteShow && (Math.abs(presentPositionX - previousPositionX) > 100)) {
                    btnShow(btn, presentPositionX);
                } else {
                    super.onTouchEvent(ev);
                }
        }
        /**
         *这里需要返回true，三种动作都是通过这里返回数值的，如果是false，就会在down结束后不再执行move和up，
         *返回false就是告诉上层的VIewGroup这个事件我们不处理或者处理不了，这样down后事件就不会再分发给MylistView了。
         */
        return true;
    }


    private void SlideAdd(MotionEvent ev, int slideMode) {
        /**
         *当界面上没有button并且滑动大于30的时候才开始处理button的显示
         */
        if (!isDeleteShow && Math.abs(ev.getX() - previousPositionX) > 30) {
            addButton();
        }
        /**
         *通过mScreenWidth 和滑动的距离设置btn的margin。动态设置margin是通过params设置的，这点和padding不一样
         *设置滑动距离小于200是为了不让button离开有边界太远，这是是我自己的情况，不一定适用于所有设备。
         */
        if (isDeleteShow && Math.abs(ev.getX() - previousPositionX) < 200) {
            /**
             *这里通过判断模式是不是右滑决定是否调用lefeSlideProcess，可以这样写是因为三种模式中只有右滑模式
             *是不处理这个左滑操作的，所以只用判断是不是右滑就可以了，不用去判断是左滑还是左右滑动。
             */
            if (ev.getX() - previousPositionX < 0 && slideMode != RIGHT_SLIDE) {
                leftSlideProcess(ev);
                /**
                 *这里的判断和左滑的一个道理。
                 */
            } else if (ev.getX() - previousPositionX > 0 && slideMode != LEFT_SLIDE) {
                rightSlideProcess(ev);
            }
        }

    }

    /**
     * 修复一个小bug，当左滑模式下，我们右滑会有button出现。
     * 给button添加了一个marginStart=mScreenWidth，这样button生成了也不会出现在界面上。
     */
    private void addButton() {
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
        btn.setTranslationX(mScreenWidth);
        viewGroup.addView(btn);
        isDeleteShow = true;
    }

    /**
     * 说实在的这里可以用margin来设置大小，当我用margin做侧滑栏时就不可以，
     *
     * @param ev
     */
    private void leftSlideProcess(MotionEvent ev) {
        btn.setTranslationX(mScreenWidth + ev.getX() - previousPositionX);
    }

    private void rightSlideProcess(MotionEvent ev) {
        btn.setTranslationX(ev.getX() - previousPositionX);
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


    private void btnShow(View v, int presentPositionX) {
//        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_show));
        /**
         * 进入该方法的条件是buttonShow，并且滑动大于100.
         * 这里判断滑动模式是为了避免在左滑模式下，右滑抬起时会直接出现button的情况。
         */
        if (slideMode != RIGHT_SLIDE & presentPositionX < previousPositionX) {
            btn.setTranslationX(mScreenWidth - 200);
        } else if (slideMode != LEFT_SLIDE & presentPositionX > previousPositionX) {
            btn.setTranslationX(0);
        }
    }

    private void btnHide(View v) {
//        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_hide));
        btn.setVisibility(GONE);
        viewGroup.removeView(btn);
        isDeleteShow = false;
    }
}
