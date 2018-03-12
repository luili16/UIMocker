package com.llx278.uimocker2;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * WebChromeClient used to get information on web elements by injections of JavaScript.
 *
 * @author Renas Reda, renas.reda@robotium.com
 */

class RobotiumWebClient extends WebChromeClient {
    WebElementCreator webElementCreator;
    private Instrumentation inst;
    private WebChromeClient robotiumWebClient;
    private WebChromeClient originalWebChromeClient = null;
    private X5WebChromeOnJsPromptCallback mCallback;

    /**
     * Constructs this object.
     *
     * @param webElementCreator the {@code WebElementCreator} instance
     */

    public RobotiumWebClient(Instrumentation inst, WebElementCreator webElementCreator) {
        this.inst = inst;
        this.webElementCreator = webElementCreator;
        robotiumWebClient = this;
        mCallback = new X5WebChromeOnJsPromptCallback();
    }

    public X5WebChromeOnJsPromptCallback getX5WebChromeHookedCallback() {
        return mCallback;
    }

    public void setOriginalWebChromeClient(WebChromeClient webChromeClient) {
        originalWebChromeClient = webChromeClient;
    }

    /**
     * Overrides onJsPrompt in order to create {@code WebElement} objects based on the web elements attributes prompted by the injections of JavaScript
     */

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult r) {

        Log.d("main", "onJsPrompt");
        if (message != null && (message.contains(";,") || message.contains("robotium-finished"))) {

            if (message.equals("robotium-finished")) {
                webElementCreator.setFinished(true);
            } else {
                float scale = view.getScale();
                int[] locationOfWebViewXY = new int[2];
                view.getLocationOnScreen(locationOfWebViewXY);
                webElementCreator.createWebElementAndAddInList(message, scale, locationOfWebViewXY);
            }
            r.confirm();
            return true;

        } else {
            if (originalWebChromeClient != null) {
                return originalWebChromeClient.onJsPrompt(view, url, message, defaultValue, r);
            }
            return true;
        }
    }

    private class X5WebChromeOnJsPromptCallback extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);

            Object thisWebView = param.args[0];

            String message = (String) param.args[2];
            if (message != null && (message.contains(";,") || message.contains("robotium-finished"))) {
                XposedBridge.log("拦截到了一个js执行！");
                if (message.equals("robotium-finished")) {
                    webElementCreator.setFinished(true);
                } else {
                    Class<?> aClass = thisWebView.getClass();
                    Method getScale = aClass.getMethod("getScale");
                    float scale = (float) getScale.invoke(thisWebView);
                    int[] locationOfWebViewXY = new int[2];
                    if (thisWebView instanceof View) {
                        ((View) thisWebView).getLocationOnScreen(locationOfWebViewXY);
                    }
                    webElementCreator.createWebElementAndAddInList(message, scale, locationOfWebViewXY);
                    Object r = param.args[4];
                    Class<?> aClass1 = r.getClass();
                    Method confirm = aClass1.getMethod("confirm");
                    confirm.invoke(r);
                    param.setResult(true);
                }
            }
        }
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (originalWebChromeClient != null) {
            return originalWebChromeClient.getDefaultVideoPoster();
        }
        return null;
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (originalWebChromeClient != null) {
            return originalWebChromeClient.getVideoLoadingProgressView();
        }
        return null;
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.getVisitedHistory(callback);
        }
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onCloseWindow(window);
        }
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (originalWebChromeClient != null) {
            return originalWebChromeClient.onConsoleMessage(consoleMessage);
        }
        return true;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (originalWebChromeClient != null) {
            return originalWebChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
        return true;
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
                                        long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onGeolocationPermissionsHidePrompt();
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    @Override
    public void onHideCustomView() {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onHideCustomView();
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (originalWebChromeClient != null) {
            return originalWebChromeClient.onJsAlert(view, url, message, result);
        }
        return true;
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (originalWebChromeClient.onJsBeforeUnload(view, url, message, result)) {
            return originalWebChromeClient.onJsBeforeUnload(view, url, message, result);
        }
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (originalWebChromeClient != null) {
            return originalWebChromeClient.onJsConfirm(view, url, message, result);
        }
        return true;
    }

    @Override
    public boolean onJsTimeout() {
        if (originalWebChromeClient != null) {
            return originalWebChromeClient.onJsTimeout();
        }
        return true;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onReceivedIcon(view, icon);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onRequestFocus(view);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (originalWebChromeClient != null) {
            originalWebChromeClient.onShowCustomView(view, callback);
        }
    }
}
