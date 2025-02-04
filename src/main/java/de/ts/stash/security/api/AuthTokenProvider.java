package de.ts.stash.security.api;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.ts.stash.domain.ApplicationUser;

public interface AuthTokenProvider {

	String provideAuthToken(ApplicationUser user) throws JsonProcessingException;
}
