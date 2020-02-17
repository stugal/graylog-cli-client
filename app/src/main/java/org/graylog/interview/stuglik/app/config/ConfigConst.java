/**
 * 
 */
package org.graylog.interview.stuglik.app.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public final class ConfigConst {
	
	/**
	 * Default values
	 */
	public static final String GRAYLOG_HTTP_INPUT_URL_DEFAULT = "http://127.0.0.1:12201/gelf";
	public static final String GELF_VERSION_DEFAULT = "1.1";
	public static final String GELF_HOST_DEFAULT = "szymonstuglik.com";
	public static final String GELF_SHORT_MESSAGE_DEFAULT = "default short msg";
	public static final String GELF_FULL_MESSAGE_DEFAULT = "default full message";
	public static final String GELF_LEVEL_DEFAULT = "1";
	
	/**
	 * Property names
	 */
	public static final String GELF_VERSION_PROPERTY_NAME = "gelf.version";
	public static final String GELF_HOST_PROPERTY_NAME = "gelf.host";
	public static final String GELF_LEVEL_PROPERTY_NAME = "gelf.level";
	public static final String GELF_SHORT_MESSAGE_PROPERTY_NAME = "gelf.short.message";
	public static final String GELF_FULL_MESSAGE_PROPERTY_NAME = "gelf.full.message";
	public static final String GRAYLOG_HTTP_INPUT_URL_PROPERTY_NAME = "graylog.http.input.url";
	
	/**
	 * Below properties do not have defaults, default implementations are provided in this app
	 * and are initialised when custom implementations are not provided
	 */
	public static final String CUSTOM_GREYLOG_DISPATCHER_CLASS = "custom.graylog.dispatcher.class";
	public static final String CUSTOM_FILE_PROCESSOR_CLASS = "custom.file.processor.class";
	
	/**
	 * Property to default value mapping (same key used for prop file and hash map)
	 */
	private static final Map<String, String> defaults = new HashMap<>(); 
	
	static {
		defaults.put(GELF_VERSION_PROPERTY_NAME, GELF_VERSION_DEFAULT);
		defaults.put(GELF_HOST_PROPERTY_NAME, GELF_HOST_DEFAULT);
		defaults.put(GELF_LEVEL_PROPERTY_NAME, GELF_LEVEL_DEFAULT);
		defaults.put(GELF_SHORT_MESSAGE_PROPERTY_NAME, GELF_SHORT_MESSAGE_DEFAULT);
		defaults.put(GELF_FULL_MESSAGE_PROPERTY_NAME, GELF_FULL_MESSAGE_DEFAULT);
		defaults.put(GRAYLOG_HTTP_INPUT_URL_PROPERTY_NAME, GRAYLOG_HTTP_INPUT_URL_DEFAULT);
	}
	
	public static String getDefault(String key) {
		return defaults.get(key);
	}
	
	private ConfigConst() {
		// dont want this class instantiated, just a place holder for config values
	}
}
