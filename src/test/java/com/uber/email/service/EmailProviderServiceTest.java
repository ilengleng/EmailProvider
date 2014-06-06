package com.uber.email.service;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import junit.framework.Assert;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.uber.email.data.MailgunSendEmailRequest;
import com.uber.email.data.MandrillSendEmailRequest;
import com.uber.email.data.SendEmailRequest;
import com.uber.email.exception.EmailProviderServiceException;
import com.uber.email.provider.EmailProvider;
import com.uber.email.provider.EmailProviderManager;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:/WEB-INF/spring/appServlet/servlet-context.xml",
		"classpath*:/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class EmailProviderServiceTest {
	private static final String JSON_BODY = "This is a fake json";
	@Mock
	private EmailProviderManager emailProviderManager;
	@Mock
	private EmailProvider mandrillEmailProvider;
	@Mock
	private EmailProvider mailgunEmailProvider;

	private EmailProviderService emailProviderService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(emailProviderManager.getOrderedEmailProvider()).thenReturn(
				Arrays.asList(mailgunEmailProvider, mandrillEmailProvider));
		emailProviderService = new EmailProviderService(emailProviderManager);
	}

	@Test
	public void testSendEmail_SuccessWithFirstEmailProvider() {
		when(mailgunEmailProvider.normalizeRequest(JSON_BODY)).thenReturn(
				buildMailgunSendEmailRequest());
		emailProviderService.sendEmail(JSON_BODY);
		verify(mailgunEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mailgunEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
		verify(mandrillEmailProvider, never()).normalizeRequest(JSON_BODY);
		verify(mandrillEmailProvider, never()).sendEmail(
				any(SendEmailRequest.class));
	}

	@Test
	public void testSendEmail_FirstEmailProviderIncorrectApiKey_SecondEmailProviderSucecss() {
		SendEmailRequest mailgunSendEmailRequest = buildMailgunSendEmailRequest();
		mockForbiddenError(mailgunSendEmailRequest);
		SendEmailRequest mandrillSendEmailRequest = buildMandrillSendEmailRequest();
		when(mandrillEmailProvider.normalizeRequest(JSON_BODY)).thenReturn(
				mandrillSendEmailRequest);
		emailProviderService.sendEmail(JSON_BODY);
		verify(mailgunEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mailgunEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
		verify(mandrillEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mandrillEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
	}

	@Test
	public void testSendEmail_FirstEmailProviderInternalFailure_SecondEmailProviderSucecss() {
		SendEmailRequest mailgunSendEmailRequest = buildMailgunSendEmailRequest();
		mockInternalFailureError(mailgunSendEmailRequest);
		SendEmailRequest mandrillSendEmailRequest = buildMandrillSendEmailRequest();
		when(mandrillEmailProvider.normalizeRequest(JSON_BODY)).thenReturn(
				mandrillSendEmailRequest);
		emailProviderService.sendEmail(JSON_BODY);
		verify(mailgunEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mailgunEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
		verify(mandrillEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mandrillEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
	}

	@Test
	public void testSendEmail_FirstSecondEmailProviderInternalFailure_Fail() {
		SendEmailRequest mailgunSendEmailRequest = buildMailgunSendEmailRequest();
		mockInternalFailureError(mailgunSendEmailRequest);
		SendEmailRequest mandrillSendEmailRequest = buildMandrillSendEmailRequest();
		mockInternalFailureError(mandrillSendEmailRequest);
		try {
			emailProviderService.sendEmail(JSON_BODY);
			fail("FirstEmailProvider should throw exception of internal failure");
		} catch (EmailProviderServiceException ex) {
			Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
					ex.getHttpStatusCode());
		}
		verify(mailgunEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mailgunEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
		verify(mandrillEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mandrillEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
	}

	@Test
	public void testSendEmail_FirstSecondEmailProviderForbidden_Fail() {
		SendEmailRequest mailgunSendEmailRequest = buildMailgunSendEmailRequest();
		mockForbiddenError(mailgunSendEmailRequest);
		SendEmailRequest mandrillSendEmailRequest = buildMandrillSendEmailRequest();
		mockForbiddenError(mandrillSendEmailRequest);
		try {
			emailProviderService.sendEmail(JSON_BODY);
			fail();
		} catch (EmailProviderServiceException ex) {
			Assert.assertEquals(HttpStatus.SC_FORBIDDEN, ex.getHttpStatusCode());
		}
		verify(mailgunEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mailgunEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
		verify(mandrillEmailProvider, times(1)).normalizeRequest(JSON_BODY);
		verify(mandrillEmailProvider, times(1)).sendEmail(
				any(SendEmailRequest.class));
	}

	private SendEmailRequest buildMailgunSendEmailRequest() {
		return new MailgunSendEmailRequest.Builder().build();
	}

	private SendEmailRequest buildMandrillSendEmailRequest() {
		return new MandrillSendEmailRequest.Builder().build();
	}

	private void mockForbiddenError(SendEmailRequest sendEmailRequest) {
		if (sendEmailRequest instanceof MailgunSendEmailRequest) {
			when(mailgunEmailProvider.normalizeRequest(JSON_BODY)).thenReturn(
					sendEmailRequest);
			doThrow(
					new EmailProviderServiceException("Forbidden",
							HttpStatus.SC_FORBIDDEN))
					.when(mailgunEmailProvider).sendEmail(sendEmailRequest);
		} else {
			when(mandrillEmailProvider.normalizeRequest(JSON_BODY)).thenReturn(
					sendEmailRequest);
			doThrow(
					new EmailProviderServiceException("Forbidden",
							HttpStatus.SC_FORBIDDEN)).when(
					mandrillEmailProvider).sendEmail(sendEmailRequest);
		}
	}

	private void mockInternalFailureError(SendEmailRequest sendEmailRequest) {
		if (sendEmailRequest instanceof MailgunSendEmailRequest) {
			when(mailgunEmailProvider.normalizeRequest(JSON_BODY)).thenReturn(
					sendEmailRequest);
			doThrow(
					new EmailProviderServiceException("Internal failure",
							HttpStatus.SC_INTERNAL_SERVER_ERROR)).when(
					mailgunEmailProvider).sendEmail(sendEmailRequest);
		} else {
			when(mandrillEmailProvider.normalizeRequest(JSON_BODY)).thenReturn(
					sendEmailRequest);
			doThrow(
					new EmailProviderServiceException("Forbidden",
							HttpStatus.SC_INTERNAL_SERVER_ERROR)).when(
					mandrillEmailProvider).sendEmail(sendEmailRequest);
		}
	}
}
