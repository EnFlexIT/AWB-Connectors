package de.enflexit.connector.nymea.dataModel;

/**
 * This class represents a state-describing variable. It is based on the "State" type from nymea/consolinno,
 * but includes some information from the "StateType" type to have everything relevant in one place. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class StateVariable {
	private String stateTypeID;
	private Object value;
	private String displayName;
	private String unit;
	
	/**
	 * Gets the state type ID.
	 * @return the state type ID
	 */
	public String getStateTypeID() {
		return stateTypeID;
	}
	/**
	 * Sets the state type ID.
	 * @param stateTypeID the new state type ID
	 */
	public void setStateTypeID(String stateTypeID) {
		this.stateTypeID = stateTypeID;
	}
	
	/**
	 * Gets the value.
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * Sets the value.
	 * @param value the new value
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * Gets the display name.
	 * @return the display name
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * Sets the display name.
	 * @param displayName the new display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * Gets the unit.
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * Sets the unit.
	 * @param unit the new unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String displayString = displayName + ": " + value;
		if (this.unit!=null && this.unit.equals("UnitNone")==false) {
			displayString = displayString + " " + this.unit.replace("Unit", "");
		}
		return displayString;
	}
	
}
