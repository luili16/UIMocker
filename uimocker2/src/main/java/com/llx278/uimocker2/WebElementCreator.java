package com.llx278.uimocker2;

import android.os.SystemClock;
import android.util.Log;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.robv.android.xposed.XposedBridge;

class WebElementCreator {

	private static final long DEFAULT_SLEEP_TIME = 200;
	private static final long DEFAULT_WAITING_TIME_OUT = 3000;
	private List<WebElement> webElements;
	private boolean isFinished = false;

	WebElementCreator(){
		webElements = new CopyOnWriteArrayList<WebElement>();
	}

	void prepareForStart(){
		setFinished(false);
		webElements.clear();
	}


	ArrayList<WebElement> getWebElementsFromWebViews(){
		return new ArrayList<>(webElements);
	}


	public boolean isFinished(){
		return isFinished;
	}


	void setFinished(boolean isFinished){
		this.isFinished = isFinished;
	}


	void createWebElementAndAddInList(String webData, float scale, int[] locationOfWebViewXY){
		WebElement webElement = createWebElementAndSetLocation(webData, scale,locationOfWebViewXY);

		if((webElement!=null)) {
			webElements.add(webElement);
		}
	}

	private void setLocation(WebElement webElement, float scale,int[] locationOfWebViewXY, int x, int y, int width, int height ){

		int locationX = (int) (locationOfWebViewXY[0] + (x + (Math.floor(width / 2))) * scale);
		int locationY = (int) (locationOfWebViewXY[1] + (y + (Math.floor(height / 2))) * scale);

		webElement.setLocationX(locationX);
		webElement.setLocationY(locationY);
	}

	private WebElement createWebElementAndSetLocation(String information, float scale,int[] locationOfWebViewxy){
		String regex = "\\#dkqjf765kdj09d\\#";
		String[] data = information.split(regex);
		String[] elements = null;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		String html = null;
		Hashtable<String, String> attributes = new Hashtable<String, String>();
		try{
			x = Math.round(Float.valueOf(data[5]));
			y = Math.round(Float.valueOf(data[6]));
			width = Math.round(Float.valueOf(data[7]));
			height = Math.round(Float.valueOf(data[8]));	
			elements = data[9].split("\\#\\$");
			html = data[10];
		}catch(Exception ignored){
		}

		if(elements != null) {
			for (int index = 0; index < elements.length; index++){
				String[] element = elements[index].split("::");
				if (element.length > 1) {
					attributes.put(element[0], element[1]);
				} else {
					attributes.put(element[0], element[0]);
				}
			}
		}

		WebElement webElement = null;

		try{
			webElement = new WebElement(data[0], data[1], data[2], data[3], data[4], attributes,html);
			setLocation(webElement,scale,locationOfWebViewxy,x,y,width,height);
		}catch(Exception ignored) {
			XposedBridge.log(ignored);
		}
		return webElement;
	}

	private void pause(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ignore) {
		}
	}


	boolean waitForWebElementsToBeCreated(){
		final long endTime = SystemClock.uptimeMillis() + DEFAULT_WAITING_TIME_OUT;

		while(SystemClock.uptimeMillis() < endTime){
			if(isFinished){
				return true;
			}
			pause(DEFAULT_SLEEP_TIME);
		}

		return false;
	}

}
