package com.zuehlke.pgadmissions.exceptions.application;

public class CannotWithdrawApplicationException extends ApplicationFormException {

    private static final long serialVersionUID = -2390352248154970804L;

    public CannotWithdrawApplicationException(String applicationNumber) {
        super(applicationNumber);
    }

}
