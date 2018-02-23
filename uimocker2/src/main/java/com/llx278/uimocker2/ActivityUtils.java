package com.llx278.uimocker2;

import android.app.Activity;

/**
 * 封装了一些activity相关的操作类
 */
public class ActivityUtils {
    private static final String TAG = "ActivityUtils";
    private final MyInstrumentation mMyInst;

    ActivityUtils(MyInstrumentation myInst) {
        mMyInst = myInst;
    }

    ActivityUtils(MyInstrumentation mInst, Activity topActivity) {
        mMyInst = mInst;
        if (topActivity != null) {
            mInst.pushActivityToStack(topActivity);
        } else {
            Logger.i(TAG, "warning : pass an empty activity reference when construct ActivityUtils!");
        }
    }

    /**
     * 等待一个activity调用onCreate方法
     * @param activityName activity名字
     * @param timeout 超时时间
     * @return true 对应的activity已经创建 false超时
     */
    public boolean waitForOnCreate(String activityName,long timeout) {

        MyInstrumentation.ActivityMonitor monitor = new MyInstrumentation.ActivityMonitor(activityName,
                MyInstrumentation.ActivityMonitor.ACTIVITY_CREATE);
        mMyInst.registerMonitor(monitor);
        boolean result = monitor.waitFor(timeout);
        mMyInst.removeMonitor(monitor);
        return result;
    }

    /**
     * 等待一个activity调用onResume方法
     * @param activityName activity名字
     * @param timeout 超时时间
     * @return true 对应的activity已经创建，false超时
     */
    public boolean waitForOnResume(String activityName,long timeout) {
        MyInstrumentation.ActivityMonitor monitor = new MyInstrumentation.ActivityMonitor(activityName,
                MyInstrumentation.ActivityMonitor.ACTIVITY_RESUME);
        mMyInst.registerMonitor(monitor);
        boolean result = monitor.waitFor(timeout);
        mMyInst.removeMonitor(monitor);
        return result;
    }

    /**
     * 等待一个activity调用onPause方法
     * @param activityName activity名字
     * @param timeout 超时时间
     * @return true 对应的activity已经创建，false超时
     */
    public boolean waitForOnPause(String activityName,long timeout) {
        MyInstrumentation.ActivityMonitor monitor = new MyInstrumentation.ActivityMonitor(activityName,
                MyInstrumentation.ActivityMonitor.ACTIVITY_PAUSE);
        mMyInst.registerMonitor(monitor);
        boolean result = monitor.waitFor(timeout);
        mMyInst.removeMonitor(monitor);
        return result;
    }

    /**
     * 等待一个activity调用onDestroy方法
     * @param activityName activity的名字
     * @param timeout 超时时间
     * @return true 对应的activity已经创建，false超时
     */
    public boolean waitForOnDestroy(String activityName,long timeout) {
        MyInstrumentation.ActivityMonitor monitor = new MyInstrumentation.ActivityMonitor(activityName,
                MyInstrumentation.ActivityMonitor.ACTIVITY_DESTROY);
        mMyInst.registerMonitor(monitor);
        boolean result = monitor.waitFor(timeout);
        mMyInst.removeMonitor(monitor);
        return result;
    }

    /**
     * 获得当前的activity
     *
     * @return 当前页面最前的activity
     */
    public Activity getCurrentActivity() {
        if (mMyInst != null) {
            return mMyInst.getCurrentActivity();
        }
        return null;
    }

    /**
     * 关闭当前所有已经打开的activity
     */
    public void finishOpenedActivities() {
        if (mMyInst != null) {
            mMyInst.finishAllOpenedActivities();
        }
    }

    /**
     * 返回到指定的activity
     *
     * @param activityName 指定的activity name
     */
    public void goBackToActivity(String activityName) {
        if (mMyInst != null) {
            mMyInst.goBackToActivity(activityName);
        }
    }
}
