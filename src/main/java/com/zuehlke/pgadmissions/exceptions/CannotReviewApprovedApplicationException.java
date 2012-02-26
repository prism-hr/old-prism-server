package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="Application has already been approved.")
public class CannotReviewApprovedApplicationException extends RuntimeException {
	
	private static final long serialVersionUID = 5855744824974222882L;
	
}
