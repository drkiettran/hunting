package com.ops.hunting.products.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.hunting.common.enums.Classification;
import com.ops.hunting.common.enums.ProductType;
import com.ops.hunting.products.dto.IntelligenceProductDto;
import com.ops.hunting.products.entity.IntelligenceProduct;
import com.ops.hunting.products.repository.IntelligenceProductRepository;

@Service
@Transactional
public class IntelligenceProductService {

	private final IntelligenceProductRepository productRepository;
	private final ProductGenerationService productGenerationService;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	public IntelligenceProductService(IntelligenceProductRepository productRepository,
			ProductGenerationService productGenerationService, KafkaTemplate<String, Object> kafkaTemplate) {
		this.productRepository = productRepository;
		this.productGenerationService = productGenerationService;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Transactional
	public IntelligenceProductDto createProduct(IntelligenceProductDto dto) {
		IntelligenceProduct entity = convertToEntity(dto);
		IntelligenceProduct saved = productRepository.save(entity);

		publishProductEvent(saved, "PRODUCT_CREATED");
		return convertToDto(saved);
	}

	@Cacheable(value = "products", key = "#id")
	public Optional<IntelligenceProductDto> getProductById(String id) {
		return productRepository.findById(id).map(this::convertToDto);
	}

	public Page<IntelligenceProductDto> getAllProducts(Pageable pageable) {
		return productRepository.findAll(pageable).map(this::convertToDto);
	}

	public Page<IntelligenceProductDto> searchProducts(String search, Pageable pageable) {
		return productRepository.searchProducts(search, pageable).map(this::convertToDto);
	}

	public List<IntelligenceProductDto> getProductsByType(ProductType type) {
		return productRepository.findByType(type).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<IntelligenceProductDto> getProductsByAuthor(String author) {
		return productRepository.findByAuthor(author).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<IntelligenceProductDto> getProductsByClassification(Classification classification) {
		return productRepository.findByClassification(classification).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<IntelligenceProductDto> getPublishedProducts() {
		return productRepository.findByPublishedDateIsNotNull().stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<IntelligenceProductDto> getProductsUnderReview() {
		return productRepository.findByReviewStatus(IntelligenceProduct.ReviewStatus.UNDER_REVIEW).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	@CacheEvict(value = "products", key = "#id")
	@Transactional
	public IntelligenceProductDto updateProduct(String id, IntelligenceProductDto dto) {
		Optional<IntelligenceProduct> existingOpt = productRepository.findById(id);
		if (existingOpt.isEmpty()) {
			throw new RuntimeException("Product not found with id: " + id);
		}

		IntelligenceProduct existing = existingOpt.get();
		existing.setTitle(dto.getTitle());
		existing.setContent(dto.getContent());
		existing.setClassification(dto.getClassification());
		existing.setDistributionList(dto.getDistributionList());

		IntelligenceProduct updated = productRepository.save(existing);
		publishProductEvent(updated, "PRODUCT_UPDATED");
		return convertToDto(updated);
	}

	@CacheEvict(value = "products", key = "#id")
	@Transactional
	public IntelligenceProductDto submitForReview(String id) {
		Optional<IntelligenceProduct> productOpt = productRepository.findById(id);
		if (productOpt.isEmpty()) {
			throw new RuntimeException("Product not found with id: " + id);
		}

		IntelligenceProduct product = productOpt.get();
		product.submitForReview();
		IntelligenceProduct updated = productRepository.save(product);

		publishProductEvent(updated, "PRODUCT_SUBMITTED_FOR_REVIEW");
		return convertToDto(updated);
	}

	@CacheEvict(value = "products", key = "#id")
	@Transactional
	public IntelligenceProductDto approveProduct(String id, String reviewedBy, String comments) {
		Optional<IntelligenceProduct> productOpt = productRepository.findById(id);
		if (productOpt.isEmpty()) {
			throw new RuntimeException("Product not found with id: " + id);
		}

		IntelligenceProduct product = productOpt.get();
		product.approve(reviewedBy, comments);
		IntelligenceProduct updated = productRepository.save(product);

		publishProductEvent(updated, "PRODUCT_APPROVED");
		return convertToDto(updated);
	}

	@CacheEvict(value = "products", key = "#id")
	@Transactional
	public IntelligenceProductDto rejectProduct(String id, String reviewedBy, String comments) {
		Optional<IntelligenceProduct> productOpt = productRepository.findById(id);
		if (productOpt.isEmpty()) {
			throw new RuntimeException("Product not found with id: " + id);
		}

		IntelligenceProduct product = productOpt.get();
		product.reject(reviewedBy, comments);
		IntelligenceProduct updated = productRepository.save(product);

		publishProductEvent(updated, "PRODUCT_REJECTED");
		return convertToDto(updated);
	}

	@CacheEvict(value = "products", key = "#id")
	@Transactional
	public IntelligenceProductDto publishProduct(String id) {
		Optional<IntelligenceProduct> productOpt = productRepository.findById(id);
		if (productOpt.isEmpty()) {
			throw new RuntimeException("Product not found with id: " + id);
		}

		IntelligenceProduct product = productOpt.get();
		product.publish();
		IntelligenceProduct updated = productRepository.save(product);

		publishProductEvent(updated, "PRODUCT_PUBLISHED");
		return convertToDto(updated);
	}

	@Transactional
	public IntelligenceProductDto generateProductFromTemplate(String templateId, String investigationId,
			String author) {
		try {
			IntelligenceProduct generated = productGenerationService.generateFromTemplate(templateId, investigationId,
					author);
			IntelligenceProduct saved = productRepository.save(generated);
			return convertToDto(saved);
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate product from template: " + e.getMessage());
		}
	}

	public byte[] exportProductAsPdf(String id) {
		Optional<IntelligenceProduct> productOpt = productRepository.findById(id);
		if (productOpt.isEmpty()) {
			throw new RuntimeException("Product not found with id: " + id);
		}

		return productGenerationService.generatePdf(productOpt.get());
	}

	public List<Object[]> getProductTypeStatistics() {
		return productRepository.countByType();
	}

	public List<Object[]> getProductClassificationStatistics() {
		return productRepository.countByClassification();
	}

	public long getPublishedProductCount() {
		return productRepository.countPublishedProducts();
	}

	private void publishProductEvent(IntelligenceProduct product, String eventType) {
		try {
			ProductEvent event = new ProductEvent(eventType, convertToDto(product));
			kafkaTemplate.send("product-events", eventType, event);
		} catch (Exception e) {
			System.err.println("Failed to publish product event: " + e.getMessage());
		}
	}

	private IntelligenceProduct convertToEntity(IntelligenceProductDto dto) {
		IntelligenceProduct entity = new IntelligenceProduct();
		entity.setTitle(dto.getTitle());
		entity.setType(dto.getType());
		entity.setContent(dto.getContent());
		entity.setAuthor(dto.getAuthor());
		entity.setClassification(dto.getClassification());
		entity.setTemplateUsed(dto.getTemplateUsed());
		entity.setDistributionList(dto.getDistributionList());
		return entity;
	}

	private IntelligenceProductDto convertToDto(IntelligenceProduct entity) {
		IntelligenceProductDto dto = new IntelligenceProductDto();
		dto.setId(entity.getId());
		dto.setTitle(entity.getTitle());
		dto.setType(entity.getType());
		dto.setContent(entity.getContent());
		dto.setAuthor(entity.getAuthor());
		dto.setPublishedDate(entity.getPublishedDate());
		dto.setClassification(entity.getClassification());
		dto.setReviewStatus(entity.getReviewStatus());
		dto.setReviewedBy(entity.getReviewedBy());
		dto.setReviewedDate(entity.getReviewedDate());
		dto.setReviewComments(entity.getReviewComments());
		dto.setTemplateUsed(entity.getTemplateUsed());
		dto.setDistributionList(entity.getDistributionList());
		dto.setCreatedDate(entity.getCreatedDate());
		return dto;
	}

	// Event class
	public static class ProductEvent {
		private String eventType;
		private IntelligenceProductDto data;
		private LocalDateTime timestamp;

		public ProductEvent(String eventType, IntelligenceProductDto data) {
			this.eventType = eventType;
			this.data = data;
			this.timestamp = LocalDateTime.now();
		}

		// Getters and setters
		public String getEventType() {
			return eventType;
		}

		public void setEventType(String eventType) {
			this.eventType = eventType;
		}

		public IntelligenceProductDto getData() {
			return data;
		}

		public void setData(IntelligenceProductDto data) {
			this.data = data;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
		}
	}
}