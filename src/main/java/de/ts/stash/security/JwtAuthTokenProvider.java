package de.ts.stash.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static de.ts.stash.security.SecurityConstants.ACCESS_TOKEN_EXPIRATION_IN_MINUTES;
import static de.ts.stash.security.SecurityConstants.SECRET;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.security.api.AuthTokenProvider;
import de.ts.stash.util.TimeProvider;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthTokenProvider implements AuthTokenProvider {

	@Autowired
	TimeProvider timeProvider;

	@Override
	public String provideAuthToken(final ApplicationUser user) throws JsonProcessingException {
		if (user == null || user.getUsername() == null) {
			throw new IllegalArgumentException("User is missing a name!");
		}

		user.setPassword(null);
		final String userPayload = new ObjectMapper().writeValueAsString(user);

		log.debug("Creating Token for: " + user.getUsername());

		final LocalDateTime expirationDate = this.timeProvider
				.currentDateTime()
				.plusMinutes(ACCESS_TOKEN_EXPIRATION_IN_MINUTES);

		final String token = JWT
				.create()
				.withSubject(userPayload)
				.withExpiresAt(this.timeProvider.convert(expirationDate))
				.sign(HMAC512(SECRET.getBytes()));

		return token;
	}
}
