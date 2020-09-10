package c8y.devteams.agent.driver;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;

import com.cumulocity.sdk.client.Platform;
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
		this.executorService = newSingleThreadScheduledExecutor(new PollingDriverThreadFactory(type + "Poller"));
		this.defaultPollingInterval = defaultPollingInterval;
		this.actualPollingInterval = defaultPollingInterval;
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
