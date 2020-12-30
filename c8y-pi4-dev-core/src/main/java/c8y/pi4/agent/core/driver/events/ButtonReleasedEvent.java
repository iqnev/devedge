package c8y.pi4.agent.core.driver.events;


import org.joda.time.DateTime;
import com.cumulocity.rest.representation.event.EventRepresentation;

/**
 * @author Ivelin Yanev
 *
 * @since 12.30.2020
 */
public class ButtonReleasedEvent extends EventRepresentation {

    public ButtonReleasedEvent() {
        setType("ButtonReleasedEvent");
        setText("Button released");
        setDateTime(new DateTime());
    }

}
