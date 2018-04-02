package com.llx278.uimocker2;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;

/**
 * 封装了一些activity相关的操作类
 */
public class ActivityUtils {
    private static final String TAG = "ActivityUtils";
    private final MyInstrumentation mMyInst;
    public static final long DEFAULT_PAUSE_TIME = 500;
    private static long PAUSE_TIME = DEFAULT_PAUSE_TIME;

    ActivityUtils(MyInstrumentation myInst) {
        mMyInst = myInst;
    }

    private void pause() {
        try {
            Thread.sleep(PAUSE_TIME);
        } catch (InterruptedException ignore) {
        }
    }

    public void addActivityLifeObserver(ActivityLifeCycleObserver observer) {
        mMyInst.addActivityLifeCycleObserver(observer);
    }

    public void removeActivityLifeObserver(ActivityLifeCycleObserver observer) {
        mMyInst.removeActivityLifeCycleObserver(observer);
    }

    /**
     * 等待一个activity调用onCreate方法
     * @param activityName activity名字
     * @param timeout 超时时间
     * @param deep 查询的深度
     * @return true 对应的activity已经创建 false超时
     */
    public boolean waitForOnCreate(String activityName,long timeout,int deep) {
        return waitFor(MyInstrumentation.ActivityStateRecord.ON_CREATE,activityName,timeout,deep);
    }

    /**
     * 等待一个activity调用onResume方法
     * @param activityName activity名字
     * @param timeout 超时时间
     * @param deep 查询的深度
     * @return true 对应的activity已经创建，false超时
     */
    public boolean waitForOnResume(String activityName,long timeout,int deep) {
        return waitFor(MyInstrumentation.ActivityStateRecord.ON_RESUME,activityName,timeout,deep);
    }

    /**
     * 等待一个activity调用onPause方法
     * @param activityName activity名字
     * @param timeout 超时时间
     * @param deep 查询的深度
     * @return true 对应的activity已经创建，false超时
     */
    public boolean waitForOnPause(String activityName,long timeout,int deep) {
        return waitFor(MyInstrumentation.ActivityStateRecord.ON_PAUSE,activityName,timeout,deep);
    }

    private boolean waitFor(int lifeCycle,String activityName,long timeout,int deep) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            if (mMyInst.isExpectedActivityLifeCycle(activityName,
                    lifeCycle,deep)) {
                return true;
            }
            pause();
        }
        return false;
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
