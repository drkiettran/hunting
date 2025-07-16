package com.ops.hunting.common;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceTestUtils {

	public static void measureExecutionTime(Runnable operation, String operationName) {
		long startTime = System.nanoTime();
		operation.run();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds

		System.out.printf("%s executed in %d ms%n", operationName, duration);

		// Assert reasonable performance (adjust thresholds as needed)
		assertTrue(duration < 1000, "Operation should complete within 1 second");
	}
}