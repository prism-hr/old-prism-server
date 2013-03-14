package com.zuehlke.pgadmissions.exceptions.application;

public class RefereeAlreadyRespondedException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    public RefereeAlreadyRespondedException(String applicationNumber) {
        super(applicationNumber);
    }

}
