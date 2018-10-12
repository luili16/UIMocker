package com.llx278.uimocker2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 一些常用的api结合，组合了{@link Clicker},{@link Searcher},{@link ViewGetter},{@link Waiter}等一些类，
 * 使得使用更加的方便一些。
 * @author llx
 */
public class Solo {

    private static final long DEFAULT_SLEEP = 200;
    private static final long DEFAULT_TIME_OUT = 1000 * 20;
    private static final long LITTLE_SLEEP = 1000;

    private static final String TAG = "Solo";

    private Clicker mClicker;
    private Scroller mScroller;
    private Searcher mSearcher;

    private ViewGetter mViewGetter;
    private Waiter mWaiter;
    private Gesture mGesture;

    private ActivityUtils mActivityUtils;
    private DialogUtils mDialogUtils;
    private WebUtils mWebUtils;
    private ActivityLifeCycleCallbackImpl mImpl;
    private Context mContext;

    @SuppressLint("PrivateApi")
    public Solo(Application app, Instrumentation instrumentation) {

        try {
            Instrumentation instrumentation1;
            if (instrumentation == null) {
                Class<?> activityThreadClass = null;
                activityThreadClass = Class.forName("android.app.ActivityThread");
                Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
                Object sCurrentActivityThread = currentActivityThreadMethod.invoke(null);
                Class<?> aClass = sCurrentActivityThread.getClass();
                Field mInstrumentationField = ReflectUtil.findFieldRecursiveImpl(aClass, "mInstrumentation");
                mInstrumentationField.setAccessible(true);
                instrumentation1 = (Instrumentation) mInstrumentationField.get(sCurrentActivityThread);
            } else {
                instrumentation1 = instrumentation;
            }

            mContext = app.getApplicationContext();
            mImpl = new ActivityLifeCycleCallbackImpl();
            app.registerActivityLifecycleCallbacks(mImpl);
            mActivityUtils = new ActivityUtils(mImpl);
            mViewGetter = new ViewGetter(mContext);
            mGesture = new Gesture(instrumentation1,mActivityUtils);
            mScroller = new Scroller(mContext, mViewGetter, mGesture);
            mSearcher = new Searcher(mViewGetter, mScroller);
            mWebUtils = new WebUtils(mContext);
            mWaiter = new Waiter( mActivityUtils, mViewGetter, mSearcher,mScroller,mWebUtils);
            mDialogUtils = new DialogUtils(mContext, mActivityUtils, mViewGetter);
            mClicker = new Clicker( mViewGetter, instrumentation1, mSearcher);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void pause(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ignore) {
        }
    }

    public Clicker getClicker() {
        return mClicker;
    }

    public Scroller getScroller() {
        return mScroller;
    }

    public Searcher getSearcher() {
        return mSearcher;
    }

    public ViewGetter getViewGetter() {
        return mViewGetter;
    }

    public Waiter getWaiter() {
        return mWaiter;
    }

    public Gesture getGesture() {
        return mGesture;
    }

    public ActivityUtils getActivityUtils() {
        return mActivityUtils;
    }

    public DialogUtils getDialogUtils() {
        return mDialogUtils;
    }

    public WebUtils getWebUtils(){
        return mWebUtils;
    }

    /**
     * 根据id找到当前activity里面的view
     *
     * @param id 指定的id
     * @return 找的view，如果为空，则说明在默认给定的时间里面没有找到
     */
    public View findViewById(int id) {
        return findViewById(id, DEFAULT_TIME_OUT);
    }

    /**
     * 根据id找到当前activity里面的view
     *
     * @param id      指定的id
     * @param timeout 超时时间
     * @return 找的view，如果为空，则说明在给定的时间里面没有找到
     */
    public View findViewById(int id, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {

            Activity currentActivity = mActivityUtils.getCurrentActivity();
            if (currentActivity == null) {
                Logger.d(TAG,"Solo.findViewById(int) currentActivity is null");
                continue;
            }
            View view = currentActivity.findViewById(id);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    /**
     * 根据id找到给定parent里面的view
     *
     * @param id     指定的id
     * @param parent 待寻找的view
     * @return 找到的view，如果为空则说明在默认超时时间里面没有找到
     */
    public View findViewById(int id, View parent) {
        return findViewById(id,parent,DEFAULT_TIME_OUT);
    }

    /**
     * 根据id找到给定parent里面的view
     *
     * @param id     指定的id
     * @param parent 父View
     * @param timeout 超时时间
     * @return 返回null:没有找到
     */
    public View findViewById(int id, View parent, long timeout) {
        if (parent == null) {
            return null;
        }

        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause(DEFAULT_SLEEP);
            View v = parent.findViewById(id);
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    /**
     * 根据Id找到给定parent里面的所有view
     * @param id id
     * @param parent 父view
     * @return 找到的view的列表
     */
    public ArrayList<View> findViewByIds(int id,View parent) {

        ArrayList<View> views = new ArrayList<>();
        if (parent == null) {
            return views;
        }

        List<View> allViews = getViewGetter().getViewList(parent, true);
        for (View v : allViews) {
            if (v.getId() == id) {
                views.add(v);
            }
        }
        return views;
    }

    public void mockSoftKeyBordSearchButton(EditText editText) throws Exception {

        Class<?> etClass = editText.getClass().getSuperclass();
        Field mEditorField = ReflectUtil.findFieldRecursiveImpl(etClass, "mEditor");
        mEditorField.setAccessible(true);
        Object mEditor = mEditorField.get(editText);
        Class<?> editorClass = mEditor.getClass();
        Field inputContentTypeField = ReflectUtil.findFieldRecursiveImpl(editorClass, "mInputContentType");
        inputContentTypeField.setAccessible(true);
        Object inputContentType = inputContentTypeField.get(mEditor);
        Class<?> inputContentTypeClass = inputContentType.getClass();
        Field onEditorActionListenerField = ReflectUtil.findFieldRecursiveImpl(inputContentTypeClass, "onEditorActionListener");
        onEditorActionListenerField.setAccessible(true);
        Object onEditorActionListener = onEditorActionListenerField.
                get(inputContentType);
        Class<?> onEditorActionListenerClass = onEditorActionListener.getClass();
        Method onEditorActionMethod = onEditorActionListenerClass.getMethod("onEditorAction", TextView.class,
                int.class, KeyEvent.class);
        onEditorActionMethod.invoke(onEditorActionListener, editText, EditorInfo.IME_ACTION_SEARCH, null);
    }

    public Context getContext() {
        return mContext;
    }

    public void sleep(long time){
        pause(time);
    }

    public void littleSleep() {
        pause(LITTLE_SLEEP);
    }

    public void littleSleep(int multiple) {
        pause(LITTLE_SLEEP * multiple);
    }

    public boolean waitForTextAndClick(String regex) {
        View view = mWaiter.waitForTextAppearAndGet(regex, DEFAULT_TIME_OUT);
        return mClicker.clickOnView(view);
    }
}
