package com.app.stargateapigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Value("${JWT_SECRET}")
	private String jwtSecret;


	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager) {
		return http //
				.csrf(ServerHttpSecurity.CsrfSpec::disable) //
				.authorizeExchange(exchanges -> exchanges //
						.pathMatchers("/api/auth/**").permitAll() //
						.pathMatchers("/api/auth/**").authenticated() //
						.anyExchange().permitAll() //
				)
				.addFilterAt(jwtAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION) //
				.build();
	}

	@Bean
	public ReactiveAuthenticationManager authenticationManager() {
		return new JwtReactiveAuthenticationManager(jwtSecret);
	}

	private AuthenticationWebFilter jwtAuthenticationFilter(ReactiveAuthenticationManager authenticationManager) {
		AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
		filter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter());
		filter.setAuthenticationFailureHandler(new JwtAuthenticationFailureHandler());
		return filter;
	}
}
