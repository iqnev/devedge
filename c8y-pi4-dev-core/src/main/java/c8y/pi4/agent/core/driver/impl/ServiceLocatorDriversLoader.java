package c8y.pi4.agent.core.driver.impl;

import java.util.ServiceLoader;

import c8y.pi4.agent.core.driver.Driver;
import c8y.pi4.agent.core.driver.DriversLoader;


/**
 * @author Ivelin Yanev
 * @since 01.09.2020
 *
 */
public class ServiceLocatorDriversLoader implements DriversLoader {

	@Override
	public Iterable<Driver> loadDrivers() {

		return ServiceLoader.load(Driver.class);
	}

}
