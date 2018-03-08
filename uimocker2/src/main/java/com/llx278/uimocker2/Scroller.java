package com.llx278.uimocker2;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import static com.llx278.uimocker2.Scroller.VerticalDirection.DOWN;
import static com.llx278.uimocker2.Scroller.VerticalDirection.UP;

/**
 * 实现了滚动相关的方法
 *
 * @author llx
 */

public class Scroller {
    private static final String TAG = "uimocker";

    private boolean mCanScroll = false;
    private final InstrumentationDecorator mInst;
    private final ViewGetter mViewGetter;
    private final Sleeper mSleeper;
    private final Gesture mGesture;

    public Scroller(InstrumentationDecorator inst, ViewGetter viewGetter, Sleeper sleeper,Gesture gesture) {
        mInst = inst;
        mViewGetter = viewGetter;
        mSleeper = sleeper;
        mGesture = gesture;
    }

    /**
     * 直接模拟滑动屏幕的动作，将view里面最后（或者第一个）可见的view滑动到最上面(或者最下面)
     * @param view 待滑动的view
     * @param direction 方向
     */
    public void forceScrollViewVertically(View view,VerticalDirection direction) {
        List<View> viewList = mViewGetter.getViewList(view, true);
        // 计算所有view所有子view的位置

    }


    /**
     * 垂直滚动一个View，注意，这种滚动的方式只适合像ScrollView这样的View不是被复用的View
     *
     * @param view      被用来滚动的view
     * @param direction 滚动方向
     * @return true 此次滚动完成，并且下次可以再滚动，false 此次滚动完成，并且下次不能再滚动
     */
    public boolean scrollViewVertically(final View view, VerticalDirection direction) {
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

    /**
     * 垂直滚动到终止的位置,注意，这种滚动的方式只适合像ScrollView这样的View不是被复用的View
     * @param view 待滚动的view
     * @param direction 方向
     */
    public void scrollViewVerticallyAllTheWay(final View view, final VerticalDirection direction) {
        while (true) {
            if (!(scrollViewVertically(view, direction))) {
                break;
            }
        }
    }

    /**
     * 滚动
     *
     * @param direction 方向
     * @param allTheWay 一直滚动，直到顶部或者底部才结束
     * @return true 下次可以滚动 false 不能滚动或者已经滚动到底部了
     */
    public boolean scrollVertically(VerticalDirection direction, boolean allTheWay) {
        ArrayList<View> viewList = UIUtil.removeInvisibleViews(mViewGetter.getViewList(true));
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
            return scrollListVertically((AbsListView) view,direction,allTheWay);
        }

        if(view instanceof WebView){
            return scrollWebView((WebView) view,direction,allTheWay);
        }

        if (allTheWay){
            scrollViewVerticallyAllTheWay(view,direction);
            return false;
        } else {
            return scrollViewVertically(view,direction);
        }
    }

    public boolean scrollVertically(VerticalDirection direction) {
        return scrollVertically(direction, false);
    }

    public boolean scrollDown() {
        return scrollVertically(DOWN);
    }

    public boolean scrollWebView(final WebView webView, VerticalDirection direction, final boolean allTheWay) {

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
     * 滚动listView
     * @param absListView 被滚动的abslistView
     * @param direction   方向
     * @param allTheWay   一直滚动到最下或者滚动到最上
     * @param <T>         extends AbsListVIew
     * @return true 如果已经不能够滚动了
     */
    public <T extends AbsListView> boolean scrollListVertically(T absListView, VerticalDirection direction, boolean allTheWay) {
        if (absListView == null) {
            return false;
        }

        if (direction == DOWN) {
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

        } else if (direction == UP) {
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
        mSleeper.sleep();
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

        mInst.runOnMainSync(new Runnable() {
            public void run() {
                view.setSelection(lineToMoveTo);
            }
        });
    }

    /**
     * 水平滚动,注意，这种滚动的方式只适合像HorizontalScrollView这样的View不是被复用的View
     * @param view 待水平滚动的view
     * @param direction 方向
     */
    public boolean scrollViewHorizontally(final View view, HorizontalDirection direction) {
        if (view == null) {
            return false;
        }
        int width = view.getWidth();
        width--;
        int scrollTo = -1;

        if (HorizontalDirection.LEFT == direction) {
            scrollTo = -width;
        } else if (HorizontalDirection.RIGHT == direction) {
            scrollTo = width;
        }

        int originalX = view.getScrollX();
        final int scrollAmount = scrollTo;
        mInst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.scrollBy(scrollAmount,0);
            }
        });
        return originalX != view.getScrollX();
    }

    /**
     * 水平滚动的终止的位置，注意，这种滚动的方式只适合像HorizontalScrollView这样的View不是被复用的View
     */
    public void scrollViewHorizontallyAllTheWay(View view,HorizontalDirection direction) {
        while (true) {
            if (!(scrollViewHorizontally(view, direction))) {
                break;
            }
        }
    }

    public enum HorizontalDirection {
        /**
         * LEFT指手指滑动方向为从左向右
         */
        LEFT,
        /**
         * RIGHT指手指滑动方向为从右向左
         */
        RIGHT
    }

    public enum VerticalDirection {
        /**
         * UP指手指滑动的方向从上向下
         */
        UP,
        /**
         * DOWN指手指滑动的方向从下向上
         */
        DOWN
    }

}
