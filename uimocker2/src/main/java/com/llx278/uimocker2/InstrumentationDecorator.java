package com.llx278.uimocker2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.input.InputManager;
import android.media.DeniedByServerException;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.Window;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 封装了从android.app.ActivityThread反射获取的Instrumentation
 * @author llx
 */
public abstract class InstrumentationDecorator extends Instrumentation {
    private static final boolean DEBUG = false;
    private static final String TAG = "InstrumentationDecorator";
    private Instrumentation mInstrumentation;
    private Context mContext;

    public InstrumentationDecorator(Context context) {
        mContext = context;
        init();
    }

    @SuppressLint("PrivateApi")
    private void init() {
        try {
            Class<?> activityThreadClass = null;
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
            Object sCurrentActivityThread = currentActivityThreadMethod.invoke(null);
            Class<?> aClass = sCurrentActivityThread.getClass();
            Looper mLooper = (Looper) aClass.getMethod("getLooper").invoke(sCurrentActivityThread);
            Field mInstrumentationField = ReflectUtil.findFieldRecursiveImpl(aClass, "mInstrumentation");
            mInstrumentationField.setAccessible(true);
            mInstrumentation = (Instrumentation) mInstrumentationField.get(sCurrentActivityThread);
            Class<? extends Instrumentation> mInstrumentationClass = mInstrumentation.getClass();
            Field mThread = ReflectUtil.findFieldRecursiveImpl(mInstrumentationClass, "mThread");
            mThread.setAccessible(true);
            mThread.set(mInstrumentation, sCurrentActivityThread);
            Field mMessageQueue = ReflectUtil.findFieldRecursiveImpl(mInstrumentationClass, "mMessageQueue");
            mMessageQueue.setAccessible(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mMessageQueue.set(mInstrumentation, mLooper.getQueue());
            } else {
                Field mQueueField = ReflectUtil.findFieldRecursiveImpl(mLooper.getClass(), "mQueue");
                mQueueField.setAccessible(true);
                mMessageQueue.set(mInstrumentation, mQueueField.get(mLooper));
            }
            mInstrumentationField.set(sCurrentActivityThread,this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @CallSuper
    public void onCreate(Bundle arguments) {
        if (DEBUG) {
            Logger.d(TAG,"onCreate");
        }
        mInstrumentation.onCreate(arguments);
    }

    @Override
    @CallSuper
    public void start() {
        if (DEBUG) {
            Logger.d(TAG,"start");
        }
        mInstrumentation.start();
    }

    @Override
    @CallSuper
    public void onStart() {
        if (DEBUG) {
            Logger.d(TAG,"onStart");
        }
        mInstrumentation.onStart();
    }

    @Override
    @CallSuper
    public boolean onException(Object obj, Throwable e) {
        if (DEBUG) {
            Logger.d(TAG,"onException");
        }
        return mInstrumentation.onException(obj, e);
    }

    @Override
    @CallSuper
    public void sendStatus(int resultCode, Bundle results) {
        if (DEBUG) {
            Logger.d(TAG,"sendStatus");
        }
        mInstrumentation.sendStatus(resultCode, results);
    }

    @Override
    @CallSuper
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addResults(Bundle results) {
        if (DEBUG) {
            Logger.d(TAG,"sendStatus");
        }
        mInstrumentation.addResults(results);
    }

    @Override
    @CallSuper
    public void finish(int resultCode, Bundle results) {
        if (DEBUG) {
            Logger.d(TAG,"finish");
        }
        mInstrumentation.finish(resultCode, results);

    }

    @Override
    @CallSuper
    public void setAutomaticPerformanceSnapshots() {
        if (DEBUG) {
            Logger.d(TAG,"setAutomaticPerformanceSnapshots");
        }
        mInstrumentation.setAutomaticPerformanceSnapshots();
    }

    @Override
    @CallSuper
    public void startPerformanceSnapshot() {
        if (DEBUG) {
            Logger.d(TAG,"startPerformanceSnapshot");
        }
        mInstrumentation.startPerformanceSnapshot();
    }

    @Override
    @CallSuper
    public void endPerformanceSnapshot() {
        if (DEBUG) {
            Logger.d(TAG,"endPerformanceSnapshot");
        }
        mInstrumentation.endPerformanceSnapshot();
    }

    @Override
    @CallSuper
    public void onDestroy() {
        if (DEBUG) {
            Logger.d(TAG,"onDestroy");
        }
        mInstrumentation.onDestroy();
    }

    @Override
    @CallSuper
    public Context getContext() {
        if (DEBUG) {
            Logger.d(TAG,"getContext");
        }
        return mContext;
    }

    @Override
    @CallSuper
    public ComponentName getComponentName() {
        if (DEBUG) {
            Logger.d(TAG,"getComponentName");
        }
        return mInstrumentation.getComponentName();
    }

    @Override
    @CallSuper
    public Context getTargetContext() {
        if (DEBUG) {
            Logger.d(TAG,"getTargetContext");
        }
        return mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @CallSuper
    public String getProcessName() {
        if (DEBUG) {
            Logger.d(TAG,"getProcessName");
        }
        return mInstrumentation.getProcessName();
    }

    @Override
    @CallSuper
    public boolean isProfiling() {
        if (DEBUG) {
            Logger.d(TAG,"isProfiling");
        }
        return mInstrumentation.isProfiling();
    }

    @Override
    @CallSuper
    public void startProfiling() {
        if (DEBUG) {
            Logger.d(TAG,"startProfiling");
        }
        mInstrumentation.startProfiling();
    }

    @Override
    @CallSuper
    public void stopProfiling() {
        if (DEBUG) {
            Logger.d(TAG,"stopProfiling");
        }
        mInstrumentation.stopProfiling();
    }

    @Override
    @CallSuper
    public void setInTouchMode(boolean inTouch) {
        if (DEBUG) {
            Logger.d(TAG,"setTouchMode");
        }
        mInstrumentation.setInTouchMode(inTouch);
    }

    @Override
    @CallSuper
    public void waitForIdle(Runnable recipient) {
        if (DEBUG) {
            Logger.d(TAG,"waitForIdle");
        }
        mInstrumentation.waitForIdle(recipient);
    }

    @Override
    @CallSuper
    public void waitForIdleSync() {
        if (DEBUG) {
            Logger.d(TAG,"waitForIdleSync");
        }
        mInstrumentation.waitForIdleSync();
    }

    @Override
    @CallSuper
    public void runOnMainSync(Runnable runner) {
        if (DEBUG) {
            Logger.d(TAG,"runOnMainSync");
        }
        mInstrumentation.runOnMainSync(runner);

    }

    @Override
    @CallSuper
    public Activity startActivitySync(Intent intent) {
        if (DEBUG) {
            Logger.d(TAG,"startActivitySync");
        }
        return mInstrumentation.startActivitySync(intent);
    }


    @Override
    @CallSuper
    public void addMonitor(Instrumentation.ActivityMonitor monitor) {
        if (DEBUG) {
            Logger.d(TAG,"registerMonitor");
        }
        mInstrumentation.addMonitor(monitor);
    }

    @Override
    @CallSuper
    public Instrumentation.ActivityMonitor addMonitor(
            IntentFilter filter, ActivityResult result, boolean block) {
        if (DEBUG) {
            Logger.d(TAG,"registerMonitor");
        }
        return mInstrumentation.addMonitor(filter, result, block);
    }

    @Override
    @CallSuper
    public Instrumentation.ActivityMonitor addMonitor(
            String cls, ActivityResult result, boolean block) {
        if (DEBUG) {
            Logger.d(TAG,"registerMonitor");
        }
        return mInstrumentation.addMonitor(cls, result, block);
    }

    @Override
    @CallSuper
    public boolean checkMonitorHit(Instrumentation.ActivityMonitor monitor, int minHits) {
        if (DEBUG) {
            Logger.d(TAG,"checkMonitorHit");
        }
        return mInstrumentation.checkMonitorHit(monitor, minHits);
    }

    @Override
    @CallSuper
    public Activity waitForMonitor(Instrumentation.ActivityMonitor monitor) {
        if (DEBUG) {
            Logger.d(TAG,"waitForMonitor");
        }
        return mInstrumentation.waitForMonitor(monitor);
    }

    @Override
    @CallSuper
    public Activity waitForMonitorWithTimeout(Instrumentation.ActivityMonitor monitor, long timeOut) {
        if (DEBUG) {
            Logger.d(TAG,"waitForMonitorWithTimeout");
        }
        return mInstrumentation.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    @CallSuper
    public void removeMonitor(Instrumentation.ActivityMonitor monitor) {
        if (DEBUG) {
            Logger.d(TAG,"removeMonitor");
        }
        mInstrumentation.removeMonitor(monitor);
    }

    @Override
    @CallSuper
    public boolean invokeMenuActionSync(Activity targetActivity,
                                        int id, int flag) {
        if (DEBUG) {
            Logger.d(TAG,"invokeMenuActionSync");
        }
        return mInstrumentation.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    @CallSuper
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        if (DEBUG) {
            Logger.d(TAG,"invokeContextMenuAction");
        }
        return mInstrumentation.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    @CallSuper
    public void sendStringSync(String text) {
        if (DEBUG) {
            Logger.d(TAG,"sendStringSync");
        }
        mInstrumentation.sendStringSync(text);
    }

    @Override
    @CallSuper
    public void sendKeySync(KeyEvent event) {
        if (DEBUG) {
            Logger.d(TAG,"sendKeySync");
        }
        mInstrumentation.sendKeySync(event);
    }

    @Override
    @CallSuper
    public void sendKeyDownUpSync(int key) {
        if (DEBUG) {
            Logger.d(TAG,"sendKeyDownUpSync");
        }
        mInstrumentation.sendKeyDownUpSync(key);
    }

    @Override
    @CallSuper
    public void sendCharacterSync(int keyCode) {
        if (DEBUG) {
            Logger.d(TAG,"sendCharacterSync");
        }
        mInstrumentation.sendCharacterSync(keyCode);
    }

    @Override
    @CallSuper
    public void sendPointerSync(MotionEvent event) {
        if (DEBUG) {
            Logger.d(TAG,"sendPointerSync");
        }
        mInstrumentation.sendPointerSync(event);
    }

    @Override
    @CallSuper
    public void sendTrackballEventSync(MotionEvent event) {
        if (DEBUG) {
            Logger.d(TAG,"sendTrackballEventSync");
        }
        mInstrumentation.sendTrackballEventSync(event);
    }

    @Override
    @CallSuper
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        if (DEBUG) {
            Logger.d(TAG,"newApplication(ClassLoader cl, String className, Context context)");
        }
        return mInstrumentation.newApplication(cl, className, context);
    }


    @CallSuper
    static public Application newApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        if (DEBUG) {
            Logger.d(TAG,"newApplication(Class<?> clazz, Context context)");
        }
        return Instrumentation.newApplication(clazz, context);
    }

    @Override
    @CallSuper
    public void callApplicationOnCreate(Application app) {
        if (DEBUG) {
            Logger.d(TAG,"callApplicationOnCreate");
        }
        mInstrumentation.callApplicationOnCreate(app);
    }

    @Override
    @CallSuper
    public Activity newActivity(Class<?> clazz, Context context,
                                IBinder token, Application application, Intent intent, ActivityInfo info,
                                CharSequence title, Activity parent, String id,
                                Object lastNonConfigurationInstance) throws InstantiationException,
            IllegalAccessException {
        if (DEBUG) {
            Logger.d(TAG,"newActivity(Class<?> clazz, Context context,\n" +
                    "IBinder token, Application application, Intent intent, ActivityInfo info,\n" +
                    "CharSequence title, Activity parent, String id,\n" +
                    "Object lastNonConfigurationInstance)");
        }
        return mInstrumentation.newActivity(clazz, context, token, application, intent, info,
                title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    @CallSuper
    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        if (DEBUG) {
            Logger.d(TAG,"newActivity(ClassLoader cl, String className,Intent intent)");
        }
        return mInstrumentation.newActivity(cl, className, intent);
    }

    @Override
    @CallSuper
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnCreate(Activity activity, Bundle icicle)");
        }
        mInstrumentation.callActivityOnCreate(activity, icicle);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    @CallSuper
    public void callActivityOnCreate(Activity activity, Bundle icicle,
                                     PersistableBundle persistentState) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnCreate(Activity activity, Bundle icicle PersistableBundle persistentState)");
        }
        mInstrumentation.callActivityOnCreate(activity, icicle, persistentState);
    }

    @Override
    @CallSuper
    public void callActivityOnDestroy(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnDestroy");
        }
        mInstrumentation.callActivityOnDestroy(activity);
    }

    @Override
    @CallSuper
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnRestoreInstanceState");
        }
        mInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    @CallSuper
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState,
                                                   PersistableBundle persistentState) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnRestoreInstanceState()Activity activity, Bundle savedInstanceState,\n" +
                    "                                                   PersistableBundle persistentState");
        }
        mInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    @CallSuper
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnPostCreate");
        }
        mInstrumentation.callActivityOnPostCreate(activity, icicle);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    @CallSuper
    public void callActivityOnPostCreate(Activity activity, Bundle icicle,
                                         PersistableBundle persistentState) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnPostCreate(Activity activity, Bundle icicle,\n" +
                    "PersistableBundle persistentState)");
        }
        mInstrumentation.callActivityOnPostCreate(activity, icicle, persistentState);
    }

    @Override
    @CallSuper
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnNewIntent");
        }
        mInstrumentation.callActivityOnNewIntent(activity, intent);
    }

    @Override
    @CallSuper
    public void callActivityOnStart(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnStart");
        }
        mInstrumentation.callActivityOnStart(activity);
    }

    @Override
    @CallSuper
    public void callActivityOnRestart(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnRestart");
        }
        mInstrumentation.callActivityOnRestart(activity);
    }

    @Override
    @CallSuper
    public void callActivityOnResume(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnResume");
        }
        mInstrumentation.callActivityOnResume(activity);
    }

    @Override
    @CallSuper
    public void callActivityOnStop(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnStop");
        }
        mInstrumentation.callActivityOnStop(activity);
    }

    @Override
    @CallSuper
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnSaveInstanceState(Activity activity, Bundle outState)");
        }
        mInstrumentation.callActivityOnSaveInstanceState(activity, outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    @CallSuper
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState,
                                                PersistableBundle outPersistentState) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnSaveInstanceState(Activity activity, Bundle outState,outPersistentState)");
        }
        mInstrumentation.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }

    @Override
    @CallSuper
    public void callActivityOnPause(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnStop");
        }
        mInstrumentation.callActivityOnPause(activity);
    }

    @Override
    @CallSuper
    public void callActivityOnUserLeaving(Activity activity) {
        if (DEBUG) {
            Logger.d(TAG,"callActivityOnUserLeaving");
        }
        mInstrumentation.callActivityOnUserLeaving(activity);
    }

    @Override
    @Deprecated
    @CallSuper
    public void startAllocCounting() {
        if (DEBUG) {
            Logger.d(TAG,"startAllocCounting");
        }
        mInstrumentation.startAllocCounting();
    }

    @Override
    @Deprecated
    @CallSuper
    public void stopAllocCounting() {
        if (DEBUG) {
            Logger.d(TAG,"stopAllocCounting");
        }
        mInstrumentation.stopAllocCounting();
    }


    @Override
    @CallSuper
    public Bundle getAllocCounts() {
        if (DEBUG) {
            Logger.d(TAG,"getAllocCounts");
        }
        return mInstrumentation.getAllocCounts();
    }

   @Override
   @CallSuper
    public Bundle getBinderCounts() {
       if (DEBUG) {
           Logger.d(TAG,"getBinderCounts");
       }
       return mInstrumentation.getBinderCounts();
    }
}
