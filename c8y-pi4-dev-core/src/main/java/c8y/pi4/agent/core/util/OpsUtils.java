package c8y.pi4.agent.core.util;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.SupportedOperations;

/**
 * @author Ivelin Yanev
 * @since 03.09.2020
 *
 */
public class OpsUtils {
	/**
	 * Private constructor to prevent instantiation.
	 */
	private OpsUtils() {
		throw new UnsupportedOperationException();
	}

	public static void addSupportedOperation(ManagedObjectRepresentation mo, String op) {
		SupportedOperations ops = mo.get(SupportedOperations.class);

		if (ops == null) {
			ops = new SupportedOperations();
			mo.set(ops);
		}

		if (!ops.contains(op)) {
			ops.add(op);
		}
	}
}
