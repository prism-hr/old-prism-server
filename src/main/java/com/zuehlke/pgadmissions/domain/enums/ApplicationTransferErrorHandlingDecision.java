package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationTransferErrorHandlingDecision {
    GIVE_UP,
    
    RETRY,
    
    STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION,
}
