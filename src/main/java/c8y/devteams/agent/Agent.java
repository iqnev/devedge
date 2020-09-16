package c8y.devteams.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.sun.jersey.api.client.ClientHandlerException;

import c8y.Hardware;
import c8y.IsDevice;
import c8y.RequiredAvailability;
import c8y.devteams.agent.components.EventHandler;
import c8y.devteams.agent.configuration.ConfigurationManager;
import c8y.devteams.agent.configuration.DeviceConfiguration;
import c8y.devteams.agent.devices.RpiTemperatureSensor;
import c8y.devteams.agent.driver.Driver;
import c8y.devteams.agent.driver.DriversLoader;
import c8y.devteams.agent.driver.OperationExecutor;
import c8y.devteams.agent.operation.OperationDispatcher;
import c8y.devteams.agent.services.DeviceMonitor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 01.09.2020
 *
 */
@Slf4j
public class Agent {

	public static final String XTIDTYPE = "c8y_Serial";
	public static final String TYPE = "c8y_Linux";
	public static final String AGENT_TYPE = "C8Y-4-DEVTEAMS-AGENT";
	public static final int RESPONSE_INTERVAL_MIN = 3;
	private static long RETRY_WAIT_MS = 60L;
	private DeviceConfiguration deviceConfiguration;
	private List<Driver> drivers;
	public Platform platform;
	private InventoryApi inventoryApi;
	private final ManagedObjectRepresentation mo = new ManagedObjectRepresentation();

	private Agent() {

	}

	private static class LazyHolder {
		static final Agent INSTANCE = new Agent();
	}

	public static Agent getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void init(ConfigurationManager configurationManager, DriversLoader driversLoader) {
		log.info("Starting agent...");

		
		this.deviceConfiguration = configurationManager.getDeviceConfiguration();
		this.drivers = initializeDrivers(driversLoader);
		this.platform = initializePlatform(configurationManager);

		setupSensors();

		initializeDriverPlatforms();
		Map<String, List<OperationExecutor>> dispatchMap = initializeInventory();

		discoverChildren();
		startDrivers();

		initComponents();
		startServices();

		new OperationDispatcher(this.platform.getDeviceControlApi(), mo.getId(), dispatchMap);

	}

	/**
	 * 
	 */
	private void setupSensors() {
		log.info("setting up raspberry temperature sensor");

		drivers.add(new RpiTemperatureSensor("13"));
	}

	private void startServices() {
		DeviceMonitor.getInstance().start(platform, mo, deviceConfiguration);

	}

	private void initComponents() {
		EventHandler.getInstance().init(platform, mo);

	}

	/**
	 * Starting drivers.
	 */
	private void startDrivers() {
		log.info("Starting drivers");
		for (final Driver driver : drivers) {
			driver.start();
		}
	}

	private void discoverChildren() {
		log.info("Discovering child devices");
		for (Driver driver : drivers) {
			driver.discoverChildren(mo);
		}
	}

	/**
	 * @return
	 */
	private Map<String, List<OperationExecutor>> initializeInventory() {
		log.info("Initializing inventory...");

		Map<String, List<OperationExecutor>> dispatchMap = new HashMap<>();

		for (Driver driver : drivers) {
			driver.initializeInventory(mo);

			for (OperationExecutor operationExecutor : driver.getSupportedOperations()) {
				if (operationExecutor == null) {
					continue;
				}

				String supportedOp = operationExecutor.supportedOperationType();

				if (dispatchMap.containsKey(supportedOp)) {
					dispatchMap.get(supportedOp).add(operationExecutor);
				} else {
					final List<OperationExecutor> newList = new ArrayList<OperationExecutor>();
					newList.add(operationExecutor);
					dispatchMap.put(supportedOp, newList);
				}
			}
		}

		Hardware hardware = mo.get(Hardware.class);// TODO helper which create object from the file properties

		String model = hardware.getModel();
		String serial = hardware.getSerialNumber();

		ID extId = asExternalId(hardware);

		mo.setType(TYPE);
		mo.setName(model + " " + serial);
		mo.set(new com.cumulocity.model.Agent());
		mo.set(new IsDevice());
		mo.set(new RequiredAvailability(RESPONSE_INTERVAL_MIN));

		checkConnection();

		log.debug("Agent representation is {}, updating inventory", mo);

		if (new DeviceManagedObject(platform).createOrUpdate(mo, extId, null)) {
			log.debug("Agent was created in the inventory");
		} else {
			log.debug("Agent was updated in the inventory");
		}

		return dispatchMap;
	}

	/**
	 * @return
	 * 
	 */
	private boolean checkConnection() throws SDKException {
		log.info("Checking platform connectivity");
		boolean connected = false;

		while (!connected) {
			try {
				if (inventoryApi == null) {
					log.debug("initialize inventory API");
					inventoryApi = platform.getInventoryApi();
				}

				platform.getInventoryApi().getManagedObjects().get(1);
				connected = true;
			} catch (ClientHandlerException x) {
				log.debug("No connectivity, wait and retry {}", x);
				try {
					Thread.sleep(RETRY_WAIT_MS);
				} catch (InterruptedException e) {
					log.error("Failed to connect {}", e);
				}
			}
		}

		return connected;
	}

	/**
	 * @param hardware
	 * @return
	 */
	private ID asExternalId(Hardware hardware) {
		String id = "linux-" + hardware.getSerialNumber();
		ID extId = new ID(id);
		extId.setType(XTIDTYPE);
		return extId;
	}

	/**
	 * 
	 */
	private void initializeDriverPlatforms() {
		for (Driver driver : drivers) {
			try {
				driver.initialize(platform);
			} catch (Exception e) {
				log.error("Can't initialize driver platform " + driver.getClass(), e);
			}
		}

	}

	/**
	 * 
	 * @param credentialsManager
	 * @return
	 */
	private Platform initializePlatform(ConfigurationManager configurationManager) {
		CumulocityCredentials credentials = configurationManager.getDeviceCredentials();
		if (credentials == null) {
			// Hardware hardware = specifyHardware();
			// credentials = deviceBootstrapProcessor.process(hardware.getSerialNumber());
		}
		return new PlatformImpl(configurationManager.getHost(), credentials);
	}

	/**
	 * @param driversLoader
	 * @return
	 */
	private List<Driver> initializeDrivers(DriversLoader driversLoader) {
		final List<Driver> drivers = new ArrayList<>();
		log.info("Initializing drivers");

		for (Driver driver : driversLoader.loadDrivers()) { 
			try {
				log.info("Initializing " + driver.getClass());
				driver.initialize();
				drivers.add(driver);
			} catch (Exception e) {
				log.warn("Skipping driver {}", driver.getClass());
				log.debug("Driver error message: {}", e);
			} catch (UnsatisfiedLinkError error) {
				log.warn("Skipping driver {}", driver.getClass());
				log.debug("Driver error message: " + driver.getClass(), error);
			}
		}
		return drivers;
	}
}
