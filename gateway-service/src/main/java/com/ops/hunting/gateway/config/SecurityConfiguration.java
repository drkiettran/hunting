package com.ops.hunting.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

//import com.ops.hunting.common.security.JwtTokenProvider;
import com.ops.hunting.gateway.filter.JwtAuthenticationFilter;
import com.ops.hunting.gateway.security.JwtAuthenticationEntryPoint;
import com.ops.hunting.gateway.service.JwtService;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

	@Autowired
	private JwtService jwtService;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http.cors(cors -> cors.disable()) // Handled by CorsWebFilter
				.csrf(csrf -> csrf.disable())
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers("/actuator/**", "/swagger-ui/**", "/webjars/**", "/v3/api-docs/**").permitAll()
						.pathMatchers("/api/auth/**").permitAll().pathMatchers("/fallback/**").permitAll()
						.pathMatchers("/api/admin/**").hasRole("ADMIN").pathMatchers("/api/analyst/**")
						.hasAnyRole("ADMIN", "ANALYST").anyExchange().authenticated())
				.addFilterBefore(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION).build();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtService);
	}

	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	private ReactiveUserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ReactiveAuthenticationManager authenticationManager() {
		UserDetailsRepositoryReactiveAuthenticationManager authManager = new UserDetailsRepositoryReactiveAuthenticationManager(
				userDetailsService);
		authManager.setPasswordEncoder(passwordEncoder());
		return authManager;
	}
}

//import com.ops.hunting.gateway.filter.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsConfigurationSource;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//	private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//	}
//
//	@Bean
//	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//		return http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
//				.authorizeExchange(exchanges -> exchanges
//						// Public endpoints
//						.pathMatchers(HttpMethod.POST, "/api/users/login", "/api/users/register").permitAll()
//						.pathMatchers("/api/users/health", "/actuator/**").permitAll()
//						.pathMatchers("/", "/index.html", "/static/**", "/favicon.ico").permitAll()
//
//						// Admin only endpoints
//						.pathMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
//						.pathMatchers("/api/users/admin/**").hasRole("ADMIN")
//
//						// Analyst endpoints
//						.pathMatchers("/api/alerts/**", "/api/investigations/**", "/api/cases/**")
//						.hasAnyRole("ANALYST", "PRODUCTION_STAFF", "ADMIN")
//						.pathMatchers("/api/threat-intelligence/**", "/api/analytics/**")
//						.hasAnyRole("ANALYST", "PRODUCTION_STAFF", "ADMIN")
//
//						// Production staff endpoints
//						.pathMatchers("/api/products/**").hasAnyRole("PRODUCTION_STAFF", "ADMIN")
//
//						// All other endpoints require authentication
//						.anyExchange().authenticated())
//				.addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION).build();
//	}
//
//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "https://localhost:*"));
//		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//		configuration.setAllowedHeaders(Arrays.asList("*"));
//		configuration.setAllowCredentials(true);
//
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", configuration);
//		return source;
//	}
//}
