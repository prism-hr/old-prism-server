package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

public class DeafListener implements TransferListener {

    @Override
    public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
    }

    @Override
    public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
    }

    @Override
    public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
    }

    @Override
    public void sftpTransferStarted(ApplicationForm form) {
    }

    @Override
    public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
    }

    @Override
    public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
    }
}
