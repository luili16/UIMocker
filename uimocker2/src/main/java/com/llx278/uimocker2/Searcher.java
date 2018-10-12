package com.llx278.uimocker2;

import android.os.SystemClock;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 搜索当前ui上的一些元素
 * Created by llx on 03/01/2018.
 */

public class Searcher {

    private static final String TAG = "uimocker";
    
    private static final long DEFAULT_PAUSE_TIME = 100;
    /**
     * 暂停的时间
     */
    public static long PAUSE_TIME = DEFAULT_PAUSE_TIME;
    
    
    private final ViewGetter mViewGetter;
    private final Scroller mScroller;

    public Searcher(ViewGetter viewGetter, Scroller scroller) {
        mViewGetter = viewGetter;
        mScroller = scroller;
    }
    
    private void pause() {
        try {
            Thread.sleep(PAUSE_TIME);
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * 滚动的搜索 textView
     *
     * @param regex          正则表达式
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 匹配的TextView或者null
     */
    public TextView searchTextViewByTextWithVerticallyScroll(String regex,
                                                             boolean onlyVisible,
                                                             View scrollableView,
                                                             Scroller.VerticalDirection direction,
                                                             long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            TextView tv = searchTextViewByText(regex, onlyVisible);
            if (tv != null) {
                return tv;
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }

        return null;
    }

    /**
     * 滚动的搜索 textViewList,对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     *
     * @param regex          正则表达式
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 所有符合条件的TextView的列表（以超时返回或者滚动到最上或者最下两者哪个最先达到为准）
     */
    public ArrayList<TextView> searchTextViewListByTextWithVerticallyScroll(String regex,
                                                                            boolean onlyVisible,
                                                                            View scrollableView,
                                                                            Scroller.VerticalDirection direction,
                                                                            long timeout) {
        ArrayList<TextView> textViewList = new ArrayList<>();
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            ArrayList<TextView> newTextViewList = searchTextViewListByText(regex, onlyVisible);
            if (newTextViewList != null && !newTextViewList.isEmpty()) {
                textViewList.addAll(newTextViewList);
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        Set<TextView> filteredViews = UIUtil.filterSameViews(textViewList);
        return new ArrayList<>(filteredViews);
    }

    /**
     * 滚动的搜索 editText
     *
     * @param regex          正则表达式
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 匹配的editText或者null
     */
    public EditText searchEditTextByTextWithVerticallyScroll(String regex,
                                                             boolean onlyVisible,
                                                             View scrollableView,
                                                             Scroller.VerticalDirection direction,
                                                             long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();

            EditText et = searchEditTextByText(regex, onlyVisible);
            if (et != null) {
                return et;
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        return null;
    }

    /**
     * 滚动的搜索 editText 列表
     *对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param regex          正则表达式
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 匹配的editTextList(以超时返回或者滚动到最上或者最下两者哪个最先达到为准）)
     */
    public ArrayList<EditText> searchEditTextListByTextWithVerticallyScroll(String regex,
                                                                            boolean onlyVisible,
                                                                            View scrollableView,
                                                                            Scroller.VerticalDirection direction,
                                                                            long timeout) {

        ArrayList<EditText> editTextArrayList = new ArrayList<>();
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            ArrayList<EditText> newEditTextArrayList = searchEditTextListByText(regex, onlyVisible);
            if (newEditTextArrayList != null && !newEditTextArrayList.isEmpty()) {
                editTextArrayList.addAll(newEditTextArrayList);
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        Set<EditText> filteredViews = UIUtil.filterSameViews(editTextArrayList);
        return new ArrayList<>(filteredViews);
    }

    /**
     * 滚动的搜索 Button
     *
     * @param regex          正则表达式
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 匹配的Button或者null
     */
    public Button searchButtonByTextWithVerticallyScroll(String regex,
                                                         boolean onlyVisible,
                                                         View scrollableView,
                                                         Scroller.VerticalDirection direction,
                                                         long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();

            Button et = searchButtonByText(regex, onlyVisible);
            if (et != null) {
                return et;
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        return null;
    }

    /**
     * 滚动的搜索 Button 列表
     对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param regex          正则表达式
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 匹配的ButtonList(以超时返回或者滚动到最上或者最下两者哪个最先达到为准）)
     */
    public ArrayList<Button> searchButtonListByTextWithVerticallyScroll(String regex,
                                                                        boolean onlyVisible,
                                                                        View scrollableView,
                                                                        Scroller.VerticalDirection direction,
                                                                        long timeout) {

        ArrayList<Button> buttonArrayList = new ArrayList<>();
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            ArrayList<Button> newButtonArrayList = searchButtonListByText(regex, onlyVisible);
            if (newButtonArrayList != null && !newButtonArrayList.isEmpty()) {
                buttonArrayList.addAll(newButtonArrayList);
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        Set<Button> filteredViews = UIUtil.filterSameViews(buttonArrayList);
        return new ArrayList<>(filteredViews);
    }

    /**
     * 滚动的搜索 所有TextView或者TextView的子类
     *
     * @param regex          正则表达式
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 匹配的view或者null
     */
    public <T extends TextView> T searchTByTextWithVerticallyScroll(Class<T> viewClass,
                                                                    String regex,
                                                                    boolean onlyVisible,
                                                                    View scrollableView,
                                                                    Scroller.VerticalDirection direction,
                                                                    long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();

            T t = searchTByText(viewClass, regex, onlyVisible);
            if (t != null) {
                return t;
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        return null;
    }

    /**
     * 滚动的搜索 所有TextView或者TextView的子类
     *对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param regex          正则表达式
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 匹配的view列表或者null
     */
    public <T extends TextView> ArrayList<T> searchTListByTextWithVerticallyScroll(Class<T> viewClass,
                                                                                   String regex,
                                                                                   boolean onlyVisible,
                                                                                   View scrollableView,
                                                                                   Scroller.VerticalDirection direction,
                                                                                   long timeout) {
        ArrayList<T> tArrayList = new ArrayList<>();
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();

            ArrayList<T> newTList = searchTListByText(viewClass, regex, onlyVisible);
            if (newTList != null && !newTList.isEmpty()) {
                Set<T> filteredViewList = UIUtil.filterSameViews(newTList);
                tArrayList.addAll(filteredViewList);
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        Set<T> filteredViews = UIUtil.filterSameViews(tArrayList);
        return new ArrayList<>(filteredViews);
    }

    /**
     * 滚动搜索id指定的view
     *
     * @param id             待搜索的view的id
     * @param onlyVisible    true 只搜索可见view false 所有
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 匹配的Button或者null
     */
    public View searchViewByIdWithVerticallyScroll(int id,
                                                   boolean onlyVisible,
                                                   View scrollableView,
                                                   Scroller.VerticalDirection direction,
                                                   long timeout) {

        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();

            View view = searchViewById(id, onlyVisible);
            if (view != null) {
                return view;
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
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
     * @param className      类名
     * @param parent         待查找的父view，如果为null，则为当前的activity
     * @param regex          待匹配的文本
     * @param onlyVisible    是否只寻找可见的
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 查找到的view第一个view
     */
    public View forceSearchViewByTextWithVerticallyScroll(String className,
                                                          View parent,
                                                          String regex,
                                                          boolean onlyVisible,
                                                          View scrollableView,
                                                          Scroller.VerticalDirection direction,
                                                          long timeout) {

        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();

            View view = forceSearchViewByTextAndClassName(className, parent, regex, onlyVisible);
            if (view != null) {
                return view;
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
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
     * 对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     *
     * @param className      类名
     * @param parent         待查找的父view，如果为null，则为当前的activity
     * @param regex          待匹配的文本
     * @param onlyVisible    是否只寻找可见的
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction      滚动的方向
     * @param timeout        超时时间
     * @return 查找到的view的列表
     */
    public ArrayList<View> forceSearchViewListByTextWithVerticallyScroll(String className,
                                                                         View parent,
                                                                         String regex,
                                                                         boolean onlyVisible,
                                                                         View scrollableView,
                                                                         Scroller.VerticalDirection direction,
                                                                         long timeout) {
        ArrayList<View> viewList = new ArrayList<>();
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            ArrayList<View> newViewList = forceSearchViewListByTextAndClassName(className, parent, regex, onlyVisible);
            if (newViewList != null && !newViewList.isEmpty()) {
                viewList.addAll(newViewList);
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        Set<View> filteredViews = UIUtil.filterSameViews(viewList);
        return new ArrayList<>(filteredViews);
    }

    /**
     * 根据filter的条件查找到指定的类名的View，返回第一个匹配的view
     *
     * @param className   指定类名
     * @param parent      父view
     * @param filter      过滤器
     * @param onlyVisible 是否只匹配可见的
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction 滚动的方向
     * @param timeout 超时时间
     * @return 符合条件的view
     */
    public View searchViewByFilterWithVerticallyScroll(String className,
                                                       View parent,
                                                       Filter filter,
                                                       boolean onlyVisible,
                                                       View scrollableView,
                                                       Scroller.VerticalDirection direction,
                                                       long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            View view = searchViewByFilter(className, parent, filter, onlyVisible);
            if (view != null) {
                return view;
            }

            if (!mScroller.scrollVertically(direction, scrollableView)) {
                break;
            }
        }
        return null;
    }

    /**
     * 根据filter的条件查找到指定的类名的View
     *对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param className   指定的类名
     * @param filter      过滤器
     * @param onlyVisible 是否可见
     * @return 符合条件的view
     */
    public ArrayList<View> searchViewListByFilterWithVerticallyScroll(String className,
                                                                      View parent,
                                                                      Filter filter,
                                                                      boolean onlyVisible,
                                                                      View scrollableView,
                                                                      Scroller.VerticalDirection direction,
                                                                      long timeout) {
        ArrayList<View> filterViewList = new ArrayList<>();
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            pause();
            ArrayList<View> newFilterViewList = searchViewListByFilter(className,parent,filter,onlyVisible);
            if (newFilterViewList != null &&!newFilterViewList.isEmpty()) {
                filterViewList.addAll(newFilterViewList);
            }
            if (!mScroller.scrollVertically(direction,scrollableView)) {
                break;
            }
        }
        Set<View> filteredViews = UIUtil.filterSameViews(filterViewList);
        return new ArrayList<>(filteredViews);
    }

    /**
     * 通过文本搜索textView
     *
     * @param regex       正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的TextView或者null
     */
    public TextView searchTextViewByText(String regex,
                                         boolean onlyVisible) {
        return searchTByText(TextView.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索textView列表
     *对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param regex       正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的TextView列表或者null
     */
    public ArrayList<TextView> searchTextViewListByText(String regex, boolean onlyVisible) {
        return searchTListByText(TextView.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索editText
     *
     * @param regex       正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的EditText列表或者null
     */
    public EditText searchEditTextByText(String regex, boolean onlyVisible) {
        return searchTByText(EditText.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索EditText列表
     *对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param regex       正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的EditText列表或者null
     */
    public ArrayList<EditText> searchEditTextListByText(String regex, boolean onlyVisible) {
        return searchTListByText(EditText.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索Button
     *
     * @param regex       正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的Button列表或者null
     */
    public Button searchButtonByText(String regex, boolean onlyVisible) {
        return searchTByText(Button.class, regex, onlyVisible);
    }

    /**
     * 通过文本搜索Button列表
     *对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param regex       正则表达式
     * @param onlyVisible true 只搜索可见的view false 所有
     * @return 匹配的Button列表或者null
     */
    public ArrayList<Button> searchButtonListByText(String regex, boolean onlyVisible) {
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
     *对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param viewClass   哪一种view可以被搜索到(例如 Button.class)
     * @param regex       待搜索的文本
     * @param onlyVisible 是否仅仅查找可见的view
     * @return 查找到的view
     */
    public <T extends TextView> ArrayList<T> searchTListByText(Class<T> viewClass, String regex,
                                                               boolean onlyVisible) {
        ArrayList<T> uniqueTextViews = new ArrayList<>();
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
    public View searchViewById(int id, boolean onlyVisible) {

        pause();
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
     * 对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param className   类名
     * @param parent      待查找的父view，如果为null，则为当前的activity
     * @param regex       待匹配的文本
     * @param onlyVisible 是否只寻找可见的
     * @return 查找到的view的列表
     */
    public ArrayList<View> forceSearchViewListByTextAndClassName(String className, View parent, String regex,
                                                                 boolean onlyVisible) {
        ArrayList<View> uniqueCustomViews = new ArrayList<>();
        pause();

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
    public View forceSearchViewByTextAndClassName(String className, View parent, String regex,
                                                  boolean onlyVisible) {
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
     * 强制的遍历所有的view，找到所有view里面可能的文本，并返回包含正则表达式匹配的文本的view
     * @param regex 待匹配的文本
     * @param parent 待查找的父view，如过为null，则为当前的activity
     * @param onlyVisible 是否只寻找可见的
     * @return 查找到的匹配的view或者null
     */
    public View forceSearchViewByText(String regex,View parent,boolean onlyVisible) {

        List<View> currentViewList = mViewGetter.getViewList(parent, true);
        if (onlyVisible) {
            currentViewList = UIUtil.removeInvisibleViews(currentViewList);
        }

        for (View view : currentViewList) {
            if (view instanceof TextView) {
                if (UIUtil.textViewOfMatches(regex,(TextView)view)) {
                    return view;
                }
            } else {
                ArrayList<String> customViewTextList = ReflectUtil.getCustomViewText(view, regex);
                for (String text : customViewTextList) {
                    if (UIUtil.textOfMatches(regex,text)) {
                        return view;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 强制的遍历所有的view，找到所有view里面可能的文本，并返回包含正则表达式匹配的文本的view
     * @param regex 待匹配的文本
     * @param parent 待查找的父view，如过为null，则为当前的activity
     * @param onlyVisible 是否只寻找可见的
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction 滚动的方向
     * @param timeout 超时时间
     * @return 查找到的匹配的view或者null
     */
    public View forceSearchViewByTextWithVerticallyScroll(String regex,
                                                          View parent,
                                                          boolean onlyVisible,
                                                          View scrollableView,
                                                          Scroller.VerticalDirection direction,
                                                          long timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            View view = forceSearchViewByText(regex,parent,onlyVisible);
            if (view != null) {
                return view;
            }
            if (!mScroller.scrollVertically(direction,scrollableView)) {
                break;
            }
        }
        return null;
    }

    /**
     * 强制的遍历所有的view，找到所有view里面可能的文本，并返回包含正则表达式匹配的文本的view
     * 对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param regex 待匹配的文本
     * @param parent 待查找的父view，如过为null，则为当前的activity
     * @param onlyVisible 是否只寻找可见的
     * @return 查找到的匹配的view或者null
     */
    public ArrayList<View> forceSearchViewListByText(String regex,View parent,boolean onlyVisible) {
        ArrayList<View> uniqueViewList = new ArrayList<>();
        List<View> currentViewList = mViewGetter.getViewList(parent, true);
        if (onlyVisible) {
            currentViewList = UIUtil.removeInvisibleViews(currentViewList);
        }

        for (View view : currentViewList) {
            if (view instanceof TextView) {
                if (UIUtil.textViewOfMatches(regex,(TextView)view)) {
                    uniqueViewList.add(view);
                }
            } else {
                ArrayList<String> customViewTextList = ReflectUtil.getCustomViewText(view, regex);
                for (String text : customViewTextList) {
                    if (UIUtil.textOfMatches(regex,text)) {
                        uniqueViewList.add(view);
                    }
                }
            }
        }
        return uniqueViewList;
    }

    /**
     * 强制的遍历所有的view，找到所有view里面可能的文本，并返回包含正则表达式匹配的文本的view
     *
     * 对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param regex 待匹配的文本
     * @param parent 待查找的父view，如过为null，则为当前的activity
     * @param onlyVisible 是否只寻找可见的
     * @param scrollableView 可用来滚动的，包含内容的view 如果为null的话，则从当前的view列表中找到一个可用的
     * @param direction 滚动的方向
     * @param timeout 超时时间
     * @return 查找到的匹配的view或者null
     */
    public ArrayList<View> forceSearchViewListByTextWithVerticallyScroll(String regex,
                                                                         View parent,
                                                                         boolean onlyVisible,
                                                                         View scrollableView,
                                                                         Scroller.VerticalDirection direction,
                                                                         long timeout) {
        ArrayList<View> viewList = new ArrayList<>();
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {

            ArrayList<View> newViewList = forceSearchViewListByText(regex,parent,onlyVisible);
            if (newViewList != null && !newViewList.isEmpty()) {
                viewList.addAll(newViewList);
            }

            if (!mScroller.scrollVertically(direction,scrollableView)) {
                break;
            }
        }
        Set<View> filteredViews = UIUtil.filterSameViews(viewList);
        return new ArrayList<>(filteredViews);
    }

    /**
     * 根据filter的条件查找到指定的类名的View
     *
     * 对于像listView或者其他可回收view的类型这个方法是没有意义的，因为此方法返回的只是一个瞬时的状态，
     * 里面很多的view都是重复的。因此这个方法只适合像scrollView这样不会回收的view才能返回准确的值
     * @param className   指定的类名
     * @param filter      过滤器
     * @param onlyVisible 是否可见
     * @return 符合条件的view
     */
    public ArrayList<View> searchViewListByFilter(String className, View parent, Filter filter,
                                                  boolean onlyVisible) {
        ArrayList<View> uniqueCustomViews = new ArrayList<>();
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
     *
     * @param className   指定类名
     * @param parent      父view
     * @param filter      过滤器
     * @param onlyVisible 是否只匹配可见的
     * @return 符合条件的view
     */
    public View searchViewByFilter(String className, View parent, Filter filter,
                                   boolean onlyVisible) {
        pause();
        ArrayList<View> currentViews = mViewGetter.getViewListByName(className, parent,
                true);
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
