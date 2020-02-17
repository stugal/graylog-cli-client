package org.graylog.interview.stuglik;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.app.GraylogCliClient;
import org.graylog.interview.stuglik.app.config.CommandLineArgs;
import org.graylog.interview.stuglik.test.impl.CustomGraylogDispatcherImpl;
import org.graylog.interview.stuglik.test.impl.CustomMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
@RunWith(JUnit4.class)
public class GraylogCliClientTest {

	private static final Logger LOG = LogManager.getLogger(GraylogCliClientTest.class);

	@Test
	public void test_custom_fileprocessor_and_dispatcher_impl() throws InterruptedException {	
		LOG.info("Testing custom file processor and dispatcher implementations...");
		GraylogCliClient client = new GraylogCliClient();
		CommandLineArgs args = new CommandLineArgs();

		ClassLoader classLoader = getClass().getClassLoader();
		File propFile = new File(classLoader.getResource("test-gryalog-cli-client-custom-impl.properties").getFile());	
		File jsonFile = new File(classLoader.getResource("test-graylog-cli-client-file-to-parse.json").getFile());
		args.PROPERTIES_FILE_PATH = propFile.getPath();
		args.FILE_PATH = jsonFile.getPath();
		
		client.run(args);

		Field privateStringField;
		CustomGraylogDispatcherImpl dispatcher = null;
		try {
			privateStringField = GraylogCliClient.class.getDeclaredField("dispatcher");
			privateStringField.setAccessible(true);
			dispatcher = (CustomGraylogDispatcherImpl) privateStringField.get(client);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			LOG.error("Error while accessing private field of GraylogCliClient class.", e);
			fail();
		}
		
		// wait up to 3 s for the dispatcher and file processor threads to finish
		long start = System.currentTimeMillis();
		CustomMessage recvdMessage = null;
		while (System.currentTimeMillis() - start < 3000) {
			if (dispatcher != null && dispatcher.getMessage() != null) {
				recvdMessage = dispatcher.getMessage();
				break;
			}
			
			Thread.sleep(100);
		}
		assertNotNull(recvdMessage);
		assertTrue(recvdMessage.getIntValue() == 5);
		assertTrue(recvdMessage.getStringValue().equals("sample_value"));
	}	
}
