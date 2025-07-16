package com.ops.hunting.alerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

//=============================================================================
//ALERT MANAGEMENT SERVICE - COMPREHENSIVE TEST SUITE
//=============================================================================
//This test suite covers the alert-management-service module from the hunting repository
//Repository: https://github.com/drkiettran/hunting
//Service: alert-management-service (Port 8084)
//Stack: Spring Boot 3.2, Java 17, MySQL 8.0, Redis, Kafka, Elasticsearch
//=============================================================================

//=============================================================================

//1. UNIT TESTS - CONTROLLER LAYER
//=============================================================================

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.dto.AlertSummaryDTO;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.service.AlertService;
import com.ops.hunting.alerts.enums.AlertStatus;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AlertController.class)
public class AlertControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AlertService alertService;

	@Autowired
	private ObjectMapper objectMapper;

	private AlertDTO alertDTO;
	private UUID alertId;

	@BeforeEach
	void setUp() {
		alertId = UUID.randomUUID();
		alertDTO = AlertDTO.builder().id(alertId).title("Suspicious Network Activity")
				.description("Potential data exfiltration detected").severity(AlertSeverity.HIGH)
				.status(AlertStatus.OPEN).sourceSystem("IDS").sourceIp("192.168.1.100").destinationIp("10.0.0.1")
				.createdAt(LocalDateTime.now()).build();
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void createAlert_ShouldReturnCreatedAlert() throws Exception {
		when(alertService.createAlert(any(AlertDTO.class))).thenReturn(alertDTO);

		mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(alertDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(alertId.toString()))
				.andExpect(jsonPath("$.title").value("Suspicious Network Activity"))
				.andExpect(jsonPath("$.severity").value("HIGH")).andExpect(jsonPath("$.status").value("OPEN"));

		verify(alertService, times(1)).createAlert(any(AlertDTO.class));
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void getAlertById_ShouldReturnAlert() throws Exception {
		when(alertService.getAlertById(alertId)).thenReturn(alertDTO);

		mockMvc.perform(get("/api/alerts/{id}", alertId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(alertId.toString()))
				.andExpect(jsonPath("$.title").value("Suspicious Network Activity"));

		verify(alertService, times(1)).getAlertById(alertId);
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void getAllAlerts_ShouldReturnPagedAlerts() throws Exception {
		List<AlertDTO> alerts = Arrays.asList(alertDTO);
		Page<AlertDTO> page = new PageImpl<>(alerts, PageRequest.of(0, 10), 1);

		when(alertService.getAllAlerts(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/alerts").param("page", "0").param("size", "10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].id").value(alertId.toString()))
				.andExpect(jsonPath("$.totalElements").value(1));

		verify(alertService, times(1)).getAllAlerts(any(PageRequest.class));
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void updateAlertStatus_ShouldUpdateStatus() throws Exception {
		AlertDTO updatedAlert = AlertDTO.builder().id(alertId).status(AlertStatus.IN_PROGRESS).build();

		when(alertService.updateAlertStatus(alertId, AlertStatus.IN_PROGRESS, "analyst1")).thenReturn(updatedAlert);

		mockMvc.perform(patch("/api/alerts/{id}/status", alertId).param("status", "IN_PROGRESS").param("assignedTo",
				"analyst1")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("IN_PROGRESS"));

		verify(alertService, times(1)).updateAlertStatus(alertId, AlertStatus.IN_PROGRESS, "analyst1");
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void searchAlerts_ShouldReturnFilteredResults() throws Exception {
		List<AlertDTO> alerts = Arrays.asList(alertDTO);
		Page<AlertDTO> page = new PageImpl<>(alerts, PageRequest.of(0, 10), 1);

		when(alertService.searchAlerts(eq(AlertSeverity.HIGH), eq(AlertStatus.OPEN), any(LocalDateTime.class),
				any(LocalDateTime.class), any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/alerts/search").param("severity", "HIGH").param("status", "OPEN")
				.param("from", "2024-01-01T00:00:00").param("to", "2024-12-31T23:59:59")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.content[0].severity").value("HIGH"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void deleteAlert_ShouldDeleteAlert() throws Exception {
		doNothing().when(alertService).deleteAlert(alertId);

		mockMvc.perform(delete("/api/alerts/{id}", alertId)).andExpect(status().isNoContent());

		verify(alertService, times(1)).deleteAlert(alertId);
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void getAlertSummary_ShouldReturnSummary() throws Exception {
		AlertSummaryDTO summary = AlertSummaryDTO.builder().totalAlerts(100L).openAlerts(25L).inProgressAlerts(15L)
				.closedAlerts(60L).highSeverityAlerts(10L).build();

		when(alertService.getAlertSummary()).thenReturn(summary);

		mockMvc.perform(get("/api/alerts/summary")).andExpect(status().isOk())
				.andExpect(jsonPath("$.totalAlerts").value(100)).andExpect(jsonPath("$.openAlerts").value(25))
				.andExpect(jsonPath("$.highSeverityAlerts").value(10));
	}

	@Test
	void createAlert_WithoutAuthentication_ShouldReturn401() throws Exception {
		mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(alertDTO))).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = "VIEWER")
	void deleteAlert_WithInsufficientPermissions_ShouldReturn403() throws Exception {
		mockMvc.perform(delete("/api/alerts/{id}", alertId)).andExpect(status().isForbidden());
	}
}