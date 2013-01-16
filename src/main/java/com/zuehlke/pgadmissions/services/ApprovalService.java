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
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.exporters.UclExportService;
import com.zuehlke.pgadmissions.utils.EventFactory;

@Service
public class ApprovalService {

	private final ApplicationFormDAO applicationDAO;
	
	private final ApprovalRoundDAO approvalRoundDAO;
	
	private final StageDurationDAO stageDurationDAO;
	
	private final EventFactory eventFactory;
	
	private final CommentDAO commentDAO;

	private final ProgrammeDetailDAO programmeDetailDAO;
	
	private final UserService userService;
	
	private final UclExportService uclExportService;
	
	private final SupervisorDAO supervisorDAO;

	ApprovalService() {
		this(null, null, null, null, null, null, null, null, null);
	}

	@Autowired
    public ApprovalService(UserService userService, ApplicationFormDAO applicationDAO,
            ApprovalRoundDAO approvalRoundDAO, StageDurationDAO stageDurationDAO, EventFactory eventFactory,
            CommentDAO commentDAO, SupervisorDAO supervisorDAO, ProgrammeDetailDAO programmeDetailDAO,
            UclExportService uclExportService) {
		this.userService = userService;
		this.applicationDAO = applicationDAO;
		this.approvalRoundDAO = approvalRoundDAO;
		this.stageDurationDAO = stageDurationDAO;
		this.eventFactory = eventFactory;
		this.commentDAO = commentDAO;
		this.supervisorDAO = supervisorDAO;
		this.programmeDetailDAO = programmeDetailDAO;
		this.uclExportService = uclExportService;
	}

	@Transactional
	public void moveApplicationToApproval(ApplicationForm application, ApprovalRound approvalRound) {
		checkApplicationStatus(application);
		copyLastNotifiedForRepeatSupervisors(application, approvalRound);
		application.setLatestApprovalRound(approvalRound);
		
		approvalRound.setApplication(application);
		approvalRoundDAO.save(approvalRound);
		StageDuration approveStageDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.APPROVAL);
		application.setDueDate(DateUtils.addMinutes(new Date(), approveStageDuration.getDurationInMinutes()));
	
		application.getEvents().add(eventFactory.createEvent(approvalRound));
		
		application.setStatus(ApplicationFormStatus.APPROVAL);
		application.setPendingApprovalRestart(false);
		resetNotificationRecords(application);
		
		applicationDAO.save(application);
	}

	private void copyLastNotifiedForRepeatSupervisors(ApplicationForm application, ApprovalRound approvalRound) {
		ApprovalRound latestApprovalRound = application.getLatestApprovalRound();
		if(latestApprovalRound != null){
			List<Supervisor> supervisors = latestApprovalRound.getSupervisors();
			for (Supervisor supervisor : supervisors) {
				List<Supervisor> newSupervisors = approvalRound.getSupervisors();
				for (Supervisor newSupervisor : newSupervisors) {
					if(supervisor.getUser().getId().equals(newSupervisor.getUser().getId())){
						newSupervisor.setLastNotified(supervisor.getLastNotified());
					}
				}
			}
		}
	}

	private void resetNotificationRecords(ApplicationForm application) {
		NotificationRecord restartRequestNotification = application.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION);
		if(restartRequestNotification != null){
			application.removeNotificationRecord(restartRequestNotification);
		}
		NotificationRecord restartRequestReminder = application.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER);
		if(restartRequestReminder != null){
			application.removeNotificationRecord(restartRequestReminder);
		}
		NotificationRecord adminAndAproverNotificationRecord = application.getNotificationForType(NotificationType.APPROVAL_NOTIFICATION);
		if(adminAndAproverNotificationRecord != null){
			application.removeNotificationRecord(adminAndAproverNotificationRecord);
		}
	}

	@Transactional
	public void requestApprovalRestart(ApplicationForm application, RegisteredUser approver, Comment comment) {
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
		commentDAO.save(comment);
		application.setPendingApprovalRestart(true);
		application.setApproverRequestedRestart(approver);
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

	@Transactional		
	public boolean moveToApproved(ApplicationForm application) {
        if (ApplicationFormStatus.APPROVAL != application.getStatus()) {
            throw new IllegalStateException();
        }
        if (!application.isPrefferedStartDateWithinBounds()) {
            Date earliestPossibleStartDate = application.getEarliestPossibleStartDate();
            if (earliestPossibleStartDate == null) {
                return false;
            }
            application.getProgrammeDetails().setStartDate(earliestPossibleStartDate);
            programmeDetailDAO.save(application.getProgrammeDetails());
        }
        application.setStatus(ApplicationFormStatus.APPROVED);
        application.setApprover(userService.getCurrentUser());
        application.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.APPROVED));
        applicationDAO.save(application);
        
        // TODO: Enable when ready for production
        //uclExportService.sendToUCL(application);
        
        return true;
	}

	@Transactional
	public void addSupervisorInPreviousApprovalRound(ApplicationForm applicationForm, RegisteredUser newUser) {
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
