package com.llx278.uimocker2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;


public class ActivityLifeCycleCallbackImpl implements Application.ActivityLifecycleCallbacks {

    private static final boolean DEBUG = true;
    private static final String TAG = "main";
    /**
     * 记录activity运行状态
     */
    private final Stack<ActivityStateRecord> mActivityRecordStack;

    public ActivityLifeCycleCallbackImpl() {
        mActivityRecordStack = new Stack<>();
    }

    private ArrayList<ActivityLifeCycleObserver> mObserverList = new ArrayList<>();

    /**
     * 添加一个observer
     * @param observer 指定的observer
     */
    void addActivityLifeCycleObserver(ActivityLifeCycleObserver observer) {
        if (observer != null && !mObserverList.contains(observer)) {
            mObserverList.add(observer);
        }
    }

    /**
     * 移除一个observer
     * @param observer 指定的observer
     */
    void removeActivityLifeCycleObserver(ActivityLifeCycleObserver observer) {
        if (mObserverList.contains(observer)) {
            mObserverList.remove(observer);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle icicle) {

        if (DEBUG) {
            printActivityInfo(activity, icicle);
        }

        for (ActivityLifeCycleObserver observer : mObserverList) {
            observer.beforeOnCreate(activity,icicle);
        }

        addToRecordStack(activity, ActivityStateRecord.ON_CREATE);

        for (ActivityLifeCycleObserver observer : mObserverList) {
            observer.afterOnCreate(activity,icicle);
        }

        if (DEBUG) {
            printStackInfo(mActivityRecordStack);
        }

    }

    private void printStackInfo(Stack<ActivityStateRecord> activityStack) {
        Iterator<ActivityStateRecord> iterator = activityStack.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            ActivityStateRecord activity = iterator.next();
            sb.append(activity.toString()).append("   ");
        }
        Logger.d(TAG, "stackInfo : [" + sb.toString() + "]");
    }

    private void addToRecordStack(Activity activity, int currentLifeCycle) {

        synchronized (mActivityRecordStack) {
            if (mActivityRecordStack.isEmpty() && currentLifeCycle == ActivityStateRecord.ON_CREATE) {
                ActivityStateRecord record = new ActivityStateRecord();
                // 添加activity到stack
                record.mActivityWeakRef = new WeakReference<>(activity);
                record.currentLifeCycle = currentLifeCycle;
                mActivityRecordStack.push(record);
            } else {
                ActivityStateRecord topRecord = mActivityRecordStack.peek();
                Activity activityFromWeakRf = topRecord.mActivityWeakRef.get();
                if (activity.equals(activityFromWeakRf)) {
                    // 同一个activity，那么仅仅更新状态
                    topRecord.currentLifeCycle = currentLifeCycle;
                } else {
                    // 新加入一个activity
                    ActivityStateRecord newRecord = new ActivityStateRecord();
                    newRecord.mActivityWeakRef = new WeakReference<Activity>(activity);
                    newRecord.currentLifeCycle = currentLifeCycle;
                    mActivityRecordStack.push(newRecord);
                }
            }
        }
    }

    private void printActivityInfo(Activity activity, Bundle icicle) {
        Logger.d(TAG, "callActivityOnCreate : " + activity.getClass().getName());
        Logger.d(TAG, "ActivityName :" + activity.getClass().getName());
        Logger.d(TAG, "savedBundle:" + (icicle == null ? "null" : icicle.toString()));
        Logger.d(TAG, "intent : " + (activity.getIntent() == null ? "null" : activity.getIntent().toString()));
        if (activity.getIntent() != null) {
            Bundle extras = activity.getIntent().getExtras();
            Logger.d(TAG, "extras:" + (extras == null ? "null" : extras.toString()));
            if (extras != null) {
                Logger.d("printBundle");
                Set<String> ketSet = extras.keySet();
                for (String key : ketSet) {
                    Object value = extras.get(key);
                    try {
                        Logger.d(TAG, "(key : value)=(" + key + " : " + value + ")");
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("main","onActivityStarted : " + activity.getClass().getCanonicalName());
    }

    @Override
    public void onActivityResumed(Activity activity) {

        if (DEBUG) {
            Logger.d(TAG, "callActivityOnResume activityName : " + activity.getClass().getName());
        }

        for (ActivityLifeCycleObserver observer : mObserverList) {
            observer.beforeOnResume(activity);
        }

        addToRecordStack(activity, ActivityStateRecord.ON_RESUME);

        for (ActivityLifeCycleObserver observer : mObserverList) {
            observer.afterOnResume(activity);
        }

        if (DEBUG) {
            printStackInfo(mActivityRecordStack);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG, "callActivityOnPause activityName : " + activity.getClass().getName());
        }

        for (ActivityLifeCycleObserver observer : mObserverList) {
            observer.beforeOnPause(activity);
        }

        addToRecordStack(activity, ActivityStateRecord.ON_PAUSE);

        for (ActivityLifeCycleObserver observer : mObserverList) {
            observer.afterOnPause(activity);
        }

        if (DEBUG) {
            printStackInfo(mActivityRecordStack);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d("main","onActivityStopped : " + activity.getClass().getCanonicalName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d("main","onActivitySaveInstance : " + activity.getClass().getCanonicalName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG, "callActivityOnDestroy activityName : " + activity.getClass().getName());
        }

        for (ActivityLifeCycleObserver observer : mObserverList) {
            observer.beforeOnDestroy(activity);
        }

        for (ActivityLifeCycleObserver observer : mObserverList) {
            observer.afterOnDestroy(activity);
        }

        removeActivityFromStack(activity);

        if (DEBUG) {
            printStackInfo(mActivityRecordStack);
        }
    }

    /**
     * 判断给定的activityName经历了指定的lifeCycle
     * 可以这样理解：我期待一个activity是否在onCreate状态的时候，如果这个Activity已经是onResume状态了，那么
     * 它一定经历了onCreate过程，这种情况我就认为这个activity已经经历了onCreate这个LifeCycle.
     *
     * @param activityName 指定的activityName
     * @param lifeCycle    指定的lifeCycle
     * @param deep         最大的查找的activity栈的深度，例如:deep = 0,则只查找处于栈顶的activity,deep=1,则只查找
     *                     栈顶和栈顶的下一个.
     * @return true 指定的activity经历了指定的lifeCycle false 指定的lifeCycle还没有发生
     */
    boolean isExpectedActivityLifeCycle(String activityName, int lifeCycle, int deep) {

        if (lifeCycle > ActivityStateRecord.ON_PAUSE) {
            Log.d("main", "lifeCycle : " + lifeCycle);
            return false;
        }

        synchronized (mActivityRecordStack) {
            if (deep < 0 || deep >= mActivityRecordStack.size()) {
                Logger.e("illegal size of deep,current deep = " + deep + " deep must greater " +
                        "than 0 and less than " + mActivityRecordStack.size() + " or current Activity stack is empty!", null);
                return false;
            }
            int size = mActivityRecordStack.size();
            int start = size - 1;
            int end = start - deep;
            for (int i = start; i >= end; i--) {
                ActivityStateRecord record = mActivityRecordStack.get(i);
                Activity activity = record.mActivityWeakRef.get();
                boolean isSameActivity = activity != null && activity.getClass().getName().equals(activityName);
                if (isSameActivity) {
                    return record.currentLifeCycle >= lifeCycle;
                }
            }
            return false;
        }
    }

    Activity getCurrentActivity() {
        if (mActivityRecordStack.isEmpty()) {
            return null;
        }
        synchronized (mActivityRecordStack) {
            return mActivityRecordStack.peek().mActivityWeakRef.get();
        }
    }

    void goBackToActivity(String activityName) {

        ArrayList<Activity> openedActivities = getAllOpenedActivities();
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
                    Scheduler.runOnMainSync(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                currentActivity.onBackPressed();
                            } catch (Exception ignore) {
                            }
                        }
                    });
                } catch (Exception e) {
                    Logger.e("", e);
                }
            }
        }
    }

    void finishAllOpenedActivities() {
        ArrayList<Activity> activities = getAllOpenedActivities();
        for (final Activity activity : activities) {
            Scheduler.runOnMainSync(new Runnable() {
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



    // ------------ private method ------------------



    private void removeActivityFromStack(Activity activity) {

        synchronized (mActivityRecordStack) {
            Iterator<ActivityStateRecord> activityStackIterator = mActivityRecordStack.iterator();
            while (activityStackIterator.hasNext()) {
                ActivityStateRecord record = activityStackIterator.next();
                Activity activityFromWeakReference = record.mActivityWeakRef.get();

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
                // printStackInfo(mActivityRecordStack);
            }
        }
    }

    private ArrayList<Activity> getAllOpenedActivities() {

        ArrayList<Activity> activities = new ArrayList<Activity>();
        synchronized (mActivityRecordStack) {
            for (ActivityStateRecord stateRecord : mActivityRecordStack) {
                Activity activity = stateRecord.mActivityWeakRef.get();
                if (activity != null)
                    activities.add(activity);
            }
            return activities;
        }
    }

    static class ActivityStateRecord {
        static final int ON_CREATE = 0;
        static final int ON_RESUME = 1;
        static final int ON_PAUSE = 2;

        WeakReference<Activity> mActivityWeakRef;
        int currentLifeCycle;

        @Override
        public String toString() {
            Activity activity = mActivityWeakRef.get();
            String name = activity == null ? "null" : activity.getClass().getName();
            return "name : " + name + " lifeCycle : " + toSignificantString(currentLifeCycle);
        }

        String toSignificantString(int lifeCycle) {
            switch (lifeCycle) {
                case ON_CREATE:
                    return "onCreate";
                case ON_PAUSE:
                    return "onPause";
                case ON_RESUME:
                    return "onResume";
                default:
                    return "unKnow";
            }
        }
    }
}
