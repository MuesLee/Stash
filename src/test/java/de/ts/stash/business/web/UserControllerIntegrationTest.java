package de.ts.stash.business.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import de.ts.stash.security.SecurityConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}

	@Test
	public void signUpResponseHasAcessAndRefreshTokenInHeader() throws Exception {
		final byte[] registerUserData = new RegisterUserData("Peter", "x").asJson();
		mockMvc.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON).content(registerUserData))
				.andExpect(status().isCreated())
				.andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING,
						Matchers.startsWith(SecurityConstants.ACCESS_TOKEN_PREFIX)))
				.andExpect(header().string(SecurityConstants.REFRESH_HEADER_STRING, Matchers.notNullValue()));
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
		final byte[] registerUserData = new RegisterUserData("Lacy", "x").asJson();
		final ResultActions result = mockMvc.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON).content(registerUserData))
		.andExpect(status().isCreated()).andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING,
				Matchers.startsWith(SecurityConstants.ACCESS_TOKEN_PREFIX)));
		
		final String refreshToken = result.andReturn().getResponse().getHeader(SecurityConstants.REFRESH_HEADER_STRING);
		Assert.assertThat("Refreshtoken must not be null", refreshToken, Matchers.notNullValue());
		
		mockMvc.perform(post("/users/refresh").contentType(MediaType.APPLICATION_JSON).header(SecurityConstants.REFRESH_HEADER_STRING, refreshToken ))
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(header().string(SecurityConstants.AUTH_HEADER_STRING,
				Matchers.startsWith(SecurityConstants.ACCESS_TOKEN_PREFIX)))
		.andExpect(header().string(SecurityConstants.REFRESH_HEADER_STRING, Matchers.notNullValue()));
	}
}