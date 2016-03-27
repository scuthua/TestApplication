package com.flower.test.util;

import android.util.Log;

/**
 * Created by flower on 2016/3/26.
 */
public class LogUtil {
    private static final int verbose = 0;
    private static final int debug = 1;
    private static final int info = 2;
    private static final int warn = 3;
    private static final int error = 4;
    private static final int me=5;

    public void v(String tag, String msg) {
        if (me>verbose) {
            Log.v(tag, msg);
        }
    }

    public void d(String tag, String msg) {
        if (me > debug) {
            Log.d(tag, msg);
        }
    }

    public void i(String tag, String msg) {
        if (me > info) {
            Log.i(tag, msg);
        }
    }

    public void w(String tag, String msg) {
        if (me > warn) {
            Log.w(tag, msg);
        }
    }

    public void e(String tag, String msg) {
        if (me > error) {
            Log.e(tag,msg);
        }
    }

}
