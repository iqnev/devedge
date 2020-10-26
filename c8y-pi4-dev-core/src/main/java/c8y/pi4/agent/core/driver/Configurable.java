package c8y.pi4.agent.core.driver;

import java.util.Properties;

/**
 * @author Ivelin Yanev
 * @since 10.09.2020
 *
 */
public interface Configurable {
	/**
	 * 
	 * @param props
	 */
	void addDefaults(Properties props);

	/**
	 * Notifies driver of a configuration change. Unknown configuration entries
	 * should be ignored by the driver.
	 *
	 * @param props The updated configuration.
	 */
	void configurationChanged(Properties props);
}
