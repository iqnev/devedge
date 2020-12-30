package c8y.pi4.agent.core.driver.impl;

import java.util.Properties;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.joda.time.DateTime;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;

import c8y.pi4.agent.core.driver.Configurable;
import c8y.pi4.agent.core.driver.Driver;
import c8y.pi4.agent.core.driver.OperationExecutor;

/**
 * @author Ivelin Yanev
 *
 * @since 12.30.2020
 */
public class LogDriver extends AppenderSkeleton implements Configurable, Driver {

	public static final String LOG_TYPE = "c8y_DeviceLog";
	public static final String DEFAULT_ALARM_LEVEL = "ERROR";
	public static final String DEFAULT_EVENT_LEVEL = "INFO";
	public static final String ALARM_LEVEL_PROP = "c8y.log.alarmLevel";
	public static final String EVENT_LEVEL_PROP = "c8y.log.eventLevel";

	private Level eventLevel = Level.toLevel(DEFAULT_EVENT_LEVEL);
	private Level alarmLevel = Level.toLevel(DEFAULT_ALARM_LEVEL);
	private AlarmApi alarms;
	private AlarmRepresentation alarmTemplate = new AlarmRepresentation();
	private EventApi events;
	private EventRepresentation eventTemplate = new EventRepresentation();

	@Override
	public void close() {

	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		try {
			if (event.getLevel().isGreaterOrEqual(alarmLevel)) {
				alarmTemplate.setDateTime(new DateTime());
				alarmTemplate.setText(event.getLoggerName() + ": " + event.getMessage());
				alarms.create(alarmTemplate);
			} else if (event.getLevel().isGreaterOrEqual(eventLevel)) {
				eventTemplate.setDateTime(new DateTime());
				eventTemplate.setText(event.getLoggerName() + ": " + event.getMessage());
				events.create(eventTemplate);
			}
		} catch (SDKException e) {
			if (e.getHttpStatus() != 404) {
				throw e;
			}
		}

	}

	@Override
	public void initialize() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(Platform platform) throws Exception {
		alarms = platform.getAlarmApi();
		events = platform.getEventApi();

	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[0];
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		alarmTemplate.setType(LOG_TYPE);
		alarmTemplate.setSource(mo);
		alarmTemplate.setStatus(CumulocityAlarmStatuses.ACTIVE.toString());
		alarmTemplate.setSeverity(CumulocitySeverities.MAJOR.toString());

		eventTemplate.setType(LOG_TYPE);
		eventTemplate.setSource(mo);

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDefaults(Properties props) {
		props.setProperty(ALARM_LEVEL_PROP, DEFAULT_ALARM_LEVEL);
		props.setProperty(EVENT_LEVEL_PROP, DEFAULT_EVENT_LEVEL);

	}

	@Override
	public void configurationChanged(Properties props) {
		String alarmLevStr = props.getProperty(ALARM_LEVEL_PROP, DEFAULT_ALARM_LEVEL);
		alarmLevel = Level.toLevel(alarmLevStr);

		String eventLevStr = props.getProperty(EVENT_LEVEL_PROP, DEFAULT_EVENT_LEVEL);
		eventLevel = Level.toLevel(eventLevStr);

	}

}
