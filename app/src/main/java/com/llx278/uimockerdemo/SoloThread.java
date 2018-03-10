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

import junit.framework.Assert;

import static junit.framework.Assert.assertEquals;

/**
 *
 * Created by llx on 2018/2/22.
 */

public class SoloThread extends Thread {

    private Solo mSolo;

    public SoloThread(Context context) {
        mSolo = Solo.getInstance(context,null);
    }

    @Override
    public void run() {
        boolean ret1 = mSolo.waitForActivity("com.llx278.uimockerdemo.MainActivity");
        Assert.assertEquals(true,ret1);

        boolean ret = mSolo.waitForTextAndClick("^ViewGetterTest$");
        assertEquals(true,ret);

        boolean resumeRet = mSolo.waitForOnResume("com.llx278.uimockerdemo.TestActivity");
        assertEquals(true,resumeRet);

        try {
            //ViewGetterTest viewGetterTest = new ViewGetterTest();
            //viewGetterTest.run(mSolo);
            ScrollerTest scrollerTest = new ScrollerTest();
            scrollerTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            SearcherTest searcherTest = new SearcherTest();
            searcherTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            WaiterTest waiterTest = new WaiterTest();
            waiterTest.run(mSolo);
            mSolo.waitForTextAndClick("^ViewGetterTest$");
            ClickerTest clickerTest = new ClickerTest();
            clickerTest.run(mSolo);
            Log.d("main","all done!");
            mSolo.getCurrentActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
