package com.ops.hunting.common.service;

import java.util.Optional;
import java.util.UUID;

import com.ops.hunting.common.dto.ThreatIntelDto;
import com.ops.hunting.common.dto.ThreatIntelRequest;

public interface ThreatIntelService {

	/**
	 * Find threat intel by IOC value
	 */
	Optional<ThreatIntelDto> findByIocValue(String iocValue);

	/**
	 * Create new threat intelligence
	 */
	ThreatIntelDto create(ThreatIntelRequest request);

	/**
	 * Update threat intelligence
	 */
	ThreatIntelDto update(UUID id, ThreatIntelRequest request);

	/**
	 * Delete threat intelligence
	 */
	void delete(UUID id);

	/**
	 * Check if IOC is in threat intelligence
	 */
	boolean isKnownThreat(String iocType, String iocValue);

	/**
	 * Get active threat intelligence count
	 */
	long getActiveCount();
}
