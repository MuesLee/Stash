package de.ts.stash.security;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.ts.stash.auth.user.ApplicationUser;

public class JwtProviderTest {

	private JwtProvider classUnderTest = new JwtProvider();

	@Test
	public void shouldCreateTokenForUser() throws Exception {

		ApplicationUser user = new ApplicationUser(0, "Test", "123");

		String provideAuthToken = classUnderTest.provideAuthToken(user);

		Assert.assertThat("Token should not be emtpy", provideAuthToken, Matchers.notNullValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentForMissingUserName() throws Exception {

		ApplicationUser user = new ApplicationUser(0, null, "123");
		classUnderTest.provideAuthToken(user);
	}

}
