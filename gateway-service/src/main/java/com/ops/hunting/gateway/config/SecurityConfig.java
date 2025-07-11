package com.ops.hunting.gateway.config;

import com.ops.hunting.gateway.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeExchange(exchanges -> exchanges
						// Public endpoints
						.pathMatchers(HttpMethod.POST, "/api/users/login", "/api/users/register").permitAll()
						.pathMatchers("/api/users/health", "/actuator/**").permitAll()
						.pathMatchers("/", "/index.html", "/static/**", "/favicon.ico").permitAll()

						// Admin only endpoints
						.pathMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
						.pathMatchers("/api/users/admin/**").hasRole("ADMIN")

						// Analyst endpoints
						.pathMatchers("/api/alerts/**", "/api/investigations/**", "/api/cases/**")
						.hasAnyRole("ANALYST", "PRODUCTION_STAFF", "ADMIN")
						.pathMatchers("/api/threat-intelligence/**", "/api/analytics/**")
						.hasAnyRole("ANALYST", "PRODUCTION_STAFF", "ADMIN")

						// Production staff endpoints
						.pathMatchers("/api/products/**").hasAnyRole("PRODUCTION_STAFF", "ADMIN")

						// All other endpoints require authentication
						.anyExchange().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION).build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "https://localhost:*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
