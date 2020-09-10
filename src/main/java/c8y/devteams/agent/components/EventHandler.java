package c8y.devteams.agent.components;

import java.util.Objects;
import org.joda.time.DateTime;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.event.EventApi;

import c8y.devteams.agent.Agent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ivelin Yanev
 * @since 08.09.2020
 *
 */
@Slf4j
public class EventHandler {

	private EventApi eventApi;
	private Platform platform;
	private ManagedObjectRepresentation mo;

	private EventHandler() {

	}

	private static class LazyHolder {
		static final EventHandler INSTANCE = new EventHandler();
	}

	public static EventHandler getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * 
	 * @param platform
	 */
	public void init(final Platform platform, final ManagedObjectRepresentation mo) {
		this.platform = platform;
		this.eventApi = platform.getEventApi();
		this.mo = mo;
	}

	public void createEvent(final AgentEvent eventType, final String text) {
		if (Objects.isNull(eventApi) && Objects.nonNull(platform)) {
			this.eventApi = platform.getEventApi();
		}

		if (Objects.isNull(eventApi)) {
			log.error("Event API is not initialized");
			return;
		}

		final EventRepresentation event = new EventRepresentation();
		event.setDateTime(new DateTime());
		event.setText(text == null ? eventType.toString() : text);
		event.setSource(mo);
		event.setType(Agent.AGENT_TYPE + "_" + eventType.toString() + "_Event");
		eventApi.create(event);

	}
}
