package com.uber.email.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MandrillSendEmailRequest extends SendEmailRequest {

	private MandrillSendEmailRequest(String to, String toName, String from,
			String fromName, String subject, String text) {
		this.to = to;
		this.to_name = toName;
		this.from = from;
		this.from_name = fromName;
		this.subject = subject;
		this.text = text;
	}

	@Override
	public JsonObject toJson(String apiKey) {
		JsonObject json = new JsonObject();
		json.addProperty("key", apiKey);
		json.add("message", buildMessage());
		json.addProperty("async", "false");
		return json;
	}

	@Override
	public SendEmailRequest updateText(String newText) {
		return new MandrillSendEmailRequest(to, to_name, from, from_name,
				subject, newText);
	}

	@Override
	public List<NameValuePair> toParameters() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
		parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("message", buildMessage()
				.toString()));
		parameters.add(new BasicNameValuePair("async", "false"));
		return parameters;
	}

	private JsonObject buildMessage() {
		JsonObject message = new JsonObject();
		message.addProperty("from_email", from);
		message.addProperty("from_name", from_name);
		message.addProperty("subject", subject);
		message.addProperty("text", text);
		message.add("to", buildTo());
		return message;
	}

	private JsonArray buildTo() {
		JsonArray arr = new JsonArray();
		JsonObject toInfo = new JsonObject();
		toInfo.addProperty("email", to);
		toInfo.addProperty("name", to_name);
		toInfo.addProperty("type", "to");
		arr.add(toInfo);
		return arr;
	}

	public static class Builder {
		public String to;
		public String to_name;
		public String from;
		public String from_name;
		public String subject;
		public String text;

		public Builder withTo(String to) {
			this.to = to;
			return this;
		}

		public Builder withToName(String to_name) {
			this.to_name = to_name;
			return this;
		}

		public Builder withFrom(String from) {
			this.from = from;
			return this;
		}

		public Builder withFromName(String from_name) {
			this.from_name = from_name;
			return this;
		}

		public Builder withSubject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder withText(String text) {
			this.text = text;
			return this;
		}

		public MandrillSendEmailRequest build() {
			return new MandrillSendEmailRequest(to, to_name, from, from_name,
					subject, text);
		}
	}
}
