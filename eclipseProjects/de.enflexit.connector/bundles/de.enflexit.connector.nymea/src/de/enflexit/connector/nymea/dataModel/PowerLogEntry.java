package de.enflexit.connector.nymea.dataModel;

import java.util.Map;

/**
 * This class represents a power log entry, as used by nymea/consolinno HEMS systems.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class PowerLogEntry {
	
	private long timestamp;
	private String thingID;
	private double currentPower;
	private double totalConsumption;
	private double totalProduction;
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getThingID() {
		return thingID;
	}
	public void setThingID(String thingID) {
		this.thingID = thingID;
	}
	public double getCurrentPower() {
		return currentPower;
	}
	public void setCurrentPower(double currentPower) {
		this.currentPower = currentPower;
	}
	public double getTotalConsumption() {
		return totalConsumption;
	}
	public void setTotalConsumption(double totalConsumption) {
		this.totalConsumption = totalConsumption;
	}
	public double getTotalProduction() {
		return totalProduction;
	}
	public void setTotalProduction(double totalProduction) {
		this.totalProduction = totalProduction;
	}
	
	public static PowerLogEntry fromGsonMap(Map<?,?> gsonMap) {
		PowerLogEntry powerLogEntry = new PowerLogEntry();
		String thingID = (String) gsonMap.get("thingId");
		powerLogEntry.setThingID(thingID);
		Double timestamp = (Double) gsonMap.get("timestamp");
		powerLogEntry.setTimestamp(Double.valueOf(timestamp).longValue());
		Double currentPower = (Double) gsonMap.get("currentPower");
		powerLogEntry.setCurrentPower(currentPower);
		Double totalConsumption = (Double) gsonMap.get("totalConsumption");
		powerLogEntry.setTotalConsumption(totalConsumption);
		Double totalProduction = (Double)gsonMap.get("totalProduction");
		powerLogEntry.setTotalProduction(totalProduction);
		
		return powerLogEntry;
	}
}
