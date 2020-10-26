package c8y.pi4.agent.core.configuration;

import java.util.Properties;

import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.model.authentication.CumulocityCredentials;

import c8y.pi4.agent.core.util.PropUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Ivelin Yanev
 * @since 01.09.2020
 *
 */
@Getter
@EqualsAndHashCode
public class CredentialsManager {

	/**
	 * 
	 */
	private static final String HOST_FIELD = "agent.host";
	public static final String COMMON_PROPS_LOCATION = "./cfg/cumulocity.properties";
	public static final String DEVICE_PROPS_LOCATION = "C:/DEV/ForTest/agent.properties";
	private static final String DEFAULT_HOST = "http://developer.cumulocity.com";
	private static final String DEFAULT_BOOTSTRAP_TENANT = "management";
	private static final String DEFAULT_BOOTSTRAP_USER = "devicebootstrap";
	private static final String DEFAULT_BOOTSTRAP_PASSWORD = "Fhdt1bb1f";

	private final CumulocityCredentials deviceCredentials;
	private final String host;
	private final String devicePropsFile;

	public CredentialsManager(String devicePropsFile) {
		this.devicePropsFile = devicePropsFile;
		Properties deviceProps = PropUtils.fromFile(devicePropsFile);
		this.deviceCredentials = initDeviceCredentials(deviceProps);
		this.host = deviceProps.getProperty(HOST_FIELD, DEFAULT_HOST);
	}

	public static CredentialsManager defaultCredentialsManager() {
		return new CredentialsManager(DEVICE_PROPS_LOCATION);
	}

	/**
	 * @param commonProps
	 * @return
	 */
	private CumulocityCredentials initBootstrapCredentials(Properties commonProps) {
		return new CumulocityBasicCredentials(commonProps.getProperty("bootstrap.user", DEFAULT_BOOTSTRAP_USER),
				commonProps.getProperty("bootstrap.tenant", DEFAULT_BOOTSTRAP_TENANT),
				commonProps.getProperty("bootstrap.password", DEFAULT_BOOTSTRAP_PASSWORD), null, null);
	}

	/**
	 * @param deviceProps
	 * @return
	 */
	private CumulocityCredentials initDeviceCredentials(Properties deviceProps) {
		String user = deviceProps.getProperty("agent.user");
		String password = deviceProps.getProperty("agent.password");
		if (user == null || password == null) {
			return null;
		} else {
			return new CumulocityBasicCredentials(user, deviceProps.getProperty("agent.tenant", "demo"), password, null,
					null);
		}

	}

	public void saveDeviceCredentials(CumulocityCredentials cumulocityCredentials) {

	}
}
