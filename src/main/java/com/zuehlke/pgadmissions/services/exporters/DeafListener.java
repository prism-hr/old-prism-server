package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

public class DeafListener implements TransferListener {

    @Override
    public void queued() {
    }

    @Override
    public void transferStarted() {
    }

    @Override
    public void webserviceCallCompleted() {
    }

    @Override
    public void attachmentsSftpTransmissionStarted() {
    }

    @Override
    public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
    }

    @Override
    public void transferFailed(ApplicationFormTransferError error) {
    }

    @Override
    public void sshConnectionEstablished() {
    }

    @Override
    public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
    }
}
