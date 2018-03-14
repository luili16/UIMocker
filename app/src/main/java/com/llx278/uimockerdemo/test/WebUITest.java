package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.llx278.uimocker2.By;
import com.llx278.uimocker2.Logger;
import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.WebElement;
import com.llx278.uimocker2.WebUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by llx on 2018/3/11.
 */

public class WebUITest {

    public void run(Solo solo) throws Exception {
        XposedBridge.log("进入 webUiTest");
        solo.waitForTextAndClick("^浏览器demo$");
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
        XposedBridge.log("睡眠5s");
        Thread.sleep(5000);
        Class<? extends Solo> aClass = solo.getClass();
        Field mWebUtils = aClass.getDeclaredField("mWebUtils");
        mWebUtils.setAccessible(true);
        WebUtils webUtils = (WebUtils) mWebUtils.get(solo);
        /*ArrayList<WebElement> webElements = webUtils.getWebElementList(true, webView, 5000);
        XposedBridge.log("webElements : " + webElements.toString());
        ArrayList<TextView> textViewListFromWebView = webUtils.getTextViewListFromWebView(webView, 5000);
        XposedBridge.log("textViewListFromWebView : " + textViewListFromWebView.toString());*/
        //ArrayList<WebElement> webElements = webUtils.getWebElementList(true, webView, 5000);
       // XposedBridge.log(webElements.toString());
        By by = By.tagName("html");
        ArrayList<WebElement> document = webUtils.getWebElementList(by, false, webView);

        By content = By.className("login");
        boolean b1 = webUtils.clickOnWebElement(content, webView);
        XposedBridge.log("b1 : " + b1);
        XposedBridge.log("获取结束!");

    }

}
