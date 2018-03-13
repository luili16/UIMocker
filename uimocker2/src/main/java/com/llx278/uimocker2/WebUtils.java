package com.llx278.uimocker2;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;


/**
 * Contains web related methods. Examples are:
 * enterTextIntoWebElement(), getWebTexts(), getWebElementList().
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

public class WebUtils {

	public static final String DEFAULT_FRAME = "document";

	private WebElementCreator mWebElementCreator;
	private WebViewInjector mInjector;
	private WebViewExecutor mExecutor;
	private JavaScriptCreator mJSCreator;
	private InstrumentationDecorator mInst;

	public WebUtils(InstrumentationDecorator instrumentation, ViewGetter viewGetter, Sleeper sleeper){
		mInst = instrumentation;
		mWebElementCreator = new WebElementCreator(sleeper);
		mInjector = new WebViewInjector(mWebElementCreator);
		mExecutor = new WebViewExecutor(instrumentation);
		mJSCreator = new JavaScriptCreator();
	}

	/**
	 * 从给定的webview里面取出所有的Web元素，默认的frame是{@link WebUtils#DEFAULT_FRAME}
	 * @param onlySufficientlyVisible true 只抓取当前屏幕上面可见的 false 抓取由frame指定的所有web元素
	 * @param webView 指定的webview
	 * @param timeout 超时时间
	 * @return 抓取后所有元素的列表
	 */
	public ArrayList<WebElement> getWebElementList(boolean onlySufficientlyVisible, View webView, long timeout) {
		return getWebElementList(onlySufficientlyVisible,webView,timeout,DEFAULT_FRAME);
	}

	public ArrayList<WebElement> getWebElementList(By by, boolean onlySufficientlyVisbile,View webView,long timeout) {
		return getWebElementList(by,onlySufficientlyVisbile,webView,timeout,DEFAULT_FRAME);
	}

	/**
	 * 从给定的webview里面取出所有的Web元素
	 * @param onlySufficientlyVisible true 只抓取当前屏幕上面可见的 false 抓取由frame指定的所有web元素
	 * @param webView 指定的webview
	 * @param timeout 超时时间
	 * @param frame 默认是document
	 * @return 抓取后所有元素的列表
	 */
	public ArrayList<WebElement> getWebElementList(boolean onlySufficientlyVisible, View webView, long timeout, String frame){
		boolean javaScriptWasExecuted = executeJavaScriptFunction("allWebElements();",webView,timeout,frame);
		return getWebElementList(javaScriptWasExecuted, onlySufficientlyVisible,webView);
	}

	public ArrayList<WebElement> getWebElementList(By by, boolean onlySufficientlyVisbile,View webView,long timeout,String frame){
		boolean javaScriptWasExecuted = executeJavaScript(by,false,webView,timeout,frame);
		return getWebElementList(javaScriptWasExecuted,onlySufficientlyVisbile,webView);
	}

	public final boolean isWebElementSufficientlyShown(WebElement webElement,View webView){

		final int[] xyWebView = new int[2];
		if(webView != null && webElement != null){
			webView.getLocationOnScreen(xyWebView);
			if(xyWebView[1] + webView.getHeight() > webElement.getLocationY())
				return true;
		}
		return false;
	}

	public ArrayList<TextView> getTextViewListFromWebView(View webView,long timeout) {
		return getTextViewListFromWebView(webView,timeout,DEFAULT_FRAME);
	}

	public ArrayList<TextView> getTextViewListFromWebView(View webView,long timeout,String frame) {
		boolean javaScriptWasExecuted = executeJavaScriptFunction("allTexts();",webView,timeout,frame);
		return createAndReturnTextViewsFromWebElements(javaScriptWasExecuted,webView);
	}

	public boolean clickOnWebElement(By by,View webView,boolean useJavaScriptToClick) {
		return executeJavaScript(by,true,webView,5000,DEFAULT_FRAME);
	}

	public void enterTextIntoWebElement(final By by, final String text,View webView,long timeout,String frame){
		if(by instanceof By.Id){
			executeJavaScriptFunction("enterTextById(\""+by.getValue()+"\", \""+text+"\");",webView,timeout,frame);
		}
		else if(by instanceof By.Xpath){
			executeJavaScriptFunction("enterTextByXpath(\""+by.getValue()+"\", \""+text+"\");",webView,timeout,frame);
		}
		else if(by instanceof By.CssSelector){
			executeJavaScriptFunction("enterTextByCssSelector(\""+by.getValue()+"\", \""+text+"\");",webView,timeout,frame);
		}
		else if(by instanceof By.Name){
			executeJavaScriptFunction("enterTextByName(\""+by.getValue()+"\", \""+text+"\");",webView,timeout,frame);
		}
		else if(by instanceof By.ClassName){
			executeJavaScriptFunction("enterTextByClassName(\""+by.getValue()+"\", \""+text+"\");",webView,timeout,frame);
		}
		else if(by instanceof By.Text){
			executeJavaScriptFunction("enterTextByTextContent(\""+by.getValue()+"\", \""+text+"\");",webView,timeout,frame);
		}
		else if(by instanceof By.TagName){
			executeJavaScriptFunction("enterTextByTagName(\""+by.getValue()+"\", \""+text+"\");",webView,timeout,frame);
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

	private boolean executeJavaScript(final By by, boolean shouldClick,Object webView,long timeout,String frame) {
		if(by instanceof By.Id){
			return executeJavaScriptFunction("id(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,timeout,frame);
		}
		else if(by instanceof By.Xpath){
			return executeJavaScriptFunction("xpath(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,timeout,frame);
		}
		else if(by instanceof By.CssSelector){
			return executeJavaScriptFunction("cssSelector(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,timeout,frame);
		}
		else if(by instanceof By.Name){
			return executeJavaScriptFunction("name(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,timeout,frame);
		}
		else if(by instanceof By.ClassName){
			return executeJavaScriptFunction("className(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,timeout,frame);
		}
		else if(by instanceof By.Text){
			return executeJavaScriptFunction("textContent(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,timeout,frame);
		}
		else if(by instanceof By.TagName){
			return executeJavaScriptFunction("tagName(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");",webView,timeout,frame);
		}
		return false;
	}

	private boolean executeJavaScriptFunction(final String function, final Object webView,long timeout,String frame) {
		if(webView == null) {
			return false;
		}
		mWebElementCreator.prepareForStart();
		boolean finish = false;
		if (mInjector.inject(webView,function,frame)) {
			final String javaScript = mJSCreator.createJavaScript(function,frame);
			mExecutor.loadUrl(javaScript,webView);
			finish = mWebElementCreator.waitForWebElementsToBeCreated(timeout);
		}
		mInjector.unInject();
		return finish;
	}

	private ArrayList<WebElement> getWebElementList(boolean javaScriptWasExecuted, boolean onlySufficientlyVisbile,View webView){
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