package com.ops.hunting.common.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12); // Strong BCrypt with 12 rounds
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authz -> authz
						// Public endpoints
						.requestMatchers("/api/auth/**").permitAll().requestMatchers("/actuator/health").permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

						// Admin only endpoints
						.requestMatchers("/api/admin/**").hasRole("ADMIN").requestMatchers("/actuator/**")
						.hasRole("ADMIN")

						// Analyst and Admin access
						.requestMatchers("/api/artifacts/**").hasAnyRole("ANALYST", "ADMIN")
						.requestMatchers("/api/investigations/**").hasAnyRole("ANALYST", "ADMIN")
						.requestMatchers("/api/alerts/**").hasAnyRole("ANALYST", "ADMIN")

						// All other requests require authentication
						.anyRequest().authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
				.headers(headers -> headers.frameOptions().deny().contentTypeOptions().and()
						.httpStrictTransportSecurity(hstsConfig -> hstsConfig.maxAgeInSeconds(31536000)
								.includeSubDomains(true).preload(true))
						.referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN).and()
						.addHeaderWriter((request, response) -> {
							response.setHeader("X-Content-Type-Options", "nosniff");
							response.setHeader("X-Frame-Options", "DENY");
							response.setHeader("X-XSS-Protection", "1; mode=block");
							response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
							response.setHeader("Pragma", "no-cache");
							response.setHeader("Expires", "0");
							response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
						}));

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Allowed origins - be restrictive in production
		configuration.setAllowedOrigins(List.of("https://hunting.yourdomain.com", // Production frontend
				"https://localhost:3000", // Local development with HTTPS
				"http://localhost:3000" // Local development (remove in production)
		));

		// Allowed methods
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// Allowed headers
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept",
				"Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));

		// Exposed headers
		configuration
				.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));

		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L); // 1 hour preflight cache

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
