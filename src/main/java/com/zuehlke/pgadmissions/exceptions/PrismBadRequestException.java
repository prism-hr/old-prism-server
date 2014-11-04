package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class PrismBadRequestException extends RuntimeException {

    private static final long serialVersionUID = 5855744824974222882L;

    private String reason;

    public PrismBadRequestException() {
    }

    public PrismBadRequestException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
