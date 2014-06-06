package com.uber.email.data;

import java.util.List;

import org.apache.http.NameValuePair;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public abstract class SendEmailRequest {
	public String to;
	public String to_name;
	public String from;
	public String from_name;
	public String subject;
	@SerializedName("body")
	public String text;

	/**
	 * Build a copy of the request with newText
	 */
	public abstract SendEmailRequest updateText(String newText);

	/**
	 * Returns json format of request parameters
	 */
	public abstract JsonObject toJson(String apiKey);

	/**
	 * Returns list of NameValuePair format of request parameters
	 */
	public abstract List<NameValuePair> toParameters();

}
