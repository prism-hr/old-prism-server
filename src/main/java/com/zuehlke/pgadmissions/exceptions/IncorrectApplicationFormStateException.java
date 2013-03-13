package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class IncorrectApplicationFormStateException extends PgadmissionsException {

    private static final long serialVersionUID = -1058592315562054622L;

    private String applicationNumber;

    private ApplicationFormStatus expectedState;

    public IncorrectApplicationFormStateException(String applicationNumber, ApplicationFormStatus expectedState) {
        this.applicationNumber = applicationNumber;
        this.expectedState = expectedState;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public ApplicationFormStatus getExpectedState() {
        return expectedState;
    }

}
