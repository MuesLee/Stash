package de.ts.stash.security;

import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.domain.Role;
import de.ts.stash.util.TimeProvider;

public class JwtProviderTest {

	@InjectMocks
	private JwtAuthTokenProvider classUnderTest = new JwtAuthTokenProvider();

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(timeProvider.currentDateTime()).thenCallRealMethod();
	}

	@Mock
	TimeProvider timeProvider;
	
	@Test
	public void shouldCreateTokenForUser() throws Exception {

		ApplicationUser user = new ApplicationUser( "Test", "bla", Collections.singletonList(Role.USER));

		String provideAuthToken = classUnderTest.provideAuthToken(user);

		Assert.assertThat("Token should not be emtpy", provideAuthToken, Matchers.notNullValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentForMissingUserName() throws Exception {

		ApplicationUser user = new ApplicationUser(null, "123", Collections.singletonList(Role.USER));
		classUnderTest.provideAuthToken(user);
	}

}
