package com.llx278.uimocker2;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Contains web related methods. Examples are:
 * enterTextIntoWebElement(), getWebTexts(), getWebElements().
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

public class WebUtils {

	private ViewGetter viewGetter;
	private InstrumentationDecorator inst;
	RobotiumWebClient robotiumWebCLient;
	WebElementCreator webElementCreator;
	WebChromeClient originalWebChromeClient = null;


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
		webElementCreator = new WebElementCreator(sleeper);
		robotiumWebCLient = new RobotiumWebClient(instrumentation, webElementCreator);
	}

	public ArrayList<WebElement> getWebElements(boolean onlySufficientlyVisible,Object webView,Object webChromeClient){
		boolean javaScriptWasExecuted = executeJavaScriptFunction("allWebElements();",null,null);

		return getWebElements(javaScriptWasExecuted, onlySufficientlyVisible);
	}

	private ArrayList<WebElement> getWebElements(boolean javaScriptWasExecuted, boolean onlySufficientlyVisbile){
		ArrayList<WebElement> webElements = new ArrayList<WebElement>();

		if(javaScriptWasExecuted){
			for(WebElement webElement : webElementCreator.getWebElementsFromWebViews()){
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

	private boolean executeJavaScriptFunction(final String function, final Object webView, Object webChromeClient) {

		if(webView == null) {
			return false;
		}

		/*final String javaScript = setWebFrame(prepareForStartOfJavascriptExecution(webView));

		inst.runOnMainSync(new Runnable() {
			public void run() {
				if (webView instanceof WebView) {
					((WebView) webView).loadUrl("javascript:" + javaScript + function);
				}
			}
		});*/

		return true;
	}

	private String setWebFrame(String javascript){
		String frame = "document";

		if(frame.isEmpty() || frame.equals("document")){
			return javascript;
		}
		javascript = javascript.replaceAll(Pattern.quote("document, "), "document.getElementById(\""+frame+"\").contentDocument, ");
		javascript = javascript.replaceAll(Pattern.quote("document.body, "), "document.getElementById(\""+frame+"\").contentDocument, ");
		return javascript;
	}

	/**
	 * Prepares for start of JavaScript execution
	 * 
	 * @return the JavaScript as a String
	 */
	private String prepareForStartOfJavascriptExecution(WebView webViews) {
		webElementCreator.prepareForStart();

		WebChromeClient currentWebChromeClient = getCurrentWebChromeClient();

		if(currentWebChromeClient != null && !currentWebChromeClient.getClass().isAssignableFrom(RobotiumWebClient.class)){
			originalWebChromeClient = currentWebChromeClient;	
		}
		robotiumWebCLient.enableJavascriptAndSetRobotiumWebClient(webViews, originalWebChromeClient);
		return getJavaScriptAsString();
	}
	
	/**
	 * Returns the current WebChromeClient through reflection
	 * 
	 * @return the current WebChromeClient
	 * 
	 */

	private WebChromeClient getCurrentWebChromeClient(){
		WebChromeClient currentWebChromeClient = null;

		Object currentWebView = viewGetter.getFreshestView(viewGetter.getViewListByClass(WebView.class, true));

		try{
			if (android.os.Build.VERSION.SDK_INT >= 19) {
				Object mClientAdapter = new Reflect(currentWebView).field("mContentsClientAdapter").out(Object.class);
				currentWebChromeClient = new Reflect(mClientAdapter).field("mWebChromeClient").out(WebChromeClient.class);
			}
			else {
				Object mCallbackProxy = new Reflect(currentWebView).field("mCallbackProxy").out(Object.class);
				currentWebChromeClient = new Reflect(mCallbackProxy).field("mWebChromeClient").out(WebChromeClient.class);
			}
		}catch(Exception ignored){}

		return currentWebChromeClient;
	}

	/**
	 * Returns the JavaScript file RobotiumWeb.js as a String
	 *  
	 * @return the JavaScript file RobotiumWeb.js as a {@code String} 
	 */

	private String getJavaScriptAsString() {
		InputStream fis = null;
		try {
			fis = new FileInputStream("/data/local/tmp/RobotiumWeb.js");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		StringBuffer javaScript = new StringBuffer();

		try {
			BufferedReader input =  new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while (( line = input.readLine()) != null){
				javaScript.append(line);
				javaScript.append("\n");
			}
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return javaScript.toString();
	}
}