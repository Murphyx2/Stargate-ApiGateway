package com.app.stargateapigateway.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class AddJwtClaimsToHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<AddJwtClaimsToHeaderGatewayFilterFactory.Config> {


	private final JwtParser jwtParser;


	public AddJwtClaimsToHeaderGatewayFilterFactory(@Value("${JWT_SECRET}") String jwtSecret) {
		super(Config.class);
		Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		this.jwtParser = Jwts.parser().verifyWith((SecretKey) key).build();
	}

	@Override
	public GatewayFilter apply(AddJwtClaimsToHeaderGatewayFilterFactory.Config config) {
		return (exchange, chain) -> {
			String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);

				try {
					Claims claims = jwtParser.parseSignedClaims(token).getPayload();
					String userId = claims.get("unique_id", String.class);
					String email = claims.get("username", String.class);

					ServerWebExchange modifiedExchange = exchange.mutate()
							.request( builder -> builder
									.header("X-User-id", userId != null ? userId : "unknown")
									.header("X-User-username", email != null ? email : "unknown")
							)
							.build();

					return chain.filter(modifiedExchange);
				} catch (Exception e) {
					return chain.filter(exchange);
				}
			}
			return chain.filter(exchange);
		};
	}

	public static class Config {
	}
}
