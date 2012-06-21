package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
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
	private final EncryptionHelper encryptionHelper;
	private final UserService userService;
	private final DocumentDAO documentDAO;
	private final SupervisorDAO supervisorDAO;

	ApprovalService() {
		this(null, null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public ApprovalService(UserService userService, ApplicationFormDAO applicationDAO, ApprovalRoundDAO approvalRoundDAO, StageDurationDAO stageDurationDAO, MailService mailService,
			EventFactory eventFactory, CommentDAO commentDAO, DocumentDAO documentDAO, CommentFactory commentFactory, EncryptionHelper encryptionHelper, SupervisorDAO supervisorDAO) {

		this.userService = userService;
		this.applicationDAO = applicationDAO;
		this.approvalRoundDAO = approvalRoundDAO;
		this.stageDurationDAO = stageDurationDAO;
		this.mailService = mailService;
		this.eventFactory = eventFactory;
		this.commentDAO = commentDAO;
		this.documentDAO = documentDAO;
		this.commentFactory = commentFactory;
		this.encryptionHelper = encryptionHelper;
		this.supervisorDAO = supervisorDAO;

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
	public void moveApplicationToApproval(ApplicationForm application) {
		StageDuration approveStageDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.APPROVAL);
		application.setDueDate(DateUtils.addMinutes(new Date(), approveStageDuration.getDurationInMinutes()));
		application.setStatus(ApplicationFormStatus.APPROVAL);
		applicationDAO.save(application);

	}
	@Transactional		
	public void moveToApproved(ApplicationForm application, String strComment, List<String> documentIds) {
		if(ApplicationFormStatus.APPROVAL != application.getStatus()){
			throw new IllegalStateException();
		}
		application.setStatus(ApplicationFormStatus.APPROVED);
		application.setApprover(userService.getCurrentUser());
		application.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.APPROVED));
		applicationDAO.save(application);
		
		Comment approvalComment = commentFactory.createComment(application, userService.getCurrentUser(), strComment, CommentType.APPROVAL, ApplicationFormStatus.APPROVED);
		if(documentIds != null){
			for (String encryptedId : documentIds) {
				approvalComment.getDocuments().add(documentDAO.getDocumentbyId(encryptionHelper.decryptToInteger(encryptedId)));
			}
		}
		commentDAO.save(approvalComment);
		
	}
	@Transactional
	public void addSupervisorInPreviousReviewRound(ApplicationForm applicationForm, RegisteredUser newUser) {
		Supervisor supervisor = newSupervisor();
		supervisor.setUser(newUser);
		supervisorDAO.save(supervisor);
		ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();
		if (latestApprovalRound == null){
			ApprovalRound approvalRound = newApprovalRound();
			approvalRound.getSupervisors().add(supervisor);
			approvalRound.setApplication(applicationForm);
			save(approvalRound);
			applicationForm.setLatestApprovalRound(approvalRound);
		}
		else{
			latestApprovalRound.getSupervisors().add(supervisor);
			save(latestApprovalRound);
		}
		
	}
	
	public Supervisor newSupervisor() {
		return new Supervisor();
	}

	public ApprovalRound newApprovalRound() {
		ApprovalRound approvalRound = new ApprovalRound();
		return approvalRound;
	}

}
