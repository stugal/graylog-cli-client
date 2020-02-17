/**
 * 
 */
package org.graylog.interview.stuglik.api.config;

import java.io.File;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public interface ConfigProvider {
	
	public String getProperty(String propName);
	
	public File getFile();
	
}
