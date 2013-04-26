package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

public interface TransferListener {
    
    void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form);
    
    void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form);
    
    void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form);
    
    void sftpTransferStarted(ApplicationForm form);
    
    void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer);
    
    void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form);
    
}
