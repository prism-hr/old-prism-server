package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

public class DeafListener implements TransferListener {

    @Override
    public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
    }

    @Override
    public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
    }

    @Override
    public void webServiceCallFailed(ApplicationFormTransferError error) {
    }

    @Override
    public void sftpTransferStarted() {
    }

    @Override
    public void sftpTransferCompleted(String zipFilename, String applicantId, String bookingReferenceId) {
    }

    @Override
    public void sftpTransferFailed(ApplicationFormTransferError error) {
    }
}
