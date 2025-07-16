package com.ops.hunting.alerts.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.jpa.hibernate.ddl-auto=create-drop", "spring.kafka.bootstrap-servers=localhost:29092" })
@Transactional
public class AlertManagementIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private AlertDTO alertDTO;

	@BeforeEach
	void setUp() {
		alertDTO = AlertDTO.builder().id(UUID.randomUUID()).title("Integration Test Alert")
				.description("Test alert for integration testing").severity(AlertSeverity.HIGH).status(AlertStatus.OPEN)
				.sourceSystem("TEST_SYSTEM").sourceIp("192.168.1.100").destinationIp("10.0.0.1")
				.createdAt(LocalDateTime.now()).build();
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void fullAlertLifecycle_ShouldWorkCorrectly() throws Exception {
		// Create alert
		String createResponse = mockMvc
				.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(alertDTO)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Integration Test Alert"))
				.andExpect(jsonPath("$.severity").value("HIGH")).andExpect(jsonPath("$.status").value("OPEN"))
				.andReturn().getResponse().getContentAsString();

		AlertDTO createdAlert = objectMapper.readValue(createResponse, AlertDTO.class);
		UUID alertId = createdAlert.getId();

		// Get alert by ID
		mockMvc.perform(get("/api/alerts/{id}", alertId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(alertId.toString()))
				.andExpect(jsonPath("$.title").value("Integration Test Alert"));

		// Update alert status
		mockMvc.perform(patch("/api/alerts/{id}/status", alertId).param("status", "IN_PROGRESS").param("assignedTo",
				"analyst1")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("IN_PROGRESS"))
				.andExpect(jsonPath("$.assignedTo").value("analyst1"));

		// Search alerts
		mockMvc.perform(get("/api/alerts/search").param("severity", "HIGH").param("status", "IN_PROGRESS"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].severity").value("HIGH"))
				.andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));

		// Get alert summary
		mockMvc.perform(get("/api/alerts/summary")).andExpect(status().isOk())
				.andExpect(jsonPath("$.totalAlerts").exists()).andExpect(jsonPath("$.openAlerts").exists())
				.andExpect(jsonPath("$.inProgressAlerts").exists());
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void createAlert_WithInvalidData_ShouldReturnBadRequest() throws Exception {
		AlertDTO invalidAlert = AlertDTO.builder().title("") // Empty title
				.severity(null) // Null severity
				.build();

		mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidAlert))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void getAlert_WithNonExistentId_ShouldReturnNotFound() throws Exception {
		UUID nonExistentId = UUID.randomUUID();

		mockMvc.perform(get("/api/alerts/{id}", nonExistentId)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void searchAlerts_WithComplexFilters_ShouldReturnFilteredResults() throws Exception {
		// Create multiple alerts first
		for (int i = 0; i < 5; i++) {
			AlertDTO alert = AlertDTO.builder().title("Test Alert " + i)
					.severity(i % 2 == 0 ? AlertSeverity.HIGH : AlertSeverity.MEDIUM).status(AlertStatus.OPEN)
					.sourceSystem("TEST_SYSTEM").build();

			mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(alert))).andExpect(status().isCreated());
		}

		// Search for high severity alerts
		mockMvc.perform(get("/api/alerts/search").param("severity", "HIGH").param("status", "OPEN").param("size", "10"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray());
	}
}