package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class ApprovalService {

	private final ApplicationFormDAO applicationDAO;
	private final ApprovalRoundDAO approvalRoundDAO;
	private final StageDurationDAO stageDurationDAO;

	ApprovalService() {
		this(null, null, null);
	}

	@Autowired
	public ApprovalService(ApplicationFormDAO applicationDAO, ApprovalRoundDAO approvalRoundDAO, StageDurationDAO stageDurationDAO) {

		this.applicationDAO = applicationDAO;
		this.approvalRoundDAO = approvalRoundDAO;
		this.stageDurationDAO = stageDurationDAO;

	}

	@Transactional
	public void moveApplicationToApproval(ApplicationForm application, ApprovalRound approvalRound) {
		checkApplicationStatus(application);
		application.setLatestApprovalRound(approvalRound);
		approvalRound.setApplication(application);
		approvalRoundDAO.save(approvalRound);
		StageDuration approveStageDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.APPROVAL);
		application.setDueDate(DateUtils.addMinutes(new Date(), approveStageDuration.getDurationInMinutes()));
	

		application.setStatus(ApplicationFormStatus.APPROVAL);
		applicationDAO.save(application);
	}

	private void checkApplicationStatus(ApplicationForm application) {
		ApplicationFormStatus status = application.getStatus();
		switch (status) {
		case VALIDATION:
		case REVIEW:
		case INTERVIEW:
		case APPROVAL:
			break;
		default:
			throw new IllegalStateException(String.format("Application in invalid status: '%s'!", status));
		}
	}

	@Transactional
	public void save(ApprovalRound approvalRound) {
		approvalRoundDAO.save(approvalRound);
	}

}
