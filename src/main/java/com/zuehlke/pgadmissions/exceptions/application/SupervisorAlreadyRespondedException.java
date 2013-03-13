package com.zuehlke.pgadmissions.exceptions.application;

public class SupervisorAlreadyRespondedException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    public SupervisorAlreadyRespondedException(String applicationNumber) {
        super(applicationNumber);
    }

}
