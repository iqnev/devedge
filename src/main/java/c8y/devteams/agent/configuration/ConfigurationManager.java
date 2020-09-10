package c8y.devteams.agent.configuration;

import java.util.Properties;

import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.model.authentication.CumulocityCredentials;

import c8y.devteams.util.PropUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Ivelin Yanev
 * @since 01.09.2020
 *
 */
@Getter
@EqualsAndHashCode
public class ConfigurationManager {

	private static final String AGENT_PASSWORD_PROPERTY = "agent.password";
	private static final String AGENT_USER_PROPERTY = "agent.user";
	private static final String AGENT_TENANT_PROPERTY = "agent.tenant";
	private static final String DEFAULT_REQUIREDAVAILABILITY = "3";
	private static final String AGENT_REQUIRED_AVAILABILITY_PROPERTY = "agent.requiredAvailability";
	private static final String HOST_FIELD = "agent.host";
	public static final String DEVICE_PROPS_LOCATION = "C:/DEV/ForTest/agent.properties";
	private static final String DEFAULT_HOST = "http://developer.cumulocity.com";
	private static final String DEFAULT_BOOTSTRAP_TENANT = "#";
	private static final String DEFAULT_BOOTSTRAP_USER = "#";
	private static final String DEFAULT_BOOTSTRAP_PASSWORD = "#";

	private final CumulocityCredentials deviceCredentials;
	private final DeviceConfiguration deviceConfiguration;
	private final String host;
	private final String devicePropsFile;

	public ConfigurationManager(String devicePropsFile) {
		this.devicePropsFile = devicePropsFile;
		Properties deviceProps = PropUtils.fromFile(devicePropsFile);
		this.deviceCredentials = initDeviceCredentials(deviceProps);
		this.deviceConfiguration = initDeviceConfiguration(deviceProps);
		this.host = deviceProps.getProperty(HOST_FIELD, DEFAULT_HOST);
	}

	public static ConfigurationManager defaultCredentialsManager() {
		return new ConfigurationManager(DEVICE_PROPS_LOCATION);
	}

	/**
	 * @param commonProps
	 * @return
	 */
	private DeviceConfiguration initDeviceConfiguration(Properties deviceProps) {
		final DeviceConfiguration deviceConfiguration = new DeviceConfiguration();

		int requiredAvailability = Integer
				.parseInt(deviceProps.getProperty(AGENT_REQUIRED_AVAILABILITY_PROPERTY, DEFAULT_REQUIREDAVAILABILITY));

		deviceConfiguration.requiredInterval.set(requiredAvailability);
		
		return deviceConfiguration;
	}

	/**
	 * @param deviceProps
	 * @return
	 */
	private CumulocityCredentials initDeviceCredentials(Properties deviceProps) {
		String user = deviceProps.getProperty(AGENT_USER_PROPERTY);
		String password = deviceProps.getProperty(AGENT_PASSWORD_PROPERTY);
		if (user == null || password == null) {
			return null;
		} else {
			return new CumulocityBasicCredentials(user, deviceProps.getProperty(AGENT_TENANT_PROPERTY, "demo"),
					password, null, null);
		}

	}

	public void saveDeviceCredentials(CumulocityCredentials cumulocityCredentials) {

	}
}
