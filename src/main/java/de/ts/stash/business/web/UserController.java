package de.ts.stash.business.web;

import static de.ts.stash.security.SecurityConstants.ACCESS_TOKEN_PREFIX;
import static de.ts.stash.security.SecurityConstants.AUTH_HEADER_STRING;
import static de.ts.stash.security.SecurityConstants.REFRESH_HEADER_STRING;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.security.auth.RefreshFailedException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

	public void signUp(@RequestBody final RegisterUserData user, final HttpServletResponse response)
			throws JsonProcessingException {
		final List<Role> roles = Arrays.asList(Role.USER);
		final ApplicationUser newlyCreatedUser = userService
				.save(new ApplicationUser(user.getUsername(), user.getPassword(), roles));
		final String token = authTokenProvider.provideAuthToken(newlyCreatedUser);
		final RefreshToken refreshToken = refreshTokenProvider.provideToken(newlyCreatedUser);

		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + token);
		response.addHeader(SecurityConstants.REFRESH_HEADER_STRING, refreshToken.getValue());
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
		final RefreshToken refreshToken = refreshTokenProvider.provideToken(verifiedUser);
		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + token);
		response.addHeader(SecurityConstants.REFRESH_HEADER_STRING, refreshToken.getValue());
	}

	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	public void refresh(@RequestBody final RefreshToken refreshToken, final HttpServletResponse response)
			throws JsonProcessingException, RefreshFailedException {
		final RefreshToken findByValue = refreshTokenRepository.findByValue(refreshToken.getValue());

		if (findByValue == null) {
			throw new RefreshFailedException("Invalid token!");
		}

		if (LocalDateTime.now().minusDays(SecurityConstants.REFRESH_TOKEN_EXPIRATION_IN_DAYS)
				.isAfter(findByValue.getIssuedAt())) {
			throw new RefreshFailedException("Token expired!");
		}

		final ApplicationUser user = findByValue.getUser();

		final String newAccesstoken = this.authTokenProvider.provideAuthToken(user);
		final RefreshToken newRefreshToken = this.refreshTokenProvider.provideToken(user);

		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + newAccesstoken);
		response.addHeader(REFRESH_HEADER_STRING, newRefreshToken.getValue());
	}
}