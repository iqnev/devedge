/**
 * 
 */
package c8y.pi4.agent.core.driver.sensors;

import org.joda.time.DateTime;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import c8y.pi4.agent.core.driver.impl.DeviceManager;
import c8y.pi4.agent.core.driver.measurements.MotionStateMeasurement;
import c8y.Hardware;
import c8y.MotionSensor;
import c8y.pi4.agent.core.driver.Driver;
import c8y.pi4.agent.core.driver.OperationExecutor;
import c8y.pi4.agent.core.driver.events.MotionDetectedEvent;
import c8y.pi4.agent.core.driver.events.MotionUndetectedEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 *
 * @since 10.27.2020
 */
@Slf4j
public abstract class AbstractMotionSensors implements Driver {

	private static final String TYPE = "Motion";

	private Platform platform;
	private EventApi eventApi;
	private MeasurementApi measurementApi;
	private ManagedObjectRepresentation childDevice;
	private final String id;

	public enum State {
		MOTION_UNDETECTED, MOTION_DETECTED
	}

	public AbstractMotionSensors(String id) {
		log.info("AbstractMotionSensors(String id)");
		this.id = id;
	}

	@Override
	public void initialize() throws Exception {
		log.info("initializing");
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		log.info("initializing platform");

		this.platform = platform;

		eventApi = platform.getEventApi();
		measurementApi = platform.getMeasurementApi();
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		log.info("creating child");

		childDevice = DeviceManager.createChild(id, TYPE, platform, parent, getHardware(), getSupportedOperations(),
				new MotionSensor());
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[0];
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation parent) {
		log.info("initializing inventory");
	}

	@Override
	public void start() {
		log.info("starting driver");
	}

	abstract Hardware getHardware();

	private void sendMotionMeasurement(boolean isMotion) {
		final MeasurementRepresentation measurementRepresentation = new MeasurementRepresentation();

		measurementRepresentation.setSource(childDevice);
		measurementRepresentation.setType("c8y");

		MotionStateMeasurement motionStateMeasurement = new MotionStateMeasurement();
		motionStateMeasurement.setState(isMotion ? State.MOTION_UNDETECTED : State.MOTION_DETECTED);
		measurementRepresentation.set(motionStateMeasurement);
		measurementRepresentation.setDateTime(new DateTime());

		measurementApi.create(measurementRepresentation);

		// send the current state
		motionStateMeasurement = new MotionStateMeasurement();
		motionStateMeasurement.setState(isMotion ? State.MOTION_DETECTED : State.MOTION_UNDETECTED);
		measurementRepresentation.set(motionStateMeasurement);
		measurementRepresentation.setDateTime(new DateTime());

		measurementApi.create(measurementRepresentation);

	}

	void triggerMotionDetected() {
		MotionDetectedEvent event = new MotionDetectedEvent();
		event.setSource(childDevice);

		eventApi.create(event);

		sendMotionMeasurement(true);
	}

	void triggerMotionUndetected() {
		MotionUndetectedEvent event = new MotionUndetectedEvent();
		event.setSource(childDevice);

		eventApi.create(event);

		sendMotionMeasurement(false);
	}
}
