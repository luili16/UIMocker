package com.llx278.uimocker2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;


/**
 * 对Instrumentation自定义一些功能
 *
 * @author llx
 */

final class MyInstrumentation extends InstrumentationDecorator {

    private static final boolean DEBUG = false;
    private static final String TAG = "MyInstrumentation";
    /**
     * 记录activity运行状态
     */
    private final Stack<ActivityStateRecord> mActivityRecordStack;

    MyInstrumentation(Context context) {
        super(context);
        mActivityRecordStack = new Stack<>();
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if (DEBUG) {
            Logger.d(TAG, "callActivityOnCreate : " + activity.getClass().getName());
            Logger.d(TAG, "ActivityName :" + activity.getClass().getName());
            Logger.d(TAG, "savedBundle:" + (icicle == null ? "null" : icicle.toString()));
            Logger.d(TAG, "intent : " + (activity.getIntent() == null ? "null" : activity.getIntent().toString()));
            if (activity.getIntent() != null) {
                Bundle extras = activity.getIntent().getExtras();
                Logger.d(TAG, "extras:" + (extras == null ? "null" : extras.toString()));
                if (extras != null) {
                    Logger.d("printBundle");

                }
            }
        }
        // 不应该阻止activity的执行，仅仅是通知上层调用者某个activity调用了onCreate方法而已

        super.callActivityOnCreate(activity, icicle);
        addToRecordStack(activity, ActivityStateRecord.ON_CREATE);
        if (DEBUG) {
            printStackInfo(mActivityRecordStack);
        }
    }


    @Override
    public void callActivityOnResume(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG, "callActivityOnResume activityName : " + activity.getClass().getName());
        }
        super.callActivityOnResume(activity);
        addToRecordStack(activity, ActivityStateRecord.ON_RESUME);
        if (DEBUG) {
            printStackInfo(mActivityRecordStack);
        }
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG, "callActivityOnPause activityName : " + activity.getClass().getName());
        }
        super.callActivityOnPause(activity);
        addToRecordStack(activity, ActivityStateRecord.ON_PAUSE);
        if (DEBUG) {
            printStackInfo(mActivityRecordStack);
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG, "callActivityOnDestroy activityName : " + activity.getClass().getName());
        }

        super.callActivityOnDestroy(activity);
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
    public boolean isExpectedActivityLifeCycle(String activityName, int lifeCycle, int deep) {

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

    // ------------ package method ------------------

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
        for (int i = 0; i < openedActivities.size(); i++) {
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
                    Logger.e("", e);
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

    void addToRecordStack(Activity activity, int currentLifeCycle) {

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

    // ------------ private method ------------------

    private void printStackInfo(Stack<ActivityStateRecord> activityStack) {
        Iterator<ActivityStateRecord> iterator = activityStack.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            ActivityStateRecord activity = iterator.next();
            sb.append(activity.toString()).append("   ");
        }
        Logger.d(TAG, "stackInfo : [" + sb.toString() + "]");
    }

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
