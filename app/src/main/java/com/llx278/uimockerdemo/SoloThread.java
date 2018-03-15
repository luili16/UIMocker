package com.llx278.uimockerdemo;

import android.content.Context;
import android.util.Log;

import com.llx278.uimocker2.ActivityUtils;
import com.llx278.uimocker2.Solo;

import junit.framework.Assert;

import static junit.framework.Assert.assertEquals;

/**
 *
 * Created by llx on 2018/2/22.
 */

public class SoloThread extends Thread {

    private Solo mSolo;

    public SoloThread(Context context) {
        mSolo = Solo.getInstance(context);
    }

    @Override
    public void run() {
        ActivityUtils activityUtils = mSolo.getActivityUtils();
        boolean b = activityUtils.waitForOnCreate("com.llx278.uimockerdemo.MainActivity", 2000, 0);
        Assert.assertEquals(true,b);

       // boolean ret = mSolo.waitForTextAndClick("^ViewGetterTest$");
       // assertEquals(true,ret);

       // boolean resumeRet = mSolo.waitForOnResume("com.llx278.uimockerdemo.TestActivity");
       // assertEquals(true,resumeRet);
        //mSolo.waitForTextAndClick("X5WebUITest");

        try {
            //ViewGetterTest viewGetterTest = new ViewGetterTest();
            //viewGetterTest.run(mSolo);
            /*ScrollerTest scrollerTest = new ScrollerTest();
            scrollerTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            SearcherTest searcherTest = new SearcherTest();
            searcherTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            WaiterTest waiterTest = new WaiterTest();
            waiterTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            ClickerTest clickerTest = new ClickerTest();
            clickerTest.run(mSolo);*/
            //X5WebUITest webUITest = new X5WebUITest();
            //webUITest.run(mSolo);
            Log.d("main","all done!");
            //mSolo.getCurrentActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
