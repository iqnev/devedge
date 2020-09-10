package c8y.devteams.agent.operation;

import java.util.List;
import java.util.Map;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;

import c8y.devteams.agent.DeviceManagedObject;
import c8y.devteams.agent.ErrorLog;
import c8y.devteams.agent.driver.OperationExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 02.09.2020
 *
 */
@Slf4j
public class OperationDispatcher {

	private final DeviceControlApi deviceControl;

	private final GId gid;

	private final Map<String, List<OperationExecutor>> dispatchMap;

	public OperationDispatcher(DeviceControlApi deviceControl, GId gid,
			Map<String, List<OperationExecutor>> dispatchMap) throws SDKException {
		this.deviceControl = deviceControl;
		this.gid = gid;
		this.dispatchMap = dispatchMap;

		finishExecutingOps();
		listenToOperations();
		executePendingOps();
	}

	/**
	 * 
	 */
	private void executePendingOps() {
		log.info("Finishing leftover operations");
		for (OperationRepresentation operation : byStatus(OperationStatus.EXECUTING)) {
			execute(operation, true);
		}

	}

	private void execute(OperationRepresentation operation, boolean cleanup) throws SDKException {
		try {
			for (String key : operation.getAttrs().keySet()) {
				if (dispatchMap.containsKey(key)) {
					log.info("Executing operation " + key + (cleanup ? " cleanup" : ""));
					for (OperationExecutor exec : dispatchMap.get(key))
						exec.execute(operation, cleanup);
				}
			}
		} catch (Exception e) {
			operation.setStatus(OperationStatus.FAILED.toString());
			operation.setFailureReason(ErrorLog.toString(e));
			log.warn("Error while executing operation", e);
		}
		deviceControl.update(operation);
	}

	/**
	 * @param executing
	 * @return
	 */
	private Iterable<OperationRepresentation> byStatus(OperationStatus status) {
		OperationFilter opsFilter = new OperationFilter().byAgent(gid.getValue()).byStatus(status);
		return deviceControl.getOperationsByFilter(opsFilter).get().allPages();
	}

	/**
	 * 
	 */
	private void listenToOperations() throws SDKException {
		log.info("Listening for new operations");
		Subscriber<GId, OperationRepresentation> subscriber = deviceControl.getNotificationsSubscriber();
		for (int retries = 0; retries < 10; retries++) {
			try {
				subscriber.subscribe(gid, new OperationDispatcherSubscriptionListener());
				break;
			} catch (SDKException x) {
				log.warn("Couldn't subscribe to operation notifications, retry {}, {}", retries, x);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}

	}

	/**
	 * 
	 */
	private void finishExecutingOps() throws SDKException {
		log.info("Finishing leftover operations");
		for (OperationRepresentation operation : byStatus(OperationStatus.EXECUTING)) {
			execute(operation, true);
		}

	}

	private void executePending(OperationRepresentation operation) throws SDKException {
		operation.setStatus(OperationStatus.EXECUTING.toString());
		deviceControl.update(operation);
		execute(operation, false);
	}

	private class OperationDispatcherSubscriptionListener
			implements SubscriptionListener<GId, OperationRepresentation> {

		@Override
		public void onError(Subscription<GId> sub, Throwable e) {
			log.error("OperationDispatcher error!", e);
		}

		@Override
		public void onNotification(Subscription<GId> sub, OperationRepresentation operation) {
			try {
				executePending(operation);
			} catch (SDKException e) {
				log.error("OperationDispatcher error!", e);
			}
		}
	}

}
