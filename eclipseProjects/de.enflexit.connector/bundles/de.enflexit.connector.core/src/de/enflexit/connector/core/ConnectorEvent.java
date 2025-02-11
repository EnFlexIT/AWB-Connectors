package de.enflexit.connector.core;

/**
 * The Class ConnectorEvent.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorEvent {
	
	/**
	 * Possible events.
	 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
	 */
	public enum Event {
		CONNECTED, DISCONNECTED
	}
	
	private AbstractConnector source;
	private Event event;
	
	/**
	 * Instantiates a new connector event.
	 * @param source the source
	 * @param event the event
	 */
	public ConnectorEvent(AbstractConnector source, Event event) {
		this.source = source;
		this.event = event;
	}

	/**
	 * Gets the source, i.e. the {@link AbstractConnector} instance this event is related to.
	 * @return the source
	 */
	public AbstractConnector getSource() {
		return source;
	}
	
	/**
	 * Gets the actual event.
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}
	
}
