package com.app.stargateapigateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) { //
		return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)) //
				.filter(authHeader -> authHeader.startsWith("Bearer ")) //
				.map(authHeader -> authHeader.substring(7)) //
				.map(JwtAuthenticationToken::new);
	}
}
