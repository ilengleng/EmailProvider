package com.uber.email.provider;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailProviderManager {
	private List<EmailProvider> orderedEmailProvider;

	@Autowired
	public EmailProviderManager(List<EmailProvider> orderedEmailProvider) {
		this.orderedEmailProvider = orderedEmailProvider;
	}

	public List<EmailProvider> getOrderedEmailProvider() {
		return this.orderedEmailProvider;
	}
}
