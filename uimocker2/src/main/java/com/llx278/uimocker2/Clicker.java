package com.llx278.uimocker2;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

/**
 * 包含一些与click相关的方法
 *
 */

public class Clicker {

    private final String LOG_TAG = "uimocker";
    private final ViewGetter mViewGetter;
    private final InstrumentationDecorator mInst;
    private final Searcher mSearcher;
    private final int MIN_CLICK_INTERCEPT = 50;
    private final int MINI_WAIT = 300;

    public Clicker(ViewGetter viewGetter,
                   InstrumentationDecorator inst,
                   Searcher searcher) {

        this.mViewGetter = viewGetter;
        this.mInst = inst;
        this.mSearcher = searcher;
    }

    private void pause(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignore) {
        }
    }

    public boolean clickOnScreen(float x, float y) {

        try {
            MotionEvent downEvent = getSimpleMotionEvent(MotionEvent.ACTION_DOWN, x, y);
            mInst.sendPointerSync(downEvent);
            downEvent.recycle();
            //pause(MIN_CLICK_INTERCEPT);
            MotionEvent upEvent = getSimpleMotionEvent(MotionEvent.ACTION_UP, x, y);
            mInst.sendPointerSync(upEvent);
            upEvent.recycle();
        } catch (SecurityException e) {
            MLogger.e(LOG_TAG,String.format("Clicker.clickOnScreen(float,float) : " +
                    "click at (" + x + ", " + y + ") can not be completed!\n" +
                    "runtime:[x=%f,y=%f]",x,y),e);
            return false;
        }
        return true;
    }

    /**
     * 在屏幕上长按
     *
     * @param x    the x coordinate 在屏幕上的x坐标值
     * @param y    the y coordinate 在屏幕上的y坐标值
     * @param time 长按的时间
     */

    public boolean longClickOnScreen(float x, float y, long time) {

        try {
            MotionEvent downEvent = getSimpleMotionEvent(MotionEvent.ACTION_DOWN, x, y);
            mInst.sendPointerSync(downEvent);
            downEvent.recycle();

            MotionEvent moveEvent = getSimpleMotionEvent(MotionEvent.ACTION_MOVE, x + 1.0f, y + 1.0f);
            mInst.sendPointerSync(moveEvent);
            moveEvent.recycle();
            if (time > 0) {
                pause(time);
            } else {
                pause((int) (ViewConfiguration.getLongPressTimeout() * 2.5f));
            }

            MotionEvent upEvent = getSimpleMotionEvent(MotionEvent.ACTION_UP, x, y);
            mInst.sendPointerSync(upEvent);
            upEvent.recycle();
            pause(MINI_WAIT);
            return true;
        } catch (SecurityException e) {
            MLogger.e(LOG_TAG,String.format("Clicker.longClickOnScreen(float,float) : " +
                    "long click at (" + x + ", " + y + ") can not be completed!\n" +
                    "runtime:[x=%f,y=%f]",x,y),e);
            return false;
        }
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
     * 向targetView的这个View的中心位置分发点击事件，containerView为开始分发
     * 点击事件的开始，这里用view.dispatchTouchEvent实现,这种实现方式的
     * 好处是如果有其他的view拦截了你想发送点击的事件，那么通过这个方法可以绕过这个view，直接
     * 将点击的事件发送给你想要的处理点击事件的目标
     * 注意 ： targetView一定要是containerView的子view，否则不会产生点击事件
     * @param target 这个view定义了你想要发送点击事件的位置
     * @param container 从container开始分发点击事件
     */
    public boolean dispatchClickEventOnTarget(View target,View container) {
        return clickOnView(target,container,false,0);
    }

    /**
     * 向targetView的这个View的中心位置分发点击事件，containerView为开始分发
     * 点击事件的开始，这里用view.dispatchTouchEvent实现,这种实现方式的
     * 好处是如果有其他的view拦截了你想发送点击的事件，那么通过这个方法可以绕过这个view，直接
     * 将点击的事件发送给你想要的处理点击事件的目标
     * 注意 ： targetView一定要是containerView的子view，否则不会产生点击事件
     * 注意 ： targetView一定要是containerView的子view，否则不会产生点击事件
     * @param target 这个view定义了你想要发送点击事件的位置
     * @param container 从container开始分发点击事件
     */
    public boolean dispatchLongClickEventOnTarget(View target,View container,long time) {
        return clickOnView(target,container,true,time);
    }


    public boolean clickOnText(String regex) {
        return clickOnText(regex,false,0);
    }

    public boolean longClickOnText(String regex,long time) {
        return clickOnText(regex,true,time);
    }

    /**
     * 对一个view发送点击事件
     *
     * @param view      应该被点击的view
     * @param longClick true 为长按
     * @param time      长按的时间
     */

    private boolean clickOnScreen(View view, boolean longClick, long time) {

        if (view == null) {
            return false;
        }

        float[] xyToClick = getClickCoordinates(view);
        float x = xyToClick[0];
        float y = xyToClick[1];

        if (x == 0 || y == 0) {
            pause(MINI_WAIT);
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

        pause(MINI_WAIT);
        if (longClick) {
            return longClickOnScreen(x, y, time);
        } else {
            return clickOnScreen(x, y);
        }
    }

    /**
     * 向targetView的中心位置发送点击事件，containerView为开始分发
     * 点击事件的开始
     * 注意 ： targetView一定要是containerView的子view，否则不会产生点击事件
     * @param target 分发事件所代表的位置
     * @param container 分发事件开始的view
     */
    private boolean clickOnView(final View target,final View container,final boolean longClick,final long time) {
        final float[] out = new float[2];
        if (!findTouchPosition(out,container,target)) {
            MLogger.e("Clicker.clickOnView(View,View,boolean,long) find TouchPosition failed!",null);
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
                    MotionEvent moveEvent = getSimpleMotionEvent(MotionEvent.ACTION_MOVE, out[0] + 1.0f, out[0] + 1.0f);
                    container.dispatchTouchEvent(moveEvent);
                }
            });
            if (time > 0) {
                pause(time);
            } else {
                pause((int) (ViewConfiguration.getLongPressTimeout() * 2.5f));
            }
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

    private float[] getClickCoordinates(View view) {
        int[] xyLocation = new int[2];
        float[] xyToClick = new float[2];
        int trialCount = 0;

        view.getLocationOnScreen(xyLocation);
        while (xyLocation[0] == 0 && xyLocation[1] == 0 && trialCount < 10) {
            pause(MINI_WAIT);
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
            MLogger.e(LOG_TAG,"targetView is not a child of container!",e);
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

    private boolean clickOnText(String regex, boolean longClick,long time) {
        TextView tv = mSearcher.searchTextViewByText(regex, true);
        if (tv != null) {
            return clickOnScreen(tv, longClick, time);
        }
        MLogger.e(LOG_TAG,"can not search textView from regex:" + regex + " click failed!",null);
        return false;
    }
}
