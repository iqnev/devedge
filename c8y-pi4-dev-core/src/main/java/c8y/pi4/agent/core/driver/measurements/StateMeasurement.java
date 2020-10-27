/**
 * 
 */
package c8y.pi4.agent.core.driver.measurements;

import java.math.BigDecimal;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.model.measurement.StateType;
import com.cumulocity.model.measurement.ValueType;

/**
 * @author Ivelin Yanev
 *
 * @since 10.27.2020
 */
public class StateMeasurement<E extends Enum> {

	private MeasurementValue measurementValue;

	public StateMeasurement() {
		this.measurementValue = new MeasurementValue(new BigDecimal(0), "state", ValueType.BOOLEAN, "",
				StateType.ORIGINAL);
	}

	@SuppressWarnings("unused")
	public MeasurementValue getState() {
		return measurementValue;
	}

	@SuppressWarnings("unused")
	public void setState(MeasurementValue measurementValue) {
		this.measurementValue = measurementValue;
	}

	public void setState(E state) {
		this.measurementValue.setValue(stateToValue(state));
	}

	private BigDecimal stateToValue(E state) {
		return new BigDecimal(state.ordinal());
	}
}
