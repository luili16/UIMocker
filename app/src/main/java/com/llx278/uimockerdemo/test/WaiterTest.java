package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.Waiter;
import com.llx278.uimockerdemo.R;

import junit.framework.Assert;

import java.lang.reflect.Field;

/**
 * Created by llx on 2018/3/9.
 */

public class WaiterTest {

    public void run(Solo solo) throws Exception {

        Log.d("main","进入WaiterTest");
        Thread.sleep(2000);

        Class<? extends Solo> aClass = solo.getClass();
        Field mWaiter = aClass.getDeclaredField("mWaiter");
        mWaiter.setAccessible(true);
        Waiter waiter = (Waiter) mWaiter.get(solo);
        Activity currentActivity = solo.getCurrentActivity();

        boolean b = waiter.waitForTextAppear("^mytext1$", 2000);
        Assert.assertTrue(b);
        ScrollView scrollView = currentActivity.findViewById(R.id.container_4_scroll_view2);
        boolean b1 = waiter.waitForTextAppearWithVerticallyScroll("^hello Scroll18$",2000,scrollView);
        Assert.assertTrue(b1);
        boolean b2 = waiter.waitForTextAppearWithVerticallyScroll("^hello_Scroll18$",10 * 1000,scrollView);
        Assert.assertFalse(b2);
        boolean b3 = waiter.waitForTextViewAppear("^scrollText1", 2000);
        Assert.assertTrue(b3);
        ScrollView scrollView1 = currentActivity.findViewById(R.id.container_3_scrollview_1);
        boolean b4 = waiter.waitForTextViewAppearWithVerticallyScroll("^scrollText31", 5000, scrollView1);
        Assert.assertTrue(b4);
        boolean b5 = waiter.waitForButtonAppear("^hBt1", 2000);
        Assert.assertTrue(b5);
        ScrollView scrollView2 = currentActivity.findViewById(R.id.container_6_scrollView);
        boolean btt6 = waiter.waitForButtonAppearWithVerticallyScroll("btt6", 4000, scrollView2);
        Assert.assertTrue(btt6);
        ScrollView scrollView3 = currentActivity.findViewById(R.id.container_6_scrollView_0);
        boolean b6 = waiter.waitForEditTextAppear("edit2");
        Assert.assertTrue(b6);
        boolean edit11 = waiter.waitForEditTextAppearWithVerticallyScroll("edit11", 4000, scrollView3);
        Assert.assertTrue(edit11);

        Log.d("main","done");

    }

}
