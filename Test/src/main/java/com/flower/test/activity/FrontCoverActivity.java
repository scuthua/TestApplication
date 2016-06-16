package com.flower.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.Window;

import com.flower.test.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by flower on 2016/3/27.
 */
public class FrontCoverActivity extends Activity {
    public static int COVER_DISPLAY_TIME=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.front_cover_activity);
        /*
        这里使用延时的方法可以让图片显示几秒，从而实现启动界面的效果。
        但是不能用Thread.sleep，这个方法和延时有很大的区别。一开始使用这个sleep所以失败了。
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FrontCoverActivity.this, SongListActivity.class);
                startActivity(intent);
                FrontCoverActivity.this.finish();
            }
        },COVER_DISPLAY_TIME);
    }


}
