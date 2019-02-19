package de.ts.stash.security;

import de.ts.stash.auth.user.ApplicationUser;

public interface AuthTokenProvider {

	String provideAuthToken(ApplicationUser user);
}
