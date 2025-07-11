package com.ops.hunting.common.enums;

public enum AnalystTier {
	TIER_1("Tier 1 - Initial Triage"), TIER_2("Tier 2 - Deep Analysis"), TIER_3("Tier 3 - Analytics Development");

	private final String description;

	AnalystTier(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
