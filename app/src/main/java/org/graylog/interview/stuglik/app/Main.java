package org.graylog.interview.stuglik.app;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.app.config.CommandLineArgs;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * 
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 */
public class Main 
{
	private static final Logger LOG = LogManager.getLogger(Main.class);

	public static void main( String[] args ) throws IOException
	{
		GraylogCliClient app = new GraylogCliClient();				
		CommandLineArgs cmdLineArgs = new CommandLineArgs(); 
		JCommander commander = JCommander.newBuilder()
				.addObject(cmdLineArgs)
				.build();
		
		try {
			commander.parse(args);
		} catch (ParameterException e) {
			LOG.fatal("Invalid or missing parameter, use -h or --help for list of available parameters. Exiting.");
			e.usage();
			return;
		}
		
		if (cmdLineArgs.HELP) {
			commander.usage();
			return;
		}
		
		app.run(cmdLineArgs);
	}
}
