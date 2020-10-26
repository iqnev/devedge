package c8y.pi4.agent.core.configuration;

import java.util.Properties;

import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.model.authentication.CumulocityCredentials;

import c8y.pi4.agent.core.util.OSInfo;
import c8y.pi4.agent.core.util.PropUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 01.09.2020
 *
 */
@Getter
@EqualsAndHashCode
@Slf4j
public class ConfigurationManager {

	private static final String AGENT_PASSWORD_PROPERTY = "agent.password";
	private static final String AGENT_USER_PROPERTY = "agent.user";
	private static final String AGENT_TENANT_PROPERTY = "agent.tenant";
	private static final String DEFAULT_REQUIREDAVAILABILITY = "3";
	private static final String AGENT_REQUIRED_AVAILABILITY_PROPERTY = "agent.requiredAvailability";
	private static final String HOST_FIELD = "agent.host";
	public static final String WINDOWS_DEVICE_PROPS_LOCATION = "C:/c8y.4.dev/agent.properties";
	public static final String LINUX_DEVICE_PROPS_LOCATION = "/etc/c8y.4.dev/agent.properties";
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
		String devicePropsLocation = null;
		if (OSInfo.getOs().equals(OSInfo.OS.UNIX)) {
			devicePropsLocation =LINUX_DEVICE_PROPS_LOCATION;
		} else if (OSInfo.getOs().equals(OSInfo.OS.WINDOWS)) {
			devicePropsLocation =  WINDOWS_DEVICE_PROPS_LOCATION;
		} else {
			log.error("The OS is not recognized");
			return null;
		}
		return new ConfigurationManager(devicePropsLocation);
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
