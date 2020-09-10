package c8y.devteams.agent.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

import c8y.RequiredAvailability;
import c8y.devteams.agent.components.AgentEvent;
import c8y.devteams.agent.components.EventHandler;
import c8y.devteams.agent.configuration.DeviceConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 08.09.2020
 *
 */
@Slf4j
public class DeviceMonitor {

	private ScheduledExecutorService scheduleExecutor;
	private ScheduledFuture<?> scheduleManager;
	private Runnable timeTask;

	private DeviceMonitor() {

	}

	private static class LazyHolder {
		static final DeviceMonitor INSTANCE = new DeviceMonitor();
	}

	public static DeviceMonitor getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void start(final Platform platform, ManagedObjectRepresentation mObjectRepresentation,
			DeviceConfiguration deviceConfiguration) {
		log.debug("Starting DeviceMonitor");

		scheduleExecutor = Executors.newScheduledThreadPool(1, new DeviceMonitorThreadFactory());
		timeTask = () -> {
			EventHandler.getInstance().createEvent(AgentEvent.DEVICE_ACTIVE, "The Device is working properly");

			int realRequiredAvailability = mObjectRepresentation.get(RequiredAvailability.class).getResponseInterval();
			int currentRequiredAvailability = deviceConfiguration.requiredInterval.get();
			if (realRequiredAvailability != currentRequiredAvailability) {
				deviceConfiguration.requiredInterval.set(realRequiredAvailability);
				changeScheduleTime(realRequiredAvailability);
			}
		};

		scheduleManager = scheduleExecutor.scheduleAtFixedRate(timeTask, 0, deviceConfiguration.requiredInterval.get(),
				TimeUnit.MINUTES);

	}

	public void changeScheduleTime(int timeMinutes) {
		if (scheduleManager != null) {
			scheduleManager.cancel(true);
		}

		scheduleManager = scheduleExecutor.scheduleAtFixedRate(timeTask, 0, timeMinutes, TimeUnit.MINUTES);
	}

	private static final class DeviceMonitorThreadFactory implements ThreadFactory {
		private int counter = 1;

		@Override
		public Thread newThread(final Runnable r) {
			final Thread thread = new Thread(r);
			thread.setDaemon(true);
			thread.setName("DeviceMonitor-" + counter++);
			return thread;
		}
	}
}
