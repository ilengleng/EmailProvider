package com.uber.email.provider;

import org.apache.http.HttpStatus;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.uber.email.data.EmailProviderConst;
import com.uber.email.data.MandrillSendEmailRequest;
import com.uber.email.data.SendEmailRequest;
import com.uber.email.exception.EmailProviderServiceException;

@Component("mandrillEmailProvider")
public class MandrillEmailProvider extends EmailProvider {
	@Override
	public HttpPost buildHttpPost(SendEmailRequest request) {
		HttpPost post = new HttpPost(EmailProviderConst.MANDRILL_API_ENDPOINT);
		EntityBuilder entityBuilder = EntityBuilder
				.create()
				.setContentType(ContentType.APPLICATION_JSON)
				.setText(
						request.toJson(
								env.getProperty(EmailProviderConst.MANDRILL_API_KEY_PROP))
								.toString());
		post.setEntity(entityBuilder.build());
		return post;
	}

	@Override
	public SendEmailRequest normalizeRequest(String jsonBody) {
		MandrillSendEmailRequest request;
		try {
			request = new Gson().fromJson(jsonBody,
					MandrillSendEmailRequest.class);
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
