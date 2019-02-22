package de.ts.stash.security;

import java.io.IOException;

import de.ts.stash.auth.user.ApplicationUser;

public interface AuthTokenReader {

	public ApplicationUser extractUser(String token) throws IOException;
	
}
