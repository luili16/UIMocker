package com.llx278.uimocker2;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现了滚动相关的方法
 *
 * @author llx
 */

public class Scroller {
    private static final String TAG = "uimocker";
    static final int DOWN = 0;
    static final int UP = 1;

    enum Side {LEFT, RIGHT}

    ;
    private boolean mCanScroll = false;
    private final InstrumentationDecorator mInst;
    private final ViewGetter mViewGetter;
    private final Sleeper mSleeper;

    public Scroller(InstrumentationDecorator inst, ViewGetter viewGetter, Sleeper sleeper) {
        mInst = inst;
        mViewGetter = viewGetter;
        mSleeper = sleeper;
    }

    /**
     * 拖动 从x坐标开始到y坐标结束
     *
     * @param fromX     在屏幕上的x开始坐标
     * @param toX       在屏幕上的x结束坐标
     * @param fromY     在屏幕上的y的开始坐标
     * @param toY       在屏幕上的y的结束坐标
     * @param stepCount 移动的次数
     */
    public void drag(float fromX, float toX, float fromY, float toY, int stepCount) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float y = fromY;
        float x = fromX;
        float yStep = (toY - fromY) / stepCount;
        float xStep = (toX - fromX) / stepCount;
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN,
                fromX, fromY, 0);
        try {
            mInst.sendPointerSync(event);
        } catch (SecurityException ignored) {
        }
        for (int i = 0; i < stepCount; ++i) {
            y += yStep;
            x += xStep;
            eventTime = SystemClock.uptimeMillis();
            event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y, 0);
            try {
                mInst.sendPointerSync(event);
            } catch (SecurityException ignored) {
            }
        }
        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, toX, toY, 0);
        try {
            mInst.sendPointerSync(event);
        } catch (SecurityException ignored) {
        }
    }

    /**
     * 滚动一个scrollView
     *
     * @param view      被用来滚动的view
     * @param direction 滚动方向
     * @return
     */
    public boolean scrollView(final View view, int direction) {
        if (view == null) {
            return false;
        }

        int height = view.getHeight();
        height--;
        int scrollTo = -1;

        if (direction == DOWN) {
            scrollTo = height;
        } else if (direction == UP) {
            scrollTo = -height;
        }

        int originalY = view.getScrollY();
        final int scrollAmount = scrollTo;
        mInst.runOnMainSync(new Runnable() {
            public void run() {
                view.scrollBy(0, scrollAmount);
            }
        });

        return originalY != view.getScrollY();
    }

    public void scrollViewAllTheWay(final View view, final int direction) {
        while (true) {
            if (!(scrollView(view, direction))) {
                break;
            }
        }
    }

    /**
     * 滚动
     *
     * @param direction 方向
     * @param allTheWay 一直滚动，直到顶部或者底部才结束
     * @return
     */
    public boolean scroll(int direction, boolean allTheWay) {
        ArrayList<View> viewList = UIUtil.removeInvisibleViews(mViewGetter.getViews(true));
        //noinspection unchecked
        ArrayList<View> filteredViews = UIUtil.filterViewsToSet(new Class[]{ListView.class, ScrollView.class, GridView.class, WebView.class},
                viewList);
        List<View> scrollableSupportPackageViews = mViewGetter.getScrollableSupportPackageViews(true);
        filteredViews.addAll(scrollableSupportPackageViews);
        View view = mViewGetter.getFreshestView(filteredViews);
        if (view == null){
            return false;
        }

        if(view instanceof AbsListView){
            return scrollList((AbsListView) view,direction,allTheWay);
        }

        if(view instanceof WebView){
            return scrollWebView((WebView) view,direction,allTheWay);
        }

        if (allTheWay){
            scrollViewAllTheWay(view,direction);
            return false;
        } else {
            return scrollView(view,direction);
        }
    }

    public boolean scroll(int direction) {
        return scroll(direction, false);
    }

    public boolean scrollDown() {
        return scroll(Scroller.DOWN);
    }

    public boolean scrollWebView(final WebView webView, int direction, final boolean allTheWay) {

        if (direction == DOWN) {
            mInst.runOnMainSync(new Runnable() {
                public void run() {
                    mCanScroll = webView.pageDown(allTheWay);
                }
            });
        }
        if (direction == UP) {
            mInst.runOnMainSync(new Runnable() {
                public void run() {
                    mCanScroll = webView.pageUp(allTheWay);
                }
            });
        }
        return mCanScroll;
    }

    /**
     * @param absListView 被滚动的abslistView
     * @param direction   方向
     * @param allTheWay   一直滚动到最下或者滚动到最上
     * @param <T>         extends AbsListVIew
     * @return true 如果已经不能够滚动了
     */
    public <T extends AbsListView> boolean scrollList(T absListView, int direction, boolean allTheWay) {
        if (absListView == null) {
            return false;
        }

        if (direction == DOWN) {
            int listCount = absListView.getCount();
            int lastVisiblePosition = absListView.getLastVisiblePosition();
            if (allTheWay) {
                scrollListToLine(absListView, listCount - 1);
                return false;
            }

            if (lastVisiblePosition >= listCount - 1) {
                if (lastVisiblePosition > 0) {
                    scrollListToLine(absListView, lastVisiblePosition);
                }
                return false;
            }

            int firstVisiblePosition = absListView.getFirstVisiblePosition();


            if (firstVisiblePosition != lastVisiblePosition) {
                scrollListToLine(absListView, lastVisiblePosition);
            } else {
                scrollListToLine(absListView, firstVisiblePosition + 1);
            }

        } else if (direction == UP) {
            int firstVisiblePosition = absListView.getFirstVisiblePosition();

            if (allTheWay || firstVisiblePosition < 2) {
                scrollListToLine(absListView, 0);
                return false;
            }
            int lastVisiblePosition = absListView.getLastVisiblePosition();

            final int lines = lastVisiblePosition - firstVisiblePosition;

            int lineToScrollTo = firstVisiblePosition - lines;

            if (lineToScrollTo == lastVisiblePosition) {
                lineToScrollTo--;
            }
            if (lineToScrollTo < 0) {
                lineToScrollTo = 0;
            }
            scrollListToLine(absListView, lineToScrollTo);
        }
        mSleeper.sleep();
        return true;
    }

    /**
     * 滚动到指定行
     *
     * @param view 被滚动的absListView
     * @param line 行
     * @param <T>  extends absListView
     */
    public <T extends AbsListView> void scrollListToLine(@NonNull final T view, final int line) {

        final int lineToMoveTo;
        if (view instanceof GridView) {
            lineToMoveTo = line + 1;
        } else {
            lineToMoveTo = line;
        }

        mInst.runOnMainSync(new Runnable() {
            public void run() {
                view.setSelection(lineToMoveTo);
            }
        });
    }

    /**
     * Scrolls horizontally.
     *
     * @param side           the side to which to scroll; {@link com.llx278.uimocker.Scroller.Side#RIGHT} or {@link com.llx278.uimocker.Scroller.Side#LEFT}
     * @param scrollPosition the position to scroll to, from 0 to 1 where 1 is all the way. Example is: 0.55.
     * @param stepCount      match many move steps to include in the scroll. Less steps results in a faster scroll
     */

    @SuppressWarnings("deprecation")
    public void scrollToSide(Side side, float scrollPosition, int stepCount) {
        WindowManager windowManager = (WindowManager)
                mInst.getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return;
        }

        int screenHeight = windowManager.getDefaultDisplay()
                .getHeight();
        int screenWidth = windowManager.getDefaultDisplay()
                .getWidth();
        float x = screenWidth * scrollPosition;
        float y = screenHeight / 2.0f;
        if (side == Side.LEFT) {
            drag(70, x, y, y, stepCount);
        } else if (side == Side.RIGHT) {
            drag(x, 0, y, y, stepCount);
        }
    }

    /**
     * Scrolls view horizontally.
     *
     * @param view           the view to scroll
     * @param side           the side to which to scroll; {@link com.llx278.uimocker.Scroller.Side#RIGHT} or {@link com.llx278.uimocker.Scroller.Side#LEFT}
     * @param scrollPosition the position to scroll to, from 0 to 1 where 1 is all the way. Example is: 0.55.
     * @param stepCount      match many move steps to include in the scroll. Less steps results in a faster scroll
     */

    public void scrollViewToSide(View view, Side side, float scrollPosition, int stepCount) {
        int[] corners = new int[2];
        view.getLocationOnScreen(corners);
        int viewHeight = view.getHeight();
        int viewWidth = view.getWidth();
        float x = corners[0] + viewWidth * scrollPosition;
        float y = corners[1] + viewHeight / 2.0f;
        if (side == Side.LEFT)
            drag(corners[0], x, y, y, stepCount);
        else if (side == Side.RIGHT)
            drag(x, corners[0], y, y, stepCount);
    }

}
