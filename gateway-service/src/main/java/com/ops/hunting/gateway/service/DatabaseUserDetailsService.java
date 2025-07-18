package com.ops.hunting.gateway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ops.hunting.gateway.entity.User;
import com.ops.hunting.gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements ReactiveUserDetailsService {

	private final UserRepository userRepository;

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return userRepository.findByUsername(username)
				.switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
				.map(this::mapUserToUserDetails);
	}

	private UserDetails mapUserToUserDetails(User user) {
		List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).collect(Collectors.toList());

		return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
				.password(user.getPassword()).authorities(authorities).accountExpired(!user.isAccountNonExpired())
				.accountLocked(!user.isAccountNonLocked()).credentialsExpired(!user.isCredentialsNonExpired())
				.disabled(!user.isEnabled()).build();
	}
}