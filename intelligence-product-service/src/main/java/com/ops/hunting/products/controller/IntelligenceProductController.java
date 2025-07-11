package com.ops.hunting.products.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ops.hunting.common.enums.Classification;
import com.ops.hunting.common.enums.ProductType;
import com.ops.hunting.common.util.ResponseWrapper;
import com.ops.hunting.products.dto.IntelligenceProductDto;
import com.ops.hunting.products.service.IntelligenceProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class IntelligenceProductController {

	private final IntelligenceProductService productService;

	@Autowired
	public IntelligenceProductController(IntelligenceProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<IntelligenceProductDto>> createProduct(
			@Valid @RequestBody IntelligenceProductDto dto, Principal principal) {
		try {
			dto.setAuthor(principal.getName());
			IntelligenceProductDto created = productService.createProduct(dto);
			return ResponseEntity.ok(ResponseWrapper.success("Intelligence product created successfully", created));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to create intelligence product: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<IntelligenceProductDto>> getProductById(@PathVariable String id) {
		try {
			IntelligenceProductDto dto = productService.getProductById(id)
					.orElseThrow(() -> new RuntimeException("Intelligence product not found"));
			return ResponseEntity.ok(ResponseWrapper.success(dto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get intelligence product: " + e.getMessage()));
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Page<IntelligenceProductDto>>> getAllProducts(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "createdDate") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDirection, @RequestParam(required = false) String search) {

		try {
			Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
					: Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);

			Page<IntelligenceProductDto> result = search != null && !search.trim().isEmpty()
					? productService.searchProducts(search, pageable)
					: productService.getAllProducts(pageable);

			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get intelligence products: " + e.getMessage()));
		}
	}

	@GetMapping("/by-type/{type}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<IntelligenceProductDto>>> getProductsByType(
			@PathVariable ProductType type) {
		try {
			List<IntelligenceProductDto> result = productService.getProductsByType(type);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get products by type: " + e.getMessage()));
		}
	}

	@GetMapping("/published")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<IntelligenceProductDto>>> getPublishedProducts() {
		try {
			List<IntelligenceProductDto> result = productService.getPublishedProducts();
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get published products: " + e.getMessage()));
		}
	}

	@GetMapping("/under-review")
	@PreAuthorize("hasAnyRole('PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<IntelligenceProductDto>>> getProductsUnderReview() {
		try {
			List<IntelligenceProductDto> result = productService.getProductsUnderReview();
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get products under review: " + e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<IntelligenceProductDto>> updateProduct(@PathVariable String id,
			@Valid @RequestBody IntelligenceProductDto dto) {
		try {
			IntelligenceProductDto updated = productService.updateProduct(id, dto);
			return ResponseEntity.ok(ResponseWrapper.success("Intelligence product updated successfully", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to update intelligence product: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/submit-for-review")
	@PreAuthorize("hasAnyRole('PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<IntelligenceProductDto>> submitForReview(@PathVariable String id) {
		try {
			IntelligenceProductDto updated = productService.submitForReview(id);
			return ResponseEntity.ok(ResponseWrapper.success("Product submitted for review", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to submit product for review: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/approve")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResponseWrapper<IntelligenceProductDto>> approveProduct(@PathVariable String id,
			@RequestBody Map<String, String> request, Principal principal) {
		try {
			String comments = request.get("comments");
			IntelligenceProductDto updated = productService.approveProduct(id, principal.getName(), comments);
			return ResponseEntity.ok(ResponseWrapper.success("Product approved", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to approve product: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/reject")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResponseWrapper<IntelligenceProductDto>> rejectProduct(@PathVariable String id,
			@RequestBody Map<String, String> request, Principal principal) {
		try {
			String comments = request.get("comments");
			IntelligenceProductDto updated = productService.rejectProduct(id, principal.getName(), comments);
			return ResponseEntity.ok(ResponseWrapper.success("Product rejected", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to reject product: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/publish")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResponseWrapper<IntelligenceProductDto>> publishProduct(@PathVariable String id) {
		try {
			IntelligenceProductDto updated = productService.publishProduct(id);
			return ResponseEntity.ok(ResponseWrapper.success("Product published successfully", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to publish product: " + e.getMessage()));
		}
	}

	@PostMapping("/generate-from-template")
	@PreAuthorize("hasAnyRole('PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<IntelligenceProductDto>> generateFromTemplate(
			@RequestBody Map<String, String> request, Principal principal) {
		try {
			String templateId = request.get("templateId");
			String investigationId = request.get("investigationId");

			IntelligenceProductDto generated = productService.generateProductFromTemplate(templateId, investigationId,
					principal.getName());
			return ResponseEntity.ok(ResponseWrapper.success("Product generated from template", generated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to generate product from template: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}/export/pdf")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<byte[]> exportProductAsPdf(@PathVariable String id) {
		try {
			byte[] pdfData = productService.exportProductAsPdf(id);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "intelligence-product-" + id + ".pdf");

			return ResponseEntity.ok().headers(headers).body(pdfData);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/statistics/type")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getTypeStatistics() {
		try {
			List<Object[]> stats = productService.getProductTypeStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> ((ProductType) arr[0]).name(), arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get type statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/classification")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getClassificationStatistics() {
		try {
			List<Object[]> stats = productService.getProductClassificationStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> ((Classification) arr[0]).name(), arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get classification statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/health")
	public ResponseEntity<ResponseWrapper<String>> health() {
		return ResponseEntity.ok(ResponseWrapper.success("Intelligence Product Service is healthy"));
	}
}