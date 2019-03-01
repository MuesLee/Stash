package de.ts.stash.security;

import java.time.LocalDateTime;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.ts.stash.auth.user.RefreshToken;
import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.persistence.RefreshTokenRepository;
import de.ts.stash.security.api.RefreshTokenProvider;
import de.ts.stash.util.TimeProvider;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StringRefreshTokenProvider implements RefreshTokenProvider {

	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Autowired
	TimeProvider timeProvider;
	
	@Override
	public RefreshToken provideToken(final ApplicationUser user) {
		final String value = RandomStringUtils.randomAlphanumeric(500);
		final LocalDateTime now = timeProvider.currentDateTime();
		final RefreshToken token = RefreshToken.builder().issuedAt(now).user(user).value(value).build();
		refreshTokenRepository.saveAndFlush(token);
		log.debug("Created token for user: " + user.getUsername());
		return token;
	}
}
