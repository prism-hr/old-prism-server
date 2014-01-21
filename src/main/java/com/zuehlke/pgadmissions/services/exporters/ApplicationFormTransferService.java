package com.zuehlke.pgadmissions.services.exporters;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Service
public class ApplicationFormTransferService {
    
    private final ApplicationsService applicationsService;
    private final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;
    private final ApplicationFormTransferDAO applicationFormTransferDAO;
    
    public ApplicationFormTransferService() {
        this(null, null, null);
    }
    
    @Autowired
    public ApplicationFormTransferService(final ApplicationsService applicationsService, final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO,
            final ApplicationFormTransferDAO applicationFormTransferDAO) {
        this.applicationsService = applicationsService;
        this.applicationFormTransferErrorDAO = applicationFormTransferErrorDAO;
        this.applicationFormTransferDAO = applicationFormTransferDAO;
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
        applicationsService.transformUKCountriesAndDomiciles(form);
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
    
}