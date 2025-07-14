package com.ops.hunting.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {

	@Value("${security.jwt.secret}")
	private String jwtSecret;

	@Value("${security.jwt.expiration:3600}") // Default 1 hour
	private Long jwtExpiration;

	@Value("${security.jwt.refresh-expiration:86400}") // Default 24 hours
	private Long refreshExpiration;

	private SecretKey getSigningKey() {
		// Ensure the secret is strong enough (at least 256 bits for HS256)
		if (jwtSecret.length() < 32) {
			throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 characters)");
		}
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername(), jwtExpiration);
	}

	public String generateRefreshToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("type", "refresh");
		return createToken(claims, userDetails.getUsername(), refreshExpiration);
	}

	public String generateTokenWithClaims(UserDetails userDetails, Map<String, Object> extraClaims) {
		Map<String, Object> claims = new HashMap<>(extraClaims);
		return createToken(claims, userDetails.getUsername(), jwtExpiration);
	}

	private String createToken(Map<String, Object> claims, String subject, Long expiration) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration * 1000);

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(now).setExpiration(expiryDate)
				.setIssuer("persistent-hunting-system").signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		try {
			final String username = getUsernameFromToken(token);
			return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		} catch (Exception e) {
			return false;
		}
	}

	public Boolean canTokenBeRefreshed(String token) {
		return !isTokenExpired(token);
	}
}
