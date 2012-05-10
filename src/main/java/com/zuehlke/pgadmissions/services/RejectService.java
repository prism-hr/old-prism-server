package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class RejectService {

	private final ApplicationFormDAO applicationDAO;

	RejectService() {
		this(null);
	}

	@Autowired
	public RejectService(ApplicationFormDAO applicationDAO) {
		this.applicationDAO = applicationDAO;
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
		applicationDAO.save(application);
	}
}
