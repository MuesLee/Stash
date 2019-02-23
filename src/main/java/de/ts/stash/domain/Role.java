package de.ts.stash.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

	USER;

	@Override
	public String getAuthority() {
		return this.toString();
	}
}
