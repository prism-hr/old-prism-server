package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PrismForbiddenException extends RuntimeException {

    public PrismForbiddenException(String message) {
        super(message);
    }

}
