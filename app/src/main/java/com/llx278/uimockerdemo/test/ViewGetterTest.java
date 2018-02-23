package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.ViewGetter;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by llx on 2018/2/22.
 */

public class ViewGetterTest {

    public void run(Solo solo) {
        Activity currentActivity = solo.getCurrentActivity();
        Assert.assertEquals("com.llx278.uimockerdemo.MainActivity",currentActivity.getClass().getName());
        boolean ret = solo.waitForTextAndClick("^ViewGetterTest$");
        Assert.assertEquals(true,ret);
        boolean resumeRet = solo.waitForOnResume("com.llx278.uimockerdemo.ViewGetterTestActivity");
        Assert.assertEquals(true,resumeRet);
        Class<? extends Solo> aClass = solo.getClass();
        try {
            Field mViewGetter = aClass.getDeclaredField("mViewGetter");
            mViewGetter.setAccessible(true);
            ViewGetter viewGetter = (ViewGetter) mViewGetter.get(solo);
            ArrayList<View> viewsBefore = viewGetter.getViews();
            Assert.assertNotNull(viewsBefore);
            Log.d("main",viewsBefore.toString());
            boolean text1Ret = solo.waitForText("^text1$");
            Assert.assertTrue(text1Ret);
            ArrayList<View> viewsAfter = viewGetter.getViews();
            Assert.assertNotNull(viewsAfter);
            Log.d("main",viewsAfter.toString());
        } catch (Exception e) {
            e.printStackTrace();

        }
        Log.d("main","finish!!!!!!!");
    }
}
