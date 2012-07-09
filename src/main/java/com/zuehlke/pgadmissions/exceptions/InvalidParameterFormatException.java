package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidParameterFormatException extends RuntimeException {

	private static final long serialVersionUID = 3924440725678288974L;

	public InvalidParameterFormatException() {
		super();
	}

	public InvalidParameterFormatException(String message) {
		super(message);

	}

	public InvalidParameterFormatException(Throwable cause) {
		super(cause);

	}

}
