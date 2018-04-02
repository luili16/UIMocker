package com.llx278.uimocker2;

import android.app.Activity;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 等待某些特定事件的发生（例如：文本产生和消失，Activity的创建和销毁）
 * Created by llx on 03/01/2018.
 */

public class Waiter {

    private static final long DEFAULT_WAIT_TIMEOUT = 1000 * 20;
    public static final long DEFAULT_PAUSE_TIMEOUT = 500;
    /**
     * 等待循环中每一次循环的暂停时间默认是{@link Waiter#DEFAULT_PAUSE_TIMEOUT},
     * 可以修改此值来改变暂停时间
     */
    public static long sPauseTimeOut = DEFAULT_PAUSE_TIMEOUT;

    private static final String TAG = "Waiter";
    private final ActivityUtils mActivityUtils;
    private final ViewGetter mViewGetter;
    private final Searcher mSearcher;
    private final Scroller mScroller;
    private final WebUtils mWebUtils;

    public Waiter(ActivityUtils activityUtils,
                  ViewGetter viewGetter, Searcher searcher,Scroller scroller,WebUtils webUtils) {

        mActivityUtils = activityUtils;
        mViewGetter = viewGetter;
        mSearcher = searcher;
        mScroller = scroller;
        mWebUtils = webUtils;
    }

    private void pause() {
        try {
            Thread.sleep(sPauseTimeOut);
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * 等待指定的Activity出现
     *
     * @param activityName 全修饰的activity类名
     * @return true 指定的activity已经出现，false 超时返回
     */
    public boolean waitForActivity(String activityName) {
        return waitForActivity(activityName, DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 等待指定的Activity出现
     *
     * @param activityName 全修饰的activity类名
     * @param timeout      等待的时间
     * @return true 指定的activity已经出现，false 超时返回
     */
    public boolean waitForActivity(String activityName, long timeout) {
        if (isActivityMatching(mActivityUtils.getCurrentActivity(), activityName)) {
            return true;
        }
        final long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            Activity currentActivity = mActivityUtils.getCurrentActivity();
            if (isActivityMatching(currentActivity,activityName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 等待指定的activty出现
     *
     * @param activityClass 全修饰的activity类名
     * @return true 指定的activity出现 false 超时返回
     */
    public boolean waitForActivity(Class<? extends Activity> activityClass) {
        return waitForActivity(activityClass, DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 等待指定的activity出现
     *
     * @param activityClass 全修饰的activity类名
     * @param timeout       等待的时间
     * @return true 指定的activity出现 false 超时返回
     */
    public boolean waitForActivity(Class<? extends Activity> activityClass, long timeout) {
        if (isActivityMatching(activityClass, mActivityUtils.getCurrentActivity())) {
            return true;
        }

        long currentTime = SystemClock.uptimeMillis();
        final long endTime = currentTime + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            Activity currentActivity = mActivityUtils.getCurrentActivity();
            if(isActivityMatching(activityClass,currentActivity)) {
                return true;
            }
        }
        return false;
    }

    public boolean waitForActivityOnCreate(String activityName,long timeout,int deep) {
        return mActivityUtils.waitForOnCreate(activityName,timeout,deep);
    }

    public boolean waitForActivityOnResume(String activityName,long timeout,int deep) {
        return mActivityUtils.waitForOnResume(activityName,timeout,deep);
    }

    public boolean waitForActivityOnPause(String activityName,long timeout,int deep) {
        return mActivityUtils.waitForOnPause(activityName,timeout,deep);
    }

    /**
     * 等待window的decorView出现
     *
     * @return
     */
    public boolean waitForWindowDecorViews() {
        return waitForWindowDecorViews(DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 等待window的decorView出现
     *
     * @param timeout 超时时间
     * @return true decorView已经出现，false 超时
     */
    public boolean waitForWindowDecorViews(long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            pause();
            List<View> views = mViewGetter.getWindowViews();
            if (views != null && !views.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean waitForTextAppear(String regex) {
        return waitForTextAppear(regex,DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 等待某个文本出现，此方法会强制遍历所有的view(不限于TextView)
     * @param regex 待匹配的文本
     * @param timeout 超时时间
     * @return true 等待的文本出现
     */
    public boolean waitForTextAppear(String regex,long timeout) {
        return waitForTextAppearAndGet(regex,timeout) != null;
    }

    public View waitForTextAppearAndGet(String regex) {
        return waitForTextAppearAndGet(regex,DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 等待某个文本出现，此方法会强制遍历所有的view(不限于TextView)
     * @param regex 待匹配的文本
     * @param timeout 超时时间
     * @return  包含等待的文本的view
     */
    public View waitForTextAppearAndGet(String regex,long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            View view = mSearcher.forceSearchViewByText(regex,null,true);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    public boolean waitForTextAppearWithVerticallyScroll(String regex,long timeout,View scrollableView) {
        return waitForTextAppearWithVerticallyScrollAndGet(regex,timeout,scrollableView) != null;
    }

    public View waitForTextAppearWithVerticallyScrollAndGet(String regex,View scrollableView) {
        return waitForTextAppearWithVerticallyScrollAndGet(regex,DEFAULT_WAIT_TIMEOUT,scrollableView);
    }

    /**
     * 等待某个文本出现，此方法会强制遍历所有的view(不限于TextView)，并在合适的时候自动滚动
     * @param regex 待匹配的文本
     * @param timeout 超时时间
     * @return true 等待的文本出现
     */
    public View waitForTextAppearWithVerticallyScrollAndGet(String regex,long timeout,View scrollableView) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        Scroller.VerticalDirection currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            View view = mSearcher.forceSearchViewByText(regex,null,true);
            if (view != null) {
                return view;
            }

            // 先向下滚动，一直滚动到最下面，如果还没有到超时时间的话那就再向上滚动，如果滚动到
            // 最上面还没有到超时时间那就再向下滚动，如此反复
            if (currentDirection == Scroller.VerticalDirection.DOWN_TO_UP) {
                if (!mScroller.scrollVertically(currentDirection,scrollableView)){
                    //证明滚动到了最下变，改变currentDirection为上
                    currentDirection = Scroller.VerticalDirection.UP_TO_DOWN;
                }
            } else if (currentDirection == Scroller.VerticalDirection.UP_TO_DOWN) {
                if (!mScroller.scrollVertically(currentDirection,scrollableView)) {
                    // 证明滚动到了最上边，改变currentDirection为下
                    currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
                }
            }
        }
        return null;
    }

    /**
     * 等待指定的文本出现 （至少有一个符合regex的文本出现）
     * 注意，这会导致当前的页面发生滚动，并且如果滚动的控件是listview或者recyclerview这种可回收view的控件的话,
     * 你获得到的这个view的list实际上是没有意义的，只有当滚动的控件是scrollView这种不回收View的控件才好用。
     * @param regex 符合正则表达式的文本
     * @return true 符合条件的文本已经出现 false 超时
     */
    public boolean waitForTextViewAppear(String regex) {
        return waitForTextViewAppear(regex, DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 等待指定的文本出现至 (少有一个符合regex的文本出现）
     *  注意，这会导致当前的页面发生滚动，并且如果滚动的控件是listview或者recyclerview这种可回收view的控件的话,
     * 你获得到的这个view的list实际上是没有意义的，只有当滚动的控件是scrollView这种不回收View的控件才好用。
     * @param regex 符合正则表达式的文本
     * @return true 符合条件的文本已经出现 false 超时
     */
    public boolean waitForTextViewAppear(String regex, long timeout) {
        return waitForTextViewAppearAndGet(regex,timeout) != null;
    }

    /**
     * 等待指定的文本出现，并获得包含该文本的textview
     * @param regex 符合正则表达式的文本
     * @param timeout 超时时间
     * @return true 找到了符合正则表达式的文本 false 超时
     */
    public TextView waitForTextViewAppearAndGet(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            pause();
            TextView textView = mSearcher.searchTextViewByText(regex, true);
            if (textView != null) {
                return textView;
            }
        }
        return null;
    }

    public boolean waitForTextViewAppearWithVerticallyScroll(String regex,long timeout,View scrollableView) {
        return waitForTextViewAppearWithVerticallyScrollAndGet(regex,timeout,scrollableView) != null;
    }

    /**
     * 等待指定的文本出现，并获得包含该文本的TextView并且在需要的时候进行自动滚动
     * @param regex 待匹配的文本
     * @param timeout 超时时间
     * @param scrollableView 可以滚动的内容view，如果为null，则自动匹配当前最近被绘制的view
     * @return 包含有等待文本的TextView
     */
    public TextView waitForTextViewAppearWithVerticallyScrollAndGet(String regex,
                                                                long timeout,
                                                                View scrollableView) {
        // 这里面需要对mSearcher做更加精细的控制
        long endTime = SystemClock.uptimeMillis() + timeout;
        Scroller.VerticalDirection currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            TextView textView = mSearcher.searchTextViewByText(regex, true);
            if (textView != null) {
                return textView;
            }

            // 先向下滚动，一直滚动到最下面，如果还没有到超时时间的话那就再向上滚动，如果滚动到
            // 最上面还没有到超时时间那就再向下滚动，如此反复
            if (currentDirection == Scroller.VerticalDirection.DOWN_TO_UP) {
                if (!mScroller.scrollVertically(currentDirection,scrollableView)){
                    //证明滚动到了最下变，改变currentDirection为上
                    currentDirection = Scroller.VerticalDirection.UP_TO_DOWN;
                }
            } else if (currentDirection == Scroller.VerticalDirection.UP_TO_DOWN) {
                if (!mScroller.scrollVertically(currentDirection,scrollableView)) {
                    // 证明滚动到了最上边，改变currentDirection为下
                    currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
                }
            }
        }
        return null;
    }


    /**
     * 等待某个文本消失在屏幕上面
     * @param regex 待匹配的正则表达式
     * @param timeout 超时时间
     * @return true 等待的文本消失了，false 等待的文本在超时时间以后还存在
     */
    public boolean waitForTextDisappear(String regex,long timeout) {

        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            View view = mSearcher.forceSearchViewByText(regex,null,true);
            if (view == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 等待指定hint的EditText出现（至少有一个符合regex的Edit出现）
     * @param regex 带匹配的hint
     * @return true 找到了匹配的edittext false 没找到
     */
    public boolean waitForEditTextAppear(String regex) {
        return waitForEditTextAppear(regex, DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 等待拥有指定文本的EditText出现
     *
     * @param regex 带匹配的hint
     * @param timeout 指定超时的时间
     * @return true 找到了匹配的EditText false 没找到
     */
    public boolean waitForEditTextAppear(String regex, long timeout) {
        return waitForEditTextAppearAndGet(regex,timeout) != null;
    }

    public EditText waitForEditTextAppearAndGet(String regex) {
        return waitForEditTextAppearAndGet(regex,DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 等待指定hint的EditText出现
     * @param regex 待匹配的hint
     * @param timeout 指定超时的时间
     * @return 匹配到的EditText
     */
    public EditText waitForEditTextAppearAndGet(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            pause();
            EditText editText = mSearcher.searchEditTextByText(regex,
                    true);
            if (editText != null) {
                return editText;
            }
        }
        return null;
    }

    public boolean waitForEditTextAppearWithVerticallyScroll(String regex,long timeout,View scrollableView) {
        return waitForEditTextAppearVerticallyWithScrollAndGet(regex,timeout,scrollableView) != null;
    }

    /**
     * 等待某个指定的文本editText出现，并返回。在需要的时候会自动进行滚动
     * @param regex 待匹配的正则表达式
     * @param timeout 超时时间
     * @param scrollableView 内容view，在必要的时候可以滚动
     * @return 匹配的到的EditText
     */
    public EditText waitForEditTextAppearVerticallyWithScrollAndGet(String regex, long timeout, View scrollableView) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        Scroller.VerticalDirection currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            EditText editText = mSearcher.searchEditTextByText(regex,true);
            if (editText != null) {
                return editText;
            }

            // 先向下滚动，一直滚动到最下面，如果还没有到超时时间的话那就再向上滚动，如果滚动到
            // 最上面还没有到超时时间那就再向下滚动，如此反复
            if (currentDirection == Scroller.VerticalDirection.DOWN_TO_UP) {
                if (!mScroller.scrollVertically(currentDirection,scrollableView)){
                    //证明滚动到了最下变，改变currentDirection为上
                    currentDirection = Scroller.VerticalDirection.UP_TO_DOWN;
                }
            } else if (currentDirection == Scroller.VerticalDirection.UP_TO_DOWN) {
                if (!mScroller.scrollVertically(currentDirection,scrollableView)) {
                    // 证明滚动到了最上边，改变currentDirection为下
                    currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
                }
            }

        }
        return null;
    }

    /**
     * 等待匹配指定文本的button出现
     * @param regex 等待匹配的正则表达式
     * @param timeout 超时时间
     * @return
     */
    public boolean waitForButtonAppear(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            pause();
            ArrayList<Button> buttons = mSearcher.searchButtonListByText(regex,
                    true);
            if (buttons != null && !buttons.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public Button waitForButtonAppearAndGet(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            pause();
            Button button = mSearcher.searchButtonByText(regex,  true);
            if (button != null) {
                return button;
            }
        }
        return null;
    }

    public boolean waitForButtonAppearWithVerticallyScroll(String regex, long timeout, View scrollableView) {
        return waitForButtonAppearWithVerticallyScrollAndGet(regex,timeout,scrollableView) != null;
    }

    public Button waitForButtonAppearWithVerticallyScrollAndGet(String regex, long timeout,
                                                                View scrollableView) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        Scroller.VerticalDirection currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            Button button = mSearcher.searchButtonByText(regex,true);
            if (button != null) {
                return button;
            }

            // 先向下滚动，一直滚动到最下面，如果还没有到超时时间的话那就再向上滚动，如果滚动到
            // 最上面还没有到超时时间那就再向下滚动，如此反复
            if (currentDirection == Scroller.VerticalDirection.DOWN_TO_UP) {
                if (!mScroller.scrollVertically(currentDirection,scrollableView)){
                    //证明滚动到了最下变，改变currentDirection为上
                    currentDirection = Scroller.VerticalDirection.UP_TO_DOWN;
                }
            } else if (currentDirection == Scroller.VerticalDirection.UP_TO_DOWN) {
                if (!mScroller.scrollVertically(currentDirection,scrollableView)) {
                    // 证明滚动到了最上边，改变currentDirection为下
                    currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
                }
            }
        }
        return null;
    }

    /**
     * 等待by指定的元素已经加载进dom，并返回此webElement的列表
     * @param by 指定的by
     * @param webView 指定的webView
     * @param timeout 超时时间
     * @return 返回by指定的所有元素的列表
     */
    public ArrayList<WebElement> waitForWebElementAppearAndGet(By by,View webView,long timeout) {

        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {

            pause();
            ArrayList<WebElement> webElementList = mWebUtils.getWebElementList(by, false, webView);
            if (webElementList != null && !webElementList.isEmpty()) {
                return webElementList;
            }
        }

        return null;
    }

    /**
     * 等待by指定的元素已经加载进dom
     * 指定的元素是否显示在屏幕上面并没有太大的意义。
     * @param by by
     * @param webView 指定的webview
     * @param timeout 超时时间
     * @return true 出现在屏幕上，false 没有出现在屏幕上
     */
    public boolean waitForWebElementAppear(By by,View webView,long timeout) {
        return waitForWebElementAppearAndGet(by,webView,timeout) != null;
    }

    public <T extends View> boolean waitForViewListAppear(Class<T> aClass,boolean includeSubClass) {
        return waitForViewListAppear(aClass,includeSubClass,DEFAULT_WAIT_TIMEOUT);
    }

    public <T extends View> boolean waitForViewListAppear(Class<T> aClass,boolean includeSubClass,long timeout) {
        ArrayList<T> viewListByClass = waitForViewListAppearAndGet(aClass,includeSubClass,timeout);
        return viewListByClass != null && !viewListByClass.isEmpty();
    }

    public <T extends View> ArrayList<T> waitForViewListAppearAndGet(Class<T> aClass,boolean includeSubClass){
        return waitForViewListAppearAndGet(aClass,includeSubClass,DEFAULT_WAIT_TIMEOUT);
    }

    public <T extends View> ArrayList<T> waitForViewListAppearAndGet(Class<T> aClass,boolean includeSubClass,long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            ArrayList<T> viewListByClass = mViewGetter.getViewListByClass(aClass, includeSubClass, null, true);
            if (viewListByClass != null && !viewListByClass.isEmpty()) {
                return viewListByClass;
            }
        }
        return null;
    }

    public boolean waitForViewListAppear(String className,View parent) {
        ArrayList<View> views = waitForViewListAppearAndGet(className, parent, DEFAULT_WAIT_TIMEOUT);
        return views != null && !views.isEmpty();
    }

    public ArrayList<View> waitForViewListAppearAndGet(String className,View parent) {
        return waitForViewListAppearAndGet(className,parent,DEFAULT_WAIT_TIMEOUT);
    }

    public ArrayList<View> waitForViewListAppearAndGet(String className,View parent,long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            ArrayList<View> viewListByName = mViewGetter.getViewListByName(className, parent, true);
            if (viewListByName != null && !viewListByName.isEmpty()) {
                return viewListByName;
            }
        }
        return null;
    }

    private boolean isActivityMatching(Activity currentActivity, String activityName) {
        return currentActivity != null && currentActivity.getClass().getName().equals(activityName);

    }

    private boolean isActivityMatching(Class<? extends Activity> activityClass, Activity currentActivity) {
        return currentActivity != null && currentActivity.getClass().equals(activityClass);
    }

}
