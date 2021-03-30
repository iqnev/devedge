package c8y.rpi.driver;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.pi4j.system.SystemInfo;

import c8y.Hardware;
import c8y.pi4.agent.core.driver.Driver;
import c8y.pi4.agent.core.driver.HardwareProvider;
import c8y.pi4.agent.core.driver.OperationExecutor;
import c8y.pi4.agent.core.util.OpsUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 03.09.2020
 *
 */
@Slf4j
public class GatewayRpiHardwareDriver implements Driver, OperationExecutor, HardwareProvider {

	public final static String UNKNOWN = "unknown";
	private GId gid;
	private final Hardware hardware = new Hardware(UNKNOWN, UNKNOWN, UNKNOWN);

	@Override
	public Hardware getHardware() {
		return hardware;
	}

	@Override
	public String supportedOperationType() {
		return "c8y_Restart,polling_interval";
	}

	@Override
	public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
		log.debug("OPERATION: {}", operation.toJSON());
		
		
		operation.setStatus(OperationStatus.SUCCESSFUL.toString());
	}

	@Override
	public void initialize() throws Exception {
	/*	hardware.setModel(SystemInfo.getModelName());
		hardware.setRevision(SystemInfo.getRevision());
		hardware.setSerialNumber(SystemInfo.getSerial()); */
		
		hardware.setModel("IIIIII");
		hardware.setRevision("IIIIII");
		hardware.setSerialNumber("IIIIII");

	}

	@Override
	public void initialize(Platform platform) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] { this };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		mo.set(hardware);
		
		for (String operation : supportedOperationType().split(",")) {
			log.info("registering supported operation type '" + operation + "'");

			OpsUtils.addSupportedOperation(mo, operation);
		}
		

	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		this.gid = mo.getId();

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

}
