package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.NotificationType.APPLICATION_MOVED_TO_APPROVED_NOTIFICATION;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RequestRestartComment;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ApprovalService {

	private final Logger log = LoggerFactory.getLogger(ApprovalService.class);

	private final ApplicationFormDAO applicationDAO;

    private final ApprovalRoundDAO approvalRoundDAO;

    private final StageDurationService stageDurationService;

    private final EventFactory eventFactory;

    private final CommentDAO commentDAO;

    private final ProgrammeDetailDAO programmeDetailDAO;

    private final UserService userService;

    private final PorticoQueueService approvedSenderService;

    private final SupervisorDAO supervisorDAO;

    private final MailSendingService mailSendingService;

    public ApprovalService() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ApprovalService(UserService userService, ApplicationFormDAO applicationDAO,
            ApprovalRoundDAO approvalRoundDAO, StageDurationService stageDurationService, EventFactory eventFactory,
            CommentDAO commentDAO, SupervisorDAO supervisorDAO, ProgrammeDetailDAO programmeDetailDAO,
            PorticoQueueService approvedSenderService, MailSendingService mailSendingService) {
        this.userService = userService;
        this.applicationDAO = applicationDAO;
        this.approvalRoundDAO = approvalRoundDAO;
        this.stageDurationService = stageDurationService;
        this.eventFactory = eventFactory;
        this.commentDAO = commentDAO;
        this.supervisorDAO = supervisorDAO;
        this.programmeDetailDAO = programmeDetailDAO;
        this.approvedSenderService = approvedSenderService;
        this.mailSendingService = mailSendingService;
    }

    public void confirmOrDeclineSupervision(ApplicationForm form, ConfirmSupervisionDTO confirmSupervisionDTO) {
        ApprovalRound approvalRound = form.getLatestApprovalRound();
        Supervisor supervisor = approvalRound.getPrimarySupervisor();
        Boolean confirmed = confirmSupervisionDTO.getConfirmedSupervision();

        supervisor.setConfirmedSupervision(confirmed);

        if (BooleanUtils.isTrue(confirmed)) {
            approvalRound.setProjectDescriptionAvailable(true);
            approvalRound.setProjectTitle(confirmSupervisionDTO.getProjectTitle());
            approvalRound.setProjectAbstract(confirmSupervisionDTO.getProjectAbstract());
            approvalRound.setRecommendedConditionsAvailable(confirmSupervisionDTO.getRecommendedConditionsAvailable());
            approvalRound.setRecommendedConditions(confirmSupervisionDTO.getRecommendedConditions());
            approvalRound.setRecommendedStartDate(confirmSupervisionDTO.getRecommendedStartDate());
            supervisor.setConfirmedSupervisionDate(new Date());
            mailSendingService.scheduleSupervisionConfirmedNotification(form);
        }

        if (BooleanUtils.isFalse(confirmed)) {
            supervisor.setDeclinedSupervisionReason(confirmSupervisionDTO.getDeclinedSupervisionReason());
            RequestRestartComment restartComment = createRequestRestartComment(form, supervisor);
            restartApprovalStage(form, supervisor.getUser(), restartComment);
        }

        SupervisionConfirmationComment supervisionConfirmationComment = createSupervisionConfirmationComment(confirmSupervisionDTO, form, supervisor);
        commentDAO.save(supervisionConfirmationComment);
    }

    private RequestRestartComment createRequestRestartComment(ApplicationForm form, Supervisor supervisor) {
        RequestRestartComment restartComment = new RequestRestartComment();
        restartComment.setApplication(form);
        restartComment.setDate(new Date());
        restartComment.setUser(supervisor.getUser());
        restartComment.setComment(String.format("%s %s was unable to confirm the supervision arrangements that were proposed.", supervisor.getUser()
                .getFirstName(), supervisor.getUser().getLastName()));
        return restartComment;
    }

    private SupervisionConfirmationComment createSupervisionConfirmationComment(ConfirmSupervisionDTO confirmSupervisionDTO, ApplicationForm application, Supervisor supervisor) {
        SupervisionConfirmationComment supervisionConfirmationComment = new SupervisionConfirmationComment();
        supervisionConfirmationComment.setApplication(application);
        supervisionConfirmationComment.setDate(new Date());
        supervisionConfirmationComment.setSupervisor(supervisor);
        supervisionConfirmationComment.setType(CommentType.SUPERVISION_CONFIRMATION);
        supervisionConfirmationComment.setUser(userService.getCurrentUser());
        supervisionConfirmationComment.setComment("");

        if (BooleanUtils.isTrue(confirmSupervisionDTO.getConfirmedSupervision())) {
            supervisionConfirmationComment.setProjectTitle(confirmSupervisionDTO.getProjectTitle());
            supervisionConfirmationComment.setProjectAbstract(confirmSupervisionDTO.getProjectAbstract());
            supervisionConfirmationComment.setRecommendedStartDate(confirmSupervisionDTO.getRecommendedStartDate());
            Boolean recommendedConditionsAvailable = confirmSupervisionDTO.getRecommendedConditionsAvailable();
            supervisionConfirmationComment.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
            if (BooleanUtils.isTrue(recommendedConditionsAvailable)) {
                supervisionConfirmationComment.setRecommendedConditions(confirmSupervisionDTO.getRecommendedConditions());
            } else {
                supervisionConfirmationComment.setRecommendedConditions(null);
            }
        }

        return supervisionConfirmationComment;
    }

    public void moveApplicationToApproval(ApplicationForm form, ApprovalRound approvalRound) {
        checkApplicationStatus(form);
        checkSendToPorticoStatus(form, approvalRound);
        copyLastNotifiedForRepeatSupervisors(form, approvalRound);
        form.setLatestApprovalRound(approvalRound);
        form.setPendingApprovalRestart(false);
        form.addNotificationRecord(new NotificationRecord(NotificationType.APPROVAL_REMINDER));
        
        approvalRound.setApplication(form);
        approvalRoundDAO.save(approvalRound);
        
        StageDuration approveStageDuration = stageDurationService.getByStatus(ApplicationFormStatus.APPROVAL);
        DateTime dueDate = DateUtils.addWorkingDaysInMinutes(new DateTime(), approveStageDuration.getDurationInMinutes());
        form.setDueDate(dueDate.toDate());
        
        form.getEvents().add(eventFactory.createEvent(approvalRound));
        
        boolean sendReferenceRequest = form.getStatus()==ApplicationFormStatus.VALIDATION;

        form.setStatus(ApplicationFormStatus.APPROVAL);
        form.setPendingApprovalRestart(false);
        resetNotificationRecords(form);

        applicationDAO.save(form);

        ApprovalComment approvalComment = new ApprovalComment();
        approvalComment.setApplication(form);
        approvalComment.setComment("");
        approvalComment.setType(CommentType.APPROVAL);
        approvalComment.setProjectAbstract(approvalRound.getProjectAbstract());
        approvalComment.setProjectDescriptionAvailable(approvalRound.getProjectDescriptionAvailable());
        approvalComment.setProjectTitle(approvalRound.getProjectTitle());
        approvalComment.setRecommendedConditions(approvalRound.getRecommendedConditions());
        approvalComment.setRecommendedConditionsAvailable(approvalRound.getRecommendedConditionsAvailable());
        approvalComment.setRecommendedStartDate(approvalRound.getRecommendedStartDate());
        approvalComment.setUser(userService.getCurrentUser());

        if (sendReferenceRequest) {
            mailSendingService.sendReferenceRequest(form.getReferees(), form);
        }
        commentDAO.save(approvalComment);
    }

    private void copyLastNotifiedForRepeatSupervisors(ApplicationForm form, ApprovalRound approvalRound) {
        ApprovalRound latestApprovalRound = form.getLatestApprovalRound();
        if (latestApprovalRound != null) {
            List<Supervisor> supervisors = latestApprovalRound.getSupervisors();
            for (Supervisor supervisor : supervisors) {
                List<Supervisor> newSupervisors = approvalRound.getSupervisors();
                for (Supervisor newSupervisor : newSupervisors) {
                    if (supervisor.getUser().getId().equals(newSupervisor.getUser().getId())) {
                        newSupervisor.setLastNotified(supervisor.getLastNotified());
                    }
                }
            }
        }
    }

    private void resetNotificationRecords(ApplicationForm form) {
        form.removeNotificationRecord(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION,
                NotificationType.APPROVAL_RESTART_REQUEST_REMINDER,
                NotificationType.APPROVAL_NOTIFICATION);
    }

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
        application.removeNotificationRecord(NotificationType.APPROVAL_REMINDER, NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION);
        restartApprovalStage(application, approver, comment);
    }

    private void restartApprovalStage(ApplicationForm application, RegisteredUser approver, Comment comment) {
        commentDAO.save(comment);
        application.setPendingApprovalRestart(true);
        application.setApproverRequestedRestart(approver);
        applicationDAO.save(application);
    }

    private void checkApplicationStatus(ApplicationForm form) {
        ApplicationFormStatus status = form.getStatus();
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

    private void checkSendToPorticoStatus(ApplicationForm form, ApprovalRound approvalRound) {
        boolean explanationProvided = StringUtils.isNotBlank(approvalRound.getMissingQualificationExplanation());
        if (!form.isCompleteForSendingToPortico(explanationProvided)) {
            throw new IllegalStateException("Send to portico data is not valid");
        }
    }

    public void save(ApprovalRound approvalRound) {
        approvalRoundDAO.save(approvalRound);
    }

    public boolean moveToApproved(ApplicationForm form) {
        if (ApplicationFormStatus.APPROVAL != form.getStatus()) {
            throw new IllegalStateException();
        }
        
        if (!form.isPrefferedStartDateWithinBounds()) {
            Date earliestPossibleStartDate = form.getEarliestPossibleStartDate();
            if (earliestPossibleStartDate == null) {
                return false;
            }
            form.getProgrammeDetails().setStartDate(earliestPossibleStartDate);
            programmeDetailDAO.save(form.getProgrammeDetails());
        }
        
        form.setStatus(ApplicationFormStatus.APPROVED);
        form.setApprover(userService.getCurrentUser());
        form.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.APPROVED));
        sendNotificationToApplicant(form);
        form.removeNotificationRecord(NotificationType.APPROVAL_REMINDER);
        applicationDAO.save(form);
        return true;
    }
    
    private void sendNotificationToApplicant(ApplicationForm form) {
    	try {
    		mailSendingService.sendApprovedNotification(form);
    		NotificationRecord notificationRecord = form.getNotificationForType(APPLICATION_MOVED_TO_APPROVED_NOTIFICATION);
			if (notificationRecord == null) {
				notificationRecord = new NotificationRecord(APPLICATION_MOVED_TO_APPROVED_NOTIFICATION);
				form.addNotificationRecord(notificationRecord);
			}
			notificationRecord.setDate(new Date());
    	}
    	catch (Exception e) {
    		log.warn("{}", e);
    	}
	}

	public void sendToPortico(ApplicationForm form) {
        approvedSenderService.sendToPortico(form);
    }
    
    public void addSupervisorInPreviousApprovalRound(ApplicationForm form, RegisteredUser newUser) {
        Supervisor supervisor = newSupervisor();
        supervisor.setUser(newUser);
        supervisorDAO.save(supervisor);
        ApprovalRound latestApprovalRound = form.getLatestApprovalRound();
        if (latestApprovalRound == null) {
            ApprovalRound approvalRound = newApprovalRound();
            approvalRound.getSupervisors().add(supervisor);
            approvalRound.setApplication(form);
            save(approvalRound);
            form.setLatestApprovalRound(approvalRound);
        } else {
            latestApprovalRound.getSupervisors().add(supervisor);
            save(latestApprovalRound);
        }
    }

    public Supervisor newSupervisor() {
        return new Supervisor();
    }

    public ApprovalRound newApprovalRound() {
        return new ApprovalRound();
    }
}
