package de.enflexit.connector.nymea.dataModel;

import java.util.ArrayList;

public class Thing {
	
	private String id;
	private String name;
	private String thingClassID;
	private String type;
	
	private ArrayList<StateVariable> statesList;
	
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
	
	public String getThingClassID() {
		return thingClassID;
	}
	public void setThingClassID(String thingClassID) {
		this.thingClassID = thingClassID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public ArrayList<StateVariable> getStatesList() {
		if (statesList==null) {
			statesList = new ArrayList<StateVariable>();
		}
		return statesList;
	}
	
}
