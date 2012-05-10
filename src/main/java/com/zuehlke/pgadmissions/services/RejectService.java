package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class RejectService {

	private final ApplicationFormDAO applicationDao;
	private final RejectReasonDAO rejectDao;

	RejectService() {
		this(null, null);
	}

	@Autowired
	public RejectService(ApplicationFormDAO applicationDAO, RejectReasonDAO rejectDao) {
		this.applicationDao = applicationDAO;
		this.rejectDao = rejectDao;
	}

	@Transactional
	public void moveApplicationToReject(ApplicationForm application, RejectReason... reasons) {
		if (reasons == null || reasons.length == 0) {
			throw new IllegalArgumentException("no reasons for rejection specified!");
		}
		for (RejectReason rejectReason : reasons) {
			if( rejectReason == null) {
				throw new IllegalArgumentException("reasons for rejection is null!");
			}
			application.getRejectReasons().add(rejectReason);
		}
		application.setStatus(ApplicationFormStatus.REJECTED);
		applicationDao.save(application);
	}

	@Transactional(readOnly = true)
	public List<RejectReason> getAllRejectionReasons() {
		return rejectDao.getAllReasons();
	}
}
