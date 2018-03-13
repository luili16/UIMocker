package com.llx278.uimocker2;

import android.webkit.WebView;

/**
 * Created by llx on 2018/3/13.
 */

class WebViewProxyCreator {

    static WebViewProxy create(Object webView) {
        WebViewProxy proxy = null;

        if (webView == null) {
            return null;
        }

        if (webView instanceof WebView) {
            return new SystemWebView((WebView) webView);
        } else if (ReflectUtil.isAssignedFrom("com.tencent.smtt.sdk.WebView",webView)) {
            return new X5WebView(webView);
        }
        return null;
    }
}
