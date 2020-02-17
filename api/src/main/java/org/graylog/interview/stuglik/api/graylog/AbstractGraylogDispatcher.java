/**
 * 
 */
package org.graylog.interview.stuglik.api.graylog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.api.config.ConfigProvider;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public abstract class AbstractGraylogDispatcher<T> extends Thread {
	
	private static final Logger LOG = LogManager.getLogger(AbstractGraylogDispatcher.class);
	
	/** Config Provider */
	private ConfigProvider configProvider;
	
	/** Message Queue */
	private final BlockingQueue<T> messageQueue;
	
	/** Thread Run Flag */
	private final AtomicBoolean runFlag = new AtomicBoolean(true);
	
	/** Flag indicating whether file processor has finished or not */
	private final AtomicBoolean fileProcessorFinished = new AtomicBoolean(false);
	
	/**
	 * 
	 */
	public AbstractGraylogDispatcher() {
		super();
		this.messageQueue = new LinkedBlockingQueue<T>();
	}

	@Override
	public void run() {
		LOG.debug("Dispatcher thread starting...");
	
		long start = System.currentTimeMillis();
		long messageCount = 0;
		// break out of the loop if either file processor has finished and list is empty
		// or thread has been intentionally stopped
		while ((!this.fileProcessorFinished.get() || !this.messageQueue.isEmpty()) 
				&& (this.runFlag.get() && !Thread.currentThread().isInterrupted())) {
			try {
				// poll for up to 100ms to avoid race condition
				T message = this.messageQueue.poll(100, TimeUnit.MILLISECONDS);
				if (message == null) 
				{
					continue;
				}
				send(message);
				messageCount++;
			} catch (InterruptedException e) {
				this.runFlag.set(false);
			}
		}
		long stop = System.currentTimeMillis();
		long elapsed = stop - start;
		
		LOG.debug("Dispatcher thread finished, '{}' message(s) send in '{}' [ms].", messageCount, elapsed);		
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
	 * @param message to be added to the send queue
	 * @return true if message was added, false otherwise (due to lack of space in the queue)
	 */
	public boolean submitMessage(T message) {
		return this.messageQueue.offer(message);
	}
	
	/**
	 * Stop the dispatcher thread
	 * 
	 * @param interrupt whether to interrupt or not
	 */
	public void stopDispatcher(boolean interrupt) {
		this.runFlag.set(false);
		if (interrupt) {
			interrupt();
		}
	}
	
	/**
	 * This method should be called by file processor once its done processing or error occurs
	 */
	public void notifyEndOfFileProcessing() {
		this.fileProcessorFinished.set(true);
	}
	
	/**
	 * Initialize this dispatcher i.e. retrieve Graylog Input URL from config manager
	 * 
	 * @throws Exception
	 */
	public abstract void init() throws Exception;
		
	/**
	 * Send message to Graylog Input
	 * 
	 * @param message
	 */
	protected abstract void send(T message);
}
