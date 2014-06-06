package com.uber.email.exception;

public class EmailProviderServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final int httpStatusCode;

	public int getHttpStatusCode() {
		return this.httpStatusCode;
	}

	public EmailProviderServiceException(String message, int httpStatusCode) {
		super(message);
		this.httpStatusCode = httpStatusCode;
	}

	public EmailProviderServiceException(String message, Throwable cause, int httpStatusCode) {
		super(message, cause);
		this.httpStatusCode = httpStatusCode;
	}

}
