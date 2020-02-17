/**
 * 
 */
package org.graylog.interview.stuglik.app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.api.config.ConfigProvider;
import org.graylog.interview.stuglik.files.FileUtils;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public class ConfigManager implements ConfigProvider {
	
	private static final Logger LOG = LogManager.getLogger(ConfigManager.class);
	
	/** Command line arguments */
	private CommandLineArgs cmdLineArgs;
	
	/** Properties parsed from custom or default location (if none exists, defaults will be used) */
	private Properties props;

	/**
	 * @param cmdLineArgs
	 */
	public ConfigManager(CommandLineArgs cmdLineArgs) {
		this.cmdLineArgs = cmdLineArgs;
	}
	
	public void loadProperties() {
		
		String propPath = CommandLineArgs.PROPERTIES_FILE_PATH_DEFAULT;
		
		if (this.cmdLineArgs.PROPERTIES_FILE_PATH != null 
				&& !this.cmdLineArgs.PROPERTIES_FILE_PATH.trim().isEmpty()
				&& FileUtils.fileExists(this.cmdLineArgs.PROPERTIES_FILE_PATH.trim())) {
			propPath = this.cmdLineArgs.PROPERTIES_FILE_PATH.trim();
		}
		
		if (!FileUtils.fileExists(propPath)) {
			LOG.warn("No properties file provided nor detected in default location, using default values.");
			return;
		}
		
		LOG.info("Loading properties from: '" + propPath + "'...");
		
		try (InputStream input = new FileInputStream(propPath)) {
            this.props = new Properties();

            // load a properties file
            props.load(input);

        } catch (IOException | IllegalArgumentException e) {
            LOG.error("Error during properties file load, using default values.", e);
        }
	}

	@Override
	public String getProperty(String propName) {
		String value = null;
		if (this.props != null) {
			value = this.props.getProperty(propName);
		}
		
		if (value == null) {
			value = ConfigConst.getDefault(propName);
			
			if (value == null) {
				LOG.warn("No default value defined for '{}' property.", propName);
			} else {
				LOG.debug("Using default '{}' for '{}' property.", value, propName);
			}			
		}
		
		return value;
	}

	@Override
	public File getFile() {
		return new File(this.cmdLineArgs.FILE_PATH);
	}	
}
