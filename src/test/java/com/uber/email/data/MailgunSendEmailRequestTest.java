package com.uber.email.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;

public class MailgunSendEmailRequestTest {
	private static String to = "test@gmail.com";
	private static String to_name = "test name";
	private static String from = "from@yahoo.com";
	private static String from_name = "from name";
	private static String body = "<h1>Your Bill</h1><p>$10</p>";
	private static String subject = "this is a test email";
	private static MailgunSendEmailRequest request;

	@BeforeClass
	public static void setUp() {
		request = new MailgunSendEmailRequest.Builder().withTo(to)
				.withToName(to_name).withFrom(from).withFromName(from_name)
				.withSubject(subject).withText(body).build();
	}

	@Test
	public void testUpdateText() {
		String newText = "This is new text";
		SendEmailRequest updatedRequest = request.updateText(newText);
		Assert.assertEquals(newText, updatedRequest.text);
		Assert.assertEquals(to, updatedRequest.to);
		Assert.assertEquals(to_name, updatedRequest.to_name);
		Assert.assertEquals(from, updatedRequest.from);
		Assert.assertEquals(from_name, updatedRequest.from_name);
		Assert.assertEquals(subject, updatedRequest.subject);
	}

	@Test
	public void testToParameters() {
		List<NameValuePair> expected = new ArrayList<NameValuePair>(4);
		expected = new ArrayList<NameValuePair>();
		expected.add(new BasicNameValuePair("to", to_name + " " + to));
		expected.add(new BasicNameValuePair("from", from_name + " " + from));
		expected.add(new BasicNameValuePair("subject", subject));
		expected.add(new BasicNameValuePair("text", body));

		List<NameValuePair> actual = request.toParameters();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testToJson() {
		JsonObject expected = new JsonObject();
		expected.addProperty("to", to);
		expected.addProperty("to_name", to_name);
		expected.addProperty("from", from);
		expected.addProperty("from_name", from_name);
		expected.addProperty("subject", subject);
		expected.addProperty("body", body);

		JsonObject actual = request.toJson("");
		Assert.assertEquals(expected, actual);
	}

}
