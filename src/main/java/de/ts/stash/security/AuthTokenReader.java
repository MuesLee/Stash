package de.ts.stash.security;

import java.io.IOException;

import de.ts.stash.domain.ApplicationUser;

public interface AuthTokenReader {

	public ApplicationUser extractUser(String token) throws IOException;
	
}
