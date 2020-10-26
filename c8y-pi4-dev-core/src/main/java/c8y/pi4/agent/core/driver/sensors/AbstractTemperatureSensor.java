package c8y.pi4.agent.core.driver.sensors;

import java.math.BigDecimal;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.Hardware;
import c8y.TemperatureMeasurement;
import c8y.TemperatureSensor;
import c8y.pi4.agent.core.driver.MeasurementPollingDriver;
import c8y.pi4.agent.core.driver.impl.DeviceManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 11.09.2020
 *
 */
@Slf4j
public abstract class AbstractTemperatureSensor extends MeasurementPollingDriver {

	private static final String TYPE = "Temperature";

	private final String id;

	/**
	 * 
	 * @param id
	 */
	public AbstractTemperatureSensor(String id) {		
		super("c8y_" + TYPE + "Sensor", 5000);
		log.info("AbstractTemperatureSensor(String id)");
		this.id = id;
	}

	@Override
	public void initialize() throws Exception {
		log.info("initializing");
	}

	@Override
	public void run() {
		double temperature = getTemperature();
		log.info("public void run(): TEMP: ", temperature);
		TemperatureMeasurement temperatureMeasurement = new TemperatureMeasurement();
		temperatureMeasurement.setTemperature(new BigDecimal(temperature));
		log.info("before send");
		sendMeasurement(temperatureMeasurement);

		log.info("sending temperature measurement: " + temperature);
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		log.info("creating child");

		ManagedObjectRepresentation childDevice = DeviceManager.createChild(id, TYPE, getPlatform(), parent,
				getHardware(), getSupportedOperations(), new TemperatureSensor());
		
		 setSource(childDevice);

	}

	public abstract Hardware getHardware();

	public abstract double getTemperature();

}
