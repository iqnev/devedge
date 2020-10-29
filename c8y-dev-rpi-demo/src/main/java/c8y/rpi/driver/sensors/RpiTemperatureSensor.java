package c8y.rpi.driver.sensors;

import java.io.IOException;
import com.pi4j.system.SystemInfo;
import c8y.Hardware;
import c8y.pi4.agent.core.driver.sensors.AbstractTemperatureSensor;
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
		log.info("Hardware getHardware()");
		Hardware hardware = null;
		try {
			hardware = new Hardware("Temperature Sensor of CPU", SystemInfo.getCpuVariant(),
					SystemInfo.getCpuRevision());
		} catch (UnsupportedOperationException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn("An exception occurred while opening the file: {}", e);
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
		}

		return hardware;
	}

	@Override
	public double getTemperature() {
		log.info("getTemperature()");
		double temp = 0.0d;
		try {
			temp = SystemInfo.getCpuTemperature();
		} catch (NumberFormatException | UnsupportedOperationException | IOException | InterruptedException e) {
			log.warn("Getting raspberry CPU temperature failed " + e.getMessage(), e);
		}
		return temp;
	}

}
