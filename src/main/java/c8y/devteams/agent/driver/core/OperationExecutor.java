package c8y.devteams.agent.driver.core;

import com.cumulocity.rest.representation.operation.OperationRepresentation;

/**
 * Executes a remote control operation.
 */
public interface OperationExecutor {
	/**
	 * The type of remote control operation that this OperationExecutor can execute.
	 */
	String supportedOperationType();

	/**
	 * Execute a particular remote control operation and write the result of the
	 * operation back into the operation. Carries out additional updates, e.g., to
	 * the inventory.
	 *
	 * @param operation The operation to execute
	 * @param cleanup   If set to true, the operation was hanging in executing state
	 *                  when the agent was started. This can have multiple reasons:
	 *                  One reason is that there was a failure during first
	 *                  execution. In this case, cleanup may be needed. Another
	 *                  reason might be that the operation required a restart of the
	 *                  agent, and the operation is successful when the agent could
	 *                  be restarted.
	 */
	void execute(OperationRepresentation operation, boolean cleanup) throws Exception;
}
