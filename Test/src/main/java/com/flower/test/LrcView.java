package com.flower.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.flower.test.model.Lrc;

/**
 * Created by flower on 2016/2/27.
 */
public class LrcView extends TextView {

    private float width;
    private float height;
    private Paint currentPaint;
    private Paint notCurrentPaint;
    private float textSize = 35;
    private float textHeight = 45;
    private int currentTime;
    private float offset = 0;
    private int index = 0;

    private Lrc currentLrc;

    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(true);

        /**
         * 高亮部分初始化
         */
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setTextAlign(Paint.Align.CENTER);
        currentPaint.setTextSize(44);
        currentPaint.setColor(Color.BLUE);
        currentPaint.setTypeface(Typeface.SERIF);
        /**
         * 非高亮部分初始化
         */
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setColor(Color.BLACK);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);
    }

    //设置歌词内容
    public void setCurrentLrc(Lrc currentLrc) {
        this.currentLrc = currentLrc;
    }

    /**
     * 绘制歌词
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        try {
            setText("");
            offset = 0;
//            if (currentLrc.getLrcTime().get(index) < currentTime &&
//                    currentTime < currentLrc.getLrcTime().get(index + 1)) {
            if (index < currentLrc.getLrcTime().size() - 1 && currentLrc.getLrcTime().get(index)
                    < currentTime) {//这个判断导致了主开始的位置靠下，在开始播放时字幕会比较快的升到中间位置
                offset = ((float) (currentTime - currentLrc.getLrcTime().get(index)))
                        / (currentLrc.getLrcTime().get(index + 1) - currentLrc.getLrcTime().get
                        (index));
                offset = offset * textHeight;
            }
            canvas.drawText(currentLrc.getLrc().get(index), width / 2, height / 2 - offset,
                    currentPaint);
            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i--) {
                tempY = tempY - textHeight;
                canvas.drawText(currentLrc.getLrc().get(i), width / 2, tempY - offset,
                        notCurrentPaint);
            }
            tempY = height / 2;
            for (int i = index + 1; i < currentLrc.getLrc().size(); i++) {
                tempY = tempY + textHeight;
                canvas.drawText(currentLrc.getLrc().get(i), width / 2, tempY - offset,
                        notCurrentPaint);
            }
        } catch (Exception e) {
            setText("找不到歌词，请确定已经下载");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    public void setIndex(int index, int currentTime) {
        this.index = index;
        this.currentTime = currentTime;
    }
}
