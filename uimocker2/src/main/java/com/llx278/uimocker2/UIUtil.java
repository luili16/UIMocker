package com.llx278.uimocker2;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * Created by llx on 02/01/2018.
 */

public class UIUtil {

    /**
     * 删除所有不可见的view
     *
     * @param viewList 可遍历的集合
     * @return 返回一个过滤后的包含所有可见的view的列表
     */

    public static <T extends View> ArrayList<T> removeInvisibleViews(Iterable<T> viewList) {
        ArrayList<T> tmpViewList = new ArrayList<T>();
        for (T view : viewList) {
            if (view != null && view.isShown()) {
                tmpViewList.add(view);
            }
        }
        return tmpViewList;
    }

    /**
     * 用指定的class<T>去过滤所有的view
     *
     * @param classToFilterBy 指定的Class
     * @param viewList 用来过滤的集合
     * @return 过滤以后的view的列表
     */

    public static <T> ArrayList<T> filterViews(Class<T> classToFilterBy, Iterable<?> viewList) {
        ArrayList<T> filteredViews = new ArrayList<T>();
        for (Object view : viewList) {
            if (view != null && classToFilterBy.isAssignableFrom(view.getClass())) {
                filteredViews.add(classToFilterBy.cast(view));
            }
        }
        return filteredViews;
    }

    /**
     * 过滤掉所有不在classSet中的view
     *
     * @param classSet 待过滤的class集合
     * @param viewList 带过滤的view的集合
     * @return 过滤包含class集合里的所有的view
     */

    public  static ArrayList<View> filterViewsToSet(List<Class<? extends ViewGroup>> classSet, Iterable<View> viewList) {
        ArrayList<View> filteredViews = new ArrayList<>();
        for (View view : viewList) {
            if (view == null)
                continue;
            for (Class<?> filter : classSet) {
                if (filter.isAssignableFrom(view.getClass())) {
                    filteredViews.add(view);
                    break;
                }
            }
        }
        return filteredViews;
    }

    /**
     * 根据view在屏幕上的位置来重新对viewList进行排序
     *
     * @param views 待排序的view
     * @see ViewLocationComparator
     */

    public static void sortViewsByLocationOnScreen(List<? extends View> views) {
        Collections.sort(views, new ViewLocationComparator());
    }

    /**
     * 根据view在屏幕上的位置来重新对viewList进行排序
     *
     * @param views 待排序的view
     * @param yAxisFirst 是否先从y坐标轴开始比较
     * @see ViewLocationComparator
     */

    public static void sortViewsByLocationOnScreen(List<? extends View> views, boolean yAxisFirst) {
        Collections.sort(views, new ViewLocationComparator(yAxisFirst));
    }

    /**
     * 检查是否一个TextView的文本包含指定的regex
     *
     * @param regex 待匹配的正则表达式
     * @param view 待匹配的TextView
     * @return true 匹配成功 false 匹配失败
     */

    public static boolean textViewOfMatches(String regex, TextView view){

        Pattern pattern = null;
        try{
            pattern = Pattern.compile(regex);
        }catch(PatternSyntaxException e){
            pattern = Pattern.compile(regex, Pattern.LITERAL);
        }
        String textStr = "";
        if (view.getText() != null){
            textStr = view.getText().toString();
        }
        Matcher matcher = pattern.matcher(textStr);

        if (matcher.find()){
            return true;
        }

        if (view.getError() != null){
            matcher = pattern.matcher(view.getError().toString());
            if (matcher.find()){
                return true;
            }
        }

        if (textStr.equals("") && view.getHint() != null){
            matcher = pattern.matcher(view.getHint().toString());
            if (matcher.find()){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查指定的text是否符合指定的regex
     * @param regex 待匹配的正则表达式
     * @param text 待匹配的文本
     * @return true 匹配成功 false 匹配失败
     */
    public static boolean textOfMatches(String regex,String text) {
        Pattern pattern = null;
        try{
            pattern = Pattern.compile(regex);
        }catch(PatternSyntaxException e){
            pattern = Pattern.compile(regex, Pattern.LITERAL);
        }
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * 过滤掉一个集和中相同的view
     * @param views 待过滤的view
     * @param <T> View
     * @return 不重复的view的集合
     */
    public static <T extends View> Set<T> filterSameViews(Iterable<T> views){
        Set<T> uniqueViewSet = new HashSet<>();
        for (T v : views) {
            uniqueViewSet.add(v);
        }
        return uniqueViewSet;
    }

    /**
     * 根据regex匹配views里面所有符合条件的vie
     *
     * @param views 待匹配额view的集合
     * @param regex 待匹配的正则表达式
     * @return 返回一个符合正则表达式匹配的view的集合
     */

    public static <T extends TextView> List<T> filterViewsByText(Iterable<T> views, String regex) {
        return filterViewsByText(views, Pattern.compile(regex));
    }

    private static <T extends TextView> List<T> filterViewsByText(Iterable<T> views, Pattern regex) {
        final ArrayList<T> filteredViews = new ArrayList<T>();
        for (T view : views) {
            if (view != null && regex.matcher(view.getText()).matches()) {
                filteredViews.add(view);
            }
        }
        return filteredViews;
    }
}
