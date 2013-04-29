package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class ReportPorticoDocumentUploadFailureService {

    
    private final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;
    
    private final ApplicationFormTransferDAO applicationFormTransferDAO;
    
    private final Logger log = LoggerFactory.getLogger(ReportPorticoDocumentUploadFailureService.class);
    
    private final MailSendingService mailService;
    
    private final UserService userService;
    
    public ReportPorticoDocumentUploadFailureService() {
        this(null, null, null, null);
    }
    
    @Autowired
    public ReportPorticoDocumentUploadFailureService(
            ApplicationFormTransferDAO applicationFormTransferDAO, 
            ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO,
            final MailSendingService mailService, final UserService userService) {
        this.applicationFormTransferErrorDAO = applicationFormTransferErrorDAO;
        this.applicationFormTransferDAO = applicationFormTransferDAO;
		this.mailService = mailService;
		this.userService = userService;
    }
    
    public void reportPorticoUploadError(final String bookingReference, final String errorCode, final String message) {
        ApplicationFormTransferError transferError = saveDocumentUploadError(bookingReference, errorCode, message);
        
        String errorMessage = String
                .format("Portico reported that there was an error uploading the documents for application %s [errorCode=%s, bookingReference=%s]: %s",
                        StringUtils.trimToEmpty(transferError.getTransfer().getApplicationForm().getApplicationNumber()),
                        StringUtils.trimToEmpty(errorCode), 
                        StringUtils.trimToEmpty(bookingReference),
                        StringUtils.trimToEmpty(message));
        
        log.warn(errorMessage);

        sendErrorMessageToSuperAdministrators(errorMessage);
    }

    private void sendErrorMessageToSuperAdministrators(final String message) {
        try {
            mailService.sendExportErrorMessage(userService.getUsersInRole(Authority.SUPERADMINISTRATOR), message, new Date());
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }
    
    private ApplicationFormTransferError saveDocumentUploadError(final String bookingReference, final String errorCode, final String message) {
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getByReceivedBookingReferenceNumber(bookingReference);
        if (transfer != null) {
            ApplicationFormTransferError transferError = new ApplicationFormTransferError();
            transferError.setDiagnosticInfo(String.format("DocumentUploadError from Portico [errorCode=%s, message=%s]", errorCode, message));
            transferError.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP);
            transferError.setProblemClassification(ApplicationFormTransferErrorType.PORTICO_SFTP_DOCUMENT_HANDLING_PROBLEM);
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
