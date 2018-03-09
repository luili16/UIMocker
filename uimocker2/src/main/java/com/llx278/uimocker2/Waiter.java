package com.llx278.uimocker2;

import android.app.Activity;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

    public static final long DEFAULT_WAIT_TIMEOUT = 1000 * 20;
    public static long WAIT_TIMEOUT = DEFAULT_WAIT_TIMEOUT;
    public static final long DEFAULT_PAUSE_TIMEOUT = 200;
    public static long PAUSE_TIME_OUT = DEFAULT_PAUSE_TIMEOUT;

    private static final String TAG = "Waiter";
    private final ActivityUtils mActivityUtils;
    private final ViewGetter mViewGetter;
    private final Searcher mSearcher;
    private final Scroller mScroller;

    public Waiter(ActivityUtils activityUtils,
                  ViewGetter viewGetter, Searcher searcher,Scroller scroller) {

        mActivityUtils = activityUtils;
        mViewGetter = viewGetter;
        mSearcher = searcher;
        mScroller = scroller;
    }

    private void pause() {
        try {
            Thread.sleep(DEFAULT_PAUSE_TIMEOUT);
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
        return waitForActivity(activityName, WAIT_TIMEOUT);
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
        return waitForActivity(activityClass, WAIT_TIMEOUT);
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

    /**
     * 等待一个指定id或tag的fragment出现
     * 注意，这里面如果fragment是support包里的，而且support包也被混淆的话，
     * 那么这个方法是没有意义的，因为混淆后的方法名与标准的support包不兼容，
     * 比如说微信，就做了这个的混淆
     *
     * @param tag fragment的tag
     * @param id  fragment的id
     * @return true 等待的activity已经出现，false 没有
     */
    public boolean waitForFragment(String tag, int id) {
        return waitForFragment(tag, id, WAIT_TIMEOUT);
    }

    /**
     * 等待一个指定id或tag的fragment出现
     * 注意，这里面如果fragment是support包里的，而且support包也被混淆的话，
     * 那么这个方法是没有意义的，因为混淆后的方法名与标准的support包不兼容，
     * 比如说微信，就做了这个的混淆
     *
     * @param tag     fragment的tag
     * @param id      fragment的id
     * @param timeout 超时时间
     * @return true 等待的activity已经出现，false 没有
     */
    public boolean waitForFragment(String tag, int id, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            pause();
            if (getSupportFragment(tag, id) != null)
                return true;

            if (getFragment(tag, id) != null)
                return true;
        }
        return false;
    }

    /**
     * 等待window的decorView出现
     *
     * @return
     */
    public boolean waitForWindowDecorViews() {
        return waitForWindowDecorViews(WAIT_TIMEOUT);
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

    /**
     * 等待某个文本出现，此方法会强制遍历所有的view(不限于TextView)
     * @param regex 待匹配的文本
     * @param timeout 超时时间
     * @return true 等待的文本出现
     */
    public boolean waitForTextAppear(String regex,long timeout) {
        return waitForTextAppearAndGet(regex,timeout) != null;
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
        return waitForTextViewAppear(regex, WAIT_TIMEOUT);
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
        return waitForEditTextAppear(regex, WAIT_TIMEOUT);
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



    //     ------- private methods --------------
    private Fragment getSupportFragment(String tag, int id) {
        FragmentActivity fragmentActivity = null;

        try {
            fragmentActivity = (FragmentActivity) mActivityUtils.getCurrentActivity();
        } catch (Throwable ignored) {
        }

        if (fragmentActivity != null) {
            try {
                if (tag == null)
                    return fragmentActivity.getSupportFragmentManager().findFragmentById(id);
                else
                    return fragmentActivity.getSupportFragmentManager().findFragmentByTag(tag);
            } catch (NoSuchMethodError ignored) {
            }
        }
        return null;
    }

    private android.app.Fragment getFragment(String tag, int id) {

        try {
            if (tag == null)
                return mActivityUtils.getCurrentActivity().getFragmentManager().findFragmentById(id);
            else
                return mActivityUtils.getCurrentActivity().getFragmentManager().findFragmentByTag(tag);
        } catch (Throwable ignored) {
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
