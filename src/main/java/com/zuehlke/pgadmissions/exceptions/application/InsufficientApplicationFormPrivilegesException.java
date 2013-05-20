package com.zuehlke.pgadmissions.exceptions.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InsufficientApplicationFormPrivilegesException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    public InsufficientApplicationFormPrivilegesException(String applicationNumber) {
        super(applicationNumber);
    }

}
