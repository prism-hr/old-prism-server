package com.zuehlke.pgadmissions.exceptions.application;

import com.zuehlke.pgadmissions.exceptions.PgadmissionsException;

public abstract class ApplicationFormException extends PgadmissionsException {

    private static final long serialVersionUID = -1058592315562054622L;

    private String applicationNumber;

    public ApplicationFormException(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

}
