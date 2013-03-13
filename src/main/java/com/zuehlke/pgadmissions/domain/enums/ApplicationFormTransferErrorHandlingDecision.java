package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormTransferErrorHandlingDecision {
    GIVE_UP_THIS_TRANSFER_ONLY,
    PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY,
    PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION,
    RETRY_AFTER_CONFIGURED_DELAY
}
