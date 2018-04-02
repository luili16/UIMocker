package com.llx278.uimocker2;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

/**
 * Created by llx on 2018/3/13.
 */

class SystemWebView implements WebViewProxy {

    private WebView mWebView;

    public SystemWebView(WebView webView) {
        mWebView = webView;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object object, String name) {
        mWebView.addJavascriptInterface(object,name);
    }

    @Override
    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    @Override
    public boolean canGoBackOrForward(int steps) {
        return mWebView.canGoBackOrForward(steps);
    }

    @Override
    public boolean canGoForward() {
        return mWebView.canGoForward();
    }

    @Override
    public boolean canZoomIn() {
        return mWebView.canZoomIn();
    }

    @Override
    public boolean canZoomOut() {
        return mWebView.canZoomOut();
    }

    @Override
    public View findFocus() {
        return mWebView.findFocus();
    }

    @Override
    public void findNext(boolean forward) {
        mWebView.findNext(forward);
    }

    @Override
    public String getOriginalUrl() {
        return mWebView.getOriginalUrl();
    }

    @Override
    public int getProgress() {
        return mWebView.getProgress();
    }

    @Override
    public float getScale() {
        return mWebView.getScale();
    }

    @Override
    public Object getSettings() {
        return mWebView.getSettings();
    }

    @Override
    public String getTitle() {
        return mWebView.getTitle();
    }

    @Override
    public String getUrl() {
        return mWebView.getUrl();
    }

    @Override
    public void goBack() {
        mWebView.goBack();
    }

    @Override
    public void goBackOrForward(int steps) {
        mWebView.goBackOrForward(steps);
    }

    @Override
    public void goForward() {
        mWebView.goForward();
    }

    @Override
    public void invokeZoomPicker() {
        mWebView.invokeZoomPicker();
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        mWebView.loadData(data,mimeType,encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        mWebView.loadDataWithBaseURL(baseUrl,data,mimeType,encoding,historyUrl);
    }

    @Override
    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        mWebView.loadUrl(url,additionalHttpHeaders);
    }

    @Override
    public boolean pageDown(boolean bottom) {
        return mWebView.pageDown(bottom);
    }

    @Override
    public boolean pageUp(boolean top) {
        return mWebView.pageUp(top);
    }

    @Override
    public void pauseTimers() {
        mWebView.pauseTimers();
    }

    @Override
    public boolean performLongClick() {
        return mWebView.performLongClick();
    }

    @Override
    public void postUrl(String url, byte[] postData) {
        mWebView.postUrl(url,postData);
    }

    @Override
    public void reload() {
        mWebView.reload();
    }

    @Override
    public void removeJavascriptInterface(String name) {
        mWebView.removeJavascriptInterface(name);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        return mWebView.requestChildRectangleOnScreen(child,rect,immediate);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return mWebView.requestFocus(direction,previouslyFocusedRect);
    }

    @Override
    public void requestFocusNodeHref(Message hrefMsg) {
        mWebView.requestFocusNodeHref(hrefMsg);
    }

    @Override
    public void requestImageRef(Message msg) {
        mWebView.requestImageRef(msg);
    }

    @Override
    public void saveWebArchive(String filename) {
        mWebView.saveWebArchive(filename);
    }

    @Override
    public void setBackgroundColor(int color) {
        mWebView.setBackgroundColor(color);
    }

    @Override
    public void setDownloadListener(Object listener) {
        mWebView.setDownloadListener((DownloadListener) listener);
    }

    @Override
    public void setFindListener(Object listener) {
        mWebView.setFindListener((WebView.FindListener) listener);
    }

    @Override
    public void setHttpAuthUsernamePassword(String host, String realm, String username, String password) {
        mWebView.setHttpAuthUsernamePassword(host,realm,username,password);
    }

    @Override
    public void setInitialScale(int scaleInPercent) {
        mWebView.setInitialScale(scaleInPercent);
    }

    @Override
    public void setLayerType(int layerType, Paint paint) {
        mWebView.setLayerType(layerType,paint);
    }

    @Override
    public void setMapTrackballToArrowKeys(boolean setMap) {
        mWebView.setMapTrackballToArrowKeys(setMap);
    }

    @Override
    public void setNetworkAvailable(boolean networkUp) {
        mWebView.setNetworkAvailable(networkUp);
    }

    @Override
    public void setWebChromeClient(Object client) {
        mWebView.setWebChromeClient((WebChromeClient) client);
    }

    @Override
    public void setWebViewClient(Object client) {
        mWebView.setWebViewClient((WebViewClient) client);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return mWebView.shouldDelayChildPressedState();
    }

    @Override
    public void stopLoading() {
        mWebView.stopLoading();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void zoomBy(float zoomFactor) {
        mWebView.zoomBy(zoomFactor);
    }

    @Override
    public boolean zoomIn() {
        return mWebView.zoomIn();
    }

    @Override
    public boolean zoomOut() {
        return mWebView.zoomOut();
    }

    @Override
    public void getLocationOnScreen(int[] location) {
        mWebView.getLocationOnScreen(location);
    }

}
