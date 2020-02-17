/**
 * 
 */
package org.graylog.interview.stuglik.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.api.graylog.AbstractGraylogDispatcher;
import org.graylog.interview.stuglik.app.config.CommandLineArgs;
import org.graylog.interview.stuglik.app.config.ConfigConst;
import org.graylog.interview.stuglik.app.config.ConfigManager;
import org.graylog.interview.stuglik.app.files.LineByLineJSONFormattedFileProcessor;
import org.graylog.interview.stuglik.app.graylog.GraylogHttpInputDispatcher;
import org.graylog.interview.stuglik.files.AbstractFileProcessor;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public class GraylogCliClient {

	private static final Logger LOG = LogManager.getLogger(GraylogCliClient.class);

	/** Config manager */
	private ConfigManager cfgManager;

	/** 
	 * Graylog dispatcher, if custom implementation class is not defined in config data,
	 * sample {@link GraylogHttpInputDispatcher} will be initialized 
	 */
	@SuppressWarnings("rawtypes")
	private AbstractGraylogDispatcher dispatcher;
	
	/**
	 * File processor, if custom implementation class is not defined in config data,
	 * simple line-by-line json formatted file processor will be initialized
	 */
	private AbstractFileProcessor fileProcessor;

	public void run(CommandLineArgs cmdArgs) {
		this.cfgManager = new ConfigManager(cmdArgs);

		// load configuration
		this.cfgManager.loadProperties();

		// initialize Graylog dispatcher
		initializeGraylogDispatcher();

		// initialize file processor
		initializeFileProcessor();
		
		LOG.info("GraylogCliClient finished.");
	}

	private void initializeFileProcessor() {
		// check if custom File Processor is provided and initializable
		String customFileProcessor = this.cfgManager.getProperty(ConfigConst.CUSTOM_FILE_PROCESSOR_CLASS);
		if (customFileProcessor == null || customFileProcessor.trim().isEmpty()) {
			this.fileProcessor = new LineByLineJSONFormattedFileProcessor();
		} else {
			try {
				LOG.debug("Instantiating custom file processor, using '{}' class...", customFileProcessor);
				this.fileProcessor = (AbstractFileProcessor) Class.forName(customFileProcessor).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				LOG.fatal("Error while instantiating '" + customFileProcessor + "' file processor, exiting.", e);
				shutdown(1);
			}
		}

		this.fileProcessor.setConfigProvider(this.cfgManager);
		this.fileProcessor.setGraylogDispatcher(this.dispatcher);
		try {
			this.fileProcessor.init();
		} catch (Exception e) {
			LOG.fatal("Error while initializing '" + customFileProcessor + "' file processor, exiting.", e);
			shutdown(1);
		}

		// start file processor
		this.fileProcessor.start();
	}

	private void initializeGraylogDispatcher() {
		// check if custom Graylog Dispatcher is provided and initializable
		String customGraylogDispatcher = this.cfgManager.getProperty(ConfigConst.CUSTOM_GREYLOG_DISPATCHER_CLASS);
		if (customGraylogDispatcher == null || customGraylogDispatcher.trim().isEmpty()) {
			this.dispatcher = new GraylogHttpInputDispatcher();
		} else {
			try {
				LOG.debug("Instantiating custom dispatcher, using '{}' class...", customGraylogDispatcher);
				this.dispatcher = (AbstractGraylogDispatcher<?>) Class.forName(customGraylogDispatcher).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				LOG.fatal("Error while instantiating '" + customGraylogDispatcher + "' Graylog dispatcher, exiting.", e);
				shutdown(1);
			}
		}

		this.dispatcher.setConfigProvider(this.cfgManager);
		try {
			this.dispatcher.init();
		} catch (Exception e) {
			LOG.fatal("Error while initializing '" + customGraylogDispatcher + "' Graylog dispatcher, exiting.", e);
			shutdown(1);
		}

		// start dispatching thread
		this.dispatcher.start();
	}
	
	private void shutdown(int code) {
		if (this.fileProcessor != null && this.fileProcessor.isAlive()) {
			LOG.debug("Shutting down File Processor...");
			this.fileProcessor.stopProcessor();
			try {
				this.fileProcessor.join(1000);
			} catch (InterruptedException e) {
				LOG.warn("File processor thread interrupted.", e);
			}
		}
		
		if (this.dispatcher != null && this.dispatcher.isAlive()) {
			LOG.debug("Shutting down Dispatcher...");
			this.dispatcher.stopDispatcher(true);
			try {
				this.dispatcher.join(1000);
			} catch (InterruptedException e) {
				LOG.warn("Dispatcher thread interrupted.", e);
			}
		}
		
		System.exit(code);
	}
}
