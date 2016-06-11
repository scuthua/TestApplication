package com.flower.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by flower on 2016/3/26.
 */
public class MylistView extends ListView {
    private GestureDetector gestureDetector;
    private Context context;
    private int selectedItem;
    private int previousPositionX;
    private boolean isDeleteShow = false;
    private boolean selcetedItemChange;

    private View btn;
    private ViewGroup viewGroup;
    private OnItemDeleteListener onItemDeleteListener;


    public MylistView(Context context) {
        super(context);
        init();
    }

    public MylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MylistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
//        gestureDetector = new GestureDetector(context, this);
//        setOnTouchListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int presentPositionX;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("tag", "ACTION_DOWN");
                /*
                判断当前按下的item和之前的是不是一样
                 */
                int temp = selectedItem;
                selectedItem = pointToPosition((int) ev.getX(), (int) ev.getY());
                if (temp != selectedItem) {
                    Log.e("tag", "onTouch: caonima");
                    selcetedItemChange = true;
                } else {
                    selcetedItemChange=false;
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
                Log.e("tag", "ACTION_MOVE");
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_UP:
                Log.e("tag", "ACTION_UP");
                presentPositionX = (int) ev.getX();
                Log.e("tag", "previousPositionX:" + previousPositionX + "   presentPositionX:" +
                        presentPositionX);
                /*
                当界面上没有button，并且滑动距离向左大于200，显示button
                 */
                if (!isDeleteShow && (presentPositionX - previousPositionX < -200)) {
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
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                            .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.CENTER_VERTICAL);
                    btn.setLayoutParams(params);
                    viewGroup.addView(btn);
                    btnShow(btn);
                    isDeleteShow = true;
                    /*
                    当我们点击的还是原来的item但不是在btn上，我们应该让btn消失。
                     */
                } else if (isDeleteShow&&(Math.abs(presentPositionX-previousPositionX)<100)) {
                    btnHide(btn);
                } else {
                    super.onTouchEvent(ev);
                }
        }
        return true;
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


//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        Log.e("tag", "onTouch: ");
//        int temp = selectedItem;
//        selectedItem = pointToPosition((int) event.getX(), (int) event.getY());
//        if (temp != selectedItem) {
//            Log.e("tag", "onTouch: caonima");
//            selcetedItemChange = true;
//        }
////        if (isDeleteShow) {
////            btnHide(btn);
////            btn.setVisibility(GONE);
////            isDeleteShow = false;
////            btn = null;
////            return true;
////        } else {
//        return gestureDetector.onTouchEvent(event);
//
////        }
//
//    }
//
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        Log.e("tag ","pmFlomg");
//        Log.e("tag", "onFling: e1.x= " + e1.getX() + "e2.x= " + e2.getX());
//        if (!isDeleteShow && (e1.getX() - e2.getX() > 300)) {
//            btn = LayoutInflater.from(getContext()).inflate(R.layout.btn, null);
//            btn.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    viewGroup.removeView(btn);
//                    isDeleteShow = false;
//                    btn = null;
//                    onItemDeleteListener.onItemDelete(selectedItem);
//                }
//            });
//            viewGroup = (ViewGroup) getChildAt(selectedItem - getFirstVisiblePosition());
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
//                    .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            params.addRule(RelativeLayout.CENTER_VERTICAL);
//            btn.setLayoutParams(params);
//            viewGroup.addView(btn);
//            btnShow(btn);
//            isDeleteShow = true;
//        } else {
//            setOnTouchListener(this);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onDown(MotionEvent e) {
//        Log.e("tag", "onDown: ");
//        if (!isDeleteShow) {
//            selectedItem = pointToPosition((int) e.getX(), (int) e.getY());
//
//        } else {
//            viewGroup.removeView(btn);
//            isDeleteShow = false;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        Log.e("tag ","onScroll");
//        Log.e("tag", "onScroll: ");
//        int offset = ((int) e2.getX() - (int) e1.getX());
//        if (offset < -50) {
//            if (btn == null || (btn!=null&&selcetedItemChange)) {
//                Log.e("tag", "onScroll: ????");
//                btn = null;
//                btn = LayoutInflater.from(getContext()).inflate(R.layout.btn, null);
//                btn.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        viewGroup.removeView(btn);
//                        isDeleteShow = false;
//                        btn = null;
//                        onItemDeleteListener.onItemDelete(selectedItem);
//                    }
//                });
//                viewGroup = (ViewGroup) getChildAt(selectedItem - getFirstVisiblePosition());
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
//                        .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                params.addRule(RelativeLayout.CENTER_VERTICAL);
//                btn.setLayoutParams(params);
//                btn.setPadding(btn.getPaddingLeft(), btn.getPaddingTop(), btn.getPaddingRight
//                        (), btn
//                        .getPaddingBottom());
//                viewGroup.addView(btn);
//                isDeleteShow = true;
//                selcetedItemChange = false;
//            }
//            if (offset > -200) {//大于-200是用于限制button增加的太大，小于-50是保证向左滑动才会出现这个button
//                Log.e("tag", "onScroll: offset= " + offset);
//                Log.e("tag", "onScroll: btnmeasurewidth= " + btn.getMeasuredWidth());
//                btn.setPadding(btn.getPaddingLeft(), btn.getPaddingTop(), -btn.getMeasuredWidth
// () -
//                        offset, btn
//                        .getPaddingBottom());
//
//            }
//        }
//
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//
//    }

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
