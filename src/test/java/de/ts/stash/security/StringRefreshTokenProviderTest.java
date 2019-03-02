package de.ts.stash.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.ts.stash.auth.user.RefreshToken;
import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.persistence.RefreshTokenRepository;
import de.ts.stash.util.TimeProvider;

public class StringRefreshTokenProviderTest {

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(timeProvider.convert(Mockito.any(LocalDateTime.class))).thenCallRealMethod();
	}

	@Mock
	TimeProvider timeProvider;

	@Mock
	RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private StringRefreshTokenProvider classUnderTest = new StringRefreshTokenProvider();

	@Test
	public void shouldProvideRefreshToken() throws Exception {

		ApplicationUser givenUser = new ApplicationUser("Derp", null, Collections.emptyList());

		LocalDateTime now = LocalDateTime.now();
		Mockito.when(timeProvider.currentDateTime()).thenReturn(now);

		RefreshToken provideToken = classUnderTest.provideToken(givenUser);
		
		assertThat(provideToken.getValue()).hasLineCount(1).hasSize(500);
		assertThat(provideToken.getIssuedAt()).isEqualTo(now);
		assertThat(provideToken.getUser().getUsername()).isEqualTo(givenUser.getUsername());
	}

}
