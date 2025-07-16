package com.ops.hunting.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.dto.AlertSummaryDTO;
import com.ops.hunting.alerts.entity.Alert;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;
import com.ops.hunting.alerts.exception.AlertNotFoundException;
import com.ops.hunting.alerts.mapper.AlertMapper;
import com.ops.hunting.alerts.repository.AlertRepository;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

	@Mock
	private AlertRepository alertRepository;

	@Mock
	private AlertMapper alertMapper;

	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private AlertServiceImpl alertService;

	private Alert alert;
	private AlertDTO alertDTO;
	private UUID alertId;

	@BeforeEach
	void setUp() {
		alertId = UUID.randomUUID();
		alert = Alert.builder().id(alertId).title("Suspicious Network Activity")
				.description("Potential data exfiltration detected").severity(AlertSeverity.HIGH)
				.status(AlertStatus.OPEN).sourceSystem("IDS").sourceIp("192.168.1.100").destinationIp("10.0.0.1")
				.createdAt(LocalDateTime.now()).build();

		alertDTO = AlertDTO.builder().id(alertId).title("Suspicious Network Activity")
				.description("Potential data exfiltration detected").severity(AlertSeverity.HIGH)
				.status(AlertStatus.OPEN).sourceSystem("IDS").sourceIp("192.168.1.100").destinationIp("10.0.0.1")
				.createdAt(LocalDateTime.now()).build();
	}

	@Test
	void createAlert_ShouldCreateAndReturnAlert() {
		when(alertMapper.toEntity(alertDTO)).thenReturn(alert);
		when(alertRepository.save(alert)).thenReturn(alert);
		when(alertMapper.toDTO(alert)).thenReturn(alertDTO);

		AlertDTO result = alertService.createAlert(alertDTO);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(alertId);
		assertThat(result.getTitle()).isEqualTo("Suspicious Network Activity");
		assertThat(result.getSeverity()).isEqualTo(AlertSeverity.HIGH);

		verify(alertRepository, times(1)).save(alert);
		verify(kafkaTemplate, times(1)).send(eq("alert-created"), any());
	}

	@Test
	void getAlertById_WhenAlertExists_ShouldReturnAlert() {
		when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
		when(alertMapper.toDTO(alert)).thenReturn(alertDTO);

		AlertDTO result = alertService.getAlertById(alertId);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(alertId);
		verify(alertRepository, times(1)).findById(alertId);
	}

	@Test
	void getAlertById_WhenAlertNotExists_ShouldThrowException() {
		when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> alertService.getAlertById(alertId)).isInstanceOf(AlertNotFoundException.class)
				.hasMessage("Alert not found with id: " + alertId);
	}

	@Test
	void getAllAlerts_ShouldReturnPagedAlerts() {
		List<Alert> alerts = Arrays.asList(alert);
		Page<Alert> page = new PageImpl<>(alerts, PageRequest.of(0, 10), 1);
		Pageable pageable = PageRequest.of(0, 10);

		when(alertRepository.findAll(pageable)).thenReturn(page);
		when(alertMapper.toDTO(alert)).thenReturn(alertDTO);

		Page<AlertDTO> result = alertService.getAllAlerts(pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getId()).isEqualTo(alertId);
		verify(alertRepository, times(1)).findAll(pageable);
	}

	@Test
	void updateAlertStatus_ShouldUpdateStatusAndAssignee() {
		Alert updatedAlert = Alert.builder().id(alertId).status(AlertStatus.IN_PROGRESS).assignedTo("analyst1")
				.updatedAt(LocalDateTime.now()).build();

		AlertDTO updatedDTO = AlertDTO.builder().id(alertId).status(AlertStatus.IN_PROGRESS).assignedTo("analyst1")
				.build();

		when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
		when(alertRepository.save(any(Alert.class))).thenReturn(updatedAlert);
		when(alertMapper.toDTO(updatedAlert)).thenReturn(updatedDTO);

		AlertDTO result = alertService.updateAlertStatus(alertId, AlertStatus.IN_PROGRESS, "analyst1");

		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(AlertStatus.IN_PROGRESS);
		assertThat(result.getAssignedTo()).isEqualTo("analyst1");
		verify(kafkaTemplate, times(1)).send(eq("alert-status-updated"), any());
	}

	@Test
	void searchAlerts_ShouldReturnFilteredResults() {
		List<Alert> alerts = Arrays.asList(alert);
		Page<Alert> page = new PageImpl<>(alerts, PageRequest.of(0, 10), 1);
		Pageable pageable = PageRequest.of(0, 10);
		LocalDateTime from = LocalDateTime.now().minusDays(1);
		LocalDateTime to = LocalDateTime.now();

		when(alertRepository.findBySeverityAndStatusAndCreatedAtBetween(AlertSeverity.HIGH, AlertStatus.OPEN, from, to,
				pageable)).thenReturn(page);
		when(alertMapper.toDTO(alert)).thenReturn(alertDTO);

		Page<AlertDTO> result = alertService.searchAlerts(AlertSeverity.HIGH, AlertStatus.OPEN, from, to, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getSeverity()).isEqualTo(AlertSeverity.HIGH);
	}

	@Test
	void deleteAlert_ShouldDeleteAlert() {
		when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
		doNothing().when(alertRepository).delete(alert);

		alertService.deleteAlert(alertId);

		verify(alertRepository, times(1)).delete(alert);
		verify(kafkaTemplate, times(1)).send(eq("alert-deleted"), any());
	}

	@Test
	void getAlertSummary_ShouldReturnSummary() {
		when(alertRepository.count()).thenReturn(100L);
		when(alertRepository.countByStatus(AlertStatus.OPEN)).thenReturn(25L);
		when(alertRepository.countByStatus(AlertStatus.IN_PROGRESS)).thenReturn(15L);
		when(alertRepository.countByStatus(AlertStatus.CLOSED)).thenReturn(60L);
		when(alertRepository.countBySeverity(AlertSeverity.HIGH)).thenReturn(10L);

		AlertSummaryDTO result = alertService.getAlertSummary();

		assertThat(result).isNotNull();
		assertThat(result.getTotalAlerts()).isEqualTo(100L);
		assertThat(result.getOpenAlerts()).isEqualTo(25L);
		assertThat(result.getInProgressAlerts()).isEqualTo(15L);
		assertThat(result.getClosedAlerts()).isEqualTo(60L);
		assertThat(result.getHighSeverityAlerts()).isEqualTo(10L);
	}

	@Test
	void createAlert_WithHighSeverity_ShouldSendNotification() {
		AlertDTO highSeverityAlert = AlertDTO.builder().severity(AlertSeverity.CRITICAL)
				.title("Critical Security Breach").build();

		Alert highSeverityAlertEntity = Alert.builder().severity(AlertSeverity.CRITICAL)
				.title("Critical Security Breach").build();

		when(alertMapper.toEntity(highSeverityAlert)).thenReturn(highSeverityAlertEntity);
		when(alertRepository.save(highSeverityAlertEntity)).thenReturn(highSeverityAlertEntity);
		when(alertMapper.toDTO(highSeverityAlertEntity)).thenReturn(highSeverityAlert);

		alertService.createAlert(highSeverityAlert);

		verify(notificationService, times(1)).sendCriticalAlertNotification(highSeverityAlert);
	}
}