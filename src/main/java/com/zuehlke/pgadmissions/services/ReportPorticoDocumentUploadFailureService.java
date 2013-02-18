package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.mail.DataExportMailSender;

@Service
public class ReportPorticoDocumentUploadFailureService {

    private final DataExportMailSender dataExportMailSender;
    
    private final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;
    
    private final ApplicationFormTransferDAO applicationFormTransferDAO;
    
    private final Logger log = Logger.getLogger(ReportPorticoDocumentUploadFailureService.class);
    
    public ReportPorticoDocumentUploadFailureService() {
        this(null, null, null);
    }
    
    @Autowired
    public ReportPorticoDocumentUploadFailureService(DataExportMailSender dataExportMailSender,
            ApplicationFormTransferDAO applicationFormTransferDAO, ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO) {
        this.dataExportMailSender = dataExportMailSender;
        this.applicationFormTransferErrorDAO = applicationFormTransferErrorDAO;
        this.applicationFormTransferDAO = applicationFormTransferDAO;
    }
    
    @Transactional
    public void saveDocumentUploadError(final String bookingReference, final String errorCode, final String message) {
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getByReceivedBookingReferenceNumber(bookingReference);
        if (transfer != null) {
            ApplicationFormTransferError transferError = new ApplicationFormTransferError();
            transferError.setDiagnosticInfo(String.format("DocumentUploadError from Portico [errorCode=%s, message=%s]", errorCode, message));
            transferError.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY);
            transferError.setProblemClassification(ApplicationFormTransferErrorType.PORTICO_SFTP_DOCUMENT_HANDLING_PROBLEM);
            transferError.setRequestCopy(StringUtils.EMPTY);
            transferError.setResponseCopy(StringUtils.EMPTY);
            transferError.setTimepoint(new Date());
            transferError.setTransfer(transfer);
            applicationFormTransferErrorDAO.save(transferError);
        } else {
            log.warn(String.format("Couldn't find an existing transfer object for bookingReference: %s", bookingReference));
        }
    }
    
    public void sendErrorMessageToSuperAdministrators(final String message, final Exception exception) {
        this.dataExportMailSender.sendErrorMessage(message, exception);
    }

    public void sendErrorMessageToSuperAdministrators(final String message) {
        this.dataExportMailSender.sendErrorMessage(message);
    }
}
