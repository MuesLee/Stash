package de.ts.stash.security;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.domain.Role;
import de.ts.stash.util.TimeProvider;

public class JwtProviderTest {

	@InjectMocks
	private final JwtAuthTokenProvider classUnderTest = new JwtAuthTokenProvider();

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(timeProvider.currentDateTime()).thenCallRealMethod();
		Mockito.when(timeProvider.convert(Mockito.any(LocalDateTime.class))).thenCallRealMethod();
	}

	@Mock
	TimeProvider timeProvider;

	@Test
	public void shouldCreateTokenForUser() throws Exception {

		final ApplicationUser user = new ApplicationUser("Test", "bla", Collections.singletonList(Role.USER));

		final String provideAuthToken = classUnderTest.provideAuthToken(user);

		assertThat(provideAuthToken).isNotEmpty();
	}

	@Test
	public void tokenShouldHaveExpireClaimSet() throws Exception {

		final ApplicationUser user = new ApplicationUser("Test", "bla", Collections.singletonList(Role.USER));

		final LocalDateTime now = LocalDateTime.now();
		Mockito.when(timeProvider.currentDateTime()).thenReturn(now);
		
		final String encodedAuthToken = classUnderTest.provideAuthToken(user);
		final DecodedJWT decodedToken = JWT.decode(encodedAuthToken);
		final Date tokenExpiresAt = decodedToken.getExpiresAt();
		final Date expectedExpiresAt = timeProvider.convert(now.plusMinutes(10l)); 

		assertThat(tokenExpiresAt).isEqualToIgnoringMillis(expectedExpiresAt);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentForMissingUserName() throws Exception {

		final ApplicationUser user = new ApplicationUser(null, "123", Collections.singletonList(Role.USER));
		classUnderTest.provideAuthToken(user);
	}
}
