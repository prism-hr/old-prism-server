package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationTransferStatus {

    /** The transfer is queued for webservice call ("queue 1").*/
    QUEUED_FOR_WEBSERVICE_CALL("queued for ws call"),

    /** The transfer was rejected by PORTICO webservice (validation problem, XML schema problems etc).*/
    REJECTED_BY_WEBSERVICE("rejected"),

    /** Webservice call was completed successuflly and now the transfer is queued for attachments sendding ("queue 2"). */
    QUEUED_FOR_ATTACHMENTS_SENDING("queued for attachments sending"),

    /** Successfully completed. */
    COMPLETED("completed"),

    /** Transfer failed and is not going to be retried (i.e. was removed from queue 1 or queue 2 before completing).*/
    CANCELLED("cancelled");

    private final String displayValue;

    private ApplicationTransferStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }

}
