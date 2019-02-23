package de.ts.stash.business.web;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest
public class ItemControllerIntegrationTest {

	
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
	
	@Test
	@WithMockUser
	public void testGetAllItems() throws Exception {
		
		MockHttpServletResponse response = mvc.perform(get("/v1/Items")).andReturn().getResponse();
		int status = response.getStatus();
		String contentAsString = response.getContentAsString();
		
		
		Assert.assertThat("Status is 200", status, Matchers.equalTo(200));
		Assert.assertThat("Items must not be emtpy", contentAsString, Matchers.notNullValue());
	}
	
}
