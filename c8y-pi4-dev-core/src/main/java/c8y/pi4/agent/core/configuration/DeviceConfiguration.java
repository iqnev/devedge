package c8y.pi4.agent.core.configuration;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 08.09.2020
 *
 */
@Slf4j
public class DeviceConfiguration {
	
	public AtomicInteger requiredInterval = new AtomicInteger();
	public long pollingInterval;
	public String groupName;
	
	
	public void initRequiredInterval() {
		//TODO: read the proper property from the configuration file
		requiredInterval.set(2);
	}
	
	
	
	
}
