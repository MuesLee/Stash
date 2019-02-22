package de.ts.stash.security;

import static de.ts.stash.security.SecurityConstants.HEADER_STRING;
import static de.ts.stash.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.ts.stash.auth.user.ApplicationUser;
import de.ts.stash.auth.user.UserRepository;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;

	@Autowired
	AuthTokenProvider authTokenProvider;
	
	private UserRepository userRepository;
	

	public JwtAuthenticationFilter(final AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest req, final HttpServletResponse res)
			throws AuthenticationException {
		try {
			final User creds = new ObjectMapper().readValue(req.getInputStream(), User.class);

			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(),
					creds.getPassword(), new ArrayList<>()));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain,
			final Authentication auth) throws IOException, ServletException {

		final User principal = (User) auth.getPrincipal();
		final String username = principal.getUsername();
		final ApplicationUser findByUsername = userRepository.findByUsername(username);
		final String token = authTokenProvider.provideAuthToken(findByUsername);

		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
	}
}