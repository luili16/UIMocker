package com.llx278.uimockerdemo;

import android.content.Context;

import com.llx278.uimocker2.Solo;
import com.llx278.uimockerdemo.test.ViewGetterTest;

import junit.framework.Assert;

/**
 *
 * Created by llx on 2018/2/22.
 */

public class SoloThread extends Thread {

    private Solo mSolo;

    public SoloThread(Context context) {
        mSolo = Solo.getInstance(null,context,null);
    }

    @Override
    public void run() {
        boolean ret = mSolo.waitForActivity("com.llx278.uimockerdemo.MainActivity");
        Assert.assertEquals(true,ret);
        ViewGetterTest viewGetterTest = new ViewGetterTest();
        try {
            viewGetterTest.run(mSolo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
