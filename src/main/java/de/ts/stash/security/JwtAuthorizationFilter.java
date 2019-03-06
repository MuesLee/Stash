package de.ts.stash.security;

import static de.ts.stash.security.SecurityConstants.ACCESS_TOKEN_PREFIX;
import static de.ts.stash.security.SecurityConstants.AUTH_HEADER_STRING;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.security.api.AuthTokenReader;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	private final AuthTokenReader authTokenReader = new JwtAuthTokenReader();

	public JwtAuthorizationFilter(final AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest req, final HttpServletResponse res,
			final FilterChain chain) throws IOException, ServletException {
		final String header = req.getHeader(AUTH_HEADER_STRING);

		if (header == null || !header.startsWith(ACCESS_TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}

		final UsernamePasswordAuthenticationToken authentication = getAuthentication(
				header.replace(ACCESS_TOKEN_PREFIX, ""));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(final String token) {
		try {
			final ApplicationUser readValue = this.authTokenReader.extractUser(token);
			return new UsernamePasswordAuthenticationToken(readValue, null, readValue.getAuthorities());
		} catch (final IOException e) {
			return null;
		}
	}
}