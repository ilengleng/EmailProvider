package com.uber.email.provider;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.uber.email.data.EmailProviderConst;
import com.uber.email.data.MailgunSendEmailRequest;
import com.uber.email.data.SendEmailRequest;
import com.uber.email.exception.EmailProviderServiceException;

@Component
public class MailgunEmailProvider extends EmailProvider {
	@Override
	public HttpPost buildHttpPost(SendEmailRequest request) {
		HttpPost post = new HttpPost(EmailProviderConst.MAINGUN_API_ENDPOINT);
		EntityBuilder entityBuilder = EntityBuilder.create()
				.setContentType(ContentType.APPLICATION_FORM_URLENCODED)
				.setParameters(request.toParameters());
		post.setEntity(entityBuilder.build());
		String apiKey = env
				.getProperty(EmailProviderConst.MAINGUN_API_KEY_PROP);
		String authHeader = Base64.encodeBase64String(("api:" + apiKey)
				.getBytes(Charsets.UTF_8));
		post.setHeader("Authorization", "Basic " + authHeader);
		return post;
	}

	@Override
	public SendEmailRequest normalizeRequest(String jsonBody) {
		MailgunSendEmailRequest request;
		try {
			request = new Gson().fromJson(jsonBody,
					MailgunSendEmailRequest.class);
		} catch (JsonSyntaxException e) {
			throw new EmailProviderServiceException("Invalid input format",
					HttpStatus.SC_BAD_REQUEST);
		}
		String cleanedText = validateAndCleanRequestParameters(request.to,
				request.to_name, request.from, request.from_name,
				request.subject, request.text);
		return request.updateText(cleanedText);
	}
}
