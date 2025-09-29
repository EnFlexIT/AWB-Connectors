package de.enflexit.connector.nymea.dataModel;

public class ThingProperty {
	private String parameterTypeID;
	private Object value;
	private String displayName;
	
	public String getParameterTypeID() {
		return parameterTypeID;
	}
	public void setParameterTypeID(String parameterTypeID) {
		this.parameterTypeID = parameterTypeID;
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
	
	
}
