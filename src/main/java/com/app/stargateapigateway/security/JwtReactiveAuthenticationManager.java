package com.app.stargateapigateway.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

	private final JwtParserBuilder	jwtParserBuilder;
	private final Key signingKey;

	public JwtReactiveAuthenticationManager(String jwtSecret) {
		this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		this.jwtParserBuilder = Jwts.parser();
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String token = authentication.getCredentials().toString();
		try{
			Claims claims = jwtParserBuilder //
					.verifyWith((SecretKey) signingKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();

			String username = claims.get("email", String.class);
			@SuppressWarnings("unchecked")
			List<String> roles = (List<String>) claims.get("groups");

			List<SimpleGrantedAuthority> authorities = roles != null
					? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
					: List.of();

			return Mono.just(new JwtAuthenticationToken(token, username, authorities));

		} catch (Exception e) {
			return Mono.empty();
		}
	}
}
