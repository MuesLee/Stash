package de.ts.stash.business.web;

import static de.ts.stash.security.SecurityConstants.ACCESS_TOKEN_PREFIX;
import static de.ts.stash.security.SecurityConstants.AUTH_HEADER_STRING;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
import de.ts.stash.persistence.UserRepository;
import de.ts.stash.security.AuthTokenProvider;
import de.ts.stash.security.RefreshTokenProvider;
import de.ts.stash.security.SecurityConstants;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	AuthTokenProvider authTokenProvider;

	@Autowired
	RefreshTokenProvider refreshTokenProvider;

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserController(final UserRepository applicationUserRepository,
			final RefreshTokenRepository refreshTokenRepository, final BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = applicationUserRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostMapping("/sign-up")
	@ResponseStatus(HttpStatus.CREATED)
	public void signUp(@RequestBody final RegisterUserData user, final HttpServletResponse response)
			throws JsonProcessingException {
		final String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		final List<Role> roles = Arrays.asList(Role.USER);
		final ApplicationUser newlyCreatedUser = userRepository
				.save(new ApplicationUser(user.getUsername(), encodedPassword, roles));
		final String token = authTokenProvider.provideAuthToken(newlyCreatedUser);
		final RefreshToken refreshToken = refreshTokenProvider.provideToken(newlyCreatedUser);

		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + token);
		response.addHeader(SecurityConstants.REFRESH_HEADER_STRING, refreshToken.getValue());
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public void login(@RequestBody final RegisterUserData user, final HttpServletResponse response)
			throws JsonProcessingException {
		final ApplicationUser findByUsername = userRepository.findByUsername(user.getUsername());
		if (findByUsername == null
				|| !bCryptPasswordEncoder.matches(user.getPassword(), findByUsername.getPassword())) {
			throw new UsernameNotFoundException(
					"User " + user.getUsername() + " could not be found or a bad password has been provided");
		}

		final String token = authTokenProvider.provideAuthToken(findByUsername);
		final RefreshToken refreshToken = refreshTokenProvider.provideToken(findByUsername);
		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + token);
		response.addHeader(SecurityConstants.REFRESH_HEADER_STRING, refreshToken.getValue());
	}

	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	public void refresh(@RequestBody final RefreshToken refreshToken, final HttpServletResponse response)
			throws JsonProcessingException, RefreshFailedException {
		RefreshToken findByValue = refreshTokenRepository.findByValue(refreshToken.getValue());

		if (findByValue == null) {
			throw new RefreshFailedException("Invalid token!");
		}
		
		if(LocalDateTime.now().minusDays(SecurityConstants.REFRESH_TOKEN_EXPIRATION_TIME).isAfter(findByValue.getIssuedAt())) {
			throw new RefreshFailedException("Token expired!");
		}

		ApplicationUser user = findByValue.getUser();

		final String token = authTokenProvider.provideAuthToken(user);
		response.addHeader(AUTH_HEADER_STRING, ACCESS_TOKEN_PREFIX + token);
	}
}