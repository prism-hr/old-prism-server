package com.zuehlke.pgadmissions.exceptions.application;

public class ActionNoLongerRequiredException extends ApplicationFormException {

    private static final long serialVersionUID = -1058592315562054622L;

    public ActionNoLongerRequiredException(String applicationNumber) {
        super(applicationNumber);
    }

}
