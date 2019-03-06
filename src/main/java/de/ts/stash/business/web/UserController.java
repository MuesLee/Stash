package de.ts.stash.business.web;

import static de.ts.stash.security.SecurityConstants.ACCESS_TOKEN_PREFIX;
import static de.ts.stash.security.SecurityConstants.AUTH_HEADER_STRING;
import static de.ts.stash.security.SecurityConstants.REFRESH_COOKIE_NAME;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.security.auth.RefreshFailedException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.ts.stash.auth.user.RefreshToken;
import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.domain.Role;
import de.ts.stash.persistence.RefreshTokenRepository;
import de.ts.stash.security.SecurityConstants;
import de.ts.stash.security.api.AuthTokenProvider;
import de.ts.stash.security.api.RefreshTokenProvider;
import de.ts.stash.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	AuthTokenProvider authTokenProvider;

	@Autowired
	RefreshTokenProvider refreshTokenProvider;

	private final UserService userService;
	private final RefreshTokenRepository refreshTokenRepository;

	public UserController(final UserService applicationUserRepository,
			final RefreshTokenRepository refreshTokenRepository, final BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userService = applicationUserRepository;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@PostMapping("/sign-up")
	@ResponseStatus(HttpStatus.CREATED)
	public void signUp(@RequestBody final RegisterUserData user, final HttpServletResponse response)
			throws JsonProcessingException {
		final List<Role> roles = Arrays.asList(Role.USER);
		final ApplicationUser newlyCreatedUser = userService
				.save(new ApplicationUser(user.getUsername(), user.getPassword(), roles));
		final String token = authTokenProvider.provideAuthToken(newlyCreatedUser);
		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + token);
		addRefreshCookie(response, newlyCreatedUser);
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public void login(@RequestBody final RegisterUserData user, final HttpServletResponse response)
			throws JsonProcessingException {
		final Optional<ApplicationUser> findByUsername = userService.findByUsernameAndPassword(user.getUsername(),
				user.getPassword());
		final ApplicationUser verifiedUser = findByUsername
				.orElseThrow(() -> new UsernameNotFoundException("Invalid username / password"));
		final String token = authTokenProvider.provideAuthToken(verifiedUser);
		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + token);
		addRefreshCookie(response, verifiedUser);
	}
	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.OK)
	public void logout(final HttpServletResponse response)
			throws JsonProcessingException {
		deleteRefreshCookie(response);
	}

	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	public void refresh(@CookieValue(name=REFRESH_COOKIE_NAME, required=true) final String refreshTokenValue , final HttpServletResponse response)
			throws JsonProcessingException, RefreshFailedException {
		final RefreshToken findByValue = refreshTokenRepository.findByValue(refreshTokenValue);

		if (findByValue == null) {
			throw new RefreshFailedException("Invalid token!");
		}

		if (LocalDateTime.now().minusDays(SecurityConstants.REFRESH_TOKEN_EXPIRATION_IN_DAYS)
				.isAfter(findByValue.getIssuedAt())) {
			throw new RefreshFailedException("Token expired!");
		}

		final ApplicationUser user = findByValue.getUser();

		final String newAccesstoken = this.authTokenProvider.provideAuthToken(user);

		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + newAccesstoken);

		addRefreshCookie(response, user);
	}

	private void addRefreshCookie(final HttpServletResponse response, final ApplicationUser user) {
		final RefreshToken refreshToken = refreshTokenProvider.provideToken(user);
		final Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, refreshToken.getValue());
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(SecurityConstants.REFRESH_TOKEN_EXPIRATION_IN_SECONDS);
		cookie.setPath("/users/refresh");
		response.addCookie(cookie);
	}
	private void deleteRefreshCookie(final HttpServletResponse response) {
		final Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, null);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		cookie.setPath("/users/refresh");
		response.addCookie(cookie);
	}
}