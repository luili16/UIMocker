package com.llx278.uimocker2;

import android.app.Activity;
import android.os.Bundle;

/**
 * 对一个Activity的生命周期
 * Created by llx on 2018/3/26.
 */
public interface ActivityLifeCycleObserver {
    /**
     * activity在调用onCreate之前被调用
     * @param activity 指定的activity
     * @param icicle 保存的Bundle
     */
    void beforeOnCreate(Activity activity, Bundle icicle);
    /**
     * activity在调用onCreate之后被调用
     * @param activity 指定的activity
     * @param icicle 保存的Bundle
     */
    void afterOnCreate(Activity activity, Bundle icicle);
    /**
     * activity在调用onResume之前被调用
     * @param activity 指定的activity
     */
    void beforeOnResume(Activity activity);
    /**
     * activity在调用onResume之后被调用
     * @param activity 指定的activity
     */
    void afterOnResume(Activity activity);
    /**
     * activity在调用onPause之前被调用
     * @param activity 指定的activity
     */
    void beforeOnPause(Activity activity);
    /**
     * activity在调用onPause之后被调用
     * @param activity 指定的activity
     */
    void afterOnPause(Activity activity);
    /**
     * activity在调用onDestroy之前被调用
     * @param activity 指定的activity
     */
    void beforeOnDestroy(Activity activity);
    /**
     * activity在调用onDestroy之后被调用
     * @param activity 指定的activity
     */
    void afterOnDestroy(Activity activity);
}
