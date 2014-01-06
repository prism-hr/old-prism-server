package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationTransferStatus {

    QUEUED_FOR_WEBSERVICE_CALL("queued for ws call"),
    REJECTED_BY_WEBSERVICE("rejected"),
    QUEUED_FOR_ATTACHMENTS_SENDING("queued for attachments sending"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    private final String displayValue;

    private ApplicationTransferStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }

}