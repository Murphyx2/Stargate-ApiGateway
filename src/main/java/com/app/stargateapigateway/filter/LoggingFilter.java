package com.app.stargateapigateway.filter;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter {
	private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
	private static final String REQUEST_ID_HEADER = "X-Request-ID";

	private static final String REQUEST_LOG_TEMPLATE = "Request | ID: %s | Method: %s | Path: %s | Headers: %s";
	private static final String RESPONSE_LOG_TEMPLATE = "Response | ID: %s | Status: %s | Headers: %s";
	private static final String HEADER_AUTHORIZATION = "Authorization";


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// Generate unique request ID
		String requestId = UUID.randomUUID().toString();

		//Add request ID to headers for downstream services
		ServerHttpRequest request = exchange.getRequest().mutate() //
				.header(REQUEST_ID_HEADER, requestId) //
				.build();
		ServerWebExchange modifiedExchange = exchange.mutate().request(request).build();

		// Sanitized headers, remove any sensible information
		HttpHeaders sanitizedHeaders = new HttpHeaders();
		request.getHeaders().forEach((key, value) -> {
			if(!HEADER_AUTHORIZATION.equalsIgnoreCase(key))
				sanitizedHeaders.addAll(key, value);
		});

		// Log request
		logger.info(String.format(REQUEST_LOG_TEMPLATE, //
				requestId, //
				request.getMethod(), //
				request.getPath(), //
				sanitizedHeaders) //
		);

		// Proceed with the chain and log the response
		return chain.filter(modifiedExchange)
				.then(Mono.fromRunnable(() ->
					logger.info(String.format(RESPONSE_LOG_TEMPLATE,
							requestId,
							exchange.getResponse().getStatusCode(),
							exchange.getResponse().getHeaders()))
				));
	}
}
