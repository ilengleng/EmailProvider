package com.uber.email.service;

import java.util.List;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uber.email.data.SendEmailRequest;
import com.uber.email.exception.EmailProviderServiceException;
import com.uber.email.provider.EmailProvider;
import com.uber.email.provider.EmailProviderManager;

@Service
public class EmailProviderService {
	private static final Logger logger = LoggerFactory
			.getLogger(EmailProviderService.class);

	private EmailProviderManager emailProviderManager;

	@Autowired
	public EmailProviderService(EmailProviderManager emailProviderManager) {
		this.emailProviderManager = emailProviderManager;
	}

	/**
	 * Send email via first email provider which is defined in
	 * service.properties file. If it is failed because of internal failure or
	 * incorrectly configured api key, switch to second email provider to send
	 * the email again.
	 * 
	 * @param jsonBody
	 *            : json string from request
	 */
	public void sendEmail(String jsonBody) {
		List<EmailProvider> orderedEmailProvider = emailProviderManager
				.getOrderedEmailProvider();
		for (int i = 0; i < orderedEmailProvider.size(); i++) {
			EmailProvider curEmailProvider = orderedEmailProvider.get(i);
			try {
				SendEmailRequest normalizedRequst = curEmailProvider
						.normalizeRequest(jsonBody);
				curEmailProvider.sendEmail(normalizedRequst);
				break;
			} catch (EmailProviderServiceException ex) {
				int statusCode = ex.getHttpStatusCode();
				if ((statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR || statusCode == HttpStatus.SC_FORBIDDEN)
						&& i != orderedEmailProvider.size() - 1) {
					logger.error("[EmailController] Failed to send email with first choice email provider "
							+ curEmailProvider.getClass().getName()
							+ ". Will retry to send email with second choice email provider");
				} else {
					throw ex;
				}
			}
		}
	}
}
