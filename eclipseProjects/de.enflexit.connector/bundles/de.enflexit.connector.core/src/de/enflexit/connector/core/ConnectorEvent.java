package de.enflexit.connector.core;

/**
 * The Class ConnectorEvent.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorEvent {
	
	/**
	 * Possible event types.
	 */
	public enum EventType{
		CONNECTED, DISCONNECTED, FAILURE
	}
	
	private EventType type;
	private String message;
	
	/**
	 * Instantiates a new connector event.
	 * @param type the event type
	 */
	public ConnectorEvent(EventType type) {
		this(type, null);
	}
	
	/**
	 * Instantiates a new connector event.
	 * @param type the event type
	 * @param message the event message
	 */
	public ConnectorEvent(EventType type, String message) {
		this.type = type;
		this.message = message;
	}
	
	/**
	 * Gets the event type.
	 * @return the type
	 */
	public EventType getType() {
		return type;
	}
	/**
	 * Gets the event message.
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * Sets the event message.
	 * @param message the new event message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
}
