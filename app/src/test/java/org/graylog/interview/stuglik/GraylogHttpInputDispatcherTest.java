/**
 * 
 */
package org.graylog.interview.stuglik;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.api.config.ConfigProvider;
import org.graylog.interview.stuglik.app.graylog.GraylogHttpInputDispatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
@RunWith(JUnit4.class)
public class GraylogHttpInputDispatcherTest {
	
	private static final Logger LOG = LogManager.getLogger(GraylogHttpInputDispatcherTest.class);
	
	@Rule
	public MockServerRule mockServerRule = new MockServerRule(this, 8080);
 
	private GraylogHttpInputDispatcher dispatcher;
	
	private MockServerClient mockServerClient;
	
	private static final String MESSAGE_JSON = "{\"version\":\"1.1\"}";
 
	@Test
	public void dispatcher_should_send_json_msg_to_backend() throws Exception {
		// setting behaviour for test case
		LOG.info("Instantiating MOCK REST server...");
		mockServerClient.when(HttpRequest.request("/gelf")).respond(HttpResponse.response().withStatusCode(200));
  
		this.dispatcher = new GraylogHttpInputDispatcher();
		ConfigProvider cfg = new ConfigProvider() {

			@Override
			public String getProperty(String propName) {
				return "http://localhost:8080/gelf";
			}

			@Override
			public File getFile() {
				// TODO Auto-generated method stub
				return null;
			}			
		};
		this.dispatcher.setConfigProvider(cfg);
		this.dispatcher.init();
		this.dispatcher.start();
		this.dispatcher.submitMessage(MESSAGE_JSON);
		this.dispatcher.notifyEndOfFileProcessing();
		this.dispatcher.join();
		
		// verify server has received exactly one request
		mockServerClient.verify(HttpRequest.request("/gelf"), VerificationTimes.once()).verify(HttpRequest.request().withBody(MESSAGE_JSON));
	}	
}
