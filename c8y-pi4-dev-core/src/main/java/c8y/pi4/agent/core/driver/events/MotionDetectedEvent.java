/**
 * 
 */
package c8y.pi4.agent.core.driver.events;

import org.joda.time.DateTime;
import com.cumulocity.rest.representation.event.EventRepresentation;

/**
 * @author Ivelin Yanev
 *
 * @since 10.27.2020
 */
public class MotionDetectedEvent  extends EventRepresentation {

	 public MotionDetectedEvent() {
	        setType("MotionDetectedEvent");
	        setText("Motion detected");
	        setDateTime(new DateTime());
	    }
}
