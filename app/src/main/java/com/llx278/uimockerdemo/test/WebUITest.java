package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.WebView;

import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.WebElement;
import com.llx278.uimocker2.WebUtils;
import com.llx278.uimockerdemo.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by llx on 2018/3/11.
 */

public class WebUITest {

    public void run(Solo solo) throws Exception{
        Log.d("main","进入WebUITest");
        Thread.sleep(3000);
        Class<? extends Solo> aClass = solo.getClass();
        Field mWebUtils = aClass.getDeclaredField("mWebUtils");
        mWebUtils.setAccessible(true);
        WebUtils webUtils = (WebUtils) mWebUtils.get(solo);
        /*Activity currentActivity = solo.getCurrentActivity();
        WebView webView = currentActivity.findViewById(R.id);*/
        //webUtils.getWebElements(true);




        Log.d("main","获取结束");

    }

}
