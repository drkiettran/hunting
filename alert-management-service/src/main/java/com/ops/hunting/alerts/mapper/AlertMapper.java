package com.ops.hunting.alerts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.entity.Alert;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AlertMapper {

	AlertDTO toDTO(Alert alert);

	Alert toEntity(AlertDTO alertDTO);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	void updateEntityFromDTO(AlertDTO alertDTO, @MappingTarget Alert alert);
}
