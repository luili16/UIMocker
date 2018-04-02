package com.llx278.uimocker2;

import android.view.View;
import android.webkit.WebView;

/**
 * 根据指定的webView(可能是系统的webView，也可能是微信的x5webview)生成代理对象
 * Created by llx on 2018/3/13.
 */

class WebViewProxyCreator {
    private WebViewProxyCreator(){}
    static WebViewProxy create(Object webView) {

        if (webView == null) {
            return null;
        }

        WebViewProxy proxy = null;
        if (webView instanceof WebView) {
            proxy = new SystemWebView((WebView) webView);
        } else if (ReflectUtil.isAssignedFrom("com.tencent.smtt.sdk.WebView",webView)) {
            proxy = new X5WebView((View) webView);
        }
        return proxy;
    }
}
