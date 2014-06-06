package com.uber.email.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.uber.email.data.EmailProviderConst;
import com.uber.email.provider.EmailProvider;
import com.uber.email.provider.EmailProviderManager;
import com.uber.email.provider.EmailProviderType;
import com.uber.email.provider.MailgunEmailProvider;
import com.uber.email.provider.MandrillEmailProvider;
import com.uber.email.service.EmailProviderService;

@Configuration
public class WebAppConfig {
	private static final Logger logger = LoggerFactory
			.getLogger(WebAppConfig.class);

	@Autowired
	private Environment env;

	@Bean
	public EmailProviderManager emailProviderManager() {
		String emailProviderProp = env
				.getProperty(EmailProviderConst.EMAIL_PROVIDER_PROP);
		List<EmailProvider> orderedEmailProvider = new ArrayList<EmailProvider>();
		if (emailProviderProp == null
				|| emailProviderProp.isEmpty()
				|| EmailProviderType.MANDRILL.name().equalsIgnoreCase(
						emailProviderProp)) {
			orderedEmailProvider.add(mandrillEmailProvider());
			orderedEmailProvider.add(mailgunEmailProvider());
		} else if (EmailProviderType.MAILGUN.name().equalsIgnoreCase(
				emailProviderProp)) {
			Collections.reverse(orderedEmailProvider);
		} else {
			logger.info("[GetEmailProvider] Invalid key: " + emailProviderProp
					+ " for email provider in properties file");
			throw new RuntimeException(
					"Invalid key for email provider in properties file");
		}
		return new EmailProviderManager(orderedEmailProvider);
	}

	@Bean
	public EmailProvider mandrillEmailProvider() {
		return new MandrillEmailProvider();
	}

	@Bean
	public EmailProvider mailgunEmailProvider() {
		return new MailgunEmailProvider();
	}

	@Bean
	public EmailProviderService emailProviderService() {
		return new EmailProviderService(emailProviderManager());
	}

}
