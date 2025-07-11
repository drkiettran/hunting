package com.ops.hunting.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

	@Value("${jwt.secret:MySecretKeyForPersistentHuntSystemThatIsLongEnough}")
	private String jwtSecret;

	@Value("${jwt.expiration:86400}")
	private int jwtExpirationInSeconds;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String generateToken(String username, List<String> roles) {
		Instant now = Instant.now();
		Instant expiration = now.plus(jwtExpirationInSeconds, ChronoUnit.SECONDS);

		return Jwts.builder().setSubject(username).claim("roles", roles).setIssuedAt(Date.from(now))
				.setExpiration(Date.from(expiration)).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
			// Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) {
		return (List<String>) extractClaims(token).get("roles");
	}

	private Claims extractClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
		// return
		// Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}
}
