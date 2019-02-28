package de.ts.stash.security;

import java.time.LocalDateTime;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.ts.stash.auth.user.RefreshToken;
import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.persistence.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StringRefreshTokenProvider implements RefreshTokenProvider {

	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Override
	public RefreshToken provideToken(ApplicationUser user) {
		String value = RandomStringUtils.random(500);
		RefreshToken token = RefreshToken.builder().issuedAt(LocalDateTime.now()).user(user).value(value).build();
		refreshTokenRepository.saveAndFlush(token);
		log.debug("Created token for user: " + user.getUsername());
		return token;
	}
}
