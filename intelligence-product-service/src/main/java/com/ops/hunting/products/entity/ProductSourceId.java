package com.ops.hunting.products.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProductSourceId implements Serializable {

	@Column(name = "product_id")
	private UUID productId;

	@Column(name = "source_id")
	private UUID sourceId;

	// Constructors
	public ProductSourceId() {
	}

	public ProductSourceId(UUID productId, UUID sourceId) {
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
	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public UUID getSourceId() {
		return sourceId;
	}

	public void setSourceId(UUID sourceId) {
		this.sourceId = sourceId;
	}
}
