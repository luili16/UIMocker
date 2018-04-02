package com.llx278.uimocker2;

import java.util.Hashtable;

/**
 * 代表一个webView中的元素
 *
 */

public class WebElement {
	
	private int locationX = 0;
	private int locationY = 0;
	private String id;
	private String text;
	private String name;
	private String className;
	private String tagName;
	private Hashtable<String, String> attributes;
	private String innerHtml;

	WebElement(String webId, String textContent, String name, String className, String tagName,
			   Hashtable<String, String> attributes,String innerHtml) {

		this.id = webId;
		this.text = textContent;
		this.name = name;
		this.className = className;
		this.tagName = tagName;
		this.attributes = attributes;
		this.innerHtml = innerHtml;

	}

	/**
	 * 返回这个WebElements在屏幕上的位置.
	 */

	public void getLocationOnScreen(int[] location) {

		location[0] = locationX;
		location[1] = locationY;
	}

	public void setLocationX(int locationX){
		this.locationX = locationX;
	}

	public void setLocationY(int locationY){
		this.locationY = locationY;
	}

	public int getLocationX(){
		return this.locationX;
	}

	public int getLocationY(){
		return this.locationY;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getText() {
		return text;
	}
	
	public void setTextContent(String textContent) {
		this.text = textContent;
	}

	public String getAttribute(String attributeName) {
		if (attributeName != null){
			return this.attributes.get(attributeName);
		}
		
		return null;
	}

	public void setAttributes(Hashtable<String,String> attributes) {
		this.attributes = attributes;
	}

	public String getInnerHtml() {
		return innerHtml;
	}

	public void setInnerHtml(String innerHtml) {
		this.innerHtml = innerHtml;
	}

	@Override
	public String toString() {
		return "WebElement{" +
				"locationX=" + locationX +
				", locationY=" + locationY +
				", id='" + id + '\'' +
				", text='" + text + '\'' +
				", name='" + name + '\'' +
				", className='" + className + '\'' +
				", tagName='" + tagName + '\'' +
				", attributes=" + attributes +
				", innerHtml='" + innerHtml + '\'' +
				'}';
	}
}
