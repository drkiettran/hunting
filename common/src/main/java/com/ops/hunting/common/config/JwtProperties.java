package com.ops.hunting.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String secret = "default-secret-key-change-in-production";
	private long expiration = 86400; // 24 hours in seconds
	private long refreshExpiration = 604800; // 7 days in seconds
	private String issuer = "hunting-platform";
	private String header = "Authorization";
	private String prefix = "Bearer ";

	// Getters and setters
	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	public long getRefreshExpiration() {
		return refreshExpiration;
	}

	public void setRefreshExpiration(long refreshExpiration) {
		this.refreshExpiration = refreshExpiration;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}