package com.zuehlke.pgadmissions.exceptions.application;

public class ReviewerAlreadyRespondedException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    public ReviewerAlreadyRespondedException(String applicationNumber) {
        super(applicationNumber);
    }

}
