package com.uber.email.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MailgunSendEmailRequest extends SendEmailRequest {

	private MailgunSendEmailRequest(String to, String toName, String from,
			String fromName, String subject, String text) {
		this.to = to;
		this.to_name = toName;
		this.from = from;
		this.from_name = fromName;
		this.subject = subject;
		this.text = text;
	}

	@Override
	public SendEmailRequest updateText(String newText) {
		return new MailgunSendEmailRequest(to, to_name, from, from_name,
				subject, newText);
	}

	public List<NameValuePair> toParameters() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
		parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("to", to_name + " " + to));
		parameters.add(new BasicNameValuePair("from", from_name + " " + from));
		parameters.add(new BasicNameValuePair("subject", subject));
		parameters.add(new BasicNameValuePair("text", text));
		return parameters;
	}

	@Override
	public JsonObject toJson(String apiKey) {
		// Note: input apiKey is not used here
		return new JsonParser().parse(new Gson().toJson(this))
				.getAsJsonObject();
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

		public MailgunSendEmailRequest build() {
			return new MailgunSendEmailRequest(to, to_name, from, from_name,
					subject, text);
		}
	}
}
