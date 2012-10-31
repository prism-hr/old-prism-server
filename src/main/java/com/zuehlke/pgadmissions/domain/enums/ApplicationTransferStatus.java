package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationTransferStatus {
    SCHEDULED("scheduled"),
    IN_PROGRESS("in progress"),
    FAILED("failed"),
    FINISHED("finished"),
    CANCELLED("cancelled");

    private final String displayValue;

    private ApplicationTransferStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {

        return displayValue;
    }

}
