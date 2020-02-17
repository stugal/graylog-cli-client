package org.graylog.interview.stuglik.test.impl;

public class CustomMessage {

	private String stringValue;
	
	private int intValue;
	
	public CustomMessage() {
		super();
	}
	public CustomMessage(String stringValue, int intValue) {
		super();
		this.stringValue = stringValue;
		this.intValue = intValue;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	
	public int getIntValue() {
		return intValue;
	}
	
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
}
