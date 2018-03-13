package com.llx278.uimocker2;

/**
 *
 * Created by llx on 2018/3/13.
 */

class WebViewExecutor {

    private InstrumentationDecorator mInst;

    WebViewExecutor(InstrumentationDecorator inst) {
        mInst = inst;
    }

    void loadUrl(final String url, Object webView) {
        final WebViewProxy proxy = WebViewProxyCreator.create(webView);
        mInst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                proxy.loadUrl(url);
            }
        });
    }
}
