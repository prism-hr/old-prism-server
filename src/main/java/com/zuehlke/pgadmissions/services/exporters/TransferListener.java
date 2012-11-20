package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

/**
 * Callback interface for UCLExportService clients.
 * Allows for observing progress of submitted transfers.
 */
public interface TransferListener {
    void queued();
    void transferStarted();
    void webserviceCallCompleted();
    void sshConnectionEstablished();
    void attachmentsSftpTransmissionStarted();
    void transferCompleted(String uclUserId, String uclBookingReferenceNumber);
    void transferFailed(ApplicationFormTransferError error);
    void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request);
}
