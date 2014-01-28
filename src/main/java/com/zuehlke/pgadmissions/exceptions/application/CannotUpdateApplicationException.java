package com.zuehlke.pgadmissions.exceptions.application;

public class CannotUpdateApplicationException extends ApplicationFormException {

    private static final long serialVersionUID = -2390352248154970804L;

    public CannotUpdateApplicationException(String applicationNumber) {
        super(applicationNumber);
    }

}
