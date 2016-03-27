package com.flower.test;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flower on 2016/3/4.
 */
public class ActivityControl {
    public static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }
    public static void removeAll(){
        for (Activity activity : activityList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }


}
