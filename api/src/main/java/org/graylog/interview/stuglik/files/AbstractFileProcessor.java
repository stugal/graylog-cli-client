/**
 * 
 */
package org.graylog.interview.stuglik.files;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.api.config.ConfigProvider;
import org.graylog.interview.stuglik.api.graylog.AbstractGraylogDispatcher;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public abstract class AbstractFileProcessor extends Thread {
	
	private static final Logger LOG = LogManager.getLogger(AbstractFileProcessor.class);

	/** Config Provider */
	private ConfigProvider configProvider;

	@SuppressWarnings("rawtypes")
	private AbstractGraylogDispatcher dispatcher;

	/** Threa Run Flag */
	private final AtomicBoolean runFlag = new AtomicBoolean(true);

	@Override
	public void run() {
		processFile(this.configProvider.getFile());
		LOG.debug("File processor thread finished.");
	}

	/**
	 * @param dispatcher the dispatcher to set
	 */
	@SuppressWarnings("rawtypes")
	public final void setGraylogDispatcher(AbstractGraylogDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@SuppressWarnings("rawtypes")
	public final AbstractGraylogDispatcher getGraylogDispatcher() {
		return this.dispatcher;
	}

	/**
	 * @param configProvider the configProvider to set
	 */
	public final void setConfigProvider(ConfigProvider configProvider) {
		this.configProvider = configProvider;
	}

	/**
	 * @return config provider
	 */
	public final ConfigProvider getConfigProvider() {
		return this.configProvider;
	}

	/**
	 * Stop the dispatcher thread
	 * 
	 * @param interrupt whether to interrupt or not
	 */
	public void stopProcessor() {
		this.runFlag.set(false);
	}
	
	/**
	 * @return true if thread is to continue, false otherwise
	 */
	protected boolean isRun() {
		return this.runFlag.get();
	}
	
	/**
	 * This method implements file specific parsing method, it should also
	 * construct and dispatch GELF message in the format expected by
	 * registered {@link AbstractGraylogDispatcher}
	 * 
	 * @param file
	 */
	protected abstract void processFile(File file);
	
	/**
	 * Initialize file parser, i.e. retrieve required config data
	 * @throws Exception
	 */
	public abstract void init() throws Exception;
}
