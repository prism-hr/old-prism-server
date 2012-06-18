package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.EventFactory;

@Service
public class ApprovalService {

	private final ApplicationFormDAO applicationDAO;
	private final ApprovalRoundDAO approvalRoundDAO;
	private final StageDurationDAO stageDurationDAO;
	private final MailService mailService;
	private final EventFactory eventFactory;
	private final CommentDAO commentDAO;
	private final CommentFactory commentFactory;

	ApprovalService() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public ApprovalService(ApplicationFormDAO applicationDAO, ApprovalRoundDAO approvalRoundDAO, StageDurationDAO stageDurationDAO, MailService mailService,
			EventFactory eventFactory, CommentDAO commentDAO, CommentFactory commentFactory) {

		this.applicationDAO = applicationDAO;
		this.approvalRoundDAO = approvalRoundDAO;
		this.stageDurationDAO = stageDurationDAO;
		this.mailService = mailService;
		this.eventFactory = eventFactory;
		this.commentDAO = commentDAO;
		this.commentFactory = commentFactory;

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
		application.getEvents().add(eventFactory.createEvent(approvalRound));
		applicationDAO.save(application);
	}

	@Transactional
	public void requestApprovalRestart(ApplicationForm application, RegisteredUser approver) {
		if (!approver.isInRole(Authority.APPROVER)) {
			throw new IllegalArgumentException(String.format("User %s is not an approver!", approver.getUsername()));
		}
		if (!approver.isInRoleInProgram(Authority.APPROVER, application.getProgram())) {
			throw new IllegalArgumentException(String.format("User %s is not an approver in program %s!",//
					approver.getUsername(), application.getProgram().getTitle()));
		}
		if (ApplicationFormStatus.APPROVAL != application.getStatus()) {
			throw new IllegalArgumentException(String.format("Application %s is not in state APPROVAL!", application.getApplicationNumber()));
		}

		mailService.sendRequestRestartApproval(application, approver);
		commentDAO.save(commentFactory.createComment(application, approver, "Requested re-start of approval phase", CommentType.GENERIC, null));

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

	@Transactional
	public void moveApplicationToApproved(ApplicationForm application) {
		StageDuration approveStageDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.APPROVAL);
		application.setDueDate(DateUtils.addMinutes(new Date(), approveStageDuration.getDurationInMinutes()));
		application.setStatus(ApplicationFormStatus.APPROVAL);
		applicationDAO.save(application);
	}

}
