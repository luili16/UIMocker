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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获得指定条件的View
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
    private Sleeper mSleeper;
    
    public ViewGetter(InstrumentationDecorator instrumentation, Sleeper sleeper) {
        mInstrumentation = instrumentation;
        mSleeper = sleeper;
    }

    /**
     * 返回当前activity里面的所有可见的View，
     * @return 所有的view的列表
     */
    public ArrayList<View> getViews() {
        return getViews(null,true);
    }

    /**
     * 返回当前activity里面的所有view
     * @param onlySufficientlyVisible true 只返回在当前界面上可见的， false 返回所有
     * @return 所有view的列表
     */
    public ArrayList<View> getViews(boolean onlySufficientlyVisible) {
        return getViews(null,onlySufficientlyVisible);
    }

    /**
     * 找到当前parent里面的所有的view
     * @param parent 待输出的view，如果parent是null的话，则输出当前activity所有的里面的所有的view
     * @param onlySufficientlyVisible true 只返回在当前界面上可见的， false 返回所有
     * @return 返回所有的view
     */
    public ArrayList<View> getViews(View parent, boolean onlySufficientlyVisible) {
        final ArrayList<View> views = new ArrayList<>();
        final View parentToUse;
        if (parent == null){
            return getAllViews(onlySufficientlyVisible);
        } else {
            parentToUse = parent;
            views.add(parentToUse);
            if (parentToUse instanceof ViewGroup) {
                addChildren(views, (ViewGroup) parentToUse,onlySufficientlyVisible);
            }
        }
        return views;
    }

    /**
     * 找到当前activity里面的view，view的id与给定的id相等
     * @param id id
     * @return 所有符合条件的view
     */
    public ArrayList<View> getViewsById(int id) {
        return getViewsById(id,null,true);
    }

    /**
     * 找到指定parent里面的view，其id与给定的id相等
     * @param id id
     * @param parent 待查找的view
     * @return 所有符合条件的view
     */
    public ArrayList<View> getViewsById(int id,View parent) {
        return getViewsById(id,parent,true);
    }

    /**
     * 找到指定parent里面的view，其id与给定的id相等
     * @param id 待匹配的id
     * @param parent 待查找的view，如果为null，则默认整个activity里面的
     * @param onlySufficientlyVisible true 只返回在当前界面上可见的， false 返回所有
     * @return 符合id的view
     */
    public ArrayList<View> getViewsById(int id,View parent,boolean onlySufficientlyVisible) {

        ArrayList<View> views= getViews(parent,onlySufficientlyVisible);
        ArrayList<View> uniqueViews = new ArrayList<>();
        for (View v : views) {
            if(v != null && v.getId() == id) {
                uniqueViews.add(v);
            }
        }
        return uniqueViews;
    }



    /**
     * 找到当前activity中所有的所有class为T的view
     * @param classToFilterBy 指定的class
     * @return 符合条件的view
     */
    public <T extends View> ArrayList<T> getViewsByClass(Class<T> classToFilterBy) {
        return getViewsByClass(classToFilterBy,true,null,true);
    }

    /**
     * 找到当前activity里面的所有指定class为T的view，如果 includedSubClass为tru，则T的子类也一起返回
     * @param classToFilterBy 指定的class
     * @param includeSubclass 是否包含子类
     * @return 符合条件的view
     */
    public <T extends View> ArrayList<T> getViewsByClass(Class<T> classToFilterBy,boolean includeSubclass) {
        return getViewsByClass(classToFilterBy,includeSubclass,null,true);
    }

    /**
     * 返回指定parent View中符合classToFilterBy的View,如果parent为null，则返回当前activity中开始查找
     * @param classToFilterBy 指定的class
     * @param includeSubclass 指定的class
     * @param parent 用来查询的parent
     * @return 符合条件的view
     */
    public <T extends View> ArrayList<T> getViewsByClass(Class<T> classToFilterBy,boolean includeSubclass,View parent) {
        return getViewsByClass(classToFilterBy,includeSubclass,parent,true);
    }

    /**
     * 返回指定parent View中符合classToFilterBy的View,如果parent为null，则返回当前activity中开始查找
     * @param classToFilterBy 被过滤的view的class
     * @param includeSubclass 是否包含此class的子class
     * @param parent 用来查询的parent
     * @param <T>
     * @return 返回包含了过滤后的view的list
     */
    public <T extends View> ArrayList<T> getViewsByClass(Class<T> classToFilterBy,
                                                         boolean includeSubclass,View parent,boolean onlySufficientlyVisible) {
        ArrayList<T> filteredViews = new ArrayList<>();
        List<View> allViews = getViews(parent,onlySufficientlyVisible);
        for (View view : allViews) {
            if (view == null){
                continue;
            }
            Class<? extends View> classOfView = view.getClass();
            if (includeSubclass && classToFilterBy.isAssignableFrom(classOfView) ||
                    !includeSubclass && classToFilterBy == classOfView){
                filteredViews.add(classToFilterBy.cast(view));
            }
        }
        return filteredViews;
    }

    /**
     * 返回指定parentView中符合指定className的view，如果parent为null，则从当前activity中开始查找
     * @param classNameToFilterBy 被过滤的classname
     * @param parent 用来查询的parent
     * @return 返回了过滤后的view的list
     */
    public ArrayList<View> getViewsByName(String classNameToFilterBy, View parent,boolean onlySufficientlyVisible) {
        ArrayList<View> filteredViews = new ArrayList<>();
        List<View> allViews = getViews(parent,onlySufficientlyVisible);
        //XposedBridge.log("allVIews: " + allViews.toString());
        for (View v : allViews) {
            if (v == null){
                continue;
            }
            String className = v.getClass().getName();
            if (TextUtils.equals(className,classNameToFilterBy)) {
                filteredViews.add(v);
            }
        }
        return filteredViews;
    }

    /**
     * 获得最顶层的view
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
     * 如果存在的话，返回可以滚动的view
     * @param view 待查询的view
     * @return 可以滚动的父view(listView scrollView) 或者返回空
     */
    public View getScrollOrListParent(View view) {

        if (!(view instanceof android.widget.AbsListView) &&
                !(view instanceof ScrollView) && !(view instanceof WebView)) {
            try{
                return getScrollOrListParent((View) view.getParent());
            }catch(Exception e){
                return null;
            }
        } else {
            return view;
        }
    }

    /**
     * Tries to guess which view is the most likely to be interesting. Returns
     * the most recently drawn view, which presumably will be the one that the
     * user was most recently interacting with.
     *
     * @param views A list of potentially interesting views, likely a collection
     *            of views from a set of types, such as [{@link Button},
     *            {@link TextView}] or [{@link ScrollView}, {@link ListView}]
     * @return most recently drawn view, or null if no views were passed
     */
    public final <T extends View> T getFreshestView(ArrayList<T> views){
        final int[] locationOnScreen = new int[2];
        T viewToReturn = null;
        long drawingTime = 0;
        if(views == null){
            return null;
        }
        for(T view : views){
            if(view != null){
                view.getLocationOnScreen(locationOnScreen);

                if (locationOnScreen[0] < 0 || !(view.getHeight() > 0)){
                    continue;
                }

                if(view.getDrawingTime() > drawingTime){
                    drawingTime = view.getDrawingTime();
                    viewToReturn = view;
                }
                else if (view.getDrawingTime() == drawingTime){
                    if(view.isFocused()){
                        viewToReturn = view;
                    }
                }

            }
        }
        views = null;
        return viewToReturn;
    }

    public <T extends View> ViewGroup getRecyclerView(int recyclerViewIndex,int timeOut) {
        final long endTime = SystemClock.uptimeMillis() + timeOut;

        while (SystemClock.uptimeMillis() < endTime) {
            View recyclerView = getRecyclerView(true, recyclerViewIndex);
            if(recyclerView != null){
                return (ViewGroup) recyclerView;
            }
        }
        return null;
    }

    public View getRecyclerView(boolean shouldSleep,int recyclerViewIndex) {
        Set<View> uniqueViews = new HashSet<View>();
        if(shouldSleep){
            mSleeper.sleep();
        }

        @SuppressWarnings("unchecked")
        ArrayList<View> views = UIUtil.filterViewsToSet(new Class[]{ViewGroup.class},
                getAllViews(false));
        for (View view : views) {
            if(isViewType(view.getClass(), "widget.RecyclerView")){
                uniqueViews.add(view);
            }

            if(uniqueViews.size() > recyclerViewIndex) {
                return (ViewGroup) view;
            }
        }
        return null;
    }

    /**
     * Returns a Set of all RecyclerView or empty Set if none is found
     *
     *
     * @return a Set of RecyclerViews
     */

    public List<View> getScrollableSupportPackageViews(boolean shouldSleep){
        List <View> viewsToReturn = new ArrayList<View>();
        if(shouldSleep){
            mSleeper.sleep();
        }

        @SuppressWarnings("unchecked")
        ArrayList<View> views = UIUtil.filterViewsToSet(new Class[] {ViewGroup.class}, getAllViews(true));
        views = UIUtil.removeInvisibleViews(views);

        for(View view : views){

            if(isViewType(view.getClass(), "widget.RecyclerView") ||
                    isViewType(view.getClass(), "widget.NestedScrollView")){
                viewsToReturn.add(view);
            }

        }
        return viewsToReturn;
    }


    View getRecentDecorView(View[] views) {
        if(views == null)
            return null;

        final View[] decorViews = new View[views.length];
        int i = 0;
        View view;
        for (View view1 : views) {
            view = view1;
            if (isDecorView(view)) {
                decorViews[i] = view;
                i++;
            }
        }
        return getRecentContainer(decorViews);
    }

    @SuppressWarnings("unchecked")
    public View[] getWindowDecorViews()
    {

        Field viewsField;
        Field instanceField;
        try {
            viewsField = sWindowManager.getDeclaredField("mViews");
            String mWindowManagerString = "sDefaultWindowManager";
            instanceField = sWindowManager.getDeclaredField(mWindowManagerString);
            viewsField.setAccessible(true);
            instanceField.setAccessible(true);
            Object instance = instanceField.get(null);
            View[] result;
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                result = ((ArrayList<View>) viewsField.get(instance)).toArray(new View[0]);
            } else {
                result = (View[]) viewsField.get(instance);
            }
            return result;
        } catch (Exception e) {
            Logger.e("ViewGetter.getWindowDecorViews",e);
            return null;
        }
    }

    /**
     * 如果view完全显示在屏幕上面，则返回true
     * @param view 待检查的view
     * @return true view 完全显示在了屏幕上面
     */
    public final boolean isViewSufficientlyShown(View view){
        final int[] xyView = new int[2];
        final int[] xyParent = new int[2];

        if(view == null) {
            return false;
        }

        final float viewHeight = view.getHeight();
        final View parent = getScrollOrListParent(view);
        view.getLocationOnScreen(xyView);

        if(parent == null){
            xyParent[1] = 0;
        } else{
            parent.getLocationOnScreen(xyParent);
        }

        if(xyView[1] + (viewHeight/2.0f) > getScrollListWindowHeight(view)) {
            return false;
        } else if(xyView[1] + (viewHeight/2.0f) < xyParent[1]) {
            return false;
        }

        return true;
    }

    public float getScrollListWindowHeight(View view) {
        final int[] xyParent = new int[2];
        View parent = getScrollOrListParent(view);
        final float windowHeight;

        if(parent == null){
            WindowManager windowManager = (WindowManager)
                    mInstrumentation.getContext().getSystemService(Context.WINDOW_SERVICE);
            Point outSize = new Point();
            if (windowManager == null) {
                throw new RuntimeException("get windowManager failed!");
            }
            Display defaultDisplay = windowManager.getDefaultDisplay();
            defaultDisplay.getSize(outSize);
            windowHeight = outSize.y;
        } else{
            parent.getLocationOnScreen(xyParent);
            windowHeight = xyParent[1] + parent.getHeight();
        }
        parent = null;
        return windowHeight;
    }

    public View getIdenticalView(View view) {
        if(view == null){
            return null;
        }
        View viewToReturn = null;
        List<? extends View> visibleViews = UIUtil.removeInvisibleViews(getViewsByClass(view.getClass(),
                true,null));

        for(View v : visibleViews){
            if(areViewsIdentical(v, view)){
                viewToReturn = v;
                break;
            }
        }

        return viewToReturn;
    }

   // ---- private methods -------

    /**
     * 获得所有的view
     * @param onlySufficientlyVisible 是否只要完全显示在屏幕上的view
     * @return 返回DecorView中所包含的所有View
     */
    private ArrayList<View> getAllViews(boolean onlySufficientlyVisible) {
        final View[] views = getWindowDecorViews();
        final ArrayList<View> allViews = new ArrayList<View>();
        final View[] nonDecorViews = getNonDecorViews(views);
        View view = null;
        if(nonDecorViews != null){
            for (View nonDecorView : nonDecorViews) {
                view = nonDecorView;
                try {
                    addChildren(allViews, (ViewGroup) view, onlySufficientlyVisible);
                } catch (Exception ignored) {
                }
                if (view != null) allViews.add(view);
            }
        }

        if (views != null && views.length > 0) {
            view = getRecentDecorView(views);

            try {
                addChildren(allViews, (ViewGroup)view, onlySufficientlyVisible);
            } catch (Exception ignored) {
            }

            if(view != null) allViews.add(view);
        }

        return allViews;
    }

    private View[] getNonDecorViews(View[] views) {
        View[] nonDecorViews = null;

        if(views != null) {
            nonDecorViews = new View[views.length];

            int i = 0;
            View view;

            for (int j = 0; j < views.length; j++) {
                view = views[j];
                if (!isDecorView(view)) {
                    nonDecorViews[i] = view;
                    i++;
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
                nameOfClass.equals("com.android.internal.policy.PhoneWindow$DecorView"));
    }

    private void addChildren(ArrayList<View> views, ViewGroup viewGroup, boolean onlySufficientlyVisible) {
        if(viewGroup != null){
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                final View child = viewGroup.getChildAt(i);
                if(onlySufficientlyVisible && isViewSufficientlyShown(child)) {
                    views.add(child);
                }

                else if(!onlySufficientlyVisible && child != null) {
                    views.add(child);
                }

                if (child instanceof ViewGroup) {
                    addChildren(views, (ViewGroup) child, onlySufficientlyVisible);
                }
            }
        }
    }

    private View getRecentContainer(View[] views) {
        View container = null;
        long drawingTime = 0;
        View view;

        for(int i = 0; i < views.length; i++){
            view = views[i];
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
     * @param firstView the first view
     * @param secondView the second view
     * @return true if views are equal
     */

    private boolean areViewsIdentical(View firstView, View secondView){
        if(firstView.getId() != secondView.getId() ||
                !firstView.getClass().isAssignableFrom(secondView.getClass())){
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
