package com.xmrbi.unware.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xmrbi.unware.base.BaseActivity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wzn on 2018/3/29.
 */

public class ActivityStackUtils {
    private static Set<Activity> allActivities;

    public static void addActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<>();
        }
        allActivities.add(act);
    }

    public static void removeActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }
    public static void finishAllActivity(){
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    act.finish();
                }
            }
        }
    }
    public static void finishAllActivity(BaseActivity activity){
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    if(!act.getClass().getName().equals("com.xmrbi.unware.module.main.activity.MainActivity")){
                        act.finish();
                    }
                }
            }
        }
    }

    public static void exitApp() {
        finishAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void lauch(Context context,Class clazz ,Bundle bundle){
        Intent intent=new Intent(context,clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static Set<Activity> getAllActivities() {
        return allActivities;
    }

    public static void setAllActivities(Set<Activity> allActivities) {
        ActivityStackUtils.allActivities = allActivities;
    }
}
