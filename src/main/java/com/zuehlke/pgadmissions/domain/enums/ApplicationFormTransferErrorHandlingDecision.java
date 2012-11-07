package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormTransferErrorHandlingDecision {
    GIVE_UP_THIS_TRANSFER_ONLY,
    GIVE_UP_AND_PAUSE_TRANSFERS,
    RETRY_AS_LAST_ELEMENT_OF_CURRENT_QUEUE,
    RETRY_AFTER_CONFIGURED_DELAY
}
