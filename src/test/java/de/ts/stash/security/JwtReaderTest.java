package de.ts.stash.security;

import java.time.LocalDateTime;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.domain.Role;
import de.ts.stash.util.TimeProvider;

public class JwtReaderTest {

	@InjectMocks
	private JwtAuthTokenReader classUnderTest = new JwtAuthTokenReader();
	@InjectMocks
	private JwtAuthTokenProvider authTokenProvider = new JwtAuthTokenProvider();

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(timeProvider.currentDateTime()).thenCallRealMethod();
		Mockito.when(timeProvider.convert(Mockito.any(LocalDateTime.class))).thenCallRealMethod();
	}

	@Mock
	TimeProvider timeProvider;


	
	@Test
	public void shouldReadTokenForUser() throws Exception {

		ApplicationUser user = new ApplicationUser("Test", "x", Collections.singletonList(Role.USER));
		String provideAuthToken = authTokenProvider.provideAuthToken(user);
		ApplicationUser extractUser = classUnderTest.extractUser(provideAuthToken);

		assertThat(extractUser).isEqualToIgnoringGivenFields(user, "password");
		assertThat(extractUser.getPassword()).isNull();
	}
}
