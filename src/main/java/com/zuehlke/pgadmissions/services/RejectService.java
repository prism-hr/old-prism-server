package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.jms.PorticoQueueService;

@Service
@Transactional
public class RejectService {

	private final ApplicationFormDAO applicationDao;
	
	private final RejectReasonDAO rejectDao;
	
	private final EventFactory eventFactory;
	
	private final PorticoQueueService porticoQueueService;

	public RejectService() {
		this(null, null, null, null);
	}

	@Autowired
    public RejectService(ApplicationFormDAO applicationDAO, RejectReasonDAO rejectDao, EventFactory eventFactory, PorticoQueueService rejectedSenderService) {
		this.applicationDao = applicationDAO;
		this.rejectDao = rejectDao;
		this.eventFactory = eventFactory;
		this.porticoQueueService = rejectedSenderService;
	}

	public List<RejectReason> getAllRejectionReasons() {
		return rejectDao.getAllReasons();
	}

	public RejectReason getRejectReasonById(Integer id) {
		return rejectDao.getRejectReasonById(id);
	}

	public void moveApplicationToReject(final ApplicationForm form, final RegisteredUser approver, final Rejection rejection) {
		if (rejection == null) {
			throw new IllegalArgumentException("rejection must be provided!");
		}
		if (approver == null) {
			throw new IllegalArgumentException("approver must not be null!");
		}
		if (!(form.getProgram().isApprover(approver) || approver.hasAdminRightsOnApplication(form))) {
			throw new IllegalArgumentException("approver is not an approver or administrator in the program of the application!");
		}
		form.setApprover(approver);
		form.setStatus(ApplicationFormStatus.REJECTED);		
		form.setRejection(rejection);
		form.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.REJECTED));
		applicationDao.save(form);
	}
	
	public void sendToPortico(ApplicationForm form) {
	    // TODO: Enable when ready for production
	    porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);	    
	}
}
