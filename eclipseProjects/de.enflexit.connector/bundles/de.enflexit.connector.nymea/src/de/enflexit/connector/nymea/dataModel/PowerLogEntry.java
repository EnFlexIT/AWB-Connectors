package de.enflexit.connector.nymea.dataModel;

/**
 * This class represents a power log entry, as used by nymea/consolinno HEMS systems.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class PowerLogEntry {
	
	private long timestamp;
	private String thingId;
	private double currentPower;
	private double totalConsumption;
	private double totalProduction;
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getThingId() {
		return thingId;
	}
	public void setThingId(String thingID) {
		this.thingId = thingID;
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
	
}
