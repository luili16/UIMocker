package com.llx278.uimocker2;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.llx278.uimocker2.Scroller.VerticalDirection.DOWN_TO_UP;
import static com.llx278.uimocker2.Scroller.VerticalDirection.UP_TO_DOWN;

/**
 * 实现了滚动相关的方法
 *
 * @author llx
 */

public class Scroller {
    private static final String TAG = "uimocker";
    private static final long DEFAULT_DRAG_DURATION = 500;
    private static final long DEFAULT_PRESS_DURATION = 0;
    private static final long DEFAULT_UP_DURATION = 1000;
    private static final long DEFAULT_PAUSE_DURATION = 100;

    private boolean mCanScroll = false;
    private final ViewGetter mViewGetter;
    private final Gesture mGesture;
    private int mScreenWidth = -1;
    private int mScreenHeight = -1;


    public Scroller(Context context, ViewGetter viewGetter, Gesture gesture) {
        mViewGetter = viewGetter;
        mGesture = gesture;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (wm != null) {
            wm.getDefaultDisplay().getSize(point);
            mScreenWidth = point.x;
            mScreenHeight = point.y;
        }
    }

    private void pause() {
        try {
            Thread.sleep(DEFAULT_PAUSE_DURATION);
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * 直接模拟滑动屏幕的动作，将view里面最后（或者第一个）可见的view滑动到最上面(或者最下面)，默认滑动时间是1s
     * <p>
     * forceScroll屏蔽了不同滚动控件的差异，但缺点是无法知道是否已经滚动到了最下面或者是最上面，需要结合其他的判断
     * 逻辑来使用
     *
     * @param view      待滑动的view
     * @param direction 方向
     */
    public void forceScrollViewVertically(View view, VerticalDirection direction){
        forceScrollViewVertically(view, direction, DEFAULT_DRAG_DURATION);
    }

    /**
     * 直接模拟滑动屏幕的动作，将view里面最后（或者第一个）可见的view滑动到最上面(或者最下面)
     * <p>
     * forceScroll屏蔽了不同滚动控件的差异，但缺点是无法知道是否已经滚动到了最下面或者是最上面，需要结合其他的判断
     * 逻辑来使用
     *
     * @param view      待滑动的view
     * @param direction 方向
     * @param duration 滚动时间，这个参数对于不同height的view，应该取不同的时间，因为如果一个view的height很大（或很小）
     *                 duration很小（或很大）的情况，会导致滚动的距离误差过大。
     */
    public void forceScrollViewVertically(View view, VerticalDirection direction,long duration) {
        if (view == null || !(view instanceof ViewGroup)) {
            return;
        }

        if (!mViewGetter.isViewSufficientlyShown(view)) {
            // 如果view没有完全显示在窗体内，那么无法判断点击事件的落点
            return;
        }

        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int upXPosition = roundEdgeX(viewLocation[0] + view.getWidth()/2);
        // 加2的目的是让点精确的落在view的内部，保证点击事件能准确的被传递给view
        int upYPosition = roundEdgeY(viewLocation[1]) + 2;
        PointF upPointF = new PointF(upXPosition,upYPosition);

        int downXPosition = roundEdgeX(viewLocation[0] + view.getWidth() / 2);
        // 减2的目的是让点精确的落在view的内部，保证点击事件能准确的被传递给view
        int downYPosition = roundEdgeY(viewLocation[1] + view.getHeight()) - 2;
        PointF downPointF = new PointF(downXPosition,downYPosition);

        if (direction == VerticalDirection.UP_TO_DOWN) {
            mGesture.dragOnScreen(upPointF,downPointF,duration, DEFAULT_PRESS_DURATION, DEFAULT_UP_DURATION);
        } else if (direction == VerticalDirection.DOWN_TO_UP) {
            mGesture.dragOnScreen(downPointF,upPointF,duration, DEFAULT_PRESS_DURATION, DEFAULT_UP_DURATION);
        }
    }

    /**
     * 直接模拟滑动屏幕的动作，讲View里面最后（或者第一个）滑动到最左边(或者最右边),默认滑动时间为1s
     * <p>
     * forceScroll屏蔽了不同滚动控件的差异，但缺点是无法知道是否已经滚动到了最下面或者是最上面，需要结合其他的判断
     * 逻辑来使用
     *
     * @param view      待滑动的view
     * @param direction 方向
     */
    public void forceScrollViewHorizontally(View view, HorizontalDirection direction) {
        forceScrollViewHorizontally(view,direction, DEFAULT_DRAG_DURATION);
    }

    /**
     * 直接模拟滑动屏幕的动作，讲View里面最后（或者第一个）滑动到最左边(或者最右边)
     * <p>
     * forceScroll屏蔽了不同滚动控件滚动实现差异，但缺点是无法知道是否已经滚动到了最下面或者是最上面，需要结合其他的判断
     * 逻辑来使用
     *
     * @param view      待滑动的view
     * @param direction 方向
     * @param duration 滚动时间，这个参数对于不同weight的view，应该取不同的时间，因为如果一个view的weight很大（很小）
     *                 duration很小（很大）的情况，会导致滚动的距离误差过大。
     */
    public void forceScrollViewHorizontally(View view, HorizontalDirection direction,long duration) {
        if (view == null || !(view instanceof ViewGroup)) {
            return;
        }

        if (!mViewGetter.isViewSufficientlyShown(view)) {
            // 如果view没有完全显示在窗体内，那么无法判断点击事件的落点
            return;
        }

        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        // 加2的目的是让点精确的落在view的内部，保证点击事件能准确的被传递给view
        int leftXPosition = roundEdgeX(viewLocation[0]) + 2;
        int leftYPosition = roundEdgeY(viewLocation[1] + view.getHeight() / 2);
        PointF leftPointF = new PointF(leftXPosition,leftYPosition);

        // 减2的目的是让点精确的落在view的内部，保证点击事件能准确的被传递给view
        int rightXPosition = roundEdgeX(viewLocation[0] + view.getWidth()) - 2;
        int rightYPosition = roundEdgeY(viewLocation[1] + view.getHeight() / 2);

        PointF rightPointF = new PointF(rightXPosition,rightYPosition);

        if (direction == HorizontalDirection.LEFT_TO_RIGHT) {
            mGesture.dragOnScreen(leftPointF,rightPointF, duration, DEFAULT_PRESS_DURATION, DEFAULT_UP_DURATION);
        } else if (direction == HorizontalDirection.RIGHT_TO_LEFT) {
            mGesture.dragOnScreen(rightPointF,leftPointF, duration, DEFAULT_PRESS_DURATION, DEFAULT_UP_DURATION);
        }
    }

    /**
     * 垂直滚动一个View，注意，这种滚动的方式只适合像ScrollView这样的View不是被复用的View(区别于ListView)
     * 注意：此方法对RecyclerView有效，但是无法判断是否滚动到了最下还是最上面，即对于RecyclerView来说此方法
     * 永远返回true
     *
     * @param view      被用来滚动的view
     * @param direction 滚动方向
     * @return 返回true 证明还没有滚动到最下面或者是最上面，下次还可以滚动，false 已经不能在滚动了
     */
    public boolean scrollViewVertically(final View view, VerticalDirection direction) {
        if (view == null) {
            return false;
        }

        int height = view.getHeight();

        height--;
        int scrollTo = -1;

        if (direction == DOWN_TO_UP) {
            scrollTo = height;
        } else if (direction == UP_TO_DOWN) {
            scrollTo = -height;
        }

        int originalY = view.getScrollY();
        final int scrollAmount = scrollTo;
        Scheduler.runOnMainSync(new Runnable() {
            public void run() {
                view.scrollBy(0, scrollAmount);
            }
        });
        // 下面这行代码可以判断是否已经滚动到了最上面或者最下面
        // boolean nextCanScroll = (originalY != view.getScrollY())
        // 如果nextCanScroll为false，则这次滚动已经到了最下边或者最上边
        // 如果nextCanScroll为true，则下次还可以滚动
        // 则证明已经滚动到了最底部或者最顶部
        return (originalY != view.getScrollY());
    }

    /**
     * 垂直滚动到终止的位置,这种滚动的方式只适合像ScrollView这样的View不是被复用的View(区别于ListView)
     * 注意：此方法对RecyclerView无效，只能滚动一次
     *
     * @param view      待滚动的view
     * @param direction 方向
     */
    public void scrollViewVerticallyAllTheWay(final View view, final VerticalDirection direction) {
        while (true) {
            if (!(scrollViewVertically(view, direction))) {
                pause();
                break;
            }
        }
    }

    /**
     * 这个方法自动的查找当前view里面所有可能有滚动能力的view，
     * 并在所有的view里面找到最近被刷新的view，对这个view触发滚动事件
     * @param direction 方向
     * @return false 证明还没有滚动到最下面的view或者最上面的view， true 已经不能在滚动了
     *          注意，对于listView来说，数据集是自动填充的，因此滚动了最下面有很大的可能会刷新数据集
     *          从而重新填充adapter，这种情况下返回值就会变的不可信了
     */
    public boolean scrollVertically(VerticalDirection direction,View scrollableView) {
        View view;
        if (scrollableView == null) {
            ArrayList<View> viewList = UIUtil.removeInvisibleViews(mViewGetter.getViewList(true));
            ArrayList<View> filteredViews = UIUtil.filterViewsToSet(Arrays.asList(ListView.class, ScrollView.class,
                    GridView.class, WebView.class), viewList);
            List<View> scrollableSupportPackageViews = mViewGetter.getScrollableSupportPackageViews();
            filteredViews.addAll(scrollableSupportPackageViews);
            view = mViewGetter.getFreshestView(filteredViews);
        } else {
            view = scrollableView;
        }

        if (view == null) {
            return false;
        }

        if (view instanceof AbsListView) {
            return scrollListVertically((AbsListView) view, direction);
        }

        if (view instanceof WebView) {
            return scrollWebView((WebView) view, direction);
        }

        return scrollViewVertically(view, direction);
    }

    public boolean scrollWebView(final WebView webView, VerticalDirection direction) {
        return scrollWebView(webView,direction,false);
    }

    public boolean scrollWebView(final WebView webView, VerticalDirection direction, final boolean allTheWay) {

        if (direction == DOWN_TO_UP) {
            Scheduler.runOnMainSync(new Runnable() {
                public void run() {
                    mCanScroll = webView.pageDown(allTheWay);
                }
            });
        }
        if (direction == UP_TO_DOWN) {
            Scheduler.runOnMainSync(new Runnable() {
                public void run() {
                    mCanScroll = webView.pageUp(allTheWay);
                }
            });
        }
        return mCanScroll;
    }

    /**
     * 滚动listView
     *
     * @param absListView 被滚动的abslistView
     * @param direction   方向
     * @param <T>         extends AbsListVIew
     * @return false 证明还没有滚动到此Adapter数据集里的最后一条 true 已经滚动到最后了
     */
    public <T extends AbsListView> boolean scrollListVertically(T absListView, VerticalDirection direction){
        return scrollListVertically(absListView,direction,false);
    }

    /**
     * 滚动listView
     *
     * @param absListView 被滚动的abslistView
     * @param direction   方向
     * @param allTheWay   一直滚动到adapter数据集的最后一条或者adapter数据集的第一条
     * @param <T>         extends AbsListVIew
     * @return false 证明已经滚动到此Adapter数据集里的最后一条 true 下次还可以滚动
     */
    public <T extends AbsListView> boolean scrollListVertically(T absListView, VerticalDirection direction, boolean allTheWay) {
        if (absListView == null) {
            return false;
        }

        if (direction == DOWN_TO_UP) {
            int listCount = absListView.getCount();
            int lastVisiblePosition = absListView.getLastVisiblePosition();
            if (allTheWay) {
                scrollListVerticallyToLine(absListView, listCount - 1);
                return false;
            }

            if (lastVisiblePosition >= listCount - 1) {
                if (lastVisiblePosition > 0) {
                    scrollListVerticallyToLine(absListView, lastVisiblePosition);
                }
                return false;
            }

            int firstVisiblePosition = absListView.getFirstVisiblePosition();


            if (firstVisiblePosition != lastVisiblePosition) {
                scrollListVerticallyToLine(absListView, lastVisiblePosition);
            } else {
                scrollListVerticallyToLine(absListView, firstVisiblePosition + 1);
            }

        } else if (direction == UP_TO_DOWN) {
            int firstVisiblePosition = absListView.getFirstVisiblePosition();

            if (allTheWay || firstVisiblePosition < 2) {
                scrollListVerticallyToLine(absListView, 0);
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
            scrollListVerticallyToLine(absListView, lineToScrollTo);
        }
        pause();
        return true;
    }

    /**
     * 滚动到指定行（索引从0开始）
     *
     * @param view 被滚动的absListView
     * @param line 行
     * @param <T>  extends absListView
     */
    public <T extends AbsListView> void scrollListVerticallyToLine(@NonNull final T view, final int line) {

        final int lineToMoveTo;
        if (view instanceof GridView) {
            lineToMoveTo = line + 1;
        } else {
            lineToMoveTo = line;
        }

        Scheduler.runOnMainSync(new Runnable() {
            public void run() {
                view.setSelection(lineToMoveTo);
            }
        });
    }

    /**
     * 水平滚动,注意，这种滚动的方式只适合像HorizontalScrollView这样的View不是被复用的View
     * 注意：此方法对RecyclerView有效，但是无法判断是否滚动到了最左面还是最右面，即对于RecyclerView来说此方法
     * 永远返回true
     * @param view      待水平滚动的view
     * @param direction 方向
     * @return 返回true 证明还没有滚动到最左面或者最右面，下次还可以滚动，false 已经不能在滚动了
     */
    public boolean scrollViewHorizontally(final View view, HorizontalDirection direction) {
        if (view == null) {
            return false;
        }
        int width = view.getWidth();
        width--;
        int scrollTo = -1;

        if (HorizontalDirection.LEFT_TO_RIGHT == direction) {
            scrollTo = -width;
        } else if (HorizontalDirection.RIGHT_TO_LEFT == direction) {
            scrollTo = width;
        }

        int originalX = view.getScrollX();
        final int scrollAmount = scrollTo;
        Scheduler.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.scrollBy(scrollAmount, 0);
            }
        });
        return originalX != view.getScrollX();
    }

    /**
     * 水平滚动的终止的位置，这种滚动的方式只适合像HorizontalScrollView这样的View不是被复用的View
     * 注意：此方法对RecyclerView无效，只能滚动一次
     */
    public void scrollViewHorizontallyAllTheWay(View view, HorizontalDirection direction) {
        while (true) {
            if (!(scrollViewHorizontally(view, direction))) {
                pause();
                break;
            }
        }
    }

    public enum HorizontalDirection {
        /**
         * LEFT指手指滑动方向为从左向右
         */
        LEFT_TO_RIGHT,
        /**
         * RIGHT指手指滑动方向为从右向左
         */
        RIGHT_TO_LEFT
    }

    public enum VerticalDirection {
        /**
         * UP指手指滑动的方向从上向下
         */
        UP_TO_DOWN,
        /**
         * DOWN指手指滑动的方向从下向上
         */
        DOWN_TO_UP
    }

    /**
     * 对滑动的x点落在屏幕外面做处理
     *
     * @param xPosition x位置
     * @return 处理后的点
     */
    private int roundEdgeX(int xPosition) {
        if (mScreenWidth != -1 && mScreenHeight != -1) {

            if (xPosition <= 0) {
                return 0;
            } else if (xPosition >= mScreenWidth) {
                return mScreenWidth;
            } else {
                return xPosition;
            }

        } else {
            throw new RuntimeException("found illegal screen width and height : " +
                    "(width,height)=(" + mScreenWidth + "," + mScreenHeight + ")");
        }
    }

    /**
     * 对滑动的点y落在屏幕外面做处理
     *
     * @param yPosition y位置
     * @return 处理后的点
     */
    private int roundEdgeY(int yPosition) {

        if (mScreenWidth != -1 && mScreenHeight != -1) {

            if (yPosition <= 0) {
                return 0;
            } else if (yPosition >= mScreenHeight) {
                return mScreenHeight;
            } else {
                return yPosition;
            }

        } else {
            throw new RuntimeException("found illegal screen width and height : " +
                    "(width,height)=(" + mScreenWidth + "," + mScreenHeight + ")");
        }
    }

}
