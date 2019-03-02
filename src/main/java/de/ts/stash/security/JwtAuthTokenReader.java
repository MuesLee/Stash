package de.ts.stash.security;

import static de.ts.stash.security.SecurityConstants.ACCESS_TOKEN_PREFIX;
import static de.ts.stash.security.SecurityConstants.SECRET;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.security.api.AuthTokenReader;

@Component
public class JwtAuthTokenReader implements AuthTokenReader {

	@Override
	public ApplicationUser extractUser(final String token) throws IOException {
		if (token == null) {
			return null;
		}

		final String user = JWT
				.require(Algorithm.HMAC512(SECRET.getBytes()))
				.build()
				.verify(token.replace(ACCESS_TOKEN_PREFIX, ""))
				.getSubject();

		return new ObjectMapper().readValue(user, ApplicationUser.class);
	}
}
