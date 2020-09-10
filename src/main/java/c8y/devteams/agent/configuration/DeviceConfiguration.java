package c8y.devteams.agent.configuration;

import java.util.concurrent.atomic.AtomicInteger;

import c8y.devteams.agent.Agent;
import c8y.devteams.agent.services.DeviceMonitor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 08.09.2020
 *
 */
@Slf4j
public class DeviceConfiguration {
	
	public AtomicInteger requiredInterval = new AtomicInteger();
	
	
	public void initRequiredInterval() {
		//TODO: read the proper property from the configuration file
		requiredInterval.set(2);
	}
	
	
	
	
}
