package com.llx278.uimocker2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import de.robv.android.xposed.XposedBridge;


/**
 * 对Instrumentation自定义一些功能
 * @author llx
 */

public final class MyInstrumentation extends InstrumentationDecorator {

    private static final boolean DEBUG = false;
    private static MyInstrumentation INSTANCE;
    private static final String TAG = "MyInstrumentation";
    private final Object mSync = new Object();
    /**
     * 监控当前的activity运行状态
     */
    private final Stack<WeakReference<Activity>> mActivityStack;
    private ArrayList<ActivityMonitor> mMonitorList;
    private ArrayList<ActivityFilter> mActivityFilterList;

    public static MyInstrumentation getInstance(Context context) {
        if(INSTANCE == null){
            INSTANCE = new MyInstrumentation(context);
        }
        return INSTANCE;
    }

    private MyInstrumentation(Context context) {
        super(context);
        mActivityStack = new Stack<>();
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if(DEBUG) {
            Logger.d(TAG,"callActivityOnCreate : " + activity.getClass().getName());
            Logger.d(TAG,"ActivityName :" + activity.getClass().getName());
            Logger.d(TAG,"savedBundle:" + (icicle == null?"null":icicle.toString()));
            Logger.d(TAG,"intent : " + (activity.getIntent()==null?"null":activity.getIntent().toString()));
            if(activity.getIntent() != null) {
                Bundle extras = activity.getIntent().getExtras();
                Logger.d(TAG,"extras:" + (extras == null?"null":extras.toString()));
            }
        }
        doSomeWOrkBeforeCreate(activity,icicle);
        super.callActivityOnCreate(activity, icicle);
        boolean canCallSuper = doFilter(activity,icicle);
        if(!canCallSuper) {
            doSomeWorkAfterCreate(activity);
        }
    }

    private boolean doFilter(final Activity activity, Bundle icicle) {
        synchronized (mSync) {
            if(mActivityFilterList != null) {
                for(ActivityFilter activityFilter : mActivityFilterList) {
                    if(activityFilter.filterActivityName(activity.getClass().getName())){
                        if(DEBUG) {
                            Logger.d(TAG,"filter an Activity : " + activity.getClass().getName());
                        }
                        try {
                            activity.finish();
                        } catch (Exception e) {
                            Logger.e("",e);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void doSomeWOrkBeforeCreate(Activity activity, Bundle icicle) {

    }

    @Override
    public void callActivityOnResume(Activity activity) {
        if(DEBUG) {
            Logger.d(TAG,"callActivityOnResume activityName : " + activity.getClass().getName());
        }
        super.callActivityOnResume(activity);
        doSomeWorAfterResume(activity);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        if(DEBUG) {
            Logger.d(TAG,"callActivityOnPause activityName : " + activity.getClass().getName());
        }
        super.callActivityOnPause(activity);
        doSomeWorkAfterPause(activity);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        if(DEBUG) {
            Logger.d(TAG,"callActivityOnDestroy activityName : " + activity.getClass().getName());
        }
        doSomeWorkBeforeDestroy(activity);
        super.callActivityOnDestroy(activity);
        doSomeWOrkAfterDestroy(activity);
    }

    /**
     * 添加一个activity监视器
     * 我这里没有使用{@link android.app.Instrumentation.ActivityMonitor},因为可
     * 扩展性太差了。
     * @param monitor activity监视器
     */
    public void registerMonitor(ActivityMonitor monitor) {
        synchronized (mSync) {
            if(mMonitorList == null){
                mMonitorList = new ArrayList<>();
            }

            if (monitor != null){
                mMonitorList.add(monitor);
            }
        }
    }

    public void registerActivityFilter(ActivityFilter filter) {
        synchronized (mSync) {
            if(mActivityFilterList == null){
                mActivityFilterList = new ArrayList<>();
            }
            if(filter != null){
                mActivityFilterList.add(filter);
            }
        }
    }

    public void removeActivityFilter(ActivityFilter filter) {
        synchronized (mSync) {
            if(mActivityFilterList!=null&& !mActivityFilterList.isEmpty()) {
                mActivityFilterList.remove(filter);
            }
        }
    }

    /**
     * 移除一个activity的监视器
     * @param monitor activity监视器
     */
    public void removeMonitor(ActivityMonitor monitor) {
        synchronized (mSync){
            if(mMonitorList != null && !mMonitorList.isEmpty()) {
                mMonitorList.remove(monitor);
            }
        }
    }

    // ------------ package method ------------------

    Activity getCurrentActivity() {
        if(mActivityStack.isEmpty()) {
            return null;
        }
        synchronized (mActivityStack) {
            return mActivityStack.peek().get();
        }
    }

    void goBackToActivity(String activityName) {

        ArrayList<Activity> openedActivities = getAllOpenedActivities();
        for (int i = 0;i<openedActivities.size();i++) {
            String className = openedActivities.get(i).getClass().getName();
        }
        boolean found = false;
        for (int i = 0; i < openedActivities.size(); i++) {
            if (openedActivities.get(i).getClass().getName().equals(activityName)) {
                found = true;
                break;
            }
        }

        if (found) {
            while (true) {
                final Activity currentActivity = getCurrentActivity();

                if (currentActivity == null) {
                    continue;
                }
                if (currentActivity.getClass().getName().equals(activityName)) {
                    break;
                }

                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {
                    }
                    runOnMainSync(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                currentActivity.onBackPressed();
                            } catch (Exception ignore) {
                            }
                        }
                    });
                } catch (Exception e) {
                    Logger.e("",e);
                }
            }
        }
    }

    void finishAllOpenedActivities() {
        ArrayList<Activity> activities = getAllOpenedActivities();
        for (final Activity activity : activities) {
            runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    try {
                        activity.finish();
                    } catch (Exception ignore) {
                    }
                }
            });
        }
    }

    void pushActivityToStack(Activity activity) {
        // 添加activity到stack
        WeakReference<Activity> aw = new WeakReference<Activity>(activity);
        synchronized (mActivityStack) {
            mActivityStack.push(aw);
        }

        if (DEBUG) {
            printStackInfo(mActivityStack);
        }
    }

    // ------------ private method ------------------

    private void doSomeWorkAfterCreate(Activity activity) {
        pushActivityToStack(activity);

        if(mMonitorList != null){
            synchronized (mSync) {
                for(ActivityMonitor monitor : mMonitorList) {
                    if(monitor.match(ActivityMonitor.ACTIVITY_CREATE,activity.getClass().getName())) {
                        if (DEBUG) {
                            Logger.d(TAG,"hit " + activity.getClass().getName() + " as OnCreate");
                        }
                    }
                }
            }
        }
    }

    private void doSomeWorAfterResume(Activity activity) {
        if(mMonitorList != null){
            synchronized (mSync){
                for(ActivityMonitor monitor:mMonitorList){
                    if(monitor.match(ActivityMonitor.ACTIVITY_RESUME,activity.getClass().getName())){
                        if (DEBUG) {
                            Logger.d(TAG,"hit " + activity.getClass().getName() + " as OnResume");
                        }
                    }
                }
            }
        }
    }

    private void doSomeWorkAfterPause(Activity activity) {
        if(mMonitorList != null){
            synchronized (mSync){
                for(ActivityMonitor monitor:mMonitorList){
                    if(monitor.match(ActivityMonitor.ACTIVITY_PAUSE,activity.getClass().getName())){
                        if (DEBUG) {
                            Logger.d(TAG,"hit " + activity.getClass().getName() + " as OnPause");
                        }
                    }
                }
            }
        }
    }

    private void doSomeWorkBeforeDestroy(Activity activity) {
        removeActivityFromStack(activity);
    }

    private void doSomeWOrkAfterDestroy(Activity activity) {
        if(mMonitorList != null){
            synchronized (mSync){
                for(ActivityMonitor monitor:mMonitorList){
                    if(monitor.match(ActivityMonitor.ACTIVITY_DESTROY,activity.getClass().getName())){
                        if (DEBUG) {
                            Logger.d(TAG,"hit " + activity.getClass().getName() + " as OnDestroy");
                        }
                    }
                }
            }
        }
    }


    private void printStackInfo(Stack<WeakReference<Activity>> activityStack) {
        Iterator<WeakReference<Activity>> iterator = activityStack.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            Activity activity = iterator.next().get();
            if (activity == null) {
                sb.append("nullActivityReference");
            } else {
                sb.append(activity.getClass().getName());
            }
            sb.append("   ");
        }
        Logger.d(TAG, "stackInfo : [" + sb.toString() + "]");
    }

    private void removeActivityFromStack(Activity activity) {

        synchronized (mActivityStack) {
            Iterator<WeakReference<Activity>> activityStackIterator = mActivityStack.iterator();
            while (activityStackIterator.hasNext()) {
                Activity activityFromWeakReference = activityStackIterator.next().get();

                if (activityFromWeakReference == null) {
                    activityStackIterator.remove();
                }

                if (activity != null && activityFromWeakReference != null &&
                        activityFromWeakReference.equals(activity)) {
                    if (DEBUG) {
                        Logger.d(TAG, "hit an has destroyed activity( which name is '" +
                                activity.getClass().getName() + "'),now remove it!");
                    }
                    activityStackIterator.remove();
                }
            }

            if (DEBUG) {
                printStackInfo(mActivityStack);
            }
        }
    }

    private ArrayList<Activity> getAllOpenedActivities() {
        ArrayList<Activity> activities = new ArrayList<Activity>();

        for (WeakReference<Activity> aMActivityStack : mActivityStack) {
            Activity activity = aMActivityStack.get();
            if (activity != null)
                activities.add(activity);
        }
        return activities;
    }

    /**
     * 对某个activity的状态进行检测
     * 需要实现的功能 1. 检测activity的生命周期：onCreate onResume onPause onDestroy
     */
    public final static class ActivityMonitor {

        public static final String ACTIVITY_CREATE = "onCreate";
        public static final String ACTIVITY_RESUME = "onResume";
        public static final String ACTIVITY_PAUSE = "onPause";
        public static final String ACTIVITY_DESTROY = "onDestroy";


        private String mActivityName;
        private String mActivityStatus;
        private boolean mHasMatched;

        public ActivityMonitor(String activityName, String activityStatus) {
            mActivityName = activityName;
            mActivityStatus = activityStatus;
        }

        /**
         * 等待预置的条件出现
         */
        public boolean waitFor(long timeout) {
            synchronized (this) {
                mHasMatched = false;
                try {
                    while (!mHasMatched) {
                        wait(timeout);
                        if (!mHasMatched) {
                            return false;
                        }
                    }
                    return true;
                } catch (InterruptedException e) {
                    return mHasMatched;
                }
            }
        }

        final boolean match(String activityStatus, String activityName) {
            synchronized (this) {
                //noinspection SimplifiableIfStatement
                if (TextUtils.isEmpty(activityStatus)
                        || TextUtils.isEmpty(activityName)) {
                    return false;
                }

                if (TextUtils.equals(activityStatus, mActivityStatus)
                        && TextUtils.equals(activityName, mActivityName)) {
                    mHasMatched = true;
                    notifyAll();
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 阻止某个符合规则的activity启动
     */
    public static final class ActivityFilter {
        private String mActivityName;

        public ActivityFilter(String activityName) {
            mActivityName = activityName;
        }

        public boolean filterActivityName(String name){
            return TextUtils.equals(name,mActivityName);
        }
    }
}
