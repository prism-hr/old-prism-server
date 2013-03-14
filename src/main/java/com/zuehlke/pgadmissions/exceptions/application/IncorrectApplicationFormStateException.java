package com.zuehlke.pgadmissions.exceptions.application;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class IncorrectApplicationFormStateException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    private ApplicationFormStatus expectedState;

    public IncorrectApplicationFormStateException(String applicationNumber, ApplicationFormStatus expectedState) {
        super(applicationNumber);
        this.expectedState = expectedState;
    }

    public ApplicationFormStatus getExpectedState() {
        return expectedState;
    }

}
