package de.ts.stash.business.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Date;

import javax.servlet.http.Cookie;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.auth0.jwt.JWT;

import de.ts.stash.security.SecurityConstants;
import de.ts.stash.util.TimeProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

	@MockBean
	private TimeProvider timeProvider;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
		Mockito.when(timeProvider.convert(Mockito.any(LocalDateTime.class))).thenCallRealMethod();
		Mockito.when(timeProvider.currentDateTime()).thenReturn(LocalDateTime.now(TimeProvider.DEFAULT_ZONE));
	}

	@Test
	public void signUpResponseHasAcessAndRefreshTokenCookieInHeader() throws Exception {
		final byte[] registerUserData = new RegisterUserData("Peter", "x").asJson();
		mockMvc.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON).content(registerUserData))
				.andExpect(status().isCreated())
				.andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING,
						Matchers.startsWith(SecurityConstants.ACCESS_TOKEN_PREFIX)))
				.andExpect(cookie().value(SecurityConstants.REFRESH_HEADER_STRING, Matchers.notNullValue()));
	}

	@Test
	public void loginResponseHasStatus404ForUnknownUser() throws Exception {
		final byte[] registerUserData = new RegisterUserData("Bob", "x").asJson();
		mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(registerUserData))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()))
				.andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING, Matchers.isEmptyOrNullString()));
	}

	@Test
	public void newlyRegisteredUserCanLogin() throws Exception {
		final byte[] registerUserData = new RegisterUserData("Kevin", "x").asJson();
		mockMvc.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON).content(registerUserData))
				.andExpect(status().isCreated()).andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING,
						Matchers.startsWith(SecurityConstants.ACCESS_TOKEN_PREFIX)));

		mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(registerUserData))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING,
						Matchers.startsWith(SecurityConstants.ACCESS_TOKEN_PREFIX)));
	}

	@Test
	public void userCanRefreshAcessTokenWithRefreshToken() throws Exception {
		Mockito.when(timeProvider.currentDateTime())
				.thenReturn(LocalDateTime.now(TimeProvider.DEFAULT_ZONE).minusMinutes(8l));

		final byte[] registerUserData = new RegisterUserData("Lacy", "x").asJson();
		final ResultActions loginResult = mockMvc
				.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON).content(registerUserData))
				.andExpect(status().isCreated())
				.andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING,
						Matchers.startsWith(SecurityConstants.ACCESS_TOKEN_PREFIX)))
				.andExpect(cookie().value(SecurityConstants.REFRESH_HEADER_STRING, Matchers.notNullValue()));

		MockHttpServletResponse response = loginResult.andReturn().getResponse();

		final String initialRefreshToken = response.getCookie(SecurityConstants.REFRESH_HEADER_STRING).getValue();
		final String initialAccessToken = response.getHeader(SecurityConstants.AUTH_HEADER_STRING)
				.substring(SecurityConstants.ACCESS_TOKEN_PREFIX.length());

		assertThat(initialRefreshToken).isNotNull();
		assertThat(initialAccessToken).isNotNull();

		Mockito.when(timeProvider.currentDateTime()).thenReturn(LocalDateTime.now(TimeProvider.DEFAULT_ZONE));

		Cookie cookie = new Cookie(SecurityConstants.REFRESH_HEADER_STRING, initialRefreshToken);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		MvcResult refreshResult = mockMvc
				.perform(post("/users/refresh").contentType(MediaType.APPLICATION_JSON)
						.cookie(cookie))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING,
						Matchers.startsWith(SecurityConstants.ACCESS_TOKEN_PREFIX)))
				.andExpect(cookie().value(SecurityConstants.REFRESH_HEADER_STRING, Matchers.notNullValue()))
				.andReturn();

		final String refreshedRefreshToken = refreshResult.getResponse()
				.getHeader(SecurityConstants.REFRESH_HEADER_STRING);
		final String refreshedAccessToken = refreshResult.getResponse().getHeader(SecurityConstants.AUTH_HEADER_STRING)
				.substring(SecurityConstants.ACCESS_TOKEN_PREFIX.length());

		assertThat(initialRefreshToken).isNotEqualTo(refreshedRefreshToken);
		assertThat(initialAccessToken).isNotEqualTo(refreshedAccessToken);

		Date initialExpiresAt = JWT.decode(initialAccessToken).getExpiresAt();
		Date refreshedExpiresAt = JWT.decode(refreshedAccessToken).getExpiresAt();

		assertThat(initialExpiresAt).isBefore(refreshedExpiresAt);
	}
}