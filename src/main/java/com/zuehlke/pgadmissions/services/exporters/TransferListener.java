package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;

public interface TransferListener {
    
    void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form);
    
    void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form);
    
    void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, ApplicationForm form);
    
    void sftpTransferStarted(ApplicationForm form);
    
    void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer);
    
    void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, ApplicationForm form);
    
}
