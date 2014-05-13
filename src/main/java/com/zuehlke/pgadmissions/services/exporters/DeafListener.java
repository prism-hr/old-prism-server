package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;

public class DeafListener implements TransferListener {

    @Override
    public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
    }

    @Override
    public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
    }

    @Override
    public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, ApplicationForm form) {
    }

    @Override
    public void sftpTransferStarted(ApplicationForm form) {
    }

    @Override
    public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
    }

    @Override
    public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, ApplicationForm form) {
    }
}
