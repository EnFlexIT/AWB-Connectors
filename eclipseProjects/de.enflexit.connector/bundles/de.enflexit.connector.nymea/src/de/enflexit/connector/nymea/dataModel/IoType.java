package de.enflexit.connector.nymea.dataModel;

public enum IoType {
	
	IO_TYPE_NONE("IOTypeNone"),
	IO_TYPE_DIGITAL_INPUT("IOTypeDigitalInput"),
	IO_TYPE_DIGITAL_OUTPUT("IOTypeDigitalOutput"),
	IO_TYPE_ANALOG_INPUT("IOTypeAnalogInput"),
	IO_TYPE_ANALOG_OUTPUT("IOTypeAnalogOutput");
	
	private String value;

	IoType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static IoType getTypeForValue(String value) {
		for (IoType ioType : values()) {
			if (ioType.getValue().equals(value)) {
				return ioType;
			}
		}
		return null;
	}
	
}
