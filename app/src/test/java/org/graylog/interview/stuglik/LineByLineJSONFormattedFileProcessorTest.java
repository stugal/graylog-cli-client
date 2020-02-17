/**
 * 
 */
package org.graylog.interview.stuglik;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.api.config.ConfigProvider;
import org.graylog.interview.stuglik.api.graylog.AbstractGraylogDispatcher;
import org.graylog.interview.stuglik.app.config.ConfigConst;
import org.graylog.interview.stuglik.app.files.LineByLineJSONFormattedFileProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
@RunWith(JUnit4.class)
public class LineByLineJSONFormattedFileProcessorTest {
	
	private LineByLineJSONFormattedFileProcessor fileProcessor;
	
	private AbstractGraylogDispatcher<String> dispatcher;
	
	private String receivedMessage;
	
	private static final Logger LOG = LogManager.getLogger(AbstractGraylogDispatcher.class);
	
	private File file;
	
	private ClassLoader classLoader;
	
	private int numberOfMessagesReceivedByDispatcher = 0;
	
	private final String expectedMessage = "{\"_ClientRequestBytes\":889,\"_ClientStatus\":403,\""
			+ "_DestinationIP\":\"115.242.153.30\",\"short_message\":\"default short msg\",\"level\""
			+ ":\"1\",\"_ClientIPClass\":\"noRecord\",\"_ClientRequestURI\":\"\\/search\",\"_EdgeStartTimestamp\""
			+ ":1576929197,\"_ClientRequestUserAgent\":\"Mozilla\\/5.0 (compatible, MSIE 11, "
			+ "Windows NT 6.3; Trident\\/7.0; rv:11.0) like Gecko\",\"_OriginResponseTime\":337000000,\"_"
			+ "ClientRequestReferer\":\"graylog.org\",\"version\":\"1.1\",\"_ClientDeviceType\":\"desktop\",\"_"
			+ "ClientSrcPort\":122,\"full_message\":\"default full message\",\"_ClientIP\":\"11.73.87.52\",\"_"
			+ "EdgeServerIP\":\"156.20.151.71\",\"host\":\"szymonstuglik.com\",\"_OriginResponseBytes\":821}"; 
	
	@Before
	public void init() throws Exception {
		this.classLoader = getClass().getClassLoader();
		LOG.info("Instantiating LineByLineJSONFormattedFileProcessor...");
		ConfigProvider configProvider = new ConfigProvider() {

			@Override
			public String getProperty(String propName) {
				return ConfigConst.getDefault(propName);
			}

			@Override
			public File getFile() {
				return file;
			}			
		};
		
		this.dispatcher = new AbstractGraylogDispatcher<String>() {
			@Override
			public void init() throws Exception {	
			}

			@Override
			protected void send(String message) {
				receivedMessage = message;
				numberOfMessagesReceivedByDispatcher++;
			}
		};
		
		LOG.info("Starting mock dispatcher...");
		this.dispatcher.start();
		
		this.fileProcessor = new LineByLineJSONFormattedFileProcessor();		
		this.fileProcessor.setConfigProvider(configProvider);
		this.fileProcessor.setGraylogDispatcher(this.dispatcher);
		this.fileProcessor.init();				
	}
	
	@Test
	public void test_file_processing() throws InterruptedException {
		LOG.info("Testing LineByLineJSONFormattedFileProcessor with proper input file...");
		
		this.file = new File(classLoader.getResource("test-message.txt").getFile());
		
		// start file processor
		this.fileProcessor.start();
		
		// wait for both threads to finish
		this.fileProcessor.join();
		this.dispatcher.join();
		
		// confirm message received by dispatcher looks as expected (was properly parsed by file processor)
		assertTrue(this.expectedMessage.equals(this.receivedMessage));
	}
	
	@Test
	public void test_file_processing_malformed_line() throws InterruptedException {
		LOG.info("Testing LineByLineJSONFormattedFileProcessor with malformed line present in the input file...");
		
		this.file = new File(classLoader.getResource("test-message-broken-line.txt").getFile());
		
		// start file processor
		this.fileProcessor.start();
		
		// wait for both threads to finish
		this.fileProcessor.join();
		this.dispatcher.join();
		
		// file processor should only parse and submitt two messages
		assertTrue(this.numberOfMessagesReceivedByDispatcher == 2);
	}

}
