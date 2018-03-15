package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.llx278.uimocker2.By;
import com.llx278.uimocker2.Clicker;
import com.llx278.uimocker2.Logger;
import com.llx278.uimocker2.Reflect;
import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.Waiter;
import com.llx278.uimocker2.WebElement;
import com.llx278.uimocker2.WebUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by llx on 2018/3/11.
 */

public class X5WebUITest {

    public void run(Solo solo) throws Exception {
        XposedBridge.log("进入 webUiTest");
        solo.waitForTextAndClick("^浏览器demo$");
        boolean b = solo.getActivityUtils().waitForOnResume("com.example.test_webview_demo.BrowserActivity", 1000 * 20,0);
        if (!b) {
            XposedBridge.log("b is " + b);
            return;
        }
        long endTime = SystemClock.uptimeMillis() + 10 * 1000;
        boolean found = false;
        ArrayList<View> viewArrayList = null;
        while (SystemClock.uptimeMillis() < endTime) {
            Thread.sleep(200);
            viewArrayList = solo.getViewGetter().getViewListByName("com.example.test_webview_demo.utils.X5WebView", null,true);
            if (viewArrayList != null && !viewArrayList.isEmpty()) {
                found = true;
                break;
            }
        }
        View webView = null;
        if (found) {
            webView = viewArrayList.get(0);
        } else {
            return;
        }

        Waiter waiter = solo.getWaiter();

        /*ArrayList<WebElement> webElements = webUtils.getWebElementList(true, webView, 5000);
        XposedBridge.log("webElements : " + webElements.toString());
        ArrayList<TextView> textViewListFromWebView = webUtils.getTextViewListFromWebView(webView, 5000);
        XposedBridge.log("textViewListFromWebView : " + textViewListFromWebView.toString());*/
        //ArrayList<WebElement> webElements = webUtils.getWebElementList(true, webView, 5000);
       // XposedBridge.log(webElements.toString());

        XposedBridge.log("准备寻找logo");
        By className = By.textContent("视频");
        ArrayList<WebElement> webElements = waiter.waitForWebElementAppearAndGet(className, webView, 15000);
        XposedBridge.log(webElements.toString());
        XposedBridge.log("webElements : " + webElements.size());
        WebElement webElement = webElements.get(0);

        Thread.sleep(5000);
        Logger.d("准备点击");
        Clicker clicker = solo.getClicker();
        clicker.clickOnScreen(webElement.getLocationX(),webElement.getLocationY());
        XposedBridge.log("获取结束!");

    }

}
