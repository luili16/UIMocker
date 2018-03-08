package com.llx278.uimocker2;

import android.os.SystemClock;
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

    /**
     * 滚动的搜索 textView
     * @param regex 正则表达式
     * @param onlyVisible true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param timeout 超时时间
     * @return 匹配的TextView或者null
     */
    public TextView searchTextViewByTextWithVerticallyScroll(String regex,
                                                             boolean onlyVisible,
                                                             View scrollableView,
                                                             Scroller.VerticalDirection direction,
                                                             long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {

            TextView tv = searchTextViewByText(regex,onlyVisible);
            if (tv != null) {
                return tv;
            }

            if (scrollableView == null) {
                break;
            }

            if(mScroller.scrollVertically(direction,scrollableView)) {
               break;
            }
        }

        return null;
    }

    public ArrayList<TextView> searchTextViewListByTextWithVerticallyScroll(String regex,
                                                                            boolean onlyVisible,
                                                                            View scrollableView,
                                                                            Scroller.VerticalDirection direction,
                                                                            long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            //ArrayList
        }
        return null;
    }



    /**
     * 通过文本搜索textView
     * @param regex 正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的TextView或者null
     */
    public TextView searchTextViewByText(String regex,
                                         boolean onlyVisible) {
        return searchTByText(TextView.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索textView列表
     * @param regex 正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的TextView列表或者null
     */
    public ArrayList<TextView> searchTextViewListByText(String regex,  boolean onlyVisible) {
        return searchTListByText(TextView.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索editText
     * @param regex 正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的EditText列表或者null
     */
    public EditText searchEditTextByText(String regex, boolean onlyVisible) {
        return searchTByText(EditText.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索EditText列表
     * @param regex 正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的EditText列表或者null
     */
    public ArrayList<EditText> searchEditTextListByText(String regex, boolean onlyVisible) {
        return searchTListByText(EditText.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索Button
     * @param regex 正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的Button列表或者null
     */
    public Button searchButtonByText(String regex, boolean onlyVisible) {
        return searchTByText(Button.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索Button列表
     * @param regex 正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的Button列表或者null
     */
    public ArrayList<Button> searchButtonListByText(String regex,boolean onlyVisible) {
        return searchTListByText(Button.class, regex, onlyVisible);
    }

    /**
     * 找到符合正则匹配的标准textView，这里只返回第一次查找到的view
     *
     * @param viewClass   哪一种view可以被搜索到(例如 Button.class)
     * @param regex       待搜索的文本
     * @param onlyVisible 是否仅仅查找可见的view
     * @return 查找到的view
     */
    public <T extends TextView> T searchTByText(Class<T> viewClass, String regex,
                                                boolean onlyVisible) {
        ArrayList<T> currentViews = mViewGetter.getViewListByClass(viewClass, true,
                null);
        if (onlyVisible) {
            currentViews = UIUtil.removeInvisibleViews(currentViews);
        }

        for (T view : currentViews) {
            if (UIUtil.textViewOfMatches(regex, view)) {
                return view;
            }
        }
        return null;
    }

    /**
     * 找到符合正则匹配的标准textView
     *
     * @param viewClass   哪一种view可以被搜索到(例如 Button.class)
     * @param regex       待搜索的文本
     * @param onlyVisible 是否仅仅查找可见的view
     * @return 查找到的view
     */
    public <T extends TextView> ArrayList<T> searchTListByText(Class<T> viewClass, String regex,
                                                               boolean onlyVisible) {
        ArrayList<T> uniqueTextViews = new ArrayList<>();
        mSleeper.sleep();
        ArrayList<T> currentViews = mViewGetter.getViewListByClass(viewClass, true,
                null);
        if (onlyVisible) {
            currentViews = UIUtil.removeInvisibleViews(currentViews);
        }

        for (T view : currentViews) {
            if (UIUtil.textViewOfMatches(regex, view)) {
                uniqueTextViews.add(view);
            }
        }
        return uniqueTextViews;
    }

    /**
     * 搜索id指定的view
     *
     * @param id          指定的id
     * @param onlyVisible 是否只寻找可见的view
     * @return 查找到的view
     */
    public View searchViewById(int id,  boolean onlyVisible) {

        mSleeper.sleep();
        ArrayList<View> viewsById = mViewGetter.getViewListById(id);
        if (onlyVisible) {
            viewsById = UIUtil.removeInvisibleViews(viewsById);
        }

        for (View view : viewsById) {
            if (view == null) {
                continue;
            }
            if (view.getId() == id) {
                return view;
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
     * @param onlyVisible 是否只寻找可见的
     * @return 查找到的view的列表
     */
    public ArrayList<View> forceSearchViewListByText(String className, View parent, String regex,
                                                     boolean onlyVisible) {
        ArrayList<View> uniqueCustomViews = new ArrayList<>();
        mSleeper.sleep();

        ArrayList<View> currentViews = mViewGetter.getViewListByName(className, parent, true);
        if (onlyVisible) {
            currentViews = UIUtil.removeInvisibleViews(currentViews);
        }
        for (View view : currentViews) {
            ArrayList<String> findStrList = ReflectUtil.getCustomViewText(view, regex);
            if (findStrList != null && !findStrList.isEmpty()) {
                uniqueCustomViews.add(view);
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
     * @param onlyVisible 是否只寻找可见的
     * @return 查找到的view第一个view
     */
    public View forceSearchViewByText(String className, View parent, String regex,
                                      boolean onlyVisible) {
        mSleeper.sleep();
        ArrayList<View> currentViews = mViewGetter.getViewListByName(className, parent, true);
        if (onlyVisible) {
            currentViews = UIUtil.removeInvisibleViews(currentViews);
        }
        for (View view : currentViews) {
            ArrayList<String> findStrList = ReflectUtil.getCustomViewText(view, regex);
            if (findStrList != null && !findStrList.isEmpty()) {
                return view;
            }
        }
        return null;
    }

    /**
     * 根据filter的条件查找到指定的类名的View
     *
     * @param className   指定的类名
     * @param filter      过滤器
     * @param onlyVisible 是否可见
     * @return 符合条件的view
     */
    public ArrayList<View> searchViewListByFilter(String className, View parent, Filter filter,
                                                  boolean onlyVisible) {
        ArrayList<View> uniqueCustomViews = new ArrayList<>();
        mSleeper.sleep();
        ArrayList<View> currentViews = mViewGetter.getViewListByName(className, parent, true);
        if (onlyVisible) {
            currentViews = UIUtil.removeInvisibleViews(currentViews);
        }
        for (View view : currentViews) {
            if (filter.match(view)) {
                uniqueCustomViews.add(view);
            }
        }
        return uniqueCustomViews;
    }

    /**
     * 根据filter的条件查找到指定的类名的View，返回第一个匹配的view
     * @param className 指定类名
     * @param parent 父view
     * @param filter 过滤器
     * @param onlyVisible 是否只匹配可见的
     * @return 符合条件的view
     */
    public View searchViewByFilter(String className, View parent, Filter filter,
                                   boolean onlyVisible) {
        mSleeper.sleep();
        ArrayList<View> currentViews = mViewGetter.getViewListByName(className, parent, true);
        if (onlyVisible) {
            currentViews = UIUtil.removeInvisibleViews(currentViews);
        }
        for (View view : currentViews) {
            if (filter.match(view)) {
                return view;
            }
        }
        return null;
    }
}
