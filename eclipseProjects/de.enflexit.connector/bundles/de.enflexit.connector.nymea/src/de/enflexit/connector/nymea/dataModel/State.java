package de.enflexit.connector.nymea.dataModel;

public class State {
	private String stateTypeID;
	private Object value;
	private String displayName;
	
	public String getStateTypeID() {
		return stateTypeID;
	}
	public void setStateTypeID(String stateTypeID) {
		this.stateTypeID = stateTypeID;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String toString() {
		return displayName + ": " + value;
	}
	
}
