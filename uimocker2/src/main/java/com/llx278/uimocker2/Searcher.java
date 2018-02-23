package com.llx278.uimocker2;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 搜索当前ui上的一些元素
 * Created by llx on 03/01/2018.
 */

public class Searcher {

    private static final String TAG = "uimocker";
    private final ViewGetter mViewGetter;
    private final Scroller mScroller;
    private final Sleeper mSleeper;
    private final Solo.Config mConfig;

    public Searcher(ViewGetter viewGetter, Scroller scroller, Sleeper sleeper, Solo.Config config) {
        mViewGetter = viewGetter;
        mScroller = scroller;
        mSleeper = sleeper;
        mConfig = config;
    }

    public TextView searchForText(String regex, long timeout,
                                  boolean scroll, boolean onlyVisible) {
        return searchForText(TextView.class, regex, timeout, scroll, onlyVisible);
    }

    public <T extends TextView> T searchForText(Class<T> viewClass, String regex) {
        return searchForText(viewClass, regex, mConfig.defaultSearchTimeout, true, true);
    }

    /**
     * 找到符合正则匹配的标准textView，这里只返回第一次查找到的view
     *
     * @param viewClass   哪一种view可以被搜索到(例如 Button.class)
     * @param regex       待搜索的文本
     * @param timeout     超时时间
     * @param scroll      是否scroll
     * @param onlyVisible 是否仅仅查找可见的view
     * @return 查找到的view
     */
    public <T extends TextView> T searchForText(Class<T> viewClass, String regex, long timeout,
                                                boolean scroll, boolean onlyVisible) {
        final long endTime = SystemClock.uptimeMillis() + timeout;
        while (true) {
            mSleeper.sleep();
            final boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
            if (timedOut) {
                break;
            }
            ArrayList<T> currentViews = mViewGetter.getViewsByClass(viewClass, true,
                    null);
            //Logger.d("Searcher.SearchForText(Class,String,long,long,boolean,boolean) : currentViews : " + currentViews.toString());
            if (onlyVisible) {
                currentViews = UIUtil.removeInvisibleViews(currentViews);
            }

            for (T view : currentViews) {
                if (UIUtil.textViewOfMatches(regex, view)) {
                    return view;
                }
            }
            if (scroll && !mScroller.scrollDown()) {
                break;
            }
        }
        return null;
    }


    public EditText searchForEditText(String regex, long timeout, boolean scroll, boolean onVisible) {
        return searchForText(EditText.class, regex, timeout, scroll, onVisible);
    }

    public ArrayList<EditText> searchForEditTextList(String regex, long timeout, boolean scroll, boolean onlyVisible) {
        return searchForTextList(EditText.class, regex, timeout, scroll, onlyVisible);
    }

    public Button searchForButton(String regex, long timeout, boolean scroll, boolean onlyVisible) {
        return searchForText(Button.class, regex, timeout, scroll, onlyVisible);
    }

    public ArrayList<Button> searchForButtonList(String regex, long timeout, boolean scroll, boolean onlyVisible) {
        return searchForTextList(Button.class, regex, timeout, scroll, onlyVisible);
    }

    public ArrayList<TextView> searchForTextList(String regex, long timeout, boolean scroll, boolean onlyVisible) {
        return searchForTextList(TextView.class, regex, timeout, scroll, onlyVisible);
    }

    /**
     * 找到符合正则匹配的标准textView
     *
     * @param viewClass   哪一种view可以被搜索到(例如 Button.class)
     * @param regex       待搜索的文本
     * @param timeout     超时时间
     * @param scroll      是否scroll
     * @param onlyVisible 是否仅仅查找可见的view
     * @return 查找到的view
     */
    public <T extends TextView> ArrayList<T> searchForTextList(Class<T> viewClass, String regex, long timeout,
                                                               boolean scroll, boolean onlyVisible) {
        final long endTime = SystemClock.uptimeMillis() + timeout;
        ArrayList<T> uniqueTextViews = new ArrayList<>();
        while (true) {
            mSleeper.sleep();
            final boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
            if (timedOut) {
                break;
            }
            ArrayList<T> currentViews = mViewGetter.getViewsByClass(viewClass, true,
                    null);
            if (onlyVisible) {
                currentViews = UIUtil.removeInvisibleViews(currentViews);
            }

            for (T view : currentViews) {
                if (UIUtil.textViewOfMatches(regex, view)) {
                    uniqueTextViews.add(view);
                }
            }
            if (scroll && !mScroller.scrollDown()) {
                break;
            }
        }
        return uniqueTextViews;
    }

    /**
     * 搜索id指定的view
     *
     * @param id          指定的id
     * @param timeout     超时
     * @param scroll      是否滚动
     * @param onlyVisible 是否只寻找可见的view
     * @return 查找到的view
     */
    public View searchForId(int id, long timeout, boolean scroll, boolean onlyVisible) {
        final long endTime = SystemClock.uptimeMillis() + timeout;
        while (true) {
            mSleeper.sleep();
            final boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
            if (timedOut) {
                break;
            }
            ArrayList<View> viewsById = mViewGetter.getViewsById(id);
            for (View view : viewsById) {
                if (view == null) {
                    continue;
                }
                if (view.getId() == id) {
                    return view;
                }
            }
            if (scroll && !mScroller.scrollDown()) {
                Logger.d(TAG, "Searcher.searchForTextView scroll finished!");
                break;
            }
        }
        return null;
    }

    /**
     * 对于某些自定义的view，并不是继承系统标准的textView，那么，这种情况下还没想到什么更好的
     * 解决方案，目前的解决办法就是强制的遍历这个自定义view的所有field，用正则去匹配里面的字符串（如果存在的话）
     * 如果找到了符合的字符串，那么我就假定它是显示在屏幕上的。
     *
     * @param className   类名
     * @param parent      待查找的父view，如果为null，则为当前的activity
     * @param regex       待匹配的文本
     * @param timeout     超时时间
     * @param scroll      是否滚动
     * @param onlyVisible 是否只寻找可见的
     * @return 查找到的view的列表
     */
    public ArrayList<View> searchForNameList(String className, View parent, String regex, long timeout,
                                             boolean scroll, boolean onlyVisible) {
        final long endTime = SystemClock.uptimeMillis() + timeout;
        ArrayList<View> uniqueCustomViews = new ArrayList<>();
        while (true) {
            mSleeper.sleep();
            final boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
            if (timedOut) {
                break;
            }

            ArrayList<View> currentViews = mViewGetter.getViewsByName(className, parent, true);
            if (onlyVisible) {
                currentViews = UIUtil.removeInvisibleViews(currentViews);
            }
            for (View view : currentViews) {
                ArrayList<String> findStrList = ReflectUtil.getCustomViewText(view, regex);
                if (findStrList != null && !findStrList.isEmpty()) {
                    uniqueCustomViews.add(view);
                }
            }
            if (scroll && !mScroller.scrollDown()) {
                break;
            }
        }
        return uniqueCustomViews;
    }

    /**
     * 对于某些自定义的view，并不是继承系统标准的textView，那么，这种情况下还没想到什么更好的
     * 解决方案，目前的解决办法就是强制的遍历这个自定义view的所有field，用正则去匹配里面的字符串（如果存在的话）
     * 如果找到了符合的字符串，那么我就假定它是显示在屏幕上的。
     *
     * @param className   类名
     * @param parent      待查找的父view，如果为null，则为当前的activity
     * @param regex       待匹配的文本
     * @param timeout     超时时间
     * @param scroll      是否滚动
     * @param onlyVisible 是否只寻找可见的
     * @return 查找到的view第一个view
     */
    public View searchForName(String className, View parent, String regex, long timeout,
                              boolean scroll, boolean onlyVisible) {
        final long endTime = SystemClock.uptimeMillis() + timeout;
        while (true) {
            mSleeper.sleep();
            final boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
            if (timedOut) {
                break;
            }

            ArrayList<View> currentViews = mViewGetter.getViewsByName(className, parent, true);
            if (onlyVisible) {
                currentViews = UIUtil.removeInvisibleViews(currentViews);
            }
            for (View view : currentViews) {
                ArrayList<String> findStrList = ReflectUtil.getCustomViewText(view, regex);
                if (findStrList != null && !findStrList.isEmpty()) {
                    return view;
                }
            }
            if (scroll && !mScroller.scrollDown()) {
                break;
            }
        }
        return null;
    }

    /**
     * 根据filter的条件查找到指定的类名的View
     *
     * @param className   指定的类名
     * @param filter      过滤器
     * @param timeout     超时时间
     * @param scroll      是否滚动
     * @param onlyVisible 是否可见
     * @return 符合条件的view
     */
    public ArrayList<View> searchForNamesByFilter(String className, View parent, Filter filter, long timeout,
                                                  boolean scroll, boolean onlyVisible) {
        final long endTime = SystemClock.uptimeMillis() + timeout;
        ArrayList<View> uniqueCustomViews = new ArrayList<>();
        while (true) {
            mSleeper.sleep();
            final boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
            if (timedOut) {
                break;
            }
            ArrayList<View> currentViews = mViewGetter.getViewsByName(className, parent, true);
            if (onlyVisible) {
                currentViews = UIUtil.removeInvisibleViews(currentViews);
            }
            for (View view : currentViews) {
                if (filter.match(view)) {
                    uniqueCustomViews.add(view);
                }
            }
            if (scroll && !mScroller.scrollDown()) {
                break;
            }
        }
        return uniqueCustomViews;
    }

    public View searchForNameByFilter(String className, View parent, Filter filter, long timeout,
                                      boolean scroll, boolean onlyVisible) {
        final long endTime = SystemClock.uptimeMillis() + timeout;
        Logger.d("timeout : " + timeout);
        while (true) {
            mSleeper.sleep();
            final boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
            if (timedOut) {
                Logger.d("is timeout !!!!!!!!");
                break;
            }
            ArrayList<View> currentViews = mViewGetter.getViewsByName(className, parent, true);
            Logger.d("currentViews : " + currentViews.toString());
            if (onlyVisible) {
                currentViews = UIUtil.removeInvisibleViews(currentViews);
            }
            for (View view : currentViews) {
                if (filter.match(view)) {
                    return view;
                }
            }
            if (scroll && !mScroller.scrollDown()) {
                Log.w(TAG, "Searcher.searchForTextView scroll failed!");
                break;
            }
        }
        return null;
    }
}
