package com.zuehlke.pgadmissions.services.uclexport;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

/**
 * Callback interface for UCLExportService clients.
 * Allows for observing progress of submitted transfers.
 */
public interface TransferListener {
    void queued();
    void transferStarted();
    void webserviceCallCompleted();
    void attachmentsTransferStarted();
    void transferCompleted(String uclUserId, String uclBookingReferenceNumber);
    void transferFailed(ApplicationFormTransferError error);
}
