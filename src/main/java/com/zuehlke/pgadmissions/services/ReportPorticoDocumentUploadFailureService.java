package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class ReportPorticoDocumentUploadFailureService {

    private final Logger log = LoggerFactory.getLogger(ReportPorticoDocumentUploadFailureService.class);
    
    @Autowired
    private ApplicationTransferErrorDAO applicationFormTransferErrorDAO;
    
    @Autowired
    private ApplicationTransferDAO applicationFormTransferDAO;
    
    @Autowired
    private MailSendingService mailService;
    
    @Autowired
    private RoleService roleService;
    
    public void reportPorticoUploadError(final String bookingReference, final String errorCode, final String message) {
        ApplicationTransferError transferError = saveDocumentUploadError(bookingReference, errorCode, message);
        
        String errorMessage = String
                .format("Portico reported that there was an error uploading the documents for application %s [errorCode=%s, bookingReference=%s]: %s",
                        StringUtils.trimToEmpty(transferError.getTransfer().getApplicationForm().getApplicationNumber()),
                        StringUtils.trimToEmpty(errorCode), 
                        StringUtils.trimToEmpty(bookingReference),
                        StringUtils.trimToEmpty(message));
        
        log.warn(errorMessage);

        sendErrorMessageToSuperAdministrators(errorMessage, transferError.getTransfer().getApplicationForm());
    }

    private void sendErrorMessageToSuperAdministrators(final String message, final ApplicationForm application) {
        try {
            mailService.sendExportErrorMessage(roleService.getUsersInRole(roleService.getPrismSystem(), Authority.SYSTEM_ADMINISTRATOR), message, new Date(), application);
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }
    
    private ApplicationTransferError saveDocumentUploadError(final String bookingReference, final String errorCode, final String message) {
        ApplicationTransfer transfer = applicationFormTransferDAO.getByReceivedBookingReferenceNumber(bookingReference);
        if (transfer != null) {
            ApplicationTransferError transferError = new ApplicationTransferError();
            transferError.setDiagnosticInfo(String.format("DocumentUploadError from Portico [errorCode=%s, message=%s]", errorCode, message));
            transferError.setErrorHandlingStrategy(ApplicationTransferErrorHandlingDecision.GIVE_UP);
            transferError.setProblemClassification(ApplicationTransferErrorType.PORTICO_SFTP_DOCUMENT_HANDLING_PROBLEM);
            transferError.setRequestCopy(StringUtils.EMPTY);
            transferError.setResponseCopy(StringUtils.EMPTY);
            transferError.setTimepoint(new Date());
            transferError.setTransfer(transfer);
            applicationFormTransferErrorDAO.save(transferError);
            return transferError;
        } else {
            log.warn(String.format("Couldn't find an existing transfer object for bookingReference: %s", bookingReference));
        }
        return null;
    }
}
