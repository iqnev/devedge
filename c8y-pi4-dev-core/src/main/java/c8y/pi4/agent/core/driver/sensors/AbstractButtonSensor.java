package c8y.pi4.agent.core.driver.sensors;

import java.util.Date;

import org.joda.time.DateTime;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;


import c8y.Hardware;
import c8y.pi4.agent.core.driver.Driver;
import c8y.pi4.agent.core.driver.OperationExecutor;
import c8y.pi4.agent.core.driver.events.ButtonPressedEvent;
import c8y.pi4.agent.core.driver.impl.DeviceManager;
import c8y.pi4.agent.core.driver.measurements.ButtonStateMeasurement;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 *
 * @since 12.30.2020
 */
@Slf4j
public abstract class AbstractButtonSensor implements Driver {

	private static final String TYPE = "Button";

	private Platform platform;
	private EventApi eventApi;
	private MeasurementApi measurementApi;
	private ManagedObjectRepresentation childDevice;
	private final String id;

	class ButtonSensor {
	}

	public enum State {
		BUTTON_RELEASED, BUTTON_PRESSED
	}

	AbstractButtonSensor(String id) {
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
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[0];
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		log.info("creating child");

		childDevice = DeviceManager.createChild(id, TYPE, platform, mo, getHardware(), getSupportedOperations(),
				new ButtonSensor());

	}

	abstract Hardware getHardware();

	@Override
	public void start() {
		log.info("starting driver");
	}

	public void triggerButtonPressed() {
		ButtonPressedEvent buttonPressedEvent = new ButtonPressedEvent();
		buttonPressedEvent.setSource(childDevice);

		eventApi.create(buttonPressedEvent);
		sendButtonMeasurement(true);
	}

	void triggerButtonReleased() {
		ButtonPressedEvent buttonPressedEvent = new ButtonPressedEvent();
		buttonPressedEvent.setSource(childDevice);

		eventApi.create(buttonPressedEvent);

		sendButtonMeasurement(false);
	}

	private void sendButtonMeasurement(boolean b) {
		MeasurementRepresentation measurementRepresentation = new MeasurementRepresentation();

		measurementRepresentation.setSource(childDevice);
		measurementRepresentation.setType("ButtonDetected");
		
		ButtonStateMeasurement measurement = new ButtonStateMeasurement();
        measurement.setState(b ? State.BUTTON_RELEASED : State.BUTTON_PRESSED);
        measurementRepresentation.set(measurement);
        measurementRepresentation.setDateTime(new DateTime());

        measurementApi.create(measurementRepresentation);

        // send the current state
        measurement = new ButtonStateMeasurement();
        measurement.setState(b ? State.BUTTON_PRESSED : State.BUTTON_RELEASED);
        measurementRepresentation.set(measurement);
        measurementRepresentation.setDateTime(new DateTime());

        measurementApi.create(measurementRepresentation);

	}
}
