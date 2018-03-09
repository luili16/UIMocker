package com.llx278.uimocker2;

import android.content.Context;
import android.graphics.Point;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获得指定条件的View
 *
 * @author llx
 */

public class ViewGetter {

    private static Class<?> sWindowManager;

    static {
        try {
            // api >= 19
            sWindowManager = Class.forName("android.view.WindowManagerGlobal");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private InstrumentationDecorator mInstrumentation;

    public ViewGetter(InstrumentationDecorator instrumentation) {
        mInstrumentation = instrumentation;
    }

    /**
     * 返回当前activity里面的所有可见的View
     *
     * @return 所有的view的列表
     */
    public List<View> getViewList() {
        return getViewList(null, true);
    }

    /**
     * 返回当前activity里面的所有view
     *
     * @param onlySufficientlyVisible true 只返回在当前界面上可见的， false 返回所有
     * @return 所有view的列表
     */
    public List<View> getViewList(boolean onlySufficientlyVisible) {
        return getViewList(null, onlySufficientlyVisible);
    }

    /**
     * 找到当前parent里面的所有的view
     *
     * @param parent 待输出的view，如果parent是null的话，则输出当前activity所有的里面的所有的view
     * @param onlySufficientlyVisible true 只返回在当前window上可显示的（注意，如果当前屏幕上的view的状态是
     *                                visible或是gone的话，也认为是显示的）， false 返回所有（包括超出可见范围的view）
     * @return 返回所有的view
     */
    public List<View> getViewList(View parent, boolean onlySufficientlyVisible) {
        final ArrayList<View> viewList = new ArrayList<>();
        final View parentToUse;
        if (parent == null) {
            return getAllViews(onlySufficientlyVisible);
        } else {
            parentToUse = parent;
            viewList.add(parentToUse);
            if (parentToUse instanceof ViewGroup) {
                addChildren(viewList, (ViewGroup) parentToUse, onlySufficientlyVisible);
            }
        }
        return viewList;
    }

    /**
     * 找到当前activity里面的view，view的id与给定的id相等
     *
     * @param id id
     * @return 所有符合条件的view
     */
    public ArrayList<View> getViewListById(int id) {
        return getViewListById(id, null, true);
    }

    /**
     * 找到指定parent里面的view，其id与给定的id相等
     *
     * @param id     id
     * @param parent 待查找的view
     * @return 所有符合条件的view
     */
    public ArrayList<View> getViewListById(int id, View parent) {
        return getViewListById(id, parent, true);
    }

    /**
     * 找到指定parent里面的view，其id与给定的id相等
     *
     * @param id                      待匹配的id
     * @param parent                  待查找的view，如果为null，则默认整个activity里面的
     * @param onlySufficientlyVisible true 只返回在当前界面上可见的， false 返回所有
     * @return 符合id的view
     */
    public ArrayList<View> getViewListById(int id, View parent, boolean onlySufficientlyVisible) {

        List<View> views = getViewList(parent, onlySufficientlyVisible);
        ArrayList<View> uniqueViews = new ArrayList<>();
        for (View v : views) {
            if (v != null && v.getId() == id) {
                uniqueViews.add(v);
            }
        }
        return uniqueViews;
    }

    /**
     * 找到当前activity中所有的所有class为T的view
     *
     * @param classToFilterBy 指定的class
     * @return 符合条件的view
     */
    public <T extends View> ArrayList<T> getViewListByClass(Class<T> classToFilterBy) {
        return getViewListByClass(classToFilterBy, true, null, true);
    }

    /**
     * 找到当前activity里面的所有指定class为T的view，如果 includedSubClass为true，则T的子类也一起返回
     *
     * @param classToFilterBy 指定的class
     * @param includeSubclass 是否包含子类
     * @return 符合条件的view
     */
    public <T extends View> ArrayList<T> getViewListByClass(Class<T> classToFilterBy, boolean includeSubclass) {
        return getViewListByClass(classToFilterBy, includeSubclass, null, true);
    }

    /**
     * 返回指定parent View中符合classToFilterBy的View,如果parent为null，则返回当前activity中开始查找
     *
     * @param classToFilterBy 指定的class
     * @param includeSubclass 指定的class
     * @param parent          用来查询的parent
     * @return 符合条件的view
     */
    public <T extends View> ArrayList<T> getViewListByClass(Class<T> classToFilterBy,
                                                            boolean includeSubclass,
                                                            View parent) {
        return getViewListByClass(classToFilterBy, includeSubclass, parent, true);
    }

    /**
     * 返回指定parent View中符合classToFilterBy的View,如果parent为null，则返回当前activity中开始查找
     *
     * @param classToFilterBy 被过滤的view的class
     * @param includeSubclass 是否包含此class的子class
     * @param parent          用来查询的parent
     * @param <T>
     * @return 返回包含了过滤后的view的list
     */
    public <T extends View> ArrayList<T> getViewListByClass(Class<T> classToFilterBy,
                                                            boolean includeSubclass,
                                                            View parent,
                                                            boolean onlySufficientlyVisible) {
        ArrayList<T> filteredViews = new ArrayList<>();
        List<View> allViews = getViewList(parent, onlySufficientlyVisible);
        for (View view : allViews) {
            if (view == null) {
                continue;
            }
            Class<? extends View> classOfView = view.getClass();
            if (includeSubclass && classToFilterBy.isAssignableFrom(classOfView) ||
                    !includeSubclass && classToFilterBy == classOfView) {
                filteredViews.add(classToFilterBy.cast(view));
            }
        }
        return filteredViews;
    }

    /**
     * 返回指定parentView中符合指定className的view，如果parent为null，则从当前activity中开始查找
     *
     * @param classNameToFilterBy 被过滤的classname
     * @param parent              用来查询的parent
     * @return 返回了过滤后的view的list
     */
    public ArrayList<View> getViewListByName(String classNameToFilterBy, View parent,
                                             boolean onlySufficientlyVisible) {
        ArrayList<View> filteredViews = new ArrayList<>();
        List<View> allViews = getViewList(parent, onlySufficientlyVisible);
        for (View v : allViews) {
            if (v == null) {
                continue;
            }
            String className = v.getClass().getName();
            if (TextUtils.equals(className, classNameToFilterBy)) {
                filteredViews.add(v);
            }
        }
        return filteredViews;
    }

    /**
     * 获得最顶层的view
     *
     * @param view 带获取的view
     * @return 最顶层的view
     */
    public View getTopParent(View view) {
        final ViewParent viewParent = view.getParent();
        if (viewParent != null
                && viewParent instanceof View) {
            return getTopParent((View) viewParent);
        } else {
            return view;
        }
    }

    /**
     * 如果存在的话，返回当前view的父view中的可以滚动的view
     *
     * @param view 待查询的view
     * @return 可以滚动的父view(listView scrollViewVertically) 或者返回空
     */
    public View getScrollParent(View view) {

        if (!(view instanceof android.widget.AbsListView) &&
                !(view instanceof ScrollView) &&
                !(view instanceof WebView) &&
                !(view instanceof HorizontalScrollView) &&
                !isRecyclerView(view)) {
            try {
                return getScrollParent((View) view.getParent());
            } catch (Exception e) {
                return null;
            }
        } else {
            return view;
        }
    }

    /**
     * 最近被绘制（drawing）指的是这个view第一次被attach到window上的时候由AttachInfo所指定的，
     * 具体参考view的源码
     * @param viewList 指定的view的列表
     * @return 返回指定的views中最近被绘制的view，如果viewList为空，则返回null
     */
    public final <T extends View> T getFreshestView(List<T> viewList) {

        if (viewList == null || viewList.isEmpty()) {
            return null;
        }

        T viewToReturn = null;
        long drawingTime = 0;

        for (T view : viewList) {
            if (view != null) {
                if (isViewSufficientlyShown(view)) {
                    if (view.getDrawingTime() > drawingTime) {
                        drawingTime = view.getDrawingTime();
                        viewToReturn = view;
                    } else if (view.getDrawingTime() == drawingTime) {
                        if (view.isFocused()) {
                            viewToReturn = view;
                        }
                    }
                }
            }
        }
        return viewToReturn;
    }

    /**
     * Returns a Set of all RecyclerView or empty Set if none is found
     *
     * @return a Set of RecyclerViews
     */

    public List<View> getScrollableSupportPackageViews() {
        List<View> viewsToReturn = new ArrayList<View>();
        List<Class<? extends ViewGroup>> myList = new ArrayList<>();
        myList.add(ViewGroup.class);
        ArrayList<View> views = UIUtil.filterViewsToSet(myList, getAllViews(true));
        List<View> allVisibleViews = UIUtil.removeInvisibleViews(views);

        for (View view : allVisibleViews) {

            if (isViewType(view.getClass(), "widget.RecyclerView") ||
                    isViewType(view.getClass(), "widget.NestedScrollView")) {
                viewsToReturn.add(view);
            }

        }
        return viewsToReturn;
    }

    @SuppressWarnings("unchecked")
    public List<View> getWindowViews() {

        Field viewsField;
        Field instanceField;
        try {
            viewsField = sWindowManager.getDeclaredField("mViews");
            String mWindowManagerString = "sDefaultWindowManager";
            instanceField = sWindowManager.getDeclaredField(mWindowManagerString);
            viewsField.setAccessible(true);
            instanceField.setAccessible(true);
            Object instance = instanceField.get(null);
            return Collections.unmodifiableList((ArrayList<View>) viewsField.get(instance));
        } catch (Exception e) {
            Logger.e("ViewGetter.getWindowViews", e);
            return null;
        }
    }

    /**
     * 判断一个view是否完全显示在当前的窗体（window）上
     *
     * @param view 待判断的view
     * @return true view 完全显示在了屏幕上面 false 没有
     */
    public final boolean isViewSufficientlyShown(View view) {

        if (view == null) {
            return false;
        }

        // view窗体（window）上的位置
        final int[] xyView = new int[2];
        // 如果某个父view是可滚动的，那么xyParent代表的就是那个
        // 可以滚动的父view在窗体（window）上的位置
        final int[] xyParent = new int[2];

        view.getLocationInWindow(xyView);
        final float viewHeight = view.getHeight();
        final float viewWidth = view.getWidth();
        final View parent = getScrollParent(view);

        // view在窗体（window）上的位置
        float yCenter = xyView[1] + (viewHeight / 2.0f);
        float xCenter = xyView[0] + (viewWidth / 2.0f);
        // 判断一个view是否显示在窗体（window）上分这两种情况
        // 1.如果这个view的父view没有可滚动的view(例如 listview scrollview)，那么
        //   这个View是否显示窗体（window）上由它在窗体（window）的位置决定，如果它在窗体（window）的可见范围内，
        //   那么这个view就是可见的，如果超出的窗体（window）的可见范围(以view的中心为界限)，那么它就是不可见的
        // 2.如果这个view的父view有可滚动的view(例如 listview，scrollview)，那么如果这个view
        //   在可滚动的view可见范围里，那么它就是可见的，如果超出了，那么它就是不可见的(以view的中心为界限)

        // 父view没有可滚动的view
        if (parent == null) {
            // 获得窗体（window）的宽和高
            float windowWidth;
            float windowHeight;

            WindowManager windowManager = (WindowManager)
                    mInstrumentation.getContext().getSystemService(Context.WINDOW_SERVICE);
            Point outSize = new Point();
            if (windowManager == null) {
                throw new RuntimeException("get windowManager failed!");
            }
            Display defaultDisplay = windowManager.getDefaultDisplay();

            defaultDisplay.getSize(outSize);
            windowWidth = outSize.x;
            windowHeight = outSize.y;

            boolean isSufficientlyShownInX = xCenter > 0 && xCenter < windowWidth;
            boolean isSufficientlyShownInY = yCenter > 0 && yCenter < windowHeight;

            return isSufficientlyShownInX && isSufficientlyShownInY;

            // 找到了一个父view是可滚动的
        } else {
            // 获得这个父View在窗体（window）上的位置
            parent.getLocationInWindow(xyParent);
            float leftInWindow = xyParent[0];
            float topInWindow = xyParent[1];
            float rightInWindow = xyParent[0] + parent.getWidth();
            float bottomInWindow = xyParent[1] + parent.getHeight();

            boolean isSufficientlyShownInX = (xCenter > leftInWindow && xCenter < rightInWindow);
            boolean isSufficientlyShownInY = (yCenter > topInWindow && yCenter < bottomInWindow);

            return isSufficientlyShownInX && isSufficientlyShownInY;
        }
    }

    public View getIdenticalView(View view) {
        if (view == null) {
            return null;
        }
        View viewToReturn = null;
        List<? extends View> visibleViews = UIUtil.removeInvisibleViews(getViewListByClass(view.getClass(),
                true, null));

        for (View v : visibleViews) {
            if (areViewsIdentical(v, view)) {
                viewToReturn = v;
                break;
            }
        }
        return viewToReturn;
    }

    public View getRecentDecorView(List<View> views) {
        if (views == null) {
            return null;
        }
        final ArrayList<View> decorViews = new ArrayList<>();
        for (View view : views) {
            if (isDecorView(view)) {
                decorViews.add(view);
            }
        }
        return getRecentContainer(decorViews);
    }

    // ---- private methods -------



    private boolean isRecyclerView(View view) {
        return isViewType(view.getClass(),"widget.RecyclerView");
    }

    /**
     * 获得所有的view
     *
     * @param onlySufficientlyVisible 是否只要完全显示在屏幕上的view
     * @return 返回DecorView中所包含的所有View
     */
    private List<View> getAllViews(boolean onlySufficientlyVisible) {
        final List<View> views = getWindowViews();
        final ArrayList<View> allViews = new ArrayList<View>();
        final List<View> nonDecorViews = getNonDecorViews(views);
        View view = null;
        if (nonDecorViews != null) {
            for (View nonDecorView : nonDecorViews) {
                view = nonDecorView;
                try {
                    addChildren(allViews, (ViewGroup) view, onlySufficientlyVisible);
                } catch (Exception ignored) {
                }
                if (view != null){ allViews.add(view);}
            }
        }

        if (views != null && !views.isEmpty()) {
            view = getRecentDecorView(views);
            try {
                addChildren(allViews, (ViewGroup) view, onlySufficientlyVisible);
            } catch (Exception ignored) {
            }

            if (view != null) {
                allViews.add(view);
            }
        }

        return allViews;
    }

    private List<View> getNonDecorViews(List<View> views) {
        List<View> nonDecorViews = null;

        if (views != null) {
            nonDecorViews = new ArrayList<>();
            for (View view : views) {
                if (!isDecorView(view)) {
                    nonDecorViews.add(view);
                }
            }
        }
        return nonDecorViews;
    }

    private boolean isDecorView(View view) {
        if (view == null) {
            return false;
        }

        final String nameOfClass = view.getClass().getName();
        return (nameOfClass.equals("com.android.internal.policy.impl.PhoneWindow$DecorView") ||
                nameOfClass.equals("com.android.internal.policy.impl.MultiPhoneWindow$MultiPhoneDecorView") ||
                nameOfClass.equals("com.android.internal.policy.PhoneWindow$DecorView")) ||
                nameOfClass.equals("com.android.internal.policy.DecorView");
    }

    private void addChildren(ArrayList<View> views, ViewGroup viewGroup, boolean onlySufficientlyVisible) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                final View child = viewGroup.getChildAt(i);
                if (onlySufficientlyVisible && isViewSufficientlyShown(child)) {
                    views.add(child);
                } else if (!onlySufficientlyVisible && child != null) {
                    views.add(child);
                }

                if (child instanceof ViewGroup) {
                    addChildren(views, (ViewGroup) child, onlySufficientlyVisible);
                }
            }
        }
    }

    private View getRecentContainer(List<View> views) {
        View container = null;
        long drawingTime = 0;
        View view;

        for (int i = 0; i < views.size(); i++) {
            view = views.get(i);
            if (view != null && view.isShown() && view.hasWindowFocus() &&
                    view.getDrawingTime() > drawingTime) {
                container = view;
                drawingTime = view.getDrawingTime();
            }
        }
        return container;
    }

    private boolean isViewType(Class<?> aClass, String typeName) {
        return aClass.getName().contains(typeName) ||
                aClass.getSuperclass() != null &&
                        isViewType(aClass.getSuperclass(), typeName);
    }

    /**
     * Compares if the specified views are identical. This is used instead of View.compare
     * as it always returns false in cases where the View tree is refreshed.
     *
     * @param firstView  the first view
     * @param secondView the second view
     * @return true if views are equal
     */

    private boolean areViewsIdentical(View firstView, View secondView) {
        if (firstView.getId() != secondView.getId() ||
                !firstView.getClass().isAssignableFrom(secondView.getClass())) {
            return false;
        }

        if (firstView.getParent() != null && firstView.getParent() instanceof View &&
                secondView.getParent() != null && secondView.getParent() instanceof View) {

            return areViewsIdentical((View) firstView.getParent(), (View) secondView.getParent());
        } else {
            return true;
        }
    }


}
