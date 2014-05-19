package com.zuehlke.pgadmissions.services.exporters;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;

public interface TransferListener {
    
    void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form);
    
    void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form);
    
    void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form);
    
    void sftpTransferStarted(Application form);
    
    void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer);
    
    void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form);
    
}
