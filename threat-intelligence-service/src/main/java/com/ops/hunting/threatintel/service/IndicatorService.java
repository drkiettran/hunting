package com.ops.hunting.threatintel.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.hunting.common.enums.IndicatorType;
import com.ops.hunting.threatintel.dto.IndicatorDto;
import com.ops.hunting.threatintel.entity.Indicator;
import com.ops.hunting.threatintel.repository.IndicatorRepository;

@Service
@Transactional
public class IndicatorService {

	private final IndicatorRepository indicatorRepository;

	@Autowired
	public IndicatorService(IndicatorRepository indicatorRepository) {
		this.indicatorRepository = indicatorRepository;
	}

	@Transactional
	public IndicatorDto createIndicator(IndicatorDto dto) {
		// Check if indicator already exists
		if (indicatorRepository.existsByTypeAndValue(dto.getType(), dto.getValue())) {
			throw new RuntimeException(
					"Indicator already exists with type: " + dto.getType() + " and value: " + dto.getValue());
		}

		Indicator entity = convertToEntity(dto);
		Indicator saved = indicatorRepository.save(entity);
		return convertToDto(saved);
	}

	@Cacheable(value = "indicators", key = "#id")
	public Optional<IndicatorDto> getIndicatorById(String id) {
		return indicatorRepository.findById(id).map(this::convertToDto);
	}

	public Page<IndicatorDto> getAllIndicators(Pageable pageable) {
		return indicatorRepository.findAll(pageable).map(this::convertToDto);
	}

	public Page<IndicatorDto> searchIndicators(String search, Pageable pageable) {
		return indicatorRepository.searchIndicators(search, pageable).map(this::convertToDto);
	}

	public List<IndicatorDto> getIndicatorsByType(IndicatorType type) {
		return indicatorRepository.findByType(type).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public Optional<IndicatorDto> getIndicatorByTypeAndValue(IndicatorType type, String value) {
		return indicatorRepository.findByTypeAndValue(type, value).map(this::convertToDto);
	}

	public List<IndicatorDto> getHighConfidenceIndicators(BigDecimal minConfidence) {
		return indicatorRepository.findByConfidenceGreaterThanEqual(minConfidence).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@CacheEvict(value = "indicators", key = "#id")
	@Transactional
	public IndicatorDto updateIndicator(String id, IndicatorDto dto) {
		Optional<Indicator> existingOpt = indicatorRepository.findById(id);
		if (existingOpt.isEmpty()) {
			throw new RuntimeException("Indicator not found with id: " + id);
		}

		Indicator existing = existingOpt.get();
		existing.setType(dto.getType());
		existing.setValue(dto.getValue());
		existing.setDescription(dto.getDescription());
		existing.setConfidence(dto.getConfidence());

		Indicator updated = indicatorRepository.save(existing);
		return convertToDto(updated);
	}

	@CacheEvict(value = "indicators", key = "#id")
	@Transactional
	public void deleteIndicator(String id) {
		if (!indicatorRepository.existsById(id)) {
			throw new RuntimeException("Indicator not found with id: " + id);
		}
		indicatorRepository.deleteById(id);
	}

	public List<Object[]> getIndicatorTypeStatistics() {
		return indicatorRepository.countByType();
	}

	public Indicator convertToEntity(IndicatorDto dto) {
		Indicator entity = new Indicator();
		entity.setType(dto.getType());
		entity.setValue(dto.getValue());
		entity.setDescription(dto.getDescription());
		entity.setConfidence(dto.getConfidence());
		return entity;
	}

	public IndicatorDto convertToDto(Indicator entity) {
		IndicatorDto dto = new IndicatorDto();
		dto.setId(entity.getId());
		dto.setType(entity.getType());
		dto.setValue(entity.getValue());
		dto.setDescription(entity.getDescription());
		dto.setConfidence(entity.getConfidence());
		dto.setCreatedDate(entity.getCreatedDate());
		return dto;
	}
}