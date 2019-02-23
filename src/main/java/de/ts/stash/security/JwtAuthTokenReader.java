package de.ts.stash.security;

import static de.ts.stash.security.SecurityConstants.SECRET;
import static de.ts.stash.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ts.stash.domain.ApplicationUser;

public class JwtAuthTokenReader implements AuthTokenReader {

	public ApplicationUser extractUser(String token) throws IOException {
		if (token == null) {
			return null;
		}
		String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes())).build().verify(token.replace(TOKEN_PREFIX, ""))
				.getSubject();

		return new ObjectMapper().readValue(user, ApplicationUser.class);
	}
}
