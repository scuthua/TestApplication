package com.flower.test;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by flower on 2016/3/4.
 */
public class MyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityControl.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityControl.removeActivity(this);
    }
}
