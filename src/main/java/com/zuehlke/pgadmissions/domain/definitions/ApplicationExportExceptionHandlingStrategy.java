package com.zuehlke.pgadmissions.domain.definitions;

public enum ApplicationExportExceptionHandlingStrategy {

    RETRY_SYSTEM_INVOCATION_IMMEDIATE(0), //
    RETRY_SYSTEM_INVOCATION_DELAYED(1), //
    RETRY_USER_INVOCATION(0), ///
    GIVE_UP(0);
    
    private int dayDelay;

    private ApplicationExportExceptionHandlingStrategy(int dayDelay) {
        this.dayDelay = dayDelay;
    }

    public final int getDayDelay() {
        return dayDelay;
    }
    
}
