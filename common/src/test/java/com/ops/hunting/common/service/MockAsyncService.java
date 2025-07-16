package com.ops.hunting.common.service;

import java.util.concurrent.CompletableFuture;

/**
 * Async processing service for testing
 */
public class MockAsyncService {

	public CompletableFuture<String> processAsync(String input) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(100); // Simulate processing time
				return "processed: " + input;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		});
	}
}