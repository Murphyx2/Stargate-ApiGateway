package com.app.stargateapigateway.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken implements Authentication {
	private final String token;
	private final String username;
	private final Collection<? extends GrantedAuthority> authorities;
	private boolean authenticated = false;

	public JwtAuthenticationToken(String token) {
		this(token, null, null);
	}

	public JwtAuthenticationToken(String token, String username, Collection<? extends GrantedAuthority> authorities) {
		this.token = token;
		this.username = username;
		this.authorities = authorities;
		this.authenticated = username != null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return username;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated){
		this.authenticated = isAuthenticated;
	}

	@Override
	public String getName() {
		return username;
	}
}
