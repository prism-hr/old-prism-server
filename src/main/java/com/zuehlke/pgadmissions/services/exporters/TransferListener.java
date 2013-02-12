package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

public interface TransferListener {
    
    void webServiceCallStarted(SubmitAdmissionsApplicationRequest request);
    
    void webServiceCallCompleted(AdmissionsApplicationResponse response);
    
    void webServiceCallFailed(ApplicationFormTransferError error);
    
    void sftpTransferStarted();
    
    void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId);
    
    void sftpTransferFailed(ApplicationFormTransferError error);
    
}
