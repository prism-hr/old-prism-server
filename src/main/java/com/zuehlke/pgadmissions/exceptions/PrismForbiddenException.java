package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PrismForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 8719214782193021547L;

    public PrismForbiddenException(String message) {
        super(message);
    }

}
