package com.uber.email.provider;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.fail;

import com.google.gson.JsonObject;
import com.uber.email.data.SendEmailRequest;
import com.uber.email.exception.EmailProviderServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:/WEB-INF/spring/appServlet/servlet-context.xml",
		"classpath*:/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
@PropertySource("classpath:services.properties")
public class EmailProviderTest {

	private MandrillEmailProvider mandrillEmailProvider;
	private MailgunEmailProvider mailgunEmailProvider;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mandrillEmailProvider = new MandrillEmailProvider();
		mailgunEmailProvider = new MailgunEmailProvider();
	}

	@Test
	public void testValidateRequestParameters_MissParameters() {
		try {
			mandrillEmailProvider.validateAndCleanRequestParameters(
					"defs@test.com", null, null, "abc@test.com", "subject",
					"body");
			fail("validateRequestParameters should fail since there are no all required prameters");
		} catch (EmailProviderServiceException e) {
			Assert.assertEquals(e.getHttpStatusCode(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	@Test
	public void testValidateRequestParameters_InvalidFromEmail_WrongEmailFormat() {
		try {
			mandrillEmailProvider.validateAndCleanRequestParameters(
					"defs@test.com", "cool name", "This is not a valid email",
					"cool name", "subject", "body");
			fail("validateRequestParameters should fail since fromEmail is invalid");
		} catch (EmailProviderServiceException e) {
			Assert.assertEquals(e.getHttpStatusCode(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	@Test
	public void testValidateRequestParameters_InvalidFromEmail_Empty() {
		try {
			mandrillEmailProvider.validateAndCleanRequestParameters(
					"defs@test.com", "cool name", "  ", "cool name", "subject",
					"body");
			fail("validateRequestParameters should fail since fromEmail is a empty string");
		} catch (EmailProviderServiceException e) {
			Assert.assertEquals(e.getHttpStatusCode(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	@Test
	public void testValidateRequestParameters_InvalidToEmail_WrongEmailFormat() {
		try {
			mandrillEmailProvider.validateAndCleanRequestParameters(
					"This is not a valid email", "cool name",
					"thisisemail@abc.com", "cool name", "subject", "body");
			fail("validateRequestParameters should fail since toEmail is invalid");
		} catch (EmailProviderServiceException e) {
			Assert.assertEquals(e.getHttpStatusCode(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	@Test
	public void testValidateRequestParameters_InvalidToEmail_Empty() {
		try {
			mandrillEmailProvider.validateAndCleanRequestParameters("   ",
					"cool name", "defs@test.com", "cool name", "subject",
					"body");
			fail("validateRequestParameters should fail since toEmail is a empty string");
		} catch (EmailProviderServiceException e) {
			Assert.assertEquals(e.getHttpStatusCode(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	@Test
	public void testValidateRequestParameters_EmptyBody() {
		try {
			mandrillEmailProvider.validateAndCleanRequestParameters(
					"asda@face.com", "cool name", "defs@test.com", "cool name",
					"subject", "");
			fail("validateRequestParameters should fail since body is a empty string");
		} catch (EmailProviderServiceException e) {
			Assert.assertEquals(e.getHttpStatusCode(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	@Test
	public void testValidateRequestParameters() {
		String body = "<h1>Your Bill</h1><p>$10</p>";
		String expectedBody = "Your Bill $10";
		String cleanedText = mandrillEmailProvider
				.validateAndCleanRequestParameters("asda@face.com",
						"cool name", "defs@test.com", "cool name", "subject",
						body);
		Assert.assertEquals(expectedBody, cleanedText);
	}

	@Test
	public void testNormalizeRequest_InvalidJson() {
		try {
			mandrillEmailProvider.normalizeRequest("Invalid json");
			fail("should throw exception since it is an invlaid json");
		} catch (EmailProviderServiceException e) {
			Assert.assertEquals(e.getHttpStatusCode(),
					HttpStatus.SC_BAD_REQUEST);
		}

		try {
			mailgunEmailProvider.normalizeRequest("Invalid json");
			fail("should throw exception since it is an invlaid json");
		} catch (EmailProviderServiceException e) {
			Assert.assertEquals(e.getHttpStatusCode(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	@Test
	public void testNormalizeRequest() {
		String to = "fake@example.com";
		String to_name = "Mr.A";
		String from = "noreply@abc.com";
		String from_name = "aqz";
		String subject = "hello";
		String body = "<h1>Your Bill</h1><p>$10</p>";
		String expectedBody = "Your Bill $10";

		JsonObject json = new JsonObject();
		json.addProperty("to", to);
		json.addProperty("to_name", to_name);
		json.addProperty("from", from);
		json.addProperty("from_name", from_name);
		json.addProperty("subject", subject);
		json.addProperty("body", body);

		SendEmailRequest actualMailgunRequest = mailgunEmailProvider
				.normalizeRequest(json.toString());
		SendEmailRequest actualMandrillRequest = mandrillEmailProvider
				.normalizeRequest(json.toString());
		Assert.assertEquals(to, actualMailgunRequest.to);
		Assert.assertEquals(to_name, actualMailgunRequest.to_name);
		Assert.assertEquals(from, actualMailgunRequest.from);
		Assert.assertEquals(from_name, actualMailgunRequest.from_name);
		Assert.assertEquals(subject, actualMailgunRequest.subject);
		Assert.assertEquals(expectedBody, actualMailgunRequest.text);

		Assert.assertEquals(to, actualMandrillRequest.to);
		Assert.assertEquals(to_name, actualMandrillRequest.to_name);
		Assert.assertEquals(from, actualMandrillRequest.from);
		Assert.assertEquals(from_name, actualMandrillRequest.from_name);
		Assert.assertEquals(subject, actualMandrillRequest.subject);
		Assert.assertEquals(expectedBody, actualMandrillRequest.text);
	}
}
