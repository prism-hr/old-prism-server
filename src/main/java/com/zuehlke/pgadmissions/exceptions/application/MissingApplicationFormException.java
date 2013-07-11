package com.zuehlke.pgadmissions.exceptions.application;

public class MissingApplicationFormException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    public MissingApplicationFormException(String applicationNumber) {
        super(applicationNumber);
    }

}
