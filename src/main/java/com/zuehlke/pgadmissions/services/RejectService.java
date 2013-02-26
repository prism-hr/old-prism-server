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
import com.zuehlke.pgadmissions.services.exporters.UclExportService;
import com.zuehlke.pgadmissions.utils.EventFactory;

@Service
public class RejectService {

	private final ApplicationFormDAO applicationDao;
	private final RejectReasonDAO rejectDao;
	private final EventFactory eventFactory;
	private final UclExportService uclExportService;

	public RejectService() {
		this(null, null, null, null);
	}

	@Autowired
    public RejectService(ApplicationFormDAO applicationDAO, RejectReasonDAO rejectDao, EventFactory eventFactory,
            UclExportService exportService) {
		this.applicationDao = applicationDAO;
		this.rejectDao = rejectDao;
		this.eventFactory = eventFactory;
		this.uclExportService = exportService;
	}

	@Transactional(readOnly = true)
	public List<RejectReason> getAllRejectionReasons() {
		return rejectDao.getAllReasons();
	}

	@Transactional(readOnly = true)
	public RejectReason getRejectReasonById(Integer id) {
		return rejectDao.getRejectReasonById(id);
	}

	@Transactional
	public void moveApplicationToReject(ApplicationForm application, RegisteredUser approver, Rejection rejection) {
		if (rejection == null) {
			throw new IllegalArgumentException("rejection must be provided!");
		}
		if (approver == null) {
			throw new IllegalArgumentException("approver must not be null!");
		}
		if (!(application.getProgram().isApprover(approver) || approver.hasAdminRightsOnApplication(application))) {
			throw new IllegalArgumentException("approver is not an approver or administrator in the program of the application!");
		}
		application.setApprover(approver);
		application.setStatus(ApplicationFormStatus.REJECTED);		
		application.setRejection(rejection);
		application.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.REJECTED));
		applicationDao.save(application);
		
        // TODO: Enable when ready for production
        //uclExportService.sendToUCL(application);
	}
}
