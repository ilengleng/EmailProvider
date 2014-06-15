package com.uber.email.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.uber.email.data.SendEmailRequest;
import com.uber.email.exception.EmailProviderServiceException;

@PropertySource("classpath:services.properties")
public abstract class EmailProvider {
	private static final Logger logger = LoggerFactory
			.getLogger(MandrillEmailProvider.class);
	@Autowired
	protected Environment env;

	@Autowired
	private HttpClient httpClient;

	/**
	 * SendEmail with input SendEmailRequest. It will use corresponding email
	 * provider.
	 */
	public void sendEmail(SendEmailRequest request) {
		HttpPost post = this.buildHttpPost(request);
		int status;
		String responseText;
		try {
			HttpResponse response = httpClient.execute(post);
			status = response.getStatusLine().getStatusCode();
			responseText = convertResponse2String(response.getEntity()
					.getContent());
			if (status != HttpStatus.SC_OK) {
				logger.error("[sendEmailError] " + status + " " + responseText);
				throw new EmailProviderServiceException(responseText, status);
			}
			logger.info("[sendEmailSuccess] " + status + " " + responseText);
		} catch (IOException e) {
			throw new EmailProviderServiceException("Internal server error ",
					HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Validate all input field. If any input field is null or valid, exception
	 * with code SC_BAD_REQUEST is thrown.
	 * 
	 * @return the body filed with no html tag
	 */
	public String validateAndCleanRequestParameters(String to, String toName,
			String from, String fromName, String subject, String body) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		String exceptionMessage = null;
		String cleanedBody = null;
		if (to != null && toName != null && from != null && fromName != null
				&& subject != null && body != null) {
			if (!emailValidator.isValid(to)) {
				exceptionMessage = "Email of \"to\" is invalid";
			}

			else if (!emailValidator.isValid(from)) {
				exceptionMessage = "Email of \"from\" is invalid";
			}

			else if (toName.trim().isEmpty()) {
				exceptionMessage = "To_name is invalid";
			}

			else if (fromName.trim().isEmpty()) {
				exceptionMessage = "From_name is invalid";
			}

			else if (subject.trim().isEmpty()) {
				exceptionMessage = "Subject is invalid";
			} else {
				// TODO: might need to handle exception here
				// Extract plain text from the given string which might have
				// html tags.
				cleanedBody = Jsoup.parse(body).text();
				if (cleanedBody.trim().isEmpty()) {
					exceptionMessage = "Body is invalid";
				}
			}

		} else {
			exceptionMessage = "Please provide all required arguments";
		}
		if (exceptionMessage != null) {
			throw new EmailProviderServiceException(exceptionMessage,
					HttpStatus.SC_BAD_REQUEST);
		}
		return cleanedBody;
	}

	/**
	 * Normalize and validate the json input. If any required filed is not
	 * provided, or json input is not a valid json string, exception with code
	 * SC_BAD_REQUEST is thrown. Otherwise, returns a valid SendEmailRequest.
	 * 
	 * @param jsonBody
	 *            : json input from request
	 * @return SendEmailRequest
	 */
	public abstract SendEmailRequest normalizeRequest(String jsonBody);

	/**
	 * Build a http POST method with parameters in the given request
	 * 
	 * @return a http POST method
	 */
	public abstract HttpPost buildHttpPost(SendEmailRequest request);

	/**
	 * Convert the given InputStream to String
	 */
	private String convertResponse2String(InputStream is) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(is, writer, Charsets.UTF_8);
		} catch (IOException e) {
			logger.error("Failed to get response from email provider");
			return "Error";
		}
		return writer.toString();
	}
}
