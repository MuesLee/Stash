package de.ts.stash.security;

import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.domain.Role;

public class JwtReaderTest {

	private JwtAuthTokenReader classUnderTest = new JwtAuthTokenReader();
	private JwtAuthTokenProvider authTokenProvider = new JwtAuthTokenProvider();

	@Test
	public void shouldReadTokenForUser() throws Exception {

		ApplicationUser user = new ApplicationUser("Test", "", Collections.singletonList(Role.USER));
		String provideAuthToken = authTokenProvider.provideAuthToken(user);
		ApplicationUser extractUser = classUnderTest.extractUser(provideAuthToken);

		Assert.assertThat("User should not be emtpy", extractUser, Matchers.notNullValue());
		Assert.assertThat("Authorities should be present.", extractUser.getAuthorities(), Matchers.equalTo(user.getAuthorities()));
		Assert.assertThat("Username should be present.", extractUser.getUsername(), Matchers.equalTo(user.getUsername()));
		Assert.assertThat("Password should be empty", extractUser.getPassword(), Matchers.equalTo(""));
	}
}
