package com.zuehlke.pgadmissions.exceptions.application;

public class CannotTerminateApplicationException extends ApplicationFormException {

    private static final long serialVersionUID = -2390352248154970804L;

    public CannotTerminateApplicationException(String applicationNumber) {
        super(applicationNumber);
    }

}
