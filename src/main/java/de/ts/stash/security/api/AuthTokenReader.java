package de.ts.stash.security.api;

import java.io.IOException;

import de.ts.stash.domain.ApplicationUser;

public interface AuthTokenReader {

	ApplicationUser extractUser(String token) throws IOException;

}
