package com.app.stargateapigateway.security;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class JwtAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
	@Override
	public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
		ServerWebExchange exchange = webFilterExchange.getExchange();
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

		String errorMessage = "{\"error\": \"Unauthorized\", \"message\": \"" + exception.getMessage() + "\"}";
		byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);

		return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
				.bufferFactory().wrap(bytes)));
	}
}
