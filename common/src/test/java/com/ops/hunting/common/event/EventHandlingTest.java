package com.ops.hunting.common.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventHandlingTest {

    @Test
    @DisplayName("Should create and publish security event")
    void shouldCreateAndPublishSecurityEvent() {
        SecurityEvent event = SecurityEvent.builder()
            .eventType("AUTHENTICATION_FAILURE")
            .userId("user123")
            .ipAddress("192.168.1.100")
            .userAgent("Mozilla/5.0...")
            .timestamp(LocalDateTime.now())
            .success(false)
            .failureReason("Invalid password")
            .resourceAccessed("login_endpoint")
            .actionPerformed("LOGIN")
            .sessionId("session-123")
            .riskScore(75)
            .additionalData(Map.of("attemptCount", 3))
            .build();
        
        assertNotNull(event.getEventId());
        assertEquals("AUTHENTICATION_FAILURE", event.getEventType());
        assertEquals("user123", event.getUserId());
        assertEquals("192.168.1.100", event.getIpAddress());
        assertNotNull(event.getTimestamp());
        assertFalse(event.getSuccess());
        assertEquals("Invalid password", event.getFailureReason());
        assertEquals("login_endpoint", event.getResourceAccessed());
        assertEquals("LOGIN", event.getActionPerformed());
        assertEquals("session-123", event.getSessionId());
        assertEquals(75, event.getRiskScore());
        assertNotNull(event.getAdditionalData());
        assertEquals(3, event.getAdditionalData().get("attemptCount"));
    }

    @Test
    @DisplayName("Should create threat detection event")
    void shouldCreateThreatDetectionEvent() {
        ThreatDetectionEvent event = ThreatDetectionEvent.builder()
            .ruleId("RULE_001")
            .ruleName("Suspicious Outbound Connection")
            .severity("HIGH")
            .sourceIp("10.0.0.15")
            .destinationIp("192.168.1.100")
            .protocol("TCP")
            .port(443)
            .detectionTime(LocalDateTime.now())
            .rawData("Connection attempt to known malicious IP")
            .metadata(Map.of("confidence", 0.95, "source", "IDS"))
            .alertId("alert-456")
            .isBlocked(true)
            .blockReason("Known malicious IP")
            .build();
        
        assertNotNull(event.getEventId());
        assertEquals("RULE_001", event.getRuleId());
        assertEquals("Suspicious Outbound Connection", event.getRuleName());
        assertEquals("HIGH", event.getSeverity());
        assertEquals("10.0.0.15", event.getSourceIp());
        assertEquals("192.168.1.100", event.getDestinationIp());
        assertEquals("TCP", event.getProtocol());
        assertEquals(443, event.getPort());
        assertNotNull(event.getDetectionTime());
        assertEquals("Connection attempt to known malicious IP", event.getRawData());
        assertNotNull(event.getMetadata());
        assertEquals(0.95, event.getMetadata().get("confidence"));
        assertEquals("alert-456", event.getAlertId());
        assertTrue(event.getIsBlocked());
        assertEquals("Known malicious IP", event.getBlockReason());
    }

    @Test
    @DisplayName("Should create audit event")
    void shouldCreateAuditEvent() {
        AuditEvent event = AuditEvent.builder()
            .action("UPDATE")
            .entityType("ThreatIntel")
            .entityId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .username("analyst1")
            .timestamp(LocalDateTime.now())
            .oldValues("{\"confidence\": 80}")
            .newValues("{\"confidence\": 90}")
            .ipAddress("10.0.0.20")
            .userAgent("Mozilla/5.0...")
            .sessionId("session-789")
            .success(true)
            .build();
        
        assertNotNull(event.getEventId());
        assertEquals("UPDATE", event.getAction());
        assertEquals("ThreatIntel", event.getEntityType());
        assertNotNull(event.getEntityId());
        assertNotNull(event.getUserId());
        assertEquals("analyst1", event.getUsername());
        assertNotNull(event.getTimestamp());
        assertEquals("{\"confidence\": 80}", event.getOldValues());
        assertEquals("{\"confidence\": 90}", event.getNewValues());
        assertEquals("10.0.0.20", event.getIpAddress());
        assertEquals("session-789", event.getSessionId());
        assertTrue(event.getSuccess());
    }

    @Test
    @DisplayName("Should create investigation event")
    void shouldCreateInvestigationEvent() {
        UUID investigationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        InvestigationEvent event = InvestigationEvent.builder()
            .investigationId(investigationId)
            .eventType("ASSIGNED")
            .userId(userId)
            .username("investigator1")
            .timestamp(LocalDateTime.now())
            .description("Investigation assigned to investigator1")
            .changes(Map.of("assigneeId", userId.toString()))
            .priority("HIGH")
            .status("IN_PROGRESS")
            .build();
        
        assertNotNull(event.getEventId());
        assertEquals(investigationId, event.getInvestigationId());
        assertEquals("ASSIGNED", event.getEventType());
        assertEquals(userId, event.getUserId());
        assertEquals("investigator1", event.getUsername());
        assertNotNull(event.getTimestamp());
        assertEquals("Investigation assigned to investigator1", event.getDescription());
        assertNotNull(event.getChanges());
        assertEquals(userId.toString(), event.getChanges().get("assigneeId"));
        assertEquals("HIGH", event.getPriority());
        assertEquals("IN_PROGRESS", event.getStatus());
    }

    @Test
    @DisplayName("Should auto-generate event IDs and timestamps")
    void shouldAutoGenerateEventIdsAndTimestamps() {
        SecurityEvent event1 = SecurityEvent.builder()
            .eventType("TEST_EVENT")
            .build();
        
        SecurityEvent event2 = SecurityEvent.builder()
            .eventType("TEST_EVENT")
            .build();
        
        assertNotNull(event1.getEventId());
        assertNotNull(event2.getEventId());
        assertNotEquals(event1.getEventId(), event2.getEventId());
        
        assertNotNull(event1.getTimestamp());
        assertNotNull(event2.getTimestamp());
    }
}
