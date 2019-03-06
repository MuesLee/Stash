package de.ts.stash.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.persistence.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserService(final UserRepository userRepository, final BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public ApplicationUser save(final ApplicationUser user) {
		final boolean userExists = userExists(user);
		if (!userExists) {
			final String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
		}

		return userRepository.save(user);
	}

	private boolean userExists(final ApplicationUser user) {
		return user != null && user.getId() != null && userRepository.existsById(user.getId());
	}

	public Optional<ApplicationUser> findByUsernameAndPassword (final String username, final String password) {
		final ApplicationUser foundUser = userRepository.findByUsername(username);
		if (foundUser == null) {
			return Optional.empty();
		}
		if (bCryptPasswordEncoder.matches(password, foundUser.getPassword())) {
			return Optional.of(foundUser);
		}
		return Optional.empty();
	}

}
