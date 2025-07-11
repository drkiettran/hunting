package com.ops.hunting.products.dto;

import com.ops.hunting.common.enums.Classification;
import com.ops.hunting.common.enums.ProductType;
import com.ops.hunting.products.entity.IntelligenceProduct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class IntelligenceProductDto {

	private String id;

	@NotBlank(message = "Title is required")
	private String title;

	@NotNull(message = "Type is required")
	private ProductType type;

	@NotBlank(message = "Content is required")
	private String content;

	@NotBlank(message = "Author is required")
	private String author;

	private LocalDateTime publishedDate;

	@NotNull(message = "Classification is required")
	private Classification classification;

	private IntelligenceProduct.ReviewStatus reviewStatus;
	private String reviewedBy;
	private LocalDateTime reviewedDate;
	private String reviewComments;
	private String templateUsed;
	private String distributionList;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;

	// Metrics
	private List<String> sourceIds;
	private List<String> tags;
	private int sourceCount;
	private int tagCount;

	// Constructors
	public IntelligenceProductDto() {
	}

	public IntelligenceProductDto(String title, ProductType type, String content, String author,
			Classification classification) {
		this.title = title;
		this.type = type;
		this.content = content;
		this.author = author;
		this.classification = classification;
	}

	// Getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDateTime getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(LocalDateTime publishedDate) {
		this.publishedDate = publishedDate;
	}

	public Classification getClassification() {
		return classification;
	}

	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public IntelligenceProduct.ReviewStatus getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(IntelligenceProduct.ReviewStatus reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(String reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	public LocalDateTime getReviewedDate() {
		return reviewedDate;
	}

	public void setReviewedDate(LocalDateTime reviewedDate) {
		this.reviewedDate = reviewedDate;
	}

	public String getReviewComments() {
		return reviewComments;
	}

	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}

	public String getTemplateUsed() {
		return templateUsed;
	}

	public void setTemplateUsed(String templateUsed) {
		this.templateUsed = templateUsed;
	}

	public String getDistributionList() {
		return distributionList;
	}

	public void setDistributionList(String distributionList) {
		this.distributionList = distributionList;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public List<String> getSourceIds() {
		return sourceIds;
	}

	public void setSourceIds(List<String> sourceIds) {
		this.sourceIds = sourceIds;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public int getSourceCount() {
		return sourceCount;
	}

	public void setSourceCount(int sourceCount) {
		this.sourceCount = sourceCount;
	}

	public int getTagCount() {
		return tagCount;
	}

	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}
}
