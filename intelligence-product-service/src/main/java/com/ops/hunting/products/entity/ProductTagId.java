package com.ops.hunting.products.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProductTagId implements Serializable {

	@Column(name = "product_id")
	private String productId;

	@Column(name = "tag_id")
	private String tagId;

	// Constructors
	public ProductTagId() {
	}

	public ProductTagId(String productId, String tagId) {
		this.productId = productId;
		this.tagId = tagId;
	}

	// Equals and hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ProductTagId that = (ProductTagId) o;
		return Objects.equals(productId, that.productId) && Objects.equals(tagId, that.tagId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId, tagId);
	}

	// Getters and setters
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
}
