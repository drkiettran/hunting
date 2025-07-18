package com.ops.hunting.common.security;

import java.util.Date;
import java.util.List;

public class TokenValidationResult {
	private boolean valid;
	private String username;
	private List<String> authorities;
	private Date issuedAt;
	private Date expiresAt;
	private String tokenId;
	private String errorMessage;
	private String errorType;

	public TokenValidationResult() {
		this.valid = false;
	}

	// Getters and setters
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}

	public Date getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Date issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	@Override
	public String toString() {
		return "TokenValidationResult{" + "valid=" + valid + ", username='" + username + '\'' + ", authorities="
				+ authorities + ", issuedAt=" + issuedAt + ", expiresAt=" + expiresAt + ", tokenId='" + tokenId + '\''
				+ ", errorMessage='" + errorMessage + '\'' + ", errorType='" + errorType + '\'' + '}';
	}
}