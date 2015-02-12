package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class PrismConflictException extends RuntimeException {

    public PrismConflictException(String message) {
        super(message);
    }

}
