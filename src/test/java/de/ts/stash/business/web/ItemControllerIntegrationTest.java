package de.ts.stash.business.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ts.stash.domain.Item;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	@WithMockUser
	public void testGetAllItemsWithMockUser() throws Exception {

		MockHttpServletResponse response = mvc
				.perform(get("/v1/items"))
				.andExpect(status().is(200))
				.andReturn()
				.getResponse();
		String contentAsString = response.getContentAsString();

		List<Item> actualItems = new ObjectMapper().readValue(contentAsString, new TypeReference<List<Item>>() {
		});

		assertThat(actualItems).hasSize(2);
	}

	@Test
	public void testGetAllItemsNotAuthenticatedShouldReturn403() throws Exception {

		mvc
				.perform(get("/v1/items"))
				.andExpect(status().isForbidden())
				.andExpect(content().string(Matchers.isEmptyOrNullString()))
				.andReturn()
				.getResponse();
	}
}
