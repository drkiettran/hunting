package com.ops.hunting.products.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.Classification;
import com.ops.hunting.common.enums.ProductType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "intelligence_products")
public class IntelligenceProduct extends BaseEntity {

	@NotBlank
	@Column(nullable = false, length = 200)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductType type;

	@NotBlank
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@NotBlank
	@Column(nullable = false)
	private String author;

	@Column(name = "published_date")
	private LocalDateTime publishedDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Classification classification;

	@Column(name = "review_status")
	@Enumerated(EnumType.STRING)
	private ReviewStatus reviewStatus = ReviewStatus.DRAFT;

	@Column(name = "reviewed_by")
	private String reviewedBy;

	@Column(name = "reviewed_date")
	private LocalDateTime reviewedDate;

	@Column(name = "review_comments", columnDefinition = "TEXT")
	private String reviewComments;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ProductSource> sources = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ProductTag> tags = new ArrayList<>();

	@Column(name = "template_used")
	private String templateUsed;

	@Column(name = "distribution_list", columnDefinition = "TEXT")
	private String distributionList;

	// Constructors
	public IntelligenceProduct() {
	}

	public IntelligenceProduct(String title, ProductType type, String content, String author,
			Classification classification) {
		this.title = title;
		this.type = type;
		this.content = content;
		this.author = author;
		this.classification = classification;
		this.reviewStatus = ReviewStatus.DRAFT;
	}

	// Business methods
	public boolean isPublished() {
		return publishedDate != null && reviewStatus == ReviewStatus.APPROVED;
	}

	public boolean isUnderReview() {
		return reviewStatus == ReviewStatus.UNDER_REVIEW;
	}

	public void submitForReview() {
		this.reviewStatus = ReviewStatus.UNDER_REVIEW;
	}

	public void approve(String reviewedBy, String comments) {
		this.reviewStatus = ReviewStatus.APPROVED;
		this.reviewedBy = reviewedBy;
		this.reviewedDate = LocalDateTime.now();
		this.reviewComments = comments;
	}

	public void reject(String reviewedBy, String comments) {
		this.reviewStatus = ReviewStatus.REJECTED;
		this.reviewedBy = reviewedBy;
		this.reviewedDate = LocalDateTime.now();
		this.reviewComments = comments;
	}

	public void publish() {
		if (reviewStatus == ReviewStatus.APPROVED) {
			this.publishedDate = LocalDateTime.now();
		} else {
			throw new RuntimeException("Product must be approved before publishing");
		}
	}

	// Getters and setters
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

	public ReviewStatus getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(ReviewStatus reviewStatus) {
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

	public List<ProductSource> getSources() {
		return sources;
	}

	public void setSources(List<ProductSource> sources) {
		this.sources = sources;
	}

	public List<ProductTag> getTags() {
		return tags;
	}

	public void setTags(List<ProductTag> tags) {
		this.tags = tags;
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

	public enum ReviewStatus {
		DRAFT, UNDER_REVIEW, APPROVED, REJECTED
	}
}
