package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;
import android.widget.ScrollView;

import com.llx278.uimocker2.Clicker;
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
        scroller.scrollView(scrollView,Scroller.DOWN);
        boolean down = solo.waitForTextAppear("^scrollText8$");
        Assert.assertTrue(down);
        scroller.scrollView(scrollView,Scroller.UP);
        boolean up = solo.waitForTextAppear("^scrollText1$");
        Assert.assertTrue(up);
        scroller.scrollViewAllTheWay(scrollView,Scroller.DOWN);
        boolean lastDown = solo.waitForTextAppear("^scrollText32$");
        Assert.assertTrue(lastDown);
        scroller.scrollViewAllTheWay(scrollView,Scroller.UP);
        boolean firstUp = solo.waitForTextAppear("^scrollText1$");
        Assert.assertTrue(firstUp);

        ListView listView = currentActivity.findViewById(R.id.container_4_list_view1);
        scroller.scrollList(listView,Scroller.DOWN,false);
        boolean isText11Appear = solo.waitForTextAppear("^text11$");
        Assert.assertTrue(isText11Appear);
        scroller.scrollList(listView,Scroller.UP,false);
        boolean isText0Appear = solo.waitForTextAppear("^text0$");
        Assert.assertTrue(isText0Appear);
        scroller.scrollList(listView,Scroller.DOWN,true);
        boolean isText19Appear = solo.waitForTextAppear("^text19$");
        Assert.assertTrue(isText19Appear);
        scroller.scrollList(listView,Scroller.UP,true);
        isText0Appear = solo.waitForTextAppear("^text0$");
        Assert.assertTrue(isText0Appear);
        scroller.scrollListToLine(listView,10);
        boolean isText10Appear = solo.waitForTextAppear("^text10$");
        Assert.assertTrue(isText10Appear);

        Log.d("main","done!");
    }

}
