package de.ts.stash.security;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.ts.stash.auth.user.ApplicationUser;

public interface AuthTokenProvider {

	String provideAuthToken(ApplicationUser user) throws JsonProcessingException;
}
