package com.llx278.uimocker2;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.view.View;


import java.util.Map;


/**
 * 代理微信的x5webview
 * 注意，如果对应的api做了混淆的话，直接参考标准的api是无效的，那么就只能具体问题具体分析了，
 * Created by llx on 2018/3/13.
 */

class X5WebView implements WebViewProxy {
    private View mOriginalWebView;
    private Reflect mReflect;
    X5WebView(View originalWebView) {
        mOriginalWebView = originalWebView;
        mReflect = new Reflect(originalWebView);
    }

    @Override
    public void addJavascriptInterface(Object object, String name) {

    }

    @Override
    public boolean canGoBack() {
        try {
            return (boolean) mReflect.method("canGoBack").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public boolean canGoBackOrForward(int steps) {
        try {
            return (boolean) mReflect.method("canGoBackOrForward",int.class).invoke(steps);
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public boolean canGoForward() {

        try {
            return (boolean) mReflect.method("canGoForward").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public boolean canZoomIn() {
        try {
            return (boolean) mReflect.method("canZoomIn").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public boolean canZoomOut() {
        try {
            return (boolean) mReflect.method("canZoomOut").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public View findFocus() {
        try {
            return (View) mReflect.method("findFocus").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    @Override
    public void findNext(boolean forward) {
        try {
            mReflect.method("findNext").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public String getOriginalUrl() {
        try {
            return (String) mReflect.method("getOriginalUrl").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    @Override
    public int getProgress() {
        try {
            return (int) mReflect.method("getProgress").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return 0;
    }

    @Override
    public float getScale() {

        try {
            return (float) mReflect.method("getScale").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return 0;
    }

    @Override
    public Object getSettings() {
        try {
            Reflect.MethodRf getSettings = mReflect.method("getSettings");
            return getSettings.invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    @Override
    public String getTitle() {
        try {
            return (String) mReflect.method("getTitle").invoke();
        }catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    @Override
    public String getUrl() {
        try {
            return (String) mReflect.method("getUrl").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    @Override
    public void goBack() {
        try {
            mReflect.method("goBack").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void goBackOrForward(int steps) {
        try {
            mReflect.method("goBackOrForward",int.class).invoke(steps);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void goForward() {
        try {
            mReflect.method("goForward").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void invokeZoomPicker() {
        try {
            mReflect.method("invokeZoomPicker").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        try {
            mReflect.method("loadData",String.class,String.class,String.class).
                    invoke(data,mimeType,encoding);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        try {
            mReflect.method("loadDataWithBaseURL",String.class,String.class,String.class,String.class,String.class).
                    invoke(baseUrl,data,mimeType,encoding,historyUrl);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void loadUrl(String url) {
        try {
            mReflect.method("loadUrl",String.class).invoke(url);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        try {
            mReflect.method("loadUrl",String.class,Map.class).invoke(url,additionalHttpHeaders);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public boolean pageDown(boolean bottom) {
        try {
            return (boolean) mReflect.method("pageDown",boolean.class).invoke(bottom);
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public boolean pageUp(boolean top) {
        try {
            return (boolean) mReflect.method("pageUp",boolean.class).invoke(top);
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public void pauseTimers() {
        try {
            mReflect.method("pauseTimers").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public boolean performLongClick() {
        try {
            return (boolean) mReflect.method("performLongClick").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public void postUrl(String url, byte[] postData) {
        try {
            mReflect.method("postUrl",String.class,byte[].class).invoke(url,postData);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void reload() {
        try {
            mReflect.method("reload").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void removeJavascriptInterface(String name) {
        try {
            mReflect.method("removeJavascriptInterface",String.class).invoke(name);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        try {
            return (boolean) mReflect.method("requestChildRectangleOnScreen",View.class,Rect.class,boolean.class).
                    invoke(child,rect,immediate);
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        try {
            return (boolean) mReflect.method("requestFocus",int.class,Rect.class).
                    invoke(direction,previouslyFocusedRect);
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public void requestFocusNodeHref(Message hrefMsg) {
        try {
            mReflect.method("requestFocusNodeHref",Message.class).invoke(hrefMsg);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void requestImageRef(Message msg) {
        try {
            mReflect.method("requestImageRef",Message.class).invoke(msg);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void saveWebArchive(String filename) {
        try {
            mReflect.method("saveWebArchive",String.class).invoke(filename);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        try {
            mReflect.method("setBackgroundColor",int.class).invoke(color);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setDownloadListener(Object listener) {
        try {
            Class<?> downLoadListenerClass = mOriginalWebView.getClass().getClassLoader().
                    loadClass("com.tencent.smtt.sdk.DownloadListener");
            mReflect.method("setDownloadListener",downLoadListenerClass).invoke(listener);
        } catch (Exception e) {
            Logger.e(e);
        }

    }

    @Override
    public void setFindListener(Object listener) {
        try {
            Class<?> findClass = mOriginalWebView.getClass().getClassLoader().
                    loadClass("com.tencent.smtt.export.external.interfaces.IX5WebViewBase$FindListener");
            mReflect.method("setFindListener",findClass).invoke(listener);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setHttpAuthUsernamePassword(String host, String realm, String username, String password) {
        try {
            mReflect.method("setHttpAuthUsernamePassword",String.class,String.class,String.class,String.class).
                    invoke(host,realm,username,password);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setInitialScale(int scaleInPercent) {
        try {
            mReflect.method("setInitialScale",int.class).invoke(scaleInPercent);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setLayerType(int layerType, Paint paint) {
        try {
            mReflect.method("setLayerType",int.class,Paint.class).invoke(layerType,paint);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setMapTrackballToArrowKeys(boolean setMap) {
        try {
            mReflect.method("setMapTrackballToArrowKeys",boolean.class).invoke(setMap);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setNetworkAvailable(boolean networkUp) {
        try {
            mReflect.method("setNetworkAvailable",boolean.class).invoke(networkUp);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setWebChromeClient(Object client) {
        try {
            Class<?> webChromeClientClass = mOriginalWebView.getClass().getClassLoader().
                    loadClass("com.tencent.smtt.sdk.WebChromeClient");
            mReflect.method("setWebChromeClient",webChromeClientClass).invoke(client);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void setWebViewClient(Object client) {
        try {
            Class<?> webViewClientClass = mOriginalWebView.getClass().getClassLoader().
                    loadClass("com.tencent.smtt.sdk.WebViewClient");
            mReflect.method("setWebViewClient",webViewClientClass).invoke(client);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {

        try {
            return (boolean) mReflect.method("shouldDelayChildPressedState").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }

        return false;
    }

    @Override
    public void stopLoading() {
        try {
            mReflect.method("stopLoading").invoke();
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public void zoomBy(float zoomFactor) {
        try {
            mReflect.method("zoomBy",float.class).invoke(zoomFactor);
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    public boolean zoomIn() {
        try {

           return (boolean) mReflect.method("zoomIn").invoke();

        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public boolean zoomOut() {
        try {

            return (boolean) mReflect.method("zoomOut").invoke();

        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    @Override
    public void getLocationOnScreen(int[] location) {
        mOriginalWebView.getLocationOnScreen(location);
    }
}
