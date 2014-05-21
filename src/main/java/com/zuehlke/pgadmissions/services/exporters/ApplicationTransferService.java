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
import com.zuehlke.pgadmissions.dao.ApplicationTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferComment;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferState;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.services.WorkflowService;

@Service
public class ApplicationTransferService {

    private final ApplicationFormDAO applicationFormDAO;
    private final ApplicationTransferErrorDAO applicationFormTransferErrorDAO;
    private final ApplicationTransferDAO applicationFormTransferDAO;
    private final WorkflowService applicationFormUserRoleService;
    private final CommentDAO commentDAO;
    private final UserDAO userDAO;

    public ApplicationTransferService() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public ApplicationTransferService(final ApplicationFormDAO applicationFormDAO, final ApplicationTransferErrorDAO applicationFormTransferErrorDAO,
            final ApplicationTransferDAO applicationFormTransferDAO, WorkflowService applicationFormUserRoleService,
            CommentDAO commentDAO, UserDAO userDAO) {
        this.applicationFormDAO = applicationFormDAO;
        this.applicationFormTransferErrorDAO = applicationFormTransferErrorDAO;
        this.applicationFormTransferDAO = applicationFormTransferDAO;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.commentDAO = commentDAO;
        this.userDAO = userDAO;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateTransferStatus(final ApplicationTransfer transfer, final ApplicationTransferState status) {
        transfer.setState(status);
        transfer.setEndedTimestamp(new Date());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateTransferPorticoIds(final ApplicationTransfer transfer, final AdmissionsApplicationResponse response) {
        transfer.setExternalApplicantReference(response.getReference().getApplicantID());
        transfer.setExternalTransferReference(response.getReference().getApplicationID());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateApplicationFormPorticoIds(final Application form, final AdmissionsApplicationResponse response) {
        form.setUclBookingReferenceNumber(response.getReference().getApplicationID());
        // FIXME update user with his new UCL ID
//        form.getApplicant().setUclUserId(response.getReference().getApplicantID());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ApplicationTransferError createTransferError(final ApplicationTransferErrorBuilder builder) {
        ApplicationTransferError transferError = builder.build();
        applicationFormTransferErrorDAO.save(transferError);
        return transferError;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ApplicationTransfer createOrReturnExistingApplicationFormTransfer(final Application form) {
        ApplicationTransfer transfer = form.getTransfer();
        if (transfer == null) {
            ApplicationTransfer result = new ApplicationTransfer();
            form.setTransfer(result);
            result.setApplicationForm(form);
            result.setBeganTimestamp(new Date());
            result.setState(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL);
            applicationFormTransferDAO.save(result);
            return result;
        } else {
            transfer.setBeganTimestamp(new Date());
            transfer.setState(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL);
        }
        return transfer;
    }

    @Transactional(readOnly = true)
    public List<ApplicationTransfer> getAllTransfersWaitingToBeSentToPorticoOldestFirst() {
        return applicationFormTransferDAO.getAllTransfersWaitingToBeSentToPorticoOldestFirst();
    }

    @Transactional(readOnly = true)
    public List<Long> getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds() {
        return applicationFormTransferDAO.getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds();
    }

    @Transactional(readOnly = true)
    public ApplicationTransfer getById(final Long id) {
        return applicationFormTransferDAO.getById(id);
    }

    @Transactional
    public void processApplicationTransferError(TransferListener listener, Application application, ApplicationTransfer transfer, Exception exception,
            ApplicationTransferState newStatus, String logMessage, ApplicationTransferErrorHandlingDecision handlingDecision,
            ApplicationTransferErrorType errorType, Logger log) throws ExportServiceException {
        ApplicationTransferError transferError = createTransferError(new ApplicationTransferErrorBuilder()
                .diagnosticInfo(exception).errorHandlingStrategy(ApplicationTransferErrorHandlingDecision.GIVE_UP)
                .problemClassification(ApplicationTransferErrorType.PRISM_EXCEPTION).transfer(transfer));
        applicationFormTransferErrorDAO.save(transferError);
        updateTransferStatus(transfer, newStatus);
        listener.webServiceCallFailed(exception, transferError, application);
        applicationFormUserRoleService.applicationExportFailed(application);
        applicationFormDAO.save(application);
        commentDAO.save(new ApplicationTransferComment(application, userDAO.getSuperadministrators().get(0), transferError));
        log.error(String.format(logMessage, application.getApplicationNumber()), exception);
        throw new ExportServiceException(String.format(logMessage, application.getApplicationNumber()), exception, transferError);
    }
    
    @Transactional
    public ApplicationTransferError getErrorById(Long id) {
        return applicationFormTransferDAO.getErrorById(id);
    }
    
    @Transactional
    public void requeueApplicationTransfer(Application application) {
        applicationFormTransferDAO.requeueApplicationTransfer(application);
        applicationFormUserRoleService.applicationExportResent(application);
    }

}