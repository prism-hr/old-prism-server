package com.zuehlke.pgadmissions.exceptions.application;

public class InsufficientApplicationFormPrivilegesException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    public InsufficientApplicationFormPrivilegesException(String applicationNumber) {
        super(applicationNumber);
    }

}
