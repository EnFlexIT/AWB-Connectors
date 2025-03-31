package de.enflexit.connector.nymea.dataModel;

import java.util.Map;

public class StateType {
	private String id;
	private String name;
	private String displayName;
	private int index;
	private IoType ioType;
	private String unit;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public IoType getIoType() {
		return ioType;
	}
	public void setIoType(IoType ioType) {
		this.ioType = ioType;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public static StateType fromGsonMap(Map<?,?> gsonMap) {
		
		StateType stateType = new StateType();
		String idString = (String) gsonMap.get("id");
		stateType.setId(idString);
		String name = (String) gsonMap.get("name");
		stateType.setName(name);
		String displayName = (String) gsonMap.get("displayName");
		stateType.setDisplayName(displayName);
		String unit = (String) gsonMap.get("unit");
		stateType.setUnit(unit);
		Double index = (Double) gsonMap.get("index");
		stateType.setIndex(index.intValue());
		
		String ioTypeString = (String) gsonMap.get("ioType");
		IoType ioType = IoType.getTypeForValue(ioTypeString);
		stateType.setIoType(ioType);
		
		return stateType;
		
		
	}
	
}
