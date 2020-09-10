package c8y.devteams.agent.driver.impl;

import java.util.ServiceLoader;

import c8y.devteams.agent.driver.Driver;
import c8y.devteams.agent.driver.DriversLoader;

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
