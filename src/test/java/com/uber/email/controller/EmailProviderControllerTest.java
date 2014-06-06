package com.uber.email.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.uber.email.service.EmailProviderService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:/WEB-INF/spring/appServlet/servlet-context.xml",
		"classpath*:/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class EmailProviderControllerTest {

	@Mock
	private EmailProviderService emailProviderService;

	@InjectMocks
	private EmailProviderController emailProviderController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(emailProviderController)
				.build();
	}

	@Test
	public void testPostEmail_UnsupportedMediaType() throws Exception {
		this.mockMvc.perform(post("/email").content("")).andExpect(
				status().isUnsupportedMediaType());
	}

	@Test
	public void testPostEmail_MethodNotAllowed() throws Exception {
		this.mockMvc.perform(get("/email").content("")).andExpect(
				status().isMethodNotAllowed());
	}

	@Test
	public void testPostEmail() throws Exception {
		this.mockMvc.perform(
				post("/email").contentType(MediaType.APPLICATION_JSON).content(
						"")).andExpect(status().isOk());
	}
}
