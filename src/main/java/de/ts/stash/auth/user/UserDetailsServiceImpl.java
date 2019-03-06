package de.ts.stash.auth.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.persistence.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;

	public UserDetailsServiceImpl(final UserRepository applicationUserRepository) {
		this.userRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final ApplicationUser applicationUser = this.userRepository.findByUsername(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}
		return applicationUser;
	}
}