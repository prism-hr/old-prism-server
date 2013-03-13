package com.zuehlke.pgadmissions.exceptions.application;

public class PrimarySupervisorNotDefinedException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    public PrimarySupervisorNotDefinedException(String applicationNumber) {
        super(applicationNumber);
    }

}
