package com.ops.hunting.common.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ops.hunting.common.exception.JwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${jwt.secret:default-secret-key-change-in-production}")
	private String jwtSecret;

	@Value("${jwt.expiration:86400}")
	private long jwtExpirationInSeconds;

	@Value("${jwt.refresh-expiration:604800}")
	private long jwtRefreshExpirationInSeconds;

	@Value("${jwt.issuer:hunting-platform}")
	private String jwtIssuer;

	private SecretKey key;
	private JwtParser jwtParser;

	@PostConstruct
	public void init() {

		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
		this.jwtParser = Jwts.parser().setSigningKey(key).requireIssuer(jwtIssuer).build();

		logger.info("JWT Token Provider initialized with expiration: {} seconds", jwtExpirationInSeconds);
	}

	/**
	 * Generate JWT token for authenticated user
	 */
	public String generateToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		return generateToken(userPrincipal.getUsername(), userPrincipal.getAuthorities(), false);
	}

	/**
	 * Generate JWT token with username and authorities
	 */
	public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
		return generateToken(username, authorities, false);
	}

	/**
	 * Generate JWT token with username only
	 */
	public String generateToken(String username) {
		return generateToken(username, Collections.emptyList(), false);
	}

	/**
	 * Generate refresh token
	 */
	public String generateRefreshToken(String username) {
		return generateToken(username, Collections.emptyList(), true);
	}

	/**
	 * Generate token with authorities as string list
	 */
	public String generateToken(String username, List<String> authorities) {
		List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		return generateToken(username, grantedAuthorities, false);
	}

	/**
	 * Core token generation method
	 */
	private String generateToken(String username, Collection<? extends GrantedAuthority> authorities,
			boolean isRefreshToken) {
		Date now = new Date();
		long expirationTime = isRefreshToken ? jwtRefreshExpirationInSeconds : jwtExpirationInSeconds;
		Date expiryDate = new Date(now.getTime() + expirationTime * 1000);

		List<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		Map<String, Object> claims = new HashMap<>();
		claims.put("authorities", authorityStrings);
		claims.put("type", isRefreshToken ? "refresh" : "access");
		claims.put("iat", now.getTime() / 1000);
		claims.put("jti", UUID.randomUUID().toString());

		try {
			String token = Jwts.builder().setClaims(claims).setSubject(username).setIssuer(jwtIssuer).setIssuedAt(now)
					.setExpiration(expiryDate).signWith(key, SignatureAlgorithm.HS512).compact();

			logger.debug("Generated {} token for user: {}", isRefreshToken ? "refresh" : "access", username);
			return token;

		} catch (Exception e) {
			logger.error("Error generating JWT token for user: {}", username, e);
			throw new JwtAuthenticationException("Could not generate JWT token", e);
		}
	}

	/**
	 * Extract username from JWT token
	 */
	public String getUsername(String token) {
		try {
			Claims claims = jwtParser.parseClaimsJws(token).getBody();
			return claims.getSubject();
		} catch (Exception e) {
			logger.error("Error extracting username from token", e);
			throw new JwtAuthenticationException("Could not extract username from token", e);
		}
	}

	/**
	 * Extract authorities from JWT token
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAuthorities(String token) {
		try {
			Claims claims = jwtParser.parseClaimsJws(token).getBody();
			List<String> authorities = (List<String>) claims.get("authorities");
			return authorities != null ? authorities : Collections.emptyList();
		} catch (Exception e) {
			logger.error("Error extracting authorities from token", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Extract authorities as GrantedAuthority objects
	 */
	public Collection<GrantedAuthority> getGrantedAuthorities(String token) {
		List<String> authorities = getAuthorities(token);
		return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	/**
	 * Get token expiration date
	 */
	public Date getExpirationDate(String token) {
		try {
			Claims claims = jwtParser.parseClaimsJws(token).getBody();
			return claims.getExpiration();
		} catch (Exception e) {
			logger.error("Error extracting expiration date from token", e);
			throw new JwtAuthenticationException("Could not extract expiration date from token", e);
		}
	}

	/**
	 * Get token issued date
	 */
	public Date getIssuedDate(String token) {
		try {
			Claims claims = jwtParser.parseClaimsJws(token).getBody();
			return claims.getIssuedAt();
		} catch (Exception e) {
			logger.error("Error extracting issued date from token", e);
			throw new JwtAuthenticationException("Could not extract issued date from token", e);
		}
	}

	/**
	 * Get token ID (JTI)
	 */
	public String getTokenId(String token) {
		try {
			Claims claims = jwtParser.parseClaimsJws(token).getBody();
			return (String) claims.get("jti");
		} catch (Exception e) {
			logger.error("Error extracting token ID from token", e);
			return null;
		}
	}

	/**
	 * Check if token is refresh token
	 */
	public boolean isRefreshToken(String token) {
		try {
			Claims claims = jwtParser.parseClaimsJws(token).getBody();
			String type = (String) claims.get("type");
			return "refresh".equals(type);
		} catch (Exception e) {
			logger.error("Error checking token type", e);
			return false;
		}
	}

	/**
	 * Validate JWT token
	 */
	public boolean validateToken(String token) {
		try {
			if (token == null || token.trim().isEmpty()) {
				logger.debug("Token is null or empty");
				return false;
			}

			jwtParser.parseClaimsJws(token);
			logger.debug("Token validation successful");
			return true;

		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("Expired JWT token: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("Unsupported JWT token: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		} catch (Exception e) {
			logger.error("JWT token validation failed: {}", e.getMessage());
		}

		return false;
	}

	/**
	 * Check if token is expired
	 */
	public boolean isTokenExpired(String token) {
		try {
			Date expiration = getExpirationDate(token);
			return expiration.before(new Date());
		} catch (Exception e) {
			logger.error("Error checking token expiration", e);
			return true;
		}
	}

	/**
	 * Get remaining time until token expires (in seconds)
	 */
	public long getTimeUntilExpiration(String token) {
		try {
			Date expiration = getExpirationDate(token);
			Date now = new Date();
			return Math.max(0, (expiration.getTime() - now.getTime()) / 1000);
		} catch (Exception e) {
			logger.error("Error calculating time until expiration", e);
			return 0;
		}
	}

	/**
	 * Refresh access token using refresh token
	 */
	public String refreshToken(String refreshToken) {
		if (!validateToken(refreshToken) || !isRefreshToken(refreshToken)) {
			throw new JwtAuthenticationException("Invalid refresh token");
		}

		String username = getUsername(refreshToken);
		List<String> authorities = getAuthorities(refreshToken);

		return generateToken(username, authorities);
	}

	/**
	 * Extract all claims from token
	 */
	public Claims getAllClaims(String token) {
		try {
			return jwtParser.parseClaimsJws(token).getBody();
		} catch (Exception e) {
			logger.error("Error extracting claims from token", e);
			throw new JwtAuthenticationException("Could not extract claims from token", e);
		}
	}

	/**
	 * Create token with custom claims
	 */
	public String generateTokenWithClaims(String username, Map<String, Object> customClaims) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInSeconds * 1000);

		Map<String, Object> claims = new HashMap<>(customClaims);
		claims.put("type", "access");
		claims.put("iat", now.getTime() / 1000);
		claims.put("jti", UUID.randomUUID().toString());

		try {
			return Jwts.builder().setClaims(claims).setSubject(username).setIssuer(jwtIssuer).setIssuedAt(now)
					.setExpiration(expiryDate).signWith(key, SignatureAlgorithm.HS512).compact();
		} catch (Exception e) {
			logger.error("Error generating JWT token with custom claims for user: {}", username, e);
			throw new JwtAuthenticationException("Could not generate JWT token with custom claims", e);
		}
	}

	/**
	 * Validate token and return validation result with details
	 */
	public TokenValidationResult validateTokenWithDetails(String token) {
		TokenValidationResult result = new TokenValidationResult();

		try {
			if (token == null || token.trim().isEmpty()) {
				result.setValid(false);
				result.setErrorMessage("Token is null or empty");
				return result;
			}

			Claims claims = jwtParser.parseClaimsJws(token).getBody();

			result.setValid(true);
			result.setUsername(claims.getSubject());
			result.setIssuedAt(claims.getIssuedAt());
			result.setExpiresAt(claims.getExpiration());
			result.setTokenId((String) claims.get("jti"));

			@SuppressWarnings("unchecked")
			List<String> authorities = (List<String>) claims.get("authorities");
			result.setAuthorities(authorities != null ? authorities : Collections.emptyList());

		} catch (ExpiredJwtException e) {
			result.setValid(false);
			result.setErrorMessage("Token is expired");
			result.setErrorType("EXPIRED");
		} catch (SignatureException e) {
			result.setValid(false);
			result.setErrorMessage("Invalid token signature");
			result.setErrorType("INVALID_SIGNATURE");
		} catch (MalformedJwtException e) {
			result.setValid(false);
			result.setErrorMessage("Malformed token");
			result.setErrorType("MALFORMED");
		} catch (Exception e) {
			result.setValid(false);
			result.setErrorMessage("Token validation failed: " + e.getMessage());
			result.setErrorType("VALIDATION_ERROR");
		}

		return result;
	}

	// Getters for configuration values
	public long getJwtExpirationInSeconds() {
		return jwtExpirationInSeconds;
	}

	public long getJwtRefreshExpirationInSeconds() {
		return jwtRefreshExpirationInSeconds;
	}

	public String getJwtIssuer() {
		return jwtIssuer;
	}
}