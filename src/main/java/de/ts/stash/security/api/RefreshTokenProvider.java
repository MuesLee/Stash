package de.ts.stash.security.api;

import de.ts.stash.auth.user.RefreshToken;
import de.ts.stash.domain.ApplicationUser;

public interface RefreshTokenProvider {

	RefreshToken provideToken(ApplicationUser user);
}
