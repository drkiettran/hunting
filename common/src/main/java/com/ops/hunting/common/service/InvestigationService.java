package com.ops.hunting.common.service;

import java.util.UUID;

import com.ops.hunting.common.dto.InvestigationDto;
import com.ops.hunting.common.dto.InvestigationRequest;

public interface InvestigationService {

	/**
	 * Create new investigation
	 */
	InvestigationDto createInvestigation(InvestigationRequest request);

	/**
	 * Update investigation
	 */
	InvestigationDto updateInvestigation(UUID investigationId, InvestigationRequest request);

	/**
	 * Close investigation
	 */
	void closeInvestigation(UUID investigationId, String findings, String recommendations);

	/**
	 * Assign investigation to user
	 */
	void assignInvestigation(UUID investigationId, UUID assigneeId);

	/**
	 * Add alert to investigation
	 */
	void addAlertToInvestigation(UUID investigationId, UUID alertId);

	/**
	 * Remove alert from investigation
	 */
	void removeAlertFromInvestigation(UUID investigationId, UUID alertId);
}
