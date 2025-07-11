package com.ops.hunting.products.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProductSourceId implements Serializable {

	@Column(name = "product_id")
	private String productId;

	@Column(name = "source_id")
	private String sourceId;

	// Constructors
	public ProductSourceId() {
	}

	public ProductSourceId(String productId, String sourceId) {
		this.productId = productId;
		this.sourceId = sourceId;
	}

	// Equals and hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ProductSourceId that = (ProductSourceId) o;
		return Objects.equals(productId, that.productId) && Objects.equals(sourceId, that.sourceId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId, sourceId);
	}

	// Getters and setters
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
}
