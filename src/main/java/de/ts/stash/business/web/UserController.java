package de.ts.stash.business.web;

import static de.ts.stash.security.SecurityConstants.HEADER_STRING;
import static de.ts.stash.security.SecurityConstants.TOKEN_PREFIX;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.ts.stash.auth.user.ApplicationUser;
import de.ts.stash.auth.user.Role;
import de.ts.stash.auth.user.UserRepository;
import de.ts.stash.security.AuthTokenProvider;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	AuthTokenProvider authTokenProvider;

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserController(final UserRepository applicationUserRepository,
			final BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = applicationUserRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostMapping("/sign-up")
	public void signUp(@RequestBody final RegisterUserData user, final HttpServletResponse response)
			throws JsonProcessingException {
		final String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		final List<Role> roles = Arrays.asList(Role.USER);
		final ApplicationUser newlyCreatedUser = userRepository
				.save(new ApplicationUser(user.getUsername(), encodedPassword, roles));
		final String token = authTokenProvider.provideAuthToken(newlyCreatedUser);
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
		response.setStatus(HttpStatus.CREATED.value());
	}
}