package com.llx278.uimocker2;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * 包含操作web元素相关的方法
 */
public class WebUtils {

	private static final String DEFAULT_FRAME = "document";

	private WebElementCreator mWebElementCreator;
	private WebViewInjector mInjector;
	private WebViewExecutor mExecutor;
	private JavaScriptCreator mJSCreator;
	private InstrumentationDecorator mInst;

	WebUtils(InstrumentationDecorator instrumentation){
		mInst = instrumentation;
		mWebElementCreator = new WebElementCreator();
		mInjector = new WebViewInjector(mWebElementCreator);
		mExecutor = new WebViewExecutor(instrumentation);
		mJSCreator = new JavaScriptCreator();
	}

	/**
	 * 从给定的webview里面取出所有的Web元素，默认的frame是{@link WebUtils#DEFAULT_FRAME}
	 * @param onlySufficientlyVisible true 只抓取当前屏幕上面可见的 false 抓取由frame指定的所有web元素
	 * @param webView 指定的webview
	 * @return 抓取后所有元素的列表
	 */
	public ArrayList<WebElement> getWebElementList(boolean onlySufficientlyVisible, View webView) {
		return getWebElementList(onlySufficientlyVisible,webView,DEFAULT_FRAME);
	}

	/**
	 * 从给定的webview里面取出所有的Web元素
	 * @param onlySufficientlyVisible true 只抓取当前屏幕上面可见的 false 抓取由frame指定的所有web元素
	 * @param webView 指定的WebView
	 * @param frame 默认是document
	 * @return 抓取后所有元素的列表
	 */
	public ArrayList<WebElement> getWebElementList(boolean onlySufficientlyVisible, View webView,String frame){
		boolean javaScriptWasExecuted = executeJavaScriptFunction("allWebElements();",webView,frame);
		return getWebElementList(javaScriptWasExecuted, onlySufficientlyVisible,webView);
	}

	/**
	 * 从指定的By(例如 by.id)里面取出所有的web元素,默认的frame是document
	 * @param by 指定的by
	 * @param onlySufficientlyVisbile true 只抓取当前屏幕上面可见的 false 抓取由frame指定的所有web元素
	 * @param webView 指定的webview
	 * @return 抓取到的所有元素列表
	 */
	public ArrayList<WebElement> getWebElementList(By by, boolean onlySufficientlyVisbile,View webView) {
		return getWebElementList(by,onlySufficientlyVisbile,webView,DEFAULT_FRAME);
	}
	/**
	 * 从指定的By(例如 by.id)里面取出所有的web元素
	 * @param by 指定的by
	 * @param onlySufficientlyVisible true 只抓取当前屏幕上面可见的 false 抓取由frame指定的所有web元素
	 * @param webView 指定的webview
	 * @param frame 指定的frame
	 * @return 抓取到的所有元素列表
	 */
	public ArrayList<WebElement> getWebElementList(By by, boolean onlySufficientlyVisible,View webView,String frame){
		boolean javaScriptWasExecuted = executeJavaScript(by,false,webView,frame);
		return getWebElementList(javaScriptWasExecuted,onlySufficientlyVisible,webView);
	}

	/**
	 * 将指定WebView里面的所有元素全部转换为WebView
	 * @param webView
	 * @return
	 */
	public ArrayList<TextView> getTextViewListFromWebView(View webView) {
		return getTextViewListFromWebView(webView,DEFAULT_FRAME);
	}

	public ArrayList<TextView> getTextViewListFromWebView(View webView,String frame) {
		boolean javaScriptWasExecuted = executeJavaScriptFunction("allTexts();",webView,frame);
		return createAndReturnTextViewsFromWebElements(javaScriptWasExecuted,webView);
	}

	/**
	 * 点击by指定的元素
	 * @param by 指定的by
	 * @param webView 指定的webView
	 * @return true 点击成功 false 点击失败
	 */
	public boolean clickOnWebElement(By by,View webView) {
		return executeJavaScript(by,true,webView,DEFAULT_FRAME);
	}

	/**
	 * 判断指定的webView是否支持回退
	 * @return true 支持 false 不支持
	 */
	public boolean canGoBack(View webView) {
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		return proxy.canGoBack();
	}


	/**
	 * 判断指定的webView是否支持快进
	 * @param webView 指定的webView
	 * @return true 支持快进 false 不支持
	 */
	public boolean canGoForward(View webView) {
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		return proxy.canGoForward();
	}

	/**
	 * 回退
	 * @param webView 指定的webview
	 */
	public void goBack(View webView){
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		proxy.goBack();
	}

	/**
	 * 前进或者后退指定的steps
	 * @param webView 指定的webView
	 * @param steps 步数
	 */
	public void goBackOrForward(View webView,int steps){
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		proxy.goBackOrForward(steps);
	}

	/**
	 * 向前
	 * @param webView 指定的webView
	 */
	public void goForward(View webView){
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		proxy.goForward();
	}

	/**
	 * reload
	 * @param webView 指定的webview
	 */
	public void reload(View webView) {
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		proxy.reload();
	}

	public boolean pageDown(View webView,boolean bottom) {
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		return proxy.pageDown(bottom);
	}

	public boolean pageUp(View webView,boolean top) {
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		return proxy.pageUp(top);
	}

	/**
	 * 判断指定的webVie，是否支持goBack或者forward指定的steps
	 * @param steps 移动的步数
	 * @return true 支持 false 不支持
	 */
	public boolean canGoBackOrForward(View webView,int steps) {
		WebViewProxy proxy = WebViewProxyCreator.create(webView);
		return proxy.canGoBackOrForward(steps);
	}

	private boolean isWebElementSufficientlyShown(WebElement webElement,View webView){

		final int[] xyWebView = new int[2];
		if(webView != null && webElement != null){
			webView.getLocationOnScreen(xyWebView);
			if(xyWebView[1] + webView.getHeight() > webElement.getLocationY())
				return true;
		}
		return false;
	}

	private void enterTextIntoWebElement(final By by, final String text,View webView,String frame){
		if(by instanceof By.Id){
			executeJavaScriptFunction("enterTextById(\""+by.getValue()+"\", \""+text+"\");",webView,frame);
		}
		else if(by instanceof By.Xpath){
			executeJavaScriptFunction("enterTextByXpath(\""+by.getValue()+"\", \""+text+"\");",webView,frame);
		}
		else if(by instanceof By.CssSelector){
			executeJavaScriptFunction("enterTextByCssSelector(\""+by.getValue()+"\", \""+text+"\");",webView,frame);
		}
		else if(by instanceof By.Name){
			executeJavaScriptFunction("enterTextByName(\""+by.getValue()+"\", \""+text+"\");",webView,frame);
		}
		else if(by instanceof By.ClassName){
			executeJavaScriptFunction("enterTextByClassName(\""+by.getValue()+"\", \""+text+"\");",webView,frame);
		}
		else if(by instanceof By.Text){
			executeJavaScriptFunction("enterTextByTextContent(\""+by.getValue()+"\", \""+text+"\");",webView,frame);
		}
		else if(by instanceof By.TagName){
			executeJavaScriptFunction("enterTextByTagName(\""+by.getValue()+"\", \""+text+"\");",webView,frame);
		}
	}

	private ArrayList <TextView> createAndReturnTextViewsFromWebElements(boolean javaScriptWasExecuted,View webView){
		ArrayList<TextView> webElementsAsTextViews = new ArrayList<>();
		if(javaScriptWasExecuted){
			for(WebElement webElement : mWebElementCreator.getWebElementsFromWebViews()){
				if(isWebElementSufficientlyShown(webElement,webView)){
					MockTextView textView = new MockTextView(mInst.getContext(), webElement.getText(),
							webElement.getLocationX(), webElement.getLocationY());
					webElementsAsTextViews.add(textView);
				}
			}
		}
		return webElementsAsTextViews;
	}

	private boolean executeJavaScript(final By by, boolean shouldClick,View webView,String frame) {
		if(by instanceof By.Id){
			return executeJavaScriptFunction("id(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,frame);
		}
		else if(by instanceof By.Xpath){
			return executeJavaScriptFunction("xpath(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,frame);
		}
		else if(by instanceof By.CssSelector){
			return executeJavaScriptFunction("cssSelector(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,frame);
		}
		else if(by instanceof By.Name){
			return executeJavaScriptFunction("name(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,frame);
		}
		else if(by instanceof By.ClassName){
			return executeJavaScriptFunction("className(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,frame);
		}
		else if(by instanceof By.Text){
			return executeJavaScriptFunction("textContent(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,frame);
		}
		else if(by instanceof By.TagName){
			return executeJavaScriptFunction("tagName(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,frame);
		}
		return false;
	}

	private boolean executeJavaScriptFunction(final String function, final View webView,String frame) {
		if(webView == null) {
			return false;
		}
		mWebElementCreator.prepareForStart();
		boolean finish = false;
		if (mInjector.injectTo(webView)) {
			final String javaScript = mJSCreator.createJavaScript(function,frame);
			mExecutor.executeJavaScript(javaScript,webView);
			finish = mWebElementCreator.waitForWebElementsToBeCreated();
		}
		mInjector.unInject();
		return finish;
	}

	private ArrayList<WebElement> getWebElementList(boolean javaScriptWasExecuted,
													boolean onlySufficientlyVisbile,View webView){
		ArrayList<WebElement> webElements = new ArrayList<>();

		if(javaScriptWasExecuted){
			for(WebElement webElement : mWebElementCreator.getWebElementsFromWebViews()){
				if(!onlySufficientlyVisbile){
					webElements.add(webElement);
				} else if(isWebElementSufficientlyShown(webElement,webView)){
					webElements.add(webElement);
				}
			}
		}
		return webElements;
	}
}