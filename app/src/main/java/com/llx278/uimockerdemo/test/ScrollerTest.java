package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.llx278.uimocker2.Scroller;
import com.llx278.uimocker2.Solo;
import com.llx278.uimockerdemo.R;

import junit.framework.Assert;

import java.lang.reflect.Field;

/**
 *
 * Created by liu on 18-3-7.
 */

public class ScrollerTest {

    public void run(Solo solo) throws Exception {

        Log.d("main","进入ScrollerTest");
        Thread.sleep(2000);
        Class<? extends Solo> aClass = solo.getClass();
        Field mScroller = aClass.getDeclaredField("mScroller");
        mScroller.setAccessible(true);
        Scroller scroller = (Scroller) mScroller.get(solo);
        Activity currentActivity = solo.getCurrentActivity();

        ScrollView scrollView = currentActivity.findViewById(R.id.container_3_scrollview_1);
        boolean b2 = scroller.scrollViewVertically(scrollView, Scroller.VerticalDirection.DOWN_TO_UP);
        Assert.assertTrue(b2);
        boolean down = solo.waitForTextAppear("^scrollText8$");
        Assert.assertTrue(down);
        boolean b3 = scroller.scrollViewVertically(scrollView, Scroller.VerticalDirection.UP_TO_DOWN);
        Assert.assertTrue(b3);
        boolean up = solo.waitForTextAppear("^scrollText1$");
        Assert.assertTrue(up);
        scroller.scrollViewVerticallyAllTheWay(scrollView,Scroller.VerticalDirection.DOWN_TO_UP);
        boolean lastDown = solo.waitForTextAppear("^scrollText32$");
        Assert.assertTrue(lastDown);
        scroller.scrollViewVerticallyAllTheWay(scrollView,Scroller.VerticalDirection.UP_TO_DOWN);
        boolean firstUp = solo.waitForTextAppear("^scrollText1$");
        Assert.assertTrue(firstUp);

        ListView listView = currentActivity.findViewById(R.id.container_4_list_view1);
        boolean b = scroller.scrollListVertically(listView, Scroller.VerticalDirection.DOWN_TO_UP, false);
        Assert.assertTrue(b);
        boolean isText11Appear = solo.waitForTextAppear("^text11$");
        Assert.assertTrue(isText11Appear);
        boolean b1 = scroller.scrollListVertically(listView, Scroller.VerticalDirection.UP_TO_DOWN, false);
        Assert.assertTrue(b1);
        boolean isText0Appear = solo.waitForTextAppear("^text0$");
        Assert.assertTrue(isText0Appear);
        scroller.scrollListVertically(listView,Scroller.VerticalDirection.DOWN_TO_UP,true);
        boolean isText19Appear = solo.waitForTextAppear("^text19$");
        Assert.assertTrue(isText19Appear);
        scroller.scrollListVertically(listView,Scroller.VerticalDirection.UP_TO_DOWN,true);
        isText0Appear = solo.waitForTextAppear("^text0$");
        Assert.assertTrue(isText0Appear);
        scroller.scrollListVerticallyToLine(listView,10);
        boolean isText10Appear = solo.waitForTextAppear("^text10$");
        Assert.assertTrue(isText10Appear);
        scroller.scrollListVerticallyToLine(listView,0);
        isText0Appear = solo.waitForTextAppear("^text0$");
        Assert.assertTrue(isText0Appear);

        HorizontalScrollView horizontalScrollView = currentActivity.findViewById(R.id.container_3_scrollview_2);
        boolean horizontallyRightScrollDone = scroller.scrollViewHorizontally(horizontalScrollView, Scroller.HorizontalDirection.RIGHT_TO_LEFT);
        Assert.assertTrue(horizontallyRightScrollDone);
        boolean isHbt6Appear = solo.waitForTextAppear("^hBt6$");
        Assert.assertTrue(isHbt6Appear);
        boolean horizontallyLeftScrollDone = scroller.scrollViewHorizontally(horizontalScrollView, Scroller.HorizontalDirection.LEFT_TO_RIGHT);
        Assert.assertTrue(horizontallyLeftScrollDone);
        boolean isHbt1Appear = solo.waitForTextAppear("^hBt1$");
        Assert.assertTrue(isHbt1Appear);
        scroller.scrollViewHorizontallyAllTheWay(horizontalScrollView, Scroller.HorizontalDirection.RIGHT_TO_LEFT);
        boolean isHbt20Appear = solo.waitForTextAppear("^hBt20$");
        Assert.assertTrue(isHbt20Appear);
        scroller.scrollViewHorizontallyAllTheWay(horizontalScrollView, Scroller.HorizontalDirection.LEFT_TO_RIGHT);
        boolean isHbt1AppearAgain = solo.waitForTextAppear("^hBt1$");
        Assert.assertTrue(isHbt1AppearAgain);


        //ScrollView scrollView = currentActivity.findViewById(R.id.container_3_scrollview_1);
        //ListView listView = currentActivity.findViewById(R.id.container_4_list_view1);
        //HorizontalScrollView horizontalScrollView = currentActivity.findViewById(R.id.container_3_scrollview_2);
        scroller.forceScrollViewVertically(scrollView, Scroller.VerticalDirection.DOWN_TO_UP);
        boolean isScrollText8Appear = solo.waitForTextAppear("^scrollText8$");
        Assert.assertTrue(isScrollText8Appear);
        scroller.forceScrollViewVertically(scrollView, Scroller.VerticalDirection.UP_TO_DOWN);
        boolean isScrollText1Appear= solo.waitForTextAppear("^scrollText1$");
        Assert.assertTrue(isScrollText1Appear);

        scroller.forceScrollViewVertically(listView, Scroller.VerticalDirection.DOWN_TO_UP);
        boolean isText12Appear = solo.waitForTextAppear("^text12$");
        Assert.assertTrue(isText12Appear);
        scroller.forceScrollViewVertically(listView,Scroller.VerticalDirection.UP_TO_DOWN);
        boolean isText0Appear1 = solo.waitForTextAppear("^text0$");
        Assert.assertTrue(isText0Appear1);
        scroller.forceScrollViewHorizontally(horizontalScrollView, Scroller.HorizontalDirection.RIGHT_TO_LEFT);
        boolean isHBt7Appear = solo.waitForTextAppear("^hBt7$");
        Assert.assertTrue(isHBt7Appear);
        scroller.forceScrollViewHorizontally(horizontalScrollView,Scroller.HorizontalDirection.LEFT_TO_RIGHT);
        boolean isHBt1Appear = solo.waitForTextAppear("^hBt1$");
        Assert.assertTrue(isHBt1Appear);

        ViewGroup recyclerView = currentActivity.findViewById(R.id.container_4_recycler_view4);
        scroller.forceScrollViewVertically(recyclerView, Scroller.VerticalDirection.DOWN_TO_UP);
        boolean isRecycler14Appear = solo.waitForTextAppear("^recycler14$");
        Assert.assertTrue(isRecycler14Appear);
        scroller.forceScrollViewVertically(recyclerView, Scroller.VerticalDirection.UP_TO_DOWN);
        boolean isRecycler0Appear = solo.waitForTextAppear("^recycler0$");
        Assert.assertTrue(isRecycler0Appear);

        RecyclerView hRecyclerView = currentActivity.findViewById(R.id.container_5_1_recycler_view);
        scroller.scrollViewHorizontally(hRecyclerView, Scroller.HorizontalDirection.RIGHT_TO_LEFT);
        boolean isHRecycler = solo.waitForTextAppear("^hRecycler6");
        Assert.assertTrue(isHRecycler);
        scroller.scrollViewHorizontally(hRecyclerView, Scroller.HorizontalDirection.LEFT_TO_RIGHT);
        boolean isHRecycler0 = solo.waitForTextAppear("^hRecycler0");
        Assert.assertTrue(isHRecycler0);

        Log.d("main","done!");
        Thread.sleep(200);
        currentActivity.finish();
    }
}
