package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;

public class DeafListener implements TransferListener {

    @Override
    public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
    }

    @Override
    public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
    }

    @Override
    public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
    }

    @Override
    public void sftpTransferStarted(Application form) {
    }

    @Override
    public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
    }

    @Override
    public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
    }
}
