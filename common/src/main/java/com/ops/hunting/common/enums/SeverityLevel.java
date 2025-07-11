package com.ops.hunting.common.enums;

public enum SeverityLevel {
	LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);

	private final int numericValue;

	SeverityLevel(int numericValue) {
		this.numericValue = numericValue;
	}

	public int getNumericValue() {
		return numericValue;
	}
}
