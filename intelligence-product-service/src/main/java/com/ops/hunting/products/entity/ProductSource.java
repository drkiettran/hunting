package com.ops.hunting.products.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_sources")
public class ProductSource {

	@EmbeddedId
	private ProductSourceId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("productId")
	@JoinColumn(name = "product_id")
	private IntelligenceProduct product;

	@Column(name = "source_id")
	private String sourceId;

	@Enumerated(EnumType.STRING)
	@Column(name = "source_type")
	private SourceType sourceType;

	@Column(name = "source_title")
	private String sourceTitle;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	// Constructors
	public ProductSource() {
	}

	public ProductSource(IntelligenceProduct product, String sourceId, SourceType sourceType, String sourceTitle) {
		this.id = new ProductSourceId(product.getId(), sourceId);
		this.product = product;
		this.sourceId = sourceId;
		this.sourceType = sourceType;
		this.sourceTitle = sourceTitle;
	}

	// Getters and setters
	public ProductSourceId getId() {
		return id;
	}

	public void setId(ProductSourceId id) {
		this.id = id;
	}

	public IntelligenceProduct getProduct() {
		return product;
	}

	public void setProduct(IntelligenceProduct product) {
		this.product = product;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceTitle() {
		return sourceTitle;
	}

	public void setSourceTitle(String sourceTitle) {
		this.sourceTitle = sourceTitle;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public enum SourceType {
		THREAT_INTELLIGENCE, INVESTIGATION, CASE, EXTERNAL
	}
}
