package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = -4824654951539647668L;

}
