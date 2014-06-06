package com.uber.email.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.uber.email.exception.EmailProviderServiceException;
import com.uber.email.service.EmailProviderService;

/**
 * Handles requests for the email provider.
 */
@Controller
public class EmailProviderController {

	private static final Logger logger = LoggerFactory
			.getLogger(EmailProviderController.class);

	private EmailProviderService emailPrviderService;

	@Autowired
	public EmailProviderController(EmailProviderService emailPrviderService) {
		this.emailPrviderService = emailPrviderService;
	}

	/**
	 * TODO: remove it eventually Simply selects the home view to render by
	 * returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	/**
	 * This is the controller method to handle POST /email with required header
	 * Content-Type=application/json
	 */
	@RequestMapping(value = "/email", method = RequestMethod.POST, consumes = { "application/json" })
	public void email(@RequestBody String jsonBody, HttpServletResponse response) {
		logger.info("Welcome to EmailProvider");
		emailPrviderService.sendEmail(jsonBody);
		response.setStatus(org.springframework.http.HttpStatus.OK.value());
	}

	@ExceptionHandler(EmailProviderServiceException.class)
	public void hanldeException(HttpServletResponse response,
			EmailProviderServiceException ex) throws IOException {
		response.sendError(ex.getHttpStatusCode(), ex.getMessage());
	}
}
