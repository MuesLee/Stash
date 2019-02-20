package de.ts.stash.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static de.ts.stash.security.SecurityConstants.EXPIRATION_TIME;
import static de.ts.stash.security.SecurityConstants.SECRET;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;

import de.ts.stash.auth.user.ApplicationUser;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider implements AuthTokenProvider {

	@Override
	public String provideAuthToken(ApplicationUser user) {
		if(user == null || user.getUsername() == null)
		{
			throw new IllegalArgumentException("User is missing a name!");
		}
		
		
		log.debug("Creating Token for: " + user.getUsername());
		String token = JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(HMAC512(SECRET.getBytes()));

		return token;
	}
}
