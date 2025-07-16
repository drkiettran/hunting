package com.ops.hunting.alerts.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class AlertSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void accessWithoutAuthentication_ShouldReturn401() throws Exception {
		mockMvc.perform(get("/api/alerts")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = "VIEWER")
	void viewerRole_ShouldOnlyAllowReadOperations() throws Exception {
		// Should allow GET operations
		mockMvc.perform(get("/api/alerts")).andExpect(status().isOk());

		// Should deny POST operations
		AlertDTO alertDTO = AlertDTO.builder().title("Test Alert").severity(AlertSeverity.HIGH).status(AlertStatus.OPEN)
				.build();

		mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(alertDTO))).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void analystRole_ShouldAllowCrudOperations() throws Exception {
		AlertDTO alertDTO = AlertDTO.builder().title("Analyst Test Alert").severity(AlertSeverity.HIGH)
				.status(AlertStatus.OPEN).sourceSystem("TEST_SYSTEM").createdAt(LocalDateTime.now()).build();

		// Should allow CREATE
		mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(alertDTO))).andExpect(status().isCreated());

		// Should allow READ
		mockMvc.perform(get("/api/alerts")).andExpect(status().isOk());

		// Should allow UPDATE
		mockMvc.perform(patch("/api/alerts/{id}/status", UUID.randomUUID()).param("status", "IN_PROGRESS"))
				.andExpect(status().isNotFound()); // 404 because alert doesn't exist, but no 403
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void adminRole_ShouldAllowAllOperations() throws Exception {
		// Should allow DELETE operations
		mockMvc.perform(delete("/api/alerts/{id}", UUID.randomUUID())).andExpect(status().isNotFound()); // 404 because
																											// alert
																											// doesn't
																											// exist,
																											// but no
																											// 403
	}

	@Test
	@WithMockUser(roles = "INVALID_ROLE")
	void invalidRole_ShouldDenyAccess() throws Exception {
		mockMvc.perform(get("/api/alerts")).andExpect(status().isForbidden());
	}
}