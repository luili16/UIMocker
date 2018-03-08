package com.llx278.uimocker2;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

import static com.llx278.uimocker2.Gesture.Config.EVENT_TIME_INTERVAL_MS;
import static com.llx278.uimocker2.Gesture.Config.GESTURE_DURATION_MS;

/**
 * 封装了与手势相关的api
 */
public class Gesture {

    private static final String TAG = "Gesture";

    private ActivityUtils mActivityUtils;
    private final MyInstrumentation mInstrumentation;
    private final Sleeper mSleeper;

    public Gesture(MyInstrumentation inst, ActivityUtils activityUtils, Sleeper sleeper) {
        mInstrumentation = inst;
        mActivityUtils = activityUtils;
        mSleeper = sleeper;
    }

    /**
     * 生成一个默认的缩放手势
     */
    public void zoomIn() {
    }

    /**
     * 生成一个默认的放大手势
     */
    public void zoomOut() {
    }

    /**
     * 生成一个扫动屏幕左边的的手势（例如打开侧滑菜单）
     */
    public void swipeOnScreenLeftEdge() {
        Point outSize = getSize();
        if(outSize ==null){
            return;
        }
        // 做偏移30px
        float startX = 30;
        float startY = outSize.y / 2;
        float endX = outSize.x / 1.7f;
        float endY = outSize.y / 2;
        PointF startPoint = new PointF(startX,startY);
        PointF endPoint = new PointF(endX,endY);
        swipeOnScreen(startPoint,endPoint,Config.DEFAULT_SWIPE_DURATION);
    }

    /**
     * 生成一个滑动屏幕上边的手势（例如打开下拉功能菜单）
     */
    public void swipeOnScreenTopEdge() {

    }

    /**
     * 生成一个从屏幕下边滑动到上边的手势
     */
    public void swipeOnscreenBottomEdge() {

    }

    /**
     * 生成一个从屏幕中间开始滑动的手势（例如从一个tab页切换到另一个tab页）
     */
    public void swipeFromLeftToRight() {
        Point outSize = getSize();
        if (outSize == null) {
            return;
        }

        float startX = outSize.x / 8;
        float startY = outSize.y / 2;
        float endX = (outSize.x / 8) * 7;
        float endY = outSize.y / 2;
        PointF startPoint = new PointF(startX, startY);
        PointF endPoint = new PointF(endX, endY);
        swipeOnScreen(startPoint, endPoint,Config.DEFAULT_SWIPE_DURATION);
    }

    public void swipeFromRightToLeft() {
        Point outSize = getSize();
        if (outSize == null) {
            return;
        }

        // 把x平分为8段
        float startX = (outSize.x / 8) * 7;
        float startY = outSize.y / 2;
        float endX = outSize.x / 8;
        float endY = outSize.y / 2;

        PointF startPoint = new PointF(startX, startY);
        PointF endPoint = new PointF(endX, endY);
        swipeOnScreen(startPoint, endPoint,Config.DEFAULT_SWIPE_DURATION);
    }

    private Point getSize() {
        Activity currentActivity = mActivityUtils.getCurrentActivity();
        if (currentActivity == null) {
            Logger.d(TAG, "swipeOnScreenEdge currentActivity is null!!");
            return null;
        }
        Point outSize = new Point();
        currentActivity.getWindowManager().getDefaultDisplay().getSize(outSize);
        return outSize;
    }

    public MotionEvent getMotionEvent(long when, int action, float x, float y, float pressure, int inputSource) {
        MotionEvent event = MotionEvent.obtain(when, when, action, x, y, pressure, Config.DEFAULT_SIZE,
                Config.DEFAULT_META_STATE, Config.DEFAULT_PRECISION_X, Config.DEFAULT_PRECISION_Y,
                getInputDeviceId(inputSource), Config.DEFAULT_EDGE_FLAGS);
        event.setSource(inputSource);
        return event;
    }

    /**
     * 生成一个滑动的手势
     *
     * @param startPoint 起始点
     * @param endPoint   结束点
     * @param duration 持续时间
     */
    public void swipeOnScreen(PointF startPoint, PointF endPoint,long duration) {

        float startX = startPoint.x;
        float startY = startPoint.y;
        float endX = endPoint.x;
        float endY = endPoint.y;

        long now = SystemClock.uptimeMillis();
        MotionEvent downEvent = getMotionEvent(now, MotionEvent.ACTION_DOWN,
                startX, startY, 1, Config.DEFAULT_INPUT_SOURCE);
        mInstrumentation.sendPointerSync(downEvent);
        downEvent.recycle();
        long startTime = now;
        long endTime = startTime + duration;
        while (now < endTime) {
            long elapsedTime = now - startTime;
            float alpha = (float) elapsedTime / duration;
            float moveX = lerp(startX, endX, alpha);
            float moveY = lerp(startY, endY, alpha);
            MotionEvent moveEvent = getMotionEvent(now, MotionEvent.ACTION_MOVE, moveX, moveY, 1,
                    Config.DEFAULT_INPUT_SOURCE);
            mInstrumentation.sendPointerSync(moveEvent);
            moveEvent.recycle();
            now = SystemClock.uptimeMillis();
        }
        MotionEvent upEvent = getMotionEvent(now, MotionEvent.ACTION_UP, endX, endY, 1,
                Config.DEFAULT_INPUT_SOURCE);
        mInstrumentation.sendPointerSync(upEvent);
        upEvent.recycle();
    }

    private int getInputDeviceId(int inputSource) {
        final int DEFAULT_DEVICE_ID = 0;
        int[] devIds = InputDevice.getDeviceIds();
        for (int devId : devIds) {
            InputDevice inputDev = InputDevice.getDevice(devId);
            int sources = inputDev.getSources();
            // (mSources & source) == source;
            if ((sources & inputSource) == inputSource) {
                return devId;
            }
        }
        return DEFAULT_DEVICE_ID;
    }

    private static float lerp(float a, float b, float alpha) {
        return (b - a) * alpha + a;
    }


    /**
     * 生成一个双指缩放的手势
     *
     * @param startPoint1 第一个手指开始的位置
     * @param startPoint2 第二个手指开始的位置
     * @param endPoint1   第一个手指结束的位置
     * @param endPoint2   第二个手指结束的位置
     */
    public void Zoom(PointF startPoint1, PointF startPoint2,
                     PointF endPoint1, PointF endPoint2) {

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();

        float startX1 = startPoint1.x;
        float startY1 = startPoint1.y;
        float startX2 = startPoint2.x;
        float startY2 = startPoint2.y;

        float endX1 = endPoint1.x;
        float endY1 = endPoint1.y;
        float endX2 = endPoint2.x;
        float endY2 = endPoint2.y;

        PointerCoords[] pointerCoords = new PointerCoords[2];
        PointerCoords pc1 = new PointerCoords();
        PointerCoords pc2 = new PointerCoords();
        pc1.x = startX1;
        pc1.y = startY1;
        pc1.pressure = 1;
        pc1.size = 1;
        pc2.x = startX2;
        pc2.y = startY2;
        pc2.pressure = 1;
        pc2.size = 1;
        pointerCoords[0] = pc1;
        pointerCoords[1] = pc2;

        PointerProperties[] pointerProperties = new PointerProperties[2];
        PointerProperties pp1 = new PointerProperties();
        PointerProperties pp2 = new PointerProperties();
        pp1.id = 0;
        pp1.toolType = MotionEvent.TOOL_TYPE_FINGER;
        pp2.id = 1;
        pp2.toolType = MotionEvent.TOOL_TYPE_FINGER;
        pointerProperties[0] = pp1;
        pointerProperties[1] = pp2;

        MotionEvent event;
        // send the initial touches
        event = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, 1, pointerProperties, pointerCoords,
                0, 0, // metaState, buttonState
                1, // x precision
                1, // y precision
                0, 0, 0, 0); // deviceId, edgeFlags, source, flags
        mInstrumentation.sendPointerSync(event);

        event = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_POINTER_DOWN
                        + (pp2.id << MotionEvent.ACTION_POINTER_INDEX_SHIFT),
                2, pointerProperties, pointerCoords, 0, 0,
                1, 1, 0, 0, 0, 0);
        mInstrumentation.sendPointerSync(event);

        int numMoves = GESTURE_DURATION_MS / EVENT_TIME_INTERVAL_MS;

        float stepX1 = (endX1 - startX1) / numMoves;
        float stepY1 = (endY1 - startY1) / numMoves;
        float stepX2 = (endX2 - startX2) / numMoves;
        float stepY2 = (endY2 - startY2) / numMoves;

        // send the zoom
        for (int i = 0; i < numMoves; i++) {
            eventTime += EVENT_TIME_INTERVAL_MS;
            pointerCoords[0].x += stepX1;
            pointerCoords[0].y += stepY1;
            pointerCoords[1].x += stepX2;
            pointerCoords[1].y += stepY2;

            event = MotionEvent.obtain(downTime, eventTime,
                    MotionEvent.ACTION_MOVE, 2, pointerProperties,
                    pointerCoords, 0, 0, 1, 1,
                    0, 0, 0, 0);
            mInstrumentation.sendPointerSync(event);
        }
    }

    public static class Config {
        public static int GESTURE_DURATION_MS = 1000;
        public static int EVENT_TIME_INTERVAL_MS = 10;

        public static int DEFAULT_SWIPE_DURATION = 300;
        public static float DEFAULT_SIZE = 1.0f;
        public static int DEFAULT_META_STATE = 0;
        public static float DEFAULT_PRECISION_X = 1.0f;
        public static float DEFAULT_PRECISION_Y = 1.0f;
        public static int DEFAULT_EDGE_FLAGS = 0;
        public static int DEFAULT_INPUT_SOURCE = InputDevice.SOURCE_TOUCHSCREEN;
    }
}
