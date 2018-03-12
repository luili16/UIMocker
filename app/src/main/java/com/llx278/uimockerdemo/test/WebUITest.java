package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.WebElement;
import com.llx278.uimocker2.WebUtils;
import com.llx278.uimockerdemo.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by llx on 2018/3/11.
 */

public class WebUITest {

    public void run(Solo solo) throws Exception {
        XposedBridge.log("进入 webUiTest");

        boolean b = solo.waitForOnResume("com.example.test_webview_demo.BrowserActivity", 1000 * 20);
        if (!b) {
            XposedBridge.log("b is " + b);
            return;
        }
        Activity currentActivity = solo.getCurrentActivity();
        long endTime = SystemClock.uptimeMillis() + 10 * 1000;
        boolean found = false;
        View webView = null;
        while (SystemClock.uptimeMillis() < endTime) {
            Thread.sleep(200);
            webView = solo.getCustomView("com.example.test_webview_demo.utils.X5WebView", null);
            if (webView != null) {
                found = true;
                break;
            }
        }
        if (!found) {
            return;
        }

        Class<? extends Solo> aClass = solo.getClass();
        Field mWebUtils = aClass.getDeclaredField("mWebUtils");
        mWebUtils.setAccessible(true);
        WebUtils webUtils = (WebUtils) mWebUtils.get(solo);
        webUtils.getWebElements(true,webView,null);

        XposedBridge.log("获取结束!");

    }

}
