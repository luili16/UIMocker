package com.llx278.uimockerdemo.test;

import android.os.SystemClock;
import android.view.View;
import android.webkit.WebView;

import com.llx278.uimocker2.By;
import com.llx278.uimocker2.Clicker;
import com.llx278.uimocker2.Logger;
import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.Waiter;
import com.llx278.uimocker2.WebElement;
import com.llx278.uimocker2.WebUtils;

import java.util.ArrayList;

/**
 *
 * Created by llx on 2018/3/15.
 */

public class SysWebUITest {

    public void run(Solo solo) throws Exception {
        Logger.d("进入 SysWebUITest");
        long endTime = SystemClock.uptimeMillis() + 10 * 1000;
        boolean found = false;
        ArrayList<WebView> viewArrayList = null;
        while (SystemClock.uptimeMillis() < endTime) {
            Thread.sleep(200);
            viewArrayList = solo.getViewGetter().getViewListByClass(WebView.class);
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
        By id = By.id("logo");
        ArrayList<WebElement> webElements = waiter.waitForWebElementAppearAndGet(id, webView, 2000);
        Logger.d(webElements.toString());
        Logger.d("webElements : " + webElements.size());

        By content = By.textContent("新闻");
        Clicker clicker = solo.getClicker();
        WebUtils webUtils = solo.getWebUtils();
        ArrayList<WebElement> webElementList = webUtils.getWebElementList(content, true, webView);
        Logger.d(webElementList.toString());
        Logger.d("size : " + webElementList.size());
        WebElement webElement = webElementList.get(0);
        Thread.sleep(1500);
        Logger.d("准备点击屏幕");
        //clicker.clickOnScreen(webElement.getLocationX(),webElement.getLocationY());
        webUtils.clickOnWebElement(content,webView);
    }

}
