package c8y.devteams.agent.configuration;

import com.cumulocity.model.authentication.CumulocityCredentials;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 01.09.2020
 *
 */
@RequiredArgsConstructor
@Slf4j
public class DeviceBootstrapProcessor {
	@NonNull
	private final ConfigurationManager credentialsManager;

	public CumulocityCredentials process(String serialNumber) {
		return null;
		
	}
}
