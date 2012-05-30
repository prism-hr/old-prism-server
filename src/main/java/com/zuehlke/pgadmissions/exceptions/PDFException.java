package com.zuehlke.pgadmissions.exceptions;

public class PDFException extends RuntimeException {

	private static final long serialVersionUID = -7436743296279853075L;

	public PDFException() {
		super();
	}

	public PDFException(String message, Throwable cause) {
		super(message, cause);
	}

	public PDFException(String message) {
		super(message);
	}

	public PDFException(Throwable cause) {
		super(cause);
	}
}
