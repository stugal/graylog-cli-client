/**
 * 
 */
package org.graylog.interview.stuglik.app.config;

import com.beust.jcommander.Parameter;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public final class CommandLineArgs {

	@Parameter(names = {"--file-path", "-fp"}, required =  true, description = "[Required] Path to file containing messages")
	public String FILE_PATH;

	@Parameter(names = {"--properties", "-prop"}, description = "[Optional] Properties file path")
	public String PROPERTIES_FILE_PATH;

	@Parameter(names = {"--help", "-h"}, description = "List of available parameters", help = true, order = 0)
	public boolean HELP;

	/** default url to greylog http input */
	public static final String GREYLOG_URL_DEFAULT = "http://localhost:9090";
	
	/** default path to properties file */
	public static final String PROPERTIES_FILE_PATH_DEFAULT = "./graylog_cli.properties";
}
