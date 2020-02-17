/**
 * 
 */
package org.graylog.interview.stuglik.app.graylog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.api.graylog.AbstractGraylogDispatcher;
import org.graylog.interview.stuglik.app.config.ConfigConst;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public class GraylogHttpInputDispatcher extends AbstractGraylogDispatcher<String> {

	private static final Logger LOG = LogManager.getLogger(GraylogHttpInputDispatcher.class);
	
	private String graylogInputUrl;

	@Override
	protected void send(String message) {		
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Sending message {} ...", message);
			}
			Unirest.post(this.graylogInputUrl).body(message).asJson();
		} catch (UnirestException e) {
			LOG.error("Error during message dispatching.", e);
		}
	}

	@Override
	public void init() throws Exception {
		this.graylogInputUrl = getConfigProvider().getProperty(ConfigConst.GRAYLOG_HTTP_INPUT_URL_PROPERTY_NAME);
	}
}
