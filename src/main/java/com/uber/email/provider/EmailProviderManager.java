package com.uber.email.provider;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class EmailProviderManager {
	@Resource
	// This is the ordered email provider list
	private List<EmailProvider> sourceList;

	public List<EmailProvider> getOrderedEmailProvider() {
		return this.sourceList;
	}
}