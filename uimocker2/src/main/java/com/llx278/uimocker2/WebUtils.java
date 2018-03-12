package com.llx278.uimocker2;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * Contains web related methods. Examples are:
 * enterTextIntoWebElement(), getWebTexts(), getWebElements().
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

public class WebUtils {

	public static final String DEFAULT_FRAME = "document";

	private ViewGetter viewGetter;
	private InstrumentationDecorator inst;
	WebElementCreator mWebElementCreator;
	private WebViewInjector mInjector;

	/**
	 * Constructs this object.
	 * 
	 * @param instrumentation the {@code Instrumentation} instance
	 * @param viewGetter the {@code ViewFetcher}
	 * @param sleeper the {@code Sleeper} instance
	 */

	public WebUtils(InstrumentationDecorator instrumentation, ViewGetter viewGetter, Sleeper sleeper){
		this.inst = instrumentation;
		this.viewGetter = viewGetter;
		mWebElementCreator = new WebElementCreator(sleeper);

		mInjector = new WebViewInjector(mWebElementCreator,instrumentation);
	}

	public ArrayList<WebElement> getWebElements(boolean onlySufficientlyVisible,Object webView,Object webChromeClient){
		boolean javaScriptWasExecuted = executeJavaScriptFunction("allWebElements();",webView);
		return getWebElements(javaScriptWasExecuted, onlySufficientlyVisible);
	}

	private ArrayList<WebElement> getWebElements(boolean javaScriptWasExecuted, boolean onlySufficientlyVisbile){
		ArrayList<WebElement> webElements = new ArrayList<>();

		if(javaScriptWasExecuted){
			for(WebElement webElement : mWebElementCreator.getWebElementsFromWebViews()){
				if(!onlySufficientlyVisbile){
					webElements.add(webElement);
				}
				else if(isWebElementSufficientlyShown(webElement)){
					webElements.add(webElement);
				}
			}
		}
		return webElements;
	}

	public final boolean isWebElementSufficientlyShown(WebElement webElement){
		final WebView webView = viewGetter.getFreshestView(viewGetter.getViewListByClass(WebView.class, true));
		final int[] xyWebView = new int[2];

		if(webView != null && webElement != null){
			webView.getLocationOnScreen(xyWebView);

			if(xyWebView[1] + webView.getHeight() > webElement.getLocationY())
				return true;
		}
		return false;
	}

	private boolean executeJavaScriptFunction(final String function, final Object webView) {

		if(webView == null) {
			return false;
		}
		// 对当前的webView进行注入
		mWebElementCreator.prepareForStart();
		boolean executeJsFinish = mInjector.injectAndExecuteJs(webView,function);
		return executeJsFinish && mWebElementCreator.waitForWebElementsToBeCreated();
	}
}