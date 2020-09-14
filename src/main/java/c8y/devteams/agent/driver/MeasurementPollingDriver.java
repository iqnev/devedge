package c8y.devteams.agent.driver;

import org.joda.time.DateTime;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 11.09.2020
 *
 */
@Slf4j
public abstract class MeasurementPollingDriver extends PollingDriver {

	private MeasurementApi measurements;
	private final MeasurementRepresentation measurementRep = new MeasurementRepresentation();

	/**
	 * 
	 * @param measurementType
	 * @param defaultPollingInterval
	 */
	public MeasurementPollingDriver(String measurementType, long defaultPollingInterval) {
		super(measurementType, defaultPollingInterval);
		measurementRep.setType(measurementType);
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		super.initialize(platform);
		this.measurements = platform.getMeasurementApi();
	}

	public void setSource(ManagedObjectRepresentation mo) {
		measurementRep.setSource(mo);
	}

	protected void sendMeasurement(Object measurement) {
		try {
			measurementRep.set(measurement);
			measurementRep.setDateTime(new DateTime());
			measurements.create(measurementRep);
		} catch (SDKException e) {
			log.warn("Cannot send measurement", e);
		}

	}

}
