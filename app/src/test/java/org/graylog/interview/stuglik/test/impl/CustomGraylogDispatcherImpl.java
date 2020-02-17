package org.graylog.interview.stuglik.test.impl;

import org.graylog.interview.stuglik.api.graylog.AbstractGraylogDispatcher;

public class CustomGraylogDispatcherImpl extends AbstractGraylogDispatcher<CustomMessage> {

	private CustomMessage msg;
	
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void send(CustomMessage message) {
		this.msg = message;		
	}
	
	public CustomMessage getMessage() {
		return this.msg;
	}

}
