package de.ts.stash.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.ts.stash.auth.user.RefreshToken;
import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.persistence.RefreshTokenRepository;

public class StringRefreshTokenProviderTest {

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Mock
	RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private StringRefreshTokenProvider classUnderTest = new StringRefreshTokenProvider();

	@Test
	public void shouldProvideRefreshToken() throws Exception {

		ApplicationUser givenUser = new ApplicationUser("Derp", null, Collections.emptyList());

		RefreshToken provideToken = classUnderTest.provideToken(givenUser);
		LocalDateTime now = LocalDateTime.now();

		assertThat(provideToken.getValue()).hasLineCount(1).hasSize(500);
		assertThat(provideToken.getIssuedAt()).isBetween(now.minusSeconds(1l), now);
		assertThat(provideToken.getUser().getUsername()).isEqualTo(givenUser.getUsername());
	}

}
