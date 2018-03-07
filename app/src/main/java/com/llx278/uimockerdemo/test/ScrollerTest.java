package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.llx278.uimocker2.Scroller;
import com.llx278.uimocker2.Solo;
import com.llx278.uimockerdemo.R;

import junit.framework.Assert;

import java.lang.reflect.Field;

/**
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
        scroller.scrollViewVertically(scrollView, Scroller.VerticalDirection.DOWN);
        boolean down = solo.waitForTextAppear("^scrollText8$");
        Assert.assertTrue(down);
        scroller.scrollViewVertically(scrollView,Scroller.VerticalDirection.UP);
        boolean up = solo.waitForTextAppear("^scrollText1$");
        Assert.assertTrue(up);
        scroller.scrollViewVerticallyAllTheWay(scrollView,Scroller.VerticalDirection.DOWN);
        boolean lastDown = solo.waitForTextAppear("^scrollText32$");
        Assert.assertTrue(lastDown);
        scroller.scrollViewVerticallyAllTheWay(scrollView,Scroller.VerticalDirection.UP);
        boolean firstUp = solo.waitForTextAppear("^scrollText1$");
        Assert.assertTrue(firstUp);

        ListView listView = currentActivity.findViewById(R.id.container_4_list_view1);
        scroller.scrollListVertically(listView,Scroller.VerticalDirection.DOWN,false);
        boolean isText11Appear = solo.waitForTextAppear("^text11$");
        Assert.assertTrue(isText11Appear);
        scroller.scrollListVertically(listView,Scroller.VerticalDirection.UP,false);
        boolean isText0Appear = solo.waitForTextAppear("^text0$");
        Assert.assertTrue(isText0Appear);
        scroller.scrollListVertically(listView,Scroller.VerticalDirection.DOWN,true);
        boolean isText19Appear = solo.waitForTextAppear("^text19$");
        Assert.assertTrue(isText19Appear);
        scroller.scrollListVertically(listView,Scroller.VerticalDirection.UP,true);
        isText0Appear = solo.waitForTextAppear("^text0$");
        Assert.assertTrue(isText0Appear);
        scroller.scrollListVerticallyToLine(listView,10);
        boolean isText10Appear = solo.waitForTextAppear("^text10$");
        Assert.assertTrue(isText10Appear);

        HorizontalScrollView horizontalScrollView = currentActivity.findViewById(R.id.container_3_scrollview_2);
        boolean horizontallyRightScrollDone = scroller.scrollViewHorizontally(horizontalScrollView, Scroller.HorizontalDirection.RIGHT);
        Assert.assertTrue(horizontallyRightScrollDone);
        boolean isHbt6Appear = solo.waitForTextAppear("^hBt6$");
        Assert.assertTrue(isHbt6Appear);
        boolean horizontallyLeftScrollDone = scroller.scrollViewHorizontally(horizontalScrollView, Scroller.HorizontalDirection.LEFT);
        Assert.assertTrue(horizontallyLeftScrollDone);
        boolean isHbt1Appear = solo.waitForTextAppear("^hBt1$");
        Assert.assertTrue(isHbt1Appear);
        scroller.scrollViewHorizontallyAllTheWay(horizontalScrollView, Scroller.HorizontalDirection.RIGHT);
        boolean isHbt20Appear = solo.waitForTextAppear("^hBt20$");
        Assert.assertTrue(isHbt20Appear);
        scroller.scrollViewHorizontallyAllTheWay(horizontalScrollView, Scroller.HorizontalDirection.LEFT);
        boolean isHbt1AppearAgain = solo.waitForTextAppear("^hBt1$");
        Assert.assertTrue(isHbt1AppearAgain);

        Log.d("main","done!");
    }

}
