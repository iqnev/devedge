package c8y.devteams.agent.driver.core;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

public interface Driver {
	/**
	 * Sets up driver. Errors during setup are reported as exception and send to the
	 * log.
	 */
	void initialize() throws Exception;

	/**
	 * Initialize platform connectivity.
	 */
	void initialize(Platform platform) throws Exception;

	/**
	 * Return the operations supported by this driver.
	 */
	OperationExecutor[] getSupportedOperations();

	/**
	 * Provides additional fragments for the managed object representing the device
	 * in the inventory. Note: The managed object's representation may not have a
	 * global ID at this stage.
	 * 
	 */
	void initializeInventory(ManagedObjectRepresentation mo);

	/**
	 * Synchronizes the children of the managed object in the inventory. At this
	 * point in time, the representation has a global ID Note that the driver may
	 * only manipulate types of children that it recognizes.
	 */
	void discoverChildren(ManagedObjectRepresentation mo);

	/**
	 * Start the regular tasks of this driver, i.e., listeners for sensors events or
	 * sensor polling.
	 */
	void start();
}
