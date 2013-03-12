package com.zuehlke.pgadmissions.exceptions;

public class MissingApplicationFormException extends PgadmissionsException {

    private String applicationNumber;

    private static final long serialVersionUID = -1058592315562054622L;

    public MissingApplicationFormException(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

}
