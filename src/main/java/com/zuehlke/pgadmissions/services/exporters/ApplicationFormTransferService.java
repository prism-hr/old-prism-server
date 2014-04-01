package com.zuehlke.pgadmissions.services.exporters;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.ApplicationTransferComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.services.WorkflowService;

@Service
public class ApplicationFormTransferService {

    private final ApplicationFormDAO applicationFormDAO;
    private final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;
    private final ApplicationFormTransferDAO applicationFormTransferDAO;
    private final WorkflowService applicationFormUserRoleService;
    private final CommentDAO commentDAO;
    private final UserDAO userDAO;

    public ApplicationFormTransferService() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public ApplicationFormTransferService(final ApplicationFormDAO applicationFormDAO, final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO,
            final ApplicationFormTransferDAO applicationFormTransferDAO, WorkflowService applicationFormUserRoleService,
            CommentDAO commentDAO, UserDAO userDAO) {
        this.applicationFormDAO = applicationFormDAO;
        this.applicationFormTransferErrorDAO = applicationFormTransferErrorDAO;
        this.applicationFormTransferDAO = applicationFormTransferDAO;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.commentDAO = commentDAO;
        this.userDAO = userDAO;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateTransferStatus(final ApplicationFormTransfer transfer, final ApplicationTransferStatus status) {
        transfer.setStatus(status);
        transfer.setTransferFinishTimepoint(new Date());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateTransferPorticoIds(final ApplicationFormTransfer transfer, final AdmissionsApplicationResponse response) {
        transfer.setUclUserIdReceived(response.getReference().getApplicantID());
        transfer.setUclBookingReferenceReceived(response.getReference().getApplicationID());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateApplicationFormPorticoIds(final ApplicationForm form, final AdmissionsApplicationResponse response) {
        form.setUclBookingReferenceNumber(response.getReference().getApplicationID());
        form.getApplicant().setUclUserId(response.getReference().getApplicantID());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ApplicationFormTransferError createTransferError(final ApplicationFormTransferErrorBuilder builder) {
        ApplicationFormTransferError transferError = builder.build();
        applicationFormTransferErrorDAO.save(transferError);
        return transferError;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ApplicationFormTransfer createOrReturnExistingApplicationFormTransfer(final ApplicationForm form) {
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getByApplicationForm(form);
        if (transfer == null) {
            ApplicationFormTransfer result = new ApplicationFormTransfer();
            result.setApplicationForm(form);
            result.setTransferStartTimepoint(new Date());
            result.setStatus(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL);
            applicationFormTransferDAO.save(result);
            return result;
        } else {
            transfer.setTransferStartTimepoint(new Date());
            transfer.setStatus(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL);
        }
        return transfer;
    }

    @Transactional(readOnly = true)
    public List<ApplicationFormTransfer> getAllTransfersWaitingToBeSentToPorticoOldestFirst() {
        return applicationFormTransferDAO.getAllTransfersWaitingToBeSentToPorticoOldestFirst();
    }

    @Transactional(readOnly = true)
    public List<Long> getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds() {
        return applicationFormTransferDAO.getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds();
    }

    @Transactional(readOnly = true)
    public ApplicationFormTransfer getById(final Long id) {
        return applicationFormTransferDAO.getById(id);
    }

    @Transactional(readOnly = true)
    public ApplicationFormTransfer getByApplicationForm(final ApplicationForm form) {
        return applicationFormTransferDAO.getByApplicationForm(form);
    }

    @Transactional
    public void processApplicationTransferError(TransferListener listener, ApplicationForm application, ApplicationFormTransfer transfer, Exception exception,
            ApplicationTransferStatus newStatus, String logMessage, ApplicationFormTransferErrorHandlingDecision handlingDecision,
            ApplicationFormTransferErrorType errorType, Logger log) throws ExportServiceException {
        ApplicationFormTransferError transferError = createTransferError(new ApplicationFormTransferErrorBuilder()
                .diagnosticInfo(exception).errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP)
                .problemClassification(ApplicationFormTransferErrorType.PRISM_EXCEPTION).transfer(transfer));
        applicationFormTransferErrorDAO.save(transferError);
        updateTransferStatus(transfer, newStatus);
        listener.webServiceCallFailed(exception, transferError, application);
        applicationFormUserRoleService.applicationExportFailed(application);
        application.setExported(false);
        applicationFormDAO.save(application);
        commentDAO.save(new ApplicationTransferComment(application, userDAO.getSuperadministrators().get(0), transferError));
        log.error(String.format(logMessage, application.getApplicationNumber()), exception);
        throw new ExportServiceException(String.format(logMessage, application.getApplicationNumber()), exception, transferError);
    }
    
    @Transactional
    public ApplicationFormTransferError getErrorById(Long id) {
        return applicationFormTransferDAO.getErrorById(id);
    }
    
    @Transactional
    public void requeueApplicationTransfer(ApplicationForm application) {
        applicationFormTransferDAO.requeueApplicationTransfer(application);
        applicationFormUserRoleService.applicationExportResent(application);
    }

}