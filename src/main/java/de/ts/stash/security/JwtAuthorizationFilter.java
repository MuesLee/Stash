package de.ts.stash.security;

import static de.ts.stash.security.SecurityConstants.HEADER_STRING;
import static de.ts.stash.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import de.ts.stash.domain.ApplicationUser;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	@Autowired
	AuthTokenReader authTokenReader;

	public JwtAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		String header = req.getHeader(HEADER_STRING);

		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(header.replace(TOKEN_PREFIX, ""));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(String token) {
		try {
			ApplicationUser readValue = authTokenReader.extractUser(token);
			return new UsernamePasswordAuthenticationToken(readValue, null, readValue.getAuthorities());
		} catch (IOException e) {
			return null;
		}
	}
}