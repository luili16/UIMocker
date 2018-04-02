package com.llx278.uimocker2;

import android.graphics.Bitmap;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import de.robv.android.xposed.XC_MethodHook;


/**
 * 根据webview的类型(是系统的webview还是腾讯的x5)选择不同的注入方式
 * Created by llx on 2018/3/12.
 */

class WebViewInjector {
    private XC_MethodHook.Unhook mCurrentHookedMethod;
    private WebElementCreator mWebElementCreator;
    private InjectedWebChromeClient mInjectedClient;
    private InstrumentationDecorator mInst;

    WebViewInjector(WebElementCreator creator,InstrumentationDecorator inst) {
        mInst = inst;
        mWebElementCreator = creator;
    }

    boolean injectTo(final View webView) {

        try {
            if (webView == null) {
                return false;
            }

            if (webView instanceof WebView) {
                Object currentWebView = webView;

                currentWebView = new Reflect(currentWebView).field("mProvider").out();
                Object mClientAdapter = new Reflect(currentWebView).field("mContentsClientAdapter").out();
                WebChromeClient client = (WebChromeClient)
                        new Reflect(mClientAdapter).field("mWebChromeClient").out();
                mInjectedClient = new InjectedWebChromeClient(client);
                mInst.runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        ((WebView) webView).setWebChromeClient(mInjectedClient);
                    }
                });

                // 直接hook有一个巨坑，就是如果继承WebChromeClient的类并没有调用super，那就悲剧了
                //mCurrentHookedMethod = XposedHelpers.findAndHookMethod(WebChromeClient.class,
                //       "onJsPrompt",
                //      WebView.class,
                //    String.class,
                //  String.class,
                //String.class,
                //JsPromptResult.class,
                //new WebChromeHookedCallback());
                return true;
            } else if (ReflectUtil.isAssignedFrom("com.tencent.smtt.sdk.WebView", webView)) {
                // 直接hook有一个巨坑，就是如果继承WebChromeClient的类并没有调用super，那就悲剧了
                /*mCurrentHookedMethod = XposedHelpers.findAndHookMethod(
                        "com.tencent.smtt.sdk.WebChromeClient",
                        webView.getClass().getClassLoader(),
                        "onJsPrompt",
                        "com.tencent.smtt.sdk.WebView",
                        String.class,
                        String.class,
                        String.class,
                        "com.tencent.smtt.export.external.interfaces.JsPromptResult",
                        new X5WebChromeHookedCallback());*/
                return true;
            }

        } catch (Exception e) {
            MLogger.e(e);
        }
        return false;
    }

    void unInject(final View webView) {
        Object currentWebView = webView;
        try {
            currentWebView = new Reflect(currentWebView).field("mProvider").out();
            Object mClientAdapter = new Reflect(currentWebView).field("mContentsClientAdapter").out();
            WebChromeClient client = (WebChromeClient)
                    new Reflect(mClientAdapter).field("mWebChromeClient").out();
            if (client.equals(mInjectedClient)) {
                mInst.runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((WebView) webView).setWebChromeClient(mInjectedClient.mOriginalWebChromeClient);
                        } catch (Exception e) {
                            MLogger.e(e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            MLogger.e(e);
        }

        if (mCurrentHookedMethod != null) {
            mCurrentHookedMethod.unhook();
        }

    }

    /**
     * 处理JavaScript执行的结果
     *
     * @param webView 执行javaScript的webView
     * @param message 执行的结果
     * @return true 此结果可以被处理，false，此结果不能被处理
     */
    private boolean handleMessage(View webView, String message) {
        if (!TextUtils.isEmpty(message) && message.startsWith("inject_result")) {
            String tag = message.substring(14, 18);
            String msg = message.substring(19);
            switch (tag) {
                case "elem":
                case "text":
                    packageWebElement(webView, msg);
                    break;
                case "fini":
                    mWebElementCreator.setFinished(true);
                    break;
                case "debu":
                    //mWebElementCreator.setFinished(true);
                    MLogger.d("js-debug : " + msg);
                    break;
                default:
            }
            return true;
        }
        return false;
    }

    private void packageWebElement(View webView, String msg) {
        WebViewProxy proxy = WebViewProxyCreator.create(webView);
        float scale = proxy.getScale();
        int[] locationOfWebViewXY = new int[2];
        proxy.getLocationOnScreen(locationOfWebViewXY);
        mWebElementCreator.createWebElementAndAddInList(msg, scale, locationOfWebViewXY);
    }

    private class WebChromeHookedCallback extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            WebView thisWebView = (WebView) param.args[0];
            String message = (String) param.args[2];
            JsPromptResult r = (JsPromptResult) param.args[4];
            if (handleMessage(thisWebView, message)) {
                r.confirm();
                param.setResult(true);
            }
        }
    }

    private class X5WebChromeHookedCallback extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            View thisWebView = (View) param.args[0];
            String message = (String) param.args[2];
            Object r = param.args[4];
            if (handleMessage(thisWebView, message)) {
                Reflect reflect = new Reflect(r);
                reflect.method("confirm").invoke();
                param.setResult(true);
            }
        }
    }

    /**
     * 代理WebChromeClient
     */
    private class InjectedWebChromeClient extends WebChromeClient {

        private WebChromeClient mOriginalWebChromeClient = null;

        public InjectedWebChromeClient(WebChromeClient webChromeClient) {
            mOriginalWebChromeClient = webChromeClient;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult r) {
            if (handleMessage(view, message)) {
                r.confirm();
                return true;
            } else {
                if (mOriginalWebChromeClient != null) {
                    return mOriginalWebChromeClient.onJsPrompt(view, url, message, defaultValue, r);
                }
                return true;
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (mOriginalWebChromeClient != null) {
                return mOriginalWebChromeClient.onJsAlert(view, url, message, result);
            }
            return true;
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            if (mOriginalWebChromeClient != null) {
                return mOriginalWebChromeClient.getDefaultVideoPoster();
            }
            return null;
        }

        @Override
        public View getVideoLoadingProgressView() {
            if (mOriginalWebChromeClient != null) {
                return mOriginalWebChromeClient.getVideoLoadingProgressView();
            }
            return null;
        }

        @Override
        public void getVisitedHistory(ValueCallback<String[]> callback) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.getVisitedHistory(callback);
            }
        }

        @Override
        public void onCloseWindow(WebView window) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onCloseWindow(window);
            }
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onConsoleMessage(message, lineNumber, sourceID);
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (mOriginalWebChromeClient != null) {
                return mOriginalWebChromeClient.onConsoleMessage(consoleMessage);
            }
            return true;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            if (mOriginalWebChromeClient != null) {
                return mOriginalWebChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }
            return true;
        }

        @Override
        public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
                                            long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
            }
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onGeolocationPermissionsHidePrompt();
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        }

        @Override
        public void onHideCustomView() {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onHideCustomView();
            }
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            if (mOriginalWebChromeClient.onJsBeforeUnload(view, url, message, result)) {
                return mOriginalWebChromeClient.onJsBeforeUnload(view, url, message, result);
            }
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            if (mOriginalWebChromeClient != null) {
                return mOriginalWebChromeClient.onJsConfirm(view, url, message, result);
            }
            return true;
        }

        @Override
        public boolean onJsTimeout() {
            if (mOriginalWebChromeClient != null) {
                return mOriginalWebChromeClient.onJsTimeout();
            }
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onProgressChanged(view, newProgress);
            }
        }

        @Override
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onReceivedIcon(view, icon);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onReceivedTitle(view, title);
            }
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
            }
        }

        @Override
        public void onRequestFocus(WebView view) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onRequestFocus(view);
            }
        }

        @Override
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            if (mOriginalWebChromeClient != null) {
                mOriginalWebChromeClient.onShowCustomView(view, callback);
            }
        }
    }
}
