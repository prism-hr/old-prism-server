package com.zuehlke.pgadmissions.services.uclexport;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

public interface TransferListener {
    void queued();
    void transferStarted();
    void webserviceCallCompleted();
    void attachmentsTransferStarted();
    void transferCompleted(String uclUserId, String uclBookingReferenceNumber);
    void transferFailed(ApplicationFormTransferError error);
}
