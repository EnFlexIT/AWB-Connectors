package de.enflexit.connector.nymea.dataModel;

public enum SampleRate {
	SAMPLE_RATE_ANY("SampleRateAny"),
	SAMPLE_RATE_1_MIN("SampleRate1Min"),
	SAMPLE_RATE_15_MINS("SampleRate15Mins"),
	SAMPLE_RATE_1_HOUR("SampleRate1Hour"),
	SAMPLE_RATE_3_HOURS("SampleRate3Hours"),
	SAMPLE_RATE_1_DAY("SampleRate1Day"),
	SAMPLE_RATE_1_WEEK("SampleRate1Week"),
	SAMPLE_RATE_1_MONTH("SampleRate1Month"),
	SAMPLE_RATE_1_YEAR("SampleRate1Year");

	private String value;
	
	SampleRate(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
