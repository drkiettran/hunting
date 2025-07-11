package com.ops.hunting.products.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_tags")
public class ProductTag {

	@EmbeddedId
	private ProductTagId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("productId")
	@JoinColumn(name = "product_id")
	private IntelligenceProduct product;

	@Column(name = "tag_id")
	private String tagId;

	@Column(name = "tag_name")
	private String tagName;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	// Constructors
	public ProductTag() {
	}

	public ProductTag(IntelligenceProduct product, String tagId, String tagName) {
		this.id = new ProductTagId(product.getId(), tagId);
		this.product = product;
		this.tagId = tagId;
		this.tagName = tagName;
	}

	// Getters and setters
	public ProductTagId getId() {
		return id;
	}

	public void setId(ProductTagId id) {
		this.id = id;
	}

	public IntelligenceProduct getProduct() {
		return product;
	}

	public void setProduct(IntelligenceProduct product) {
		this.product = product;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
