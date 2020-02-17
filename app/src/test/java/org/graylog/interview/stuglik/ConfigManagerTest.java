/**
 * 
 */
package org.graylog.interview.stuglik;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.app.config.CommandLineArgs;
import org.graylog.interview.stuglik.app.config.ConfigConst;
import org.graylog.interview.stuglik.app.config.ConfigManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
@RunWith(JUnit4.class)
public class ConfigManagerTest {
	
	private static final Logger LOG = LogManager.getLogger(ConfigManagerTest.class);
	
	private ConfigManager cfgManager;

	@Before
    public void init() {
		LOG.info("Instantiating ConfigManager...");
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("test-config-manager.properties").getFile());
		
		CommandLineArgs args = new CommandLineArgs();
		args.PROPERTIES_FILE_PATH = file.getPath();
        this.cfgManager = new ConfigManager(args);
        this.cfgManager.loadProperties();
    }
		
	@Test
	public void test_properties_parsing() {
		LOG.info("Testing if proper value is obtained for 'prop1' key...");
		String value1 = this.cfgManager.getProperty("prop1");
		assertTrue("value1".equals(value1));
	}
	
	@Test
	public void test_null_property() {
		LOG.info("Testing null properties...");
		String nonExistentValue = this.cfgManager.getProperty("random_key");
		assertNull(nonExistentValue);
	}
	
	@Test
	public void test_default_property() {
		LOG.info("Testing default property resolution...");
		String gelfHost = this.cfgManager.getProperty(ConfigConst.GELF_HOST_PROPERTY_NAME);
		assertTrue(ConfigConst.GELF_HOST_DEFAULT.equals(gelfHost));
	}
}
