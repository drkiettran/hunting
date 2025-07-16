package com.ops.hunting.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.ops.hunting.common.dto.AlertDto;
import com.ops.hunting.common.dto.AlertRequest;
import com.ops.hunting.common.dto.InvestigationDto;
import com.ops.hunting.common.dto.InvestigationRequest;
import com.ops.hunting.common.dto.SecurityEventDto;
import com.ops.hunting.common.dto.ThreatIntelDto;
import com.ops.hunting.common.dto.ThreatIntelRequest;
import com.ops.hunting.common.dto.UserDto;
import com.ops.hunting.common.dto.UserRequest;
import com.ops.hunting.common.entity.Alert;
import com.ops.hunting.common.entity.Investigation;
import com.ops.hunting.common.entity.SecurityEvent;
import com.ops.hunting.common.entity.ThreatIntel;
import com.ops.hunting.common.entity.User;

/**
 * Common mapper for entity-DTO conversions
 */
@Mapper(componentModel = "spring")
public interface CommonMapper {

	CommonMapper INSTANCE = Mappers.getMapper(CommonMapper.class);

	// ThreatIntel mappings
	ThreatIntelDto toDto(ThreatIntel entity);

	ThreatIntel toEntity(ThreatIntelDto dto);

	ThreatIntel toEntity(ThreatIntelRequest request);

	// Alert mappings
	AlertDto toDto(Alert entity);

	Alert toEntity(AlertDto dto);

	Alert toEntity(AlertRequest request);

	// User mappings
	@Mapping(target = "passwordHash", ignore = true)
	UserDto toDto(User entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "lastLogin", ignore = true)
	@Mapping(target = "failedLoginAttempts", ignore = true)
	@Mapping(target = "lockedUntil", ignore = true)
	@Mapping(target = "passwordResetToken", ignore = true)
	@Mapping(target = "passwordResetExpires", ignore = true)
	User toEntity(UserRequest request);

	// Investigation mappings
	InvestigationDto toDto(Investigation entity);

	Investigation toEntity(InvestigationDto dto);

	Investigation toEntity(InvestigationRequest request);

	// SecurityEvent mappings
	SecurityEventDto toDto(SecurityEvent entity);

	SecurityEvent toEntity(SecurityEventDto dto);
}