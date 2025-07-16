package com.ops.hunting.common.mapper;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ops.hunting.common.dto.AlertDto;
import com.ops.hunting.common.dto.InvestigationDto;
import com.ops.hunting.common.dto.ThreatIntelDto;
import com.ops.hunting.common.dto.ThreatIntelRequest;
import com.ops.hunting.common.dto.UserDto;
import com.ops.hunting.common.entity.Alert;
import com.ops.hunting.common.entity.Investigation;
import com.ops.hunting.common.entity.ThreatIntel;
import com.ops.hunting.common.entity.User;
import com.ops.hunting.common.enums.UserRole;

@ExtendWith(MockitoExtension.class)
class MapperTest {

	private CommonMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = new CommonMapperImpl();
	}

	@Test
	@DisplayName("Should map ThreatIntel entity to DTO correctly")
	void shouldMapThreatIntelEntityToDtoCorrectly() {
		ThreatIntel entity = ThreatIntel.builder().iocType("IP").iocValue("192.168.1.100").threatLevel("HIGH")
				.source("MISP").confidence(85).description("Malicious IP from botnet")
				.expiresAt(LocalDateTime.now().plusDays(30)).firstSeen(LocalDateTime.now().minusDays(10))
				.lastSeen(LocalDateTime.now().minusDays(1)).tags("malware,botnet").isActive(true).externalId("ext-123")
				.build();
		entity.onCreate();

		ThreatIntelDto dto = mapper.toDto(entity);

		assertNotNull(dto);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getIocType(), dto.getIocType());
		assertEquals(entity.getIocValue(), dto.getIocValue());
		assertEquals(entity.getThreatLevel(), dto.getThreatLevel());
		assertEquals(entity.getSource(), dto.getSource());
		assertEquals(entity.getConfidence(), dto.getConfidence());
		assertEquals(entity.getDescription(), dto.getDescription());
		assertEquals(entity.getExpiresAt(), dto.getExpiresAt());
		assertEquals(entity.getFirstSeen(), dto.getFirstSeen());
		assertEquals(entity.getLastSeen(), dto.getLastSeen());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getIsActive(), dto.getIsActive());
		assertEquals(entity.getExternalId(), dto.getExternalId());
		assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
		assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
	}

	@Test
	@DisplayName("Should map ThreatIntelRequest to entity correctly")
	void shouldMapThreatIntelRequestToEntityCorrectly() {
		ThreatIntelRequest request = ThreatIntelRequest.builder().iocType("DOMAIN").iocValue("malicious.example.com")
				.threatLevel("CRITICAL").source("Commercial Feed").confidence(95).description("Known C2 domain")
				.expiresAt(LocalDateTime.now().plusMonths(1)).tags("c2,domain").externalId("ext-456").build();

		ThreatIntel entity = mapper.toEntity(request);

		assertNotNull(entity);
		assertEquals(request.getIocType(), entity.getIocType());
		assertEquals(request.getIocValue(), entity.getIocValue());
		assertEquals(request.getThreatLevel(), entity.getThreatLevel());
		assertEquals(request.getSource(), entity.getSource());
		assertEquals(request.getConfidence(), entity.getConfidence());
		assertEquals(request.getDescription(), entity.getDescription());
		assertEquals(request.getExpiresAt(), entity.getExpiresAt());
		assertEquals(request.getTags(), entity.getTags());
		assertEquals(request.getExternalId(), entity.getExternalId());
	}

	@Test
	@DisplayName("Should map Alert entity to DTO correctly")
	void shouldMapAlertEntityToDtoCorrectly() {
		Alert entity = Alert.builder().title("Suspicious Network Activity")
				.description("Unusual outbound connections detected").severity("CRITICAL").status("OPEN")
				.ruleId("RULE_001").ruleName("Outbound Connection Monitor").sourceIp("10.0.0.15")
				.destinationIp("192.168.1.100").protocol("TCP").port(443).detectedAt(LocalDateTime.now())
				.assignedTo(UUID.randomUUID()).rawData("Connection log data...").falsePositive(false).build();
		entity.onCreate();

		AlertDto dto = mapper.toDto(entity);

		assertNotNull(dto);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getTitle(), dto.getTitle());
		assertEquals(entity.getDescription(), dto.getDescription());
		assertEquals(entity.getSeverity(), dto.getSeverity());
		assertEquals(entity.getStatus(), dto.getStatus());
		assertEquals(entity.getRuleId(), dto.getRuleId());
		assertEquals(entity.getRuleName(), dto.getRuleName());
		assertEquals(entity.getSourceIp(), dto.getSourceIp());
		assertEquals(entity.getDestinationIp(), dto.getDestinationIp());
		assertEquals(entity.getProtocol(), dto.getProtocol());
		assertEquals(entity.getPort(), dto.getPort());
		assertEquals(entity.getDetectedAt(), dto.getDetectedAt());
		assertEquals(entity.getAssignedTo(), dto.getAssignedTo());
		assertEquals(entity.getRawData(), dto.getRawData());
		assertEquals(entity.getFalsePositive(), dto.getFalsePositive());
	}

	@Test
	@DisplayName("Should map User entity to DTO correctly excluding password")
	void shouldMapUserEntityToDtoCorrectlyExcludingPassword() {
		User entity = User.builder().username("analyst1").email("analyst1@company.com")
				.passwordHash("hashedPassword123").firstName("John").lastName("Doe").role(UserRole.ANALYST)
				.isActive(true).lastLogin(LocalDateTime.now().minusHours(2)).failedLoginAttempts(1).lockedUntil(null)
				.build();
		entity.onCreate();

		UserDto dto = mapper.toDto(entity);

		assertNotNull(dto);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getUsername(), dto.getUsername());
		assertEquals(entity.getEmail(), dto.getEmail());
		assertEquals(entity.getFirstName(), dto.getFirstName());
		assertEquals(entity.getLastName(), dto.getLastName());
		assertEquals(entity.getRole(), dto.getRole());
		assertEquals(entity.getIsActive(), dto.getIsActive());
		assertEquals(entity.getLastLogin(), dto.getLastLogin());
		assertEquals(entity.getFailedLoginAttempts(), dto.getFailedLoginAttempts());
		assertEquals(entity.getLockedUntil(), dto.getLockedUntil());
		// Password hash should not be included in DTO
	}

	@Test
	@DisplayName("Should handle null values in mapping")
	void shouldHandleNullValuesInMapping() {
		ThreatIntel entity = null;
		ThreatIntelDto dto = mapper.toDto(entity);
		assertNull(dto);

		ThreatIntelRequest nullRequest = null;
		ThreatIntel nullEntity = mapper.toEntity(nullRequest);
		assertNull(nullEntity);

		AlertDto nullAlertDto = null;
		Alert nullAlert = mapper.toEntity(nullAlertDto);
		assertNull(nullAlert);
	}

	@Test
	@DisplayName("Should map Investigation entity to DTO correctly")
	void shouldMapInvestigationEntityToDtoCorrectly() {
		Investigation entity = Investigation.builder().title("Data Exfiltration Investigation")
				.description("Investigating potential data exfiltration").priority("HIGH").status("IN_PROGRESS")
				.assigneeId(UUID.randomUUID()).createdBy(UUID.randomUUID()).tags("data-loss,network")
				.findings("Suspicious network activity detected")
				.recommendations("Block source IP, review access controls").build();
		entity.onCreate();

		InvestigationDto dto = mapper.toDto(entity);

		assertNotNull(dto);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getTitle(), dto.getTitle());
		assertEquals(entity.getDescription(), dto.getDescription());
		assertEquals(entity.getPriority(), dto.getPriority());
		assertEquals(entity.getStatus(), dto.getStatus());
		assertEquals(entity.getAssigneeId(), dto.getAssigneeId());
		assertEquals(entity.getCreatedBy(), dto.getCreatedBy());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getFindings(), dto.getFindings());
		assertEquals(entity.getRecommendations(), dto.getRecommendations());
	}
}
