package com.ops.hunting.common.security;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RateLimitService {

	private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

	public Bucket createBucket(String key, long capacity, Duration duration) {
		return buckets.computeIfAbsent(key, k -> {
			Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(capacity, duration));
			return Bucket.builder().addLimit(limit).build();
		});
	}

	public boolean tryConsume(String key, long tokens, long capacity, Duration duration) {
		Bucket bucket = createBucket(key, capacity, duration);
		return bucket.tryConsume(tokens);
	}

	public boolean tryConsumeFromRequest(HttpServletRequest request, long tokens, long capacity, Duration duration) {
		String key = getClientKey(request);
		return tryConsume(key, tokens, capacity, duration);
	}

	private String getClientKey(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	// Cleanup old buckets periodically
	public void cleanup() {
		buckets.entrySet().removeIf(entry -> {
			Bucket bucket = entry.getValue();
			return bucket.getAvailableTokens() == 0;
		});
	}
}
