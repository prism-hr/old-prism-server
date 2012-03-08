package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="You are not authorized to view comments")
public class CannotViewCommentsException extends RuntimeException {
	private static final long serialVersionUID = -63516306524980738L;

}
