package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class PrismConflictException extends RuntimeException {

    private static final long serialVersionUID = -4106845258057159810L;

    public PrismConflictException(String message) {
        super(message);
    }

}
