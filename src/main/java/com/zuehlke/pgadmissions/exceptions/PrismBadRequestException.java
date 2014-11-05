package com.zuehlke.pgadmissions.exceptions;

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
