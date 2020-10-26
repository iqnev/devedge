package c8y.pi4.agent.core.driver.impl;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

import c8y.pi4.agent.core.driver.Configurable;
import c8y.pi4.agent.core.driver.Driver;
import c8y.pi4.agent.core.driver.OperationExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 10.09.2020
 *
 */
@Slf4j
public abstract class PollingDriver implements Driver, Configurable, Runnable {

	private final ScheduledExecutorService executorService;

	private volatile ScheduledFuture<?> scheduledFuture;

	private final long defaultPollingInterval;

	private long actualPollingInterval;

	private Platform platform;

	public PollingDriver(final String type, final long defaultPollingInterval) {
		log.info("PollingDriver(final String type, final long defaultPollingInterval)");
		this.executorService = newSingleThreadScheduledExecutor(new PollingDriverThreadFactory(type + "Poller"));
		this.defaultPollingInterval = defaultPollingInterval;
		this.actualPollingInterval = defaultPollingInterval;
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
	}

	@Override
	public void start() {
		log.info("public void start()");
		scheduleMeasurements();
	}

	private void scheduleMeasurements() {
		log.info("void scheduleMeasurements(): " + actualPollingInterval);
		if (actualPollingInterval == 0) {
			return;
		}

		if (scheduledFuture != null) {
			return;
		}

		scheduledFuture = executorService.scheduleAtFixedRate(this, 0, actualPollingInterval, MILLISECONDS);
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[0];
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to do here
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		// Nothing to do here
	}

	@Override
	public void configurationChanged(Properties props) {
		// TODO:
	}

	@Override
	public void addDefaults(Properties props) {
		// TODO:

	}

	protected Platform getPlatform() {
		return platform;
	}

	class PollingDriverThreadFactory implements ThreadFactory {

		private final String threadName;

		public PollingDriverThreadFactory(final String threadName) {
			this.threadName = threadName;
		}

		@Override
		public Thread newThread(Runnable r) {
			final Thread thread = new Thread(r);
			thread.setDaemon(true);
			thread.setName(threadName);
			return thread;
		}

	}

}
