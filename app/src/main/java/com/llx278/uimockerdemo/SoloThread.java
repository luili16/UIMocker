package com.llx278.uimockerdemo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.llx278.uimocker2.Solo;
import com.llx278.uimockerdemo.test.ClickerTest;
import com.llx278.uimockerdemo.test.ScrollerTest;
import com.llx278.uimockerdemo.test.SearcherTest;
import com.llx278.uimockerdemo.test.ViewGetterTest;
import com.llx278.uimockerdemo.test.WaiterTest;
import com.llx278.uimockerdemo.test.WebUITest;

import junit.framework.Assert;

import static junit.framework.Assert.assertEquals;

/**
 *
 * Created by llx on 2018/2/22.
 */

public class SoloThread extends Thread {

    private Solo mSolo;

    public SoloThread(Context context) {
        mSolo = new Solo(context);
    }

    @Override
    public void run() {
        String activityName = "com.llx278.uimockerdemo.MainActivity";
        boolean ret1 = mSolo.getActivityUtils().waitForOnCreate(activityName,1000 * 10,0);
        Assert.assertEquals(true,ret1);

        boolean ret = mSolo.waitForTextAndClick("^ViewGetterTest$");
        assertEquals(true,ret);

        boolean resumeRet = mSolo.getActivityUtils().waitForOnResume(
                "com.llx278.uimockerdemo.TestActivity",
                1000 * 10,
                0);
        assertEquals(true,resumeRet);
        mSolo.waitForTextAndClick("WebUITest");

        try {
            ViewGetterTest viewGetterTest = new ViewGetterTest();
            // 受屏幕分辨率的影响，无法准确的测试
            //viewGetterTest.run(mSolo);
            ScrollerTest scrollerTest = new ScrollerTest();
            scrollerTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            SearcherTest searcherTest = new SearcherTest();
            // 受屏幕分辨率的影响，无法准确的测试
            //searcherTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            WaiterTest waiterTest = new WaiterTest();
            waiterTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            ClickerTest clickerTest = new ClickerTest();
            clickerTest.run(mSolo);
            Log.d("main","all done!");
            mSolo.getActivityUtils().finishOpenedActivities();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
