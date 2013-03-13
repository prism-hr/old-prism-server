package com.zuehlke.pgadmissions.exceptions;

public class RefereeAlreadyRespondedException extends PgadmissionsException {

    private String applicationNumber;

    private static final long serialVersionUID = -1058592315562054622L;

    public RefereeAlreadyRespondedException(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

}
