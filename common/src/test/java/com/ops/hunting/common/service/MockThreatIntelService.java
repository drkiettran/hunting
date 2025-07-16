package com.ops.hunting.common.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.ops.hunting.common.dto.ThreatIntelDto;
import com.ops.hunting.common.dto.ThreatIntelRequest;

/**
 * Mock ThreatIntelService implementation for testing
 */
public class MockThreatIntelService implements ThreatIntelService {

	private final Map<String, ThreatIntelDto> threats = new HashMap<>();

	public MockThreatIntelService() {
		// Add default test threats
		ThreatIntelDto threat = ThreatIntelDto.builder().id(UUID.randomUUID()).iocType("IP").iocValue("192.168.1.100")
				.threatLevel("HIGH").source("Test Source").confidence(85).description("Test threat intel")
				.isActive(true).createdAt(LocalDateTime.now()).build();
		threats.put("192.168.1.100", threat);
	}

	@Override
	public Optional<ThreatIntelDto> findByIocValue(String iocValue) {
		return Optional.ofNullable(threats.get(iocValue));
	}

	@Override
	public ThreatIntelDto create(ThreatIntelRequest request) {
		ThreatIntelDto threat = ThreatIntelDto.builder().id(UUID.randomUUID()).iocType(request.getIocType())
				.iocValue(request.getIocValue()).threatLevel(request.getThreatLevel()).source(request.getSource())
				.confidence(request.getConfidence()).description(request.getDescription()).isActive(true)
				.createdAt(LocalDateTime.now()).build();

		threats.put(request.getIocValue(), threat);
		return threat;
	}

	@Override
	public ThreatIntelDto update(UUID id, ThreatIntelRequest request) {
		// Mock implementation
		return threats.values().iterator().next();
	}

	@Override
	public void delete(UUID id) {
		// Mock implementation
	}

	@Override
	public boolean isKnownThreat(String iocType, String iocValue) {
		return threats.containsKey(iocValue);
	}

	@Override
	public long getActiveCount() {
		return threats.size();
	}
}
