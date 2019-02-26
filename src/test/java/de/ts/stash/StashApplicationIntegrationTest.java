package de.ts.stash;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ts.stash.business.web.RegisterUserData;
import de.ts.stash.domain.Item;
import de.ts.stash.security.SecurityConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StashApplicationIntegrationTest {

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
	public void registerNewUserAndRequestAllItems() throws Exception {
		final byte[] registerUserData = new RegisterUserData("Bob", "x").asJson();
		ResultActions result = mockMvc
				.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON_UTF8).content(registerUserData))
				.andExpect(status().isCreated()).andExpect(header().string(SecurityConstants.HEADER_STRING,
						Matchers.startsWith(SecurityConstants.TOKEN_PREFIX)));

		String autherizationHeader = result.andReturn().getResponse().getHeader(SecurityConstants.HEADER_STRING);

		MockHttpServletResponse response = mockMvc
				.perform(get("/v1/Items").header(SecurityConstants.HEADER_STRING, autherizationHeader))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andReturn().getResponse();
		String contentAsString = response.getContentAsString();

		List<Item> actualItems = new ObjectMapper().readValue(contentAsString, new TypeReference<List<Item>>() {
		});

		Assert.assertThat("Items has size 2", actualItems, Matchers.hasSize(2));

	}

}
