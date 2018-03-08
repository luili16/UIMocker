package com.llx278.uimocker2;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.TextView;

import java.lang.reflect.Constructor;

/**
 * 包含一些与click相关的方法
 *
 */

public class Clicker {

    private final String LOG_TAG = "uimocker";
    private final ViewGetter mViewGetter;
    private final InstrumentationDecorator mInst;
    private final ActivityUtils mActivityUtils;
    private final Sender mSender;
    private final Sleeper mSleeper;
    private final Searcher mSearcher;

    private final DialogUtils mDialogUtils;
    private final int MINI_WAIT = 300;
    private final int WAIT_TIME = 1500;

    public Clicker(ActivityUtils activityUtils, ViewGetter viewGetter,
                   Sender sender, InstrumentationDecorator inst,
                   Sleeper sleeper, Searcher searcher, DialogUtils dialogUtils) {

        this.mActivityUtils = activityUtils;
        this.mViewGetter = viewGetter;
        this.mSender = sender;
        this.mInst = inst;
        this.mSleeper = sleeper;
        this.mSearcher = searcher;
        this.mDialogUtils = dialogUtils;
    }

    /**
     * 点击
     * @param x x坐标
     * @param y y坐标
     * @return true 成功的发送了点击事件 false 发送点击事件失败
     */
    public boolean clickOnScreen(float x,float y){
       return clickOnScreen(x,y,null);
    }

    /**
     * 长按
     * @param x x坐标
     * @param y y坐标
     * @param time 长按时间
     * @return true 成功发送了长按事件 false 发送长按事件失败
     */
    public boolean longClickOnScreen(float x,float y,long time) {
        return longClickOnScreen(x,y,time,null);
    }

    /**
     * 对指定的view发送点击事件
     * @param target 指定的view
     * @return true 点击成功 false 点击失败
     */
    public boolean clickOnView(View target) {
        return clickOnScreen(target,false,0);
    }

    public boolean longClickOnView(View target,long time) {
        return clickOnScreen(target,true,time);
    }

    /**
     * 向targetView的这个View的中心位置发送点击事件，containerView为开始分发
     * 点击事件的开始
     * 注意 ： targetView一定要是containerView的子view，否则不会产生点击事件
     * @param target
     * @param container
     */
    public boolean clickOnView(View target,View container) {
        return clickOnView(target,container,false,0);
    }

    /**
     * 向targetView的这个View的中心位置发送点击事件，containerView为开始分发
     * 点击事件的开始
     * 注意 ： targetView一定要是containerView的子view，否则不会产生点击事件
     * @param target
     * @param container
     */
    public boolean longClickOnView(View target,View container,long time) {
        return clickOnView(target,container,true,time);
    }


    public boolean clickOnText(String regex,long timeout,boolean scroll) {
        return clickOnText(regex,timeout,false,scroll,0);
    }

    public boolean longClickOnText(String regex,long timeout,boolean scroll,long time) {
        return clickOnText(regex,timeout,true,scroll,time);
    }

    /**
     * Clicks on an ActionBar Home/Up button.
     */

    public void clickOnActionBarHomeButton() {
        Activity activity = mActivityUtils.getCurrentActivity();
        MenuItem homeMenuItem = null;

        try {
            Class<?> cls = Class.forName("com.android.internal.view.menu.ActionMenuItem");
            Class<?> partypes[] = new Class[6];
            partypes[0] = Context.class;
            partypes[1] = Integer.TYPE;
            partypes[2] = Integer.TYPE;
            partypes[3] = Integer.TYPE;
            partypes[4] = Integer.TYPE;
            partypes[5] = CharSequence.class;
            Constructor<?> ct = cls.getConstructor(partypes);
            Object argList[] = new Object[6];
            argList[0] = activity;
            argList[1] = 0;
            argList[2] = android.R.id.home;
            argList[3] = 0;
            argList[4] = 0;
            argList[5] = "";
            homeMenuItem = (MenuItem) ct.newInstance(argList);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Can not find methods to invoke Home button!");
        }

        if (homeMenuItem != null) {
            try{
                activity.getWindow().getCallback().onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, homeMenuItem);
            }catch(Exception ignored) {}
        }
    }



    public boolean clickOnMenuItem(String regex,long timeout) {
        openMenu();
        return clickOnText(regex,timeout,true);
    }

    /**
     * Opens the menu and waits for it to open.
     */

    public void openMenu(){
        mSleeper.sleepMini();

        if(!mDialogUtils.waitForDialogToOpen(MINI_WAIT, false)) {
            try{
                mSender.sendKeyCode(KeyEvent.KEYCODE_MENU);
                mDialogUtils.waitForDialogToOpen(WAIT_TIME, true);
            }catch(SecurityException e){
                Logger.d(LOG_TAG,"Can not open the menu!");
            }
        }
    }

    /**
     * Private method used to click on a given view.
     *
     * @param view      the view that should be clicked
     * @param longClick true if the click should be a long click
     * @param time      the amount of time to long click
     */

    private boolean clickOnScreen(View view, boolean longClick, long time) {
        if (view == null) {
            Logger.i(LOG_TAG, "Clicker.clickOnScreen(View,boolean,long) : clickView is null and can therefore not be clicked!");
            return false;
        }

        float[] xyToClick = getClickCoordinates(view);
        float x = xyToClick[0];
        float y = xyToClick[1];

        if (x == 0 || y == 0) {
            mSleeper.sleepMini();
            try {
                view = mViewGetter.getIdenticalView(view);
            } catch (Exception ignored) {
            }

            if (view != null) {
                xyToClick = getClickCoordinates(view);
                x = xyToClick[0];
                y = xyToClick[1];
            }
        }

        mSleeper.sleep(300);
        if (longClick) {
            return longClickOnScreen(x, y, time, view);
        } else {
            return clickOnScreen(x, y, view);
        }
    }

    /**
     * 向targetView的这个View的中心位置发送点击事件，containerView为开始分发
     * 点击事件的开始
     * 注意 ： targetView一定要是containerView的子view，否则不会产生点击事件
     * @param target
     * @param container
     */
    private boolean clickOnView(final View target,final View container,final boolean longClick,final long time) {
        final float[] out = new float[2];
        if (!findTouchPosition(out,container,target)) {
            Logger.e("Clicker.clickOnView(View,View,boolean,long) find TouchPosition failed!",null);
            return false;
        }
        if (!longClick) {
            mInst.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    MotionEvent downEvent = getSimpleMotionEvent(MotionEvent.ACTION_DOWN,out[0],out[1]);
                    MotionEvent upEvent = getSimpleMotionEvent(MotionEvent.ACTION_UP,out[0],out[1]);
                    container.dispatchTouchEvent(downEvent);
                    container.dispatchTouchEvent(upEvent);
                    downEvent.recycle();
                    upEvent.recycle();
                }
            });
        } else {
            mInst.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    MotionEvent downEvent = getSimpleMotionEvent(MotionEvent.ACTION_DOWN,out[0],out[1]);
                    container.dispatchTouchEvent(downEvent);
                    downEvent.recycle();
                }
            });
            mSleeper.sleep(time);
            mInst.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    MotionEvent upEvent = getSimpleMotionEvent(MotionEvent.ACTION_UP,out[0],out[1]);
                    container.dispatchTouchEvent(upEvent);
                    upEvent.recycle();
                }
            });
        }
        return true;
    }

    /**
     * 注意：获得的这个motionevent一定要释放！
     * @param action MotionEvent.Action_* 中的一个
     * @param x x 坐标
     * @param y y 坐标
     * @return 获得的motionevent
     */
    private MotionEvent getSimpleMotionEvent(int action,float x,float y) {
        return MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                action,
                x, y,
                0);
    }

    /**
     * Returns click coordinates for the specified view.
     *
     * @param view the view to get click coordinates from
     * @return click coordinates for a specified view
     */

    private float[] getClickCoordinates(View view) {
        int[] xyLocation = new int[2];
        float[] xyToClick = new float[2];
        int trialCount = 0;

        view.getLocationOnScreen(xyLocation);
        while (xyLocation[0] == 0 && xyLocation[1] == 0 && trialCount < 10) {
            mSleeper.sleep(300);
            view.getLocationOnScreen(xyLocation);
            trialCount++;
        }

        final int viewWidth = view.getWidth();
        final int viewHeight = view.getHeight();
        final float x = xyLocation[0] + (viewWidth / 2.0f);
        float y = xyLocation[1] + (viewHeight / 2.0f);

        xyToClick[0] = x;
        xyToClick[1] = y;

        return xyToClick;
    }

    private boolean findTouchPosition(float[] out, View container, View targetView) {
        View listItem = targetView;
        float x = targetView.getX();
        float y = targetView.getY();
        try {
            View v;
            while ((v = (View) listItem.getParent()) != null && !v.equals(container)) {
                x += v.getX();
                y += v.getY();
                listItem = v;
            }
        } catch (ClassCastException e) {
            Logger.e(LOG_TAG,"targetView is not a child of container!",e);
            return false;
        }

        // 找到中心点
        int w = targetView.getWidth();
        int h = targetView.getHeight();
        float targetX = x + w / 2;
        float targetY = y + h / 2;
        out[0] = targetX;
        out[1] = targetY;
        return true;
    }

    private boolean clickOnText(String regex, long timeout, boolean longClick, boolean scroll, long time) {
        TextView tv = mSearcher.searchByText(regex, timeout, scroll, true);
        if (tv != null) {
            return clickOnScreen(tv, longClick, time);
        }
        Logger.e(LOG_TAG,"can not search textView from regex:" + regex + " click failed!",null);
        return false;
    }

    private boolean clickOnScreen(float x, float y, View view) {
        boolean success = false;
        int retry = 0;
        SecurityException ex = null;

        while (!success && retry < 20) {
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                    MotionEvent.ACTION_DOWN, x, y, 0);
            MotionEvent event2 = MotionEvent.obtain(downTime, eventTime,
                    MotionEvent.ACTION_UP, x, y, 0);
            try {
                mInst.sendPointerSync(event);
                mInst.sendPointerSync(event2);
                success = true;
            } catch (SecurityException e) {
                ex = e;
                mDialogUtils.hideSoftKeyboard(null, false, true);
                mSleeper.sleep(MINI_WAIT);
                retry++;
                View identicalView = mViewGetter.getIdenticalView(view);
                if (identicalView != null) {
                    float[] xyToClick = getClickCoordinates(identicalView);
                    x = xyToClick[0];
                    y = xyToClick[1];
                }
            }
        }
        if (!success) {
            Logger.e(LOG_TAG,String.format("Clicker.clickOnScreen(float,float,view) : " +
                    "click at (" + x + ", " + y + ") can not be completed!\n" +
                    "runtime:[x=%f,y=%f,view=%s]",x,y,view.toString()),ex);
            return false;
        }
        //Logger.d(LOG_TAG,String.format(Locale.CHINA,"Clicker.clickOnScreen(float,float,view) : click at (%f,%f)",x,y));
        return true;
    }

    /**
     * 在屏幕上长按
     *
     * @param x    the x coordinate 在屏幕上的x坐标值
     * @param y    the y coordinate 在屏幕上的y坐标值
     * @param time 长按的时间
     */

    private boolean longClickOnScreen(float x, float y, long time, View view) {
        boolean successfull = false;
        int retry = 0;
        SecurityException ex = null;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);

        while (!successfull && retry < 20) {
            try {
                mInst.sendPointerSync(event);
                successfull = true;
                mSleeper.sleep(MINI_WAIT);
            } catch (SecurityException e) {
                ex = e;
                mDialogUtils.hideSoftKeyboard(null, false, true);
                mSleeper.sleep(MINI_WAIT);
                retry++;
                View identicalView = mViewGetter.getIdenticalView(view);
                if (identicalView != null) {
                    float[] xyToClick = getClickCoordinates(identicalView);
                    x = xyToClick[0];
                    y = xyToClick[1];
                }
            }
        }
        if (!successfull) {
            Logger.e(LOG_TAG, String.format("Clicker.longClickOnScreen(float,float,long,view) : " +
                    "Long click at (" + x + ", " + y + ") can not be completed!\n" +
                    "runtime:[x=%f,y=%f,time=%l,view=%s]",x,y,time,view.toString()),ex);
            return false;
        }

        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x + 1.0f, y + 1.0f, 0);
        mInst.sendPointerSync(event);
        if (time > 0) {
            mSleeper.sleep(time);
        } else {
            mSleeper.sleep((int) (ViewConfiguration.getLongPressTimeout() * 2.5f));
        }

        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
        mInst.sendPointerSync(event);
        mSleeper.sleep();
        //Logger.d(LOG_TAG,String.format(Locale.CHINA,"Clicker.longClickOnScreen(float,float,long,view) : click at (%f,%f)",x,y));
        return true;
    }
}
