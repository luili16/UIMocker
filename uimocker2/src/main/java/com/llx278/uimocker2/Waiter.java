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

    private static final String TAG = "Waiter";
    private final ActivityUtils mActivityUtils;
    private final ViewGetter mViewGetter;
    private final Searcher mSearcher;
    private final Solo.Config mConfig;
    private final Sleeper mSleeper;
    private final Scroller mScroller;

    public Waiter(ActivityUtils activityUtils,
                  ViewGetter viewGetter, Searcher searcher, Solo.Config config, Sleeper sleeper,Scroller scroller) {

        mActivityUtils = activityUtils;
        mViewGetter = viewGetter;
        mSearcher = searcher;
        mConfig = config;
        mSleeper = sleeper;
        mScroller = scroller;
    }

    /**
     * 等待指定的Activity出现
     *
     * @param activityName 全修饰的activity类名
     * @return true 指定的activity已经出现，false 超时返回
     */
    public boolean waitForActivity(String activityName) {
        return waitForActivity(activityName, mConfig.defaultWaitTimeout);
    }

    /**
     * 等待指定的Activity出现
     *
     * @param activityName 全修饰的activity类名
     * @param timeout      等待的时间
     * @return true 指定的activity已经出现，false 超时返回
     */
    public boolean waitForActivity(String activityName, int timeout) {
        if (isActivityMatching(mActivityUtils.getCurrentActivity(), activityName)) {
            return true;
        }
        final long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            mSleeper.sleep();
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
        return waitForActivity(activityClass, mConfig.defaultWaitTimeout);
    }

    /**
     * 等待指定的activity出现
     *
     * @param activityClass 全修饰的activity类名
     * @param timeout       等待的时间
     * @return true 指定的activity出现 false 超时返回
     */
    public boolean waitForActivity(Class<? extends Activity> activityClass, int timeout) {
        if (isActivityMatching(activityClass, mActivityUtils.getCurrentActivity())) {
            return true;
        }

        long currentTime = SystemClock.uptimeMillis();
        final long endTime = currentTime + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            mSleeper.sleep();
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
        return waitForFragment(tag, id, mConfig.defaultWaitTimeout);
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
    public boolean waitForFragment(String tag, int id, int timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
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
        return waitForWindowDecorViews(mConfig.defaultWaitTimeout);
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
            mSleeper.sleep();
            List<View> views = mViewGetter.getWindowViews();
            if (views != null && !views.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 等待有指定文本的自定义view出现
     * 注意：这个方法在执行的过程中默认是滚动的，也就是说这个方法执行结束会导致当前的有滚动的控件滚动到最后或者
     * 直到超时。
     *
     * @param className 自定义view的类名
     * @param regex     待匹配的文本
     * @param timeout   超时时间
     * @return true 找到 false 在超时时间内没有找到
     */
    public boolean waitForTextFromCustomView(String className, String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            ArrayList<View> viewsByName = mSearcher.forceSearchViewListByTextAndClassName(className, null, regex, true);
            if (viewsByName != null && !viewsByName.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 等待指定的文本出现并获得指定的View的list
     * 注意，这会导致当前的页面发生滚动，并且如果滚动的控件是listview或者recyclerview这种可回收view的控件的话,
     * 你获得到的这个view的list实际上是没有意义的，只有当滚动的控件是scrollView这种不回收View的控件才好用。
     * @param className 自定义view的类名
     * @param regex 待匹配的文本
     * @param timeout 超时时间
     * @return true 找到  false在超时时间内没有找到
     */
    public ArrayList<View> waitForTextFromCustomViewListAndGet(String className,
                                                               String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            ArrayList<View> viewsByName = mSearcher.forceSearchViewListByTextAndClassName(className, null, regex,
                    true);
            if (viewsByName != null && !viewsByName.isEmpty()) {
                return viewsByName;
            }
        }
        return null;
    }

    /**
     * 等待指定的文本出现并获得包含该文本的view，注意这个默认只会返回第一次匹配的view
     * @param className 自定义view的类名
     * @param regex 待匹配的文本
     * @param timeout 超时时间
     * @return true 找到  false在超时时间内没有找到
     */
    public View waitForTextFromCustomViewAndGet(String className,
                                                String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            View viewsByName = mSearcher.forceSearchViewByTextAndClassName(className, null, regex,
                    true);
            if (viewsByName != null) {
                return viewsByName;
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
    public boolean waitForTextAppear(String regex) {
        return waitForTextAppear(regex, mConfig.defaultWaitTimeout);
    }

    /**
     * 等待指定的文本出现至 (少有一个符合regex的文本出现）
     *  注意，这会导致当前的页面发生滚动，并且如果滚动的控件是listview或者recyclerview这种可回收view的控件的话,
     * 你获得到的这个view的list实际上是没有意义的，只有当滚动的控件是scrollView这种不回收View的控件才好用。
     * @param regex 符合正则表达式的文本
     * @return true 符合条件的文本已经出现 false 超时
     */
    public boolean waitForTextAppear(String regex, long timeout) {
        return waitForTextAppearAndGet(regex,timeout) != null;
    }

    /**
     * 等待指定的文本出现，并获得包含该文本的textview
     * @param regex 符合正则表达式的文本
     * @param timeout 超时时间
     * @return true 找到了符合正则表达式的文本 false 超时
     */
    public TextView waitForTextAppearAndGet(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            TextView textView = mSearcher.searchTextViewByText(regex, true);
            if (textView != null) {
                return textView;
            }
        }
        return null;
    }

    public TextView waitForTextAppearWithVerticallyScrollAndGet(String regex,
                                                                long timeout,
                                                                View scrollableView) {
        // 这里的timeout是搜索的超时和滚动的超时的和
        // 这里面需要对mSearcher做更加精细的控制
        long endTime = SystemClock.uptimeMillis() + timeout;
        Scroller.VerticalDirection currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
        while (SystemClock.uptimeMillis() < endTime) {
            TextView textView = mSearcher.searchTextViewByText(regex, true);
            if (textView != null) {
                return textView;
            }

            // 先向下滚动，一直滚动到最下面，如果还没有到超时时间的话那就再向上滚动，如果滚动到
            // 最上面还没有到超时时间那就再向下滚动，如此反复
            if (currentDirection == Scroller.VerticalDirection.DOWN_TO_UP) {
                if (mScroller.scrollVertically(currentDirection,scrollableView)){
                    //证明滚动到了最下变，改变currentDirection为上
                    currentDirection = Scroller.VerticalDirection.UP_TO_DOWN;
                }
            } else if (currentDirection == Scroller.VerticalDirection.UP_TO_DOWN) {
                if (mScroller.scrollVertically(currentDirection,scrollableView)) {
                    // 证明滚动到了最上边，改变currentDirection为下
                    currentDirection = Scroller.VerticalDirection.DOWN_TO_UP;
                }
            }
        }
        return null;
    }

    /**
     * 等待指定hint的EditText出现（至少有一个符合regex的Edit出现）
     * @param regex 带匹配的hint
     * @return true 找到了匹配的edittext false 没找到
     */
    public boolean waitForEditText(String regex) {
        return waitForEditText(regex, mConfig.defaultWaitTimeout);
    }

    /**
     * 等待指定hint的EditText出现（至少有一个符合regex的Edit出现）
     *
     * @param regex 带匹配的hint
     * @param timeout 指定超时的时间
     * @return true 找到了匹配的edittext false 没找到
     */
    public boolean waitForEditText(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            ArrayList<EditText> editTexts = mSearcher.searchEditTextListByText(regex,
                    true);
            if (editTexts != null && !editTexts.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 等待指定hint的EditText出现（至少有一个符合regex的Edit出现），并获得第一次匹配的editText
     * @param regex 待匹配的hint
     * @param timeout 指定超时的时间
     * @return
     */
    public EditText waitForEditTextAndGet(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            EditText editText = mSearcher.searchEditTextByText(regex,
                    true);
            if (editText != null) {
                return editText;
            }
        }
        return null;
    }

    /**
     * 等待指定hint的EditText出现（至少有一个符合regex的Edit出现），并获得匹配的editText列表
     * @param regex 待匹配的hint
     * @param timeout 指定超时的时间
     * @return
     */
    public ArrayList<EditText> waitForEditTextListAndGet(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            ArrayList<EditText> editTexts = mSearcher.searchEditTextListByText(regex, true);
            if (editTexts != null && !editTexts.isEmpty()) {
                return editTexts;
            }
        }
        return null;
    }

    public boolean waitForButton(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            ArrayList<Button> buttons = mSearcher.searchButtonListByText(regex,
                    true);
            if (buttons != null && !buttons.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public Button waitForButtonAndGet(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            Button button = mSearcher.searchButtonByText(regex,  true);
            if (button != null) {
                return button;
            }
        }
        return null;
    }

    public ArrayList<Button> waitForButtonListAndGet(String regex, long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            mSleeper.sleep();
            ArrayList<Button> buttons = mSearcher.searchButtonListByText(regex,true);
            if (buttons != null && !buttons.isEmpty()) {
                return buttons;
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
