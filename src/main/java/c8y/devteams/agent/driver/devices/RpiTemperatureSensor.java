package c8y.devteams.agent.driver.devices;

import java.util.Random;

import c8y.Hardware;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 11.09.2020
 *
 */
@Slf4j
public class RpiTemperatureSensor extends AbstractTemperatureSensor {

	
	/**
	 * @param id
	 */
	public RpiTemperatureSensor(String id) {
		super(id);
	}

	@Override
	public void initialize() throws Exception {
		super.initialize();
	}

	@Override
	public Hardware getHardware() {
		return new Hardware("Temperature Sensor", "2323232323", "1.0.0");
	}

	@Override
	public double getTemperature() {

		Random r = new Random();
		double rangeMin = 0.0;
		double rangeMax = 37.0;
		double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();

		return randomValue;
	}


}
