package com.llx278.uimocker2;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 一些常用的api结合，组合了{@link Clicker},{@link Searcher},{@link ViewGetter},{@link Waiter}等一些类，
 * 使得使用更加的方便一些。
 * @author llx
 */
public class Solo {

    private static final long DEFAULT_SLEEP = 200;
    private static final long DEFAULT_TIME_OUT = 1000 * 20;

    private static final String TAG = "Solo";
    private static Solo INSTANCE;


    private final Clicker mClicker;
    private final Scroller mScroller;
    private final Searcher mSearcher;

    private final ViewGetter mViewGetter;
    private final Waiter mWaiter;
    private final Gesture mGesture;

    private final ActivityUtils mActivityUtils;
    private final MyInstrumentation mInstrumentation;
    private final DialogUtils mDialogUtils;
    private final WebUtils mWebUtils;

    private Solo(Context context) {

        mInstrumentation = new MyInstrumentation(context);

        mActivityUtils = new ActivityUtils(mInstrumentation);
        mViewGetter = new ViewGetter(mInstrumentation);
        mGesture = new Gesture(mInstrumentation,mActivityUtils);
        mScroller = new Scroller(mInstrumentation, mViewGetter, mGesture);
        mSearcher = new Searcher(mViewGetter, mScroller);
        mWebUtils = new WebUtils(mInstrumentation);
        mWaiter = new Waiter( mActivityUtils, mViewGetter, mSearcher,mScroller,mWebUtils);
        mDialogUtils = new DialogUtils(mInstrumentation, mActivityUtils, mViewGetter);
        mClicker = new Clicker( mViewGetter, mInstrumentation, mSearcher);
    }

    private void pause(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ignore) {
        }
    }

    public static Solo getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Solo(context);
        }
        return INSTANCE;
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
    public View findViewById(int id,View parent) {
        return findViewById(id,parent,DEFAULT_TIME_OUT);
    }

    /**
     * 根据id找到给定parent里面的view
     *
     * @param id     指定的id
     * @param parent 待寻找的view
     * @param timeout 超时时间
     * @return 找到的view，如果为空则说明在超时时间里面没有找到
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
        return mInstrumentation.getContext();
    }

    public void runOnMainSync(Runnable runnable) {
        mInstrumentation.runOnMainSync(runnable);
    }

    public void sleep(long time){
        pause(time);
    }

    public void waitForTextAndClick(String regex) {
        View view = mWaiter.waitForTextAppearAndGet(regex, DEFAULT_TIME_OUT);
        mClicker.clickOnView(view);
    }
}
