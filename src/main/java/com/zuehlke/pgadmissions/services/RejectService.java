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

	/*
	 * @Transactional public void moveApplicationToReject(ApplicationForm
	 * application, RegisteredUser approver, Collection<RejectReason> reasons) {
	 * if (reasons == null || reasons.isEmpty()) { throw new
	 * IllegalArgumentException("no reasons for rejection specified!"); } for
	 * (RejectReason rejectReason : reasons) { if (rejectReason == null) { throw
	 * new IllegalArgumentException("reasons for rejection is null!"); }
	 * application.getRejectReasons().add(rejectReason); } if (approver == null)
	 * { throw new IllegalArgumentException("approver must not be null!"); } if
	 * (!(application.getProgram().isApprover(approver) ||
	 * application.getProgram().isAdministrator(approver) )) { throw new
	 * IllegalArgumentException
	 * ("approver is not an approver in the program of the application!"); }
	 * application.setApprover(approver);
	 * application.setStatus(ApplicationFormStatus.REJECTED);
	 * applicationDao.save(application); }
	 */

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
		if (!(application.getProgram().isApprover(approver) || application.getProgram().isAdministrator(approver))) {
			throw new IllegalArgumentException("approver is not an approver or administrator in the program of the application!");
		}
		application.setApprover(approver);
		application.setStatus(ApplicationFormStatus.REJECTED);
		application.setRejection(rejection);
		applicationDao.save(application);

	}

}
