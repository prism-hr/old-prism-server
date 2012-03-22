package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="You have already uploaded a reference")
public class RefereeAlreadyUploadedReference extends RuntimeException {
	private static final long serialVersionUID = -63516306524980738L;

}
