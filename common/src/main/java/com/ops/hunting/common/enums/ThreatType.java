package com.ops.hunting.common.enums;

public enum ThreatType {
	MALWARE("Malware"), PHISHING("Phishing"), APT("Advanced Persistent Threat"), INSIDER_THREAT("Insider Threat"),
	VULNERABILITY("Vulnerability"), BOTNET("Botnet");

	private final String description;

	ThreatType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
