package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PrismBadRequestException extends RuntimeException {

    private static final long serialVersionUID = 5855744824974222882L;

    public PrismBadRequestException(String message) {
        super(message);
    }

}
