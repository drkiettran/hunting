package com.ops.hunting.products.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ops.hunting.common.enums.Classification;
import com.ops.hunting.common.enums.ProductType;
import com.ops.hunting.products.entity.IntelligenceProduct;

@Service
public class ProductGenerationService {

	@Value("${product.generation.llm.enabled:false}")
	private boolean llmEnabled;

	@Value("${product.generation.llm.api-key:}")
	private String llmApiKey;

	@Value("${product.templates.default-template:threat-report-template}")
	private String defaultTemplate;

	private final RestTemplate restTemplate;

	public ProductGenerationService() {
		this.restTemplate = new RestTemplate();
	}

	public IntelligenceProduct generateFromTemplate(String templateId, String investigationId, String author) {
		// Load template
		String template = loadTemplate(templateId);

		// Get investigation data (this would call investigation service)
		Map<String, Object> investigationData = getInvestigationData(investigationId);

		// Generate content
		String content = generateContent(template, investigationData);

		// Create product
		IntelligenceProduct product = new IntelligenceProduct();
		product.setTitle(generateTitle(investigationData));
		product.setType(ProductType.THREAT_REPORT);
		product.setContent(content);
		product.setAuthor(author);
		product.setClassification(Classification.UNCLASSIFIED);
		product.setTemplateUsed(templateId);

		return product;
	}

	public byte[] generatePdf(IntelligenceProduct product) {
		try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			PDPage page = new PDPage();
			document.addPage(page);

			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				// Add title
				contentStream.beginText();
				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
				contentStream.newLineAtOffset(50, 750);
				contentStream.showText(product.getTitle());
				contentStream.endText();

				// Add metadata
				contentStream.beginText();
				contentStream.setFont(PDType1Font.HELVETICA, 10);
				contentStream.newLineAtOffset(50, 720);
				contentStream.showText("Author: " + product.getAuthor());
				contentStream.newLine();
				contentStream.showText("Classification: " + product.getClassification());
				contentStream.newLine();
				contentStream.showText("Created: " + product.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
				if (product.getPublishedDate() != null) {
					contentStream.newLine();
					contentStream.showText(
							"Published: " + product.getPublishedDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
				}
				contentStream.endText();

				// Add content (simplified - would need proper text wrapping)
				contentStream.beginText();
				contentStream.setFont(PDType1Font.HELVETICA, 12);
				contentStream.newLineAtOffset(50, 650);

				// Split content into lines (simplified)
				String[] lines = product.getContent().split("\n");
				for (int i = 0; i < Math.min(lines.length, 30); i++) {
					contentStream.showText(lines[i].length() > 80 ? lines[i].substring(0, 80) + "..." : lines[i]);
					contentStream.newLine();
				}
				contentStream.endText();
			}

			document.save(outputStream);
			return outputStream.toByteArray();

		} catch (IOException e) {
			throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
		}
	}

	private String loadTemplate(String templateId) {
		// In a real implementation, this would load from database or file system
		Map<String, String> templates = getDefaultTemplates();
		return templates.getOrDefault(templateId, templates.get(defaultTemplate));
	}

	private Map<String, String> getDefaultTemplates() {
		Map<String, String> templates = new HashMap<>();

		templates.put("threat-report-template",
				"# Threat Analysis Report\n\n" + "## Executive Summary\n" + "{{EXECUTIVE_SUMMARY}}\n\n"
						+ "## Threat Overview\n" + "{{THREAT_OVERVIEW}}\n\n" + "## Technical Analysis\n"
						+ "{{TECHNICAL_ANALYSIS}}\n\n" + "## Indicators of Compromise\n" + "{{INDICATORS}}\n\n"
						+ "## Recommendations\n" + "{{RECOMMENDATIONS}}\n\n" + "## Conclusion\n" + "{{CONCLUSION}}");

		templates.put("incident-advisory-template",
				"# Security Advisory\n\n" + "## Alert Level: {{ALERT_LEVEL}}\n\n" + "## Threat Description\n"
						+ "{{THREAT_DESCRIPTION}}\n\n" + "## Affected Systems\n" + "{{AFFECTED_SYSTEMS}}\n\n"
						+ "## Immediate Actions Required\n" + "{{IMMEDIATE_ACTIONS}}\n\n"
						+ "## Additional Information\n" + "{{ADDITIONAL_INFO}}");

		return templates;
	}

	private Map<String, Object> getInvestigationData(String investigationId) {
		// In a real implementation, this would call the investigation service
		Map<String, Object> data = new HashMap<>();
		data.put("investigationId", investigationId);
		data.put("title", "Sample Investigation");
		data.put("findings", "Sample findings from investigation");
		data.put("recommendations", "Sample recommendations");
		data.put("alertCount", 5);
		data.put("severity", "HIGH");
		return data;
	}

	private String generateContent(String template, Map<String, Object> data) {
		String content = template;

		// Simple template substitution
		content = content.replace("{{EXECUTIVE_SUMMARY}}",
				"This report provides analysis of security incident " + data.get("investigationId"));
		content = content.replace("{{THREAT_OVERVIEW}}", "Investigation findings: " + data.get("findings"));
		content = content.replace("{{TECHNICAL_ANALYSIS}}", "Technical analysis based on " + data.get("alertCount")
				+ " alerts with severity " + data.get("severity"));
		content = content.replace("{{INDICATORS}}", "Indicators of compromise identified during investigation");
		content = content.replace("{{RECOMMENDATIONS}}", data.get("recommendations").toString());
		content = content.replace("{{CONCLUSION}}",
				"Investigation completed on " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

		// If LLM is enabled, enhance with AI generation
		if (llmEnabled && !llmApiKey.isEmpty()) {
			content = enhanceWithLLM(content, data);
		}

		return content;
	}

	private String generateTitle(Map<String, Object> data) {
		return "Threat Analysis Report - Investigation " + data.get("investigationId");
	}

	private String enhanceWithLLM(String content, Map<String, Object> data) {
		// Placeholder for LLM integration (OpenAI, etc.)
		// In a real implementation, this would call an LLM API to enhance the content
		return content + "\n\n--- Content enhanced with AI analysis ---";
	}
}
