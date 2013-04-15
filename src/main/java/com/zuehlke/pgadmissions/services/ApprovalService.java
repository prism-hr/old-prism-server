package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
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
import com.zuehlke.pgadmissions.jms.PorticoQueueService;
import com.zuehlke.pgadmissions.mail.refactor.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ApprovalService {

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

    public void confirmSupervision(ApplicationForm application, ConfirmSupervisionDTO confirmSupervisionDTO) {
        ApprovalRound approvalRound = application.getLatestApprovalRound();
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
            mailSendingService.scheduleSupervisionConfirmedNotification(application);
        }

        if (BooleanUtils.isFalse(confirmed)) {
            supervisor.setDeclinedSupervisionReason(confirmSupervisionDTO.getDeclinedSupervisionReason());
            RequestRestartComment restartComment = createRequestRestartComment(application, supervisor);
            restartApprovalStage(application, supervisor.getUser(), restartComment);
        }

        SupervisionConfirmationComment supervisionConfirmationComment = createSupervisionConfirmationComment(confirmSupervisionDTO, application, supervisor);
        commentDAO.save(supervisionConfirmationComment);
    }

    private RequestRestartComment createRequestRestartComment(ApplicationForm application, Supervisor supervisor) {
        RequestRestartComment restartComment = new RequestRestartComment();
        restartComment.setApplication(application);
        restartComment.setDate(new Date());
        restartComment.setUser(supervisor.getUser());
        restartComment.setComment(String.format("%s %s was unable to confirm the supervision arrangements that were proposed.", supervisor.getUser()
                .getFirstName(), supervisor.getUser().getLastName()));
        return restartComment;
    }

    private SupervisionConfirmationComment createSupervisionConfirmationComment(ConfirmSupervisionDTO confirmSupervisionDTO, ApplicationForm application,
            Supervisor supervisor) {
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

    public void moveApplicationToApproval(ApplicationForm application, ApprovalRound approvalRound) {
        checkApplicationStatus(application);
        checkSendToPorticoStatus(application, approvalRound);
        copyLastNotifiedForRepeatSupervisors(application, approvalRound);
        application.setLatestApprovalRound(approvalRound);

        approvalRound.setApplication(application);
        approvalRoundDAO.save(approvalRound);
        
        StageDuration approveStageDuration = stageDurationService.getByStatus(ApplicationFormStatus.APPROVAL);
        DateTime dueDate = DateUtils.addWorkingDaysInMinutes(new DateTime(), approveStageDuration.getDurationInMinutes());
        application.setDueDate(dueDate.toDate());
        
        application.getEvents().add(eventFactory.createEvent(approvalRound));

        application.setStatus(ApplicationFormStatus.APPROVAL);
        application.setPendingApprovalRestart(false);
        resetNotificationRecords(application);

        applicationDAO.save(application);

        ApprovalComment approvalComment = new ApprovalComment();
        approvalComment.setApplication(application);
        approvalComment.setComment("");
        approvalComment.setType(CommentType.APPROVAL);
        approvalComment.setProjectAbstract(approvalRound.getProjectAbstract());
        approvalComment.setProjectDescriptionAvailable(approvalRound.getProjectDescriptionAvailable());
        approvalComment.setProjectTitle(approvalRound.getProjectTitle());
        approvalComment.setRecommendedConditions(approvalRound.getRecommendedConditions());
        approvalComment.setRecommendedConditionsAvailable(approvalRound.getRecommendedConditionsAvailable());
        approvalComment.setRecommendedStartDate(approvalRound.getRecommendedStartDate());
        approvalComment.setUser(userService.getCurrentUser());

        commentDAO.save(approvalComment);
    }

    private void copyLastNotifiedForRepeatSupervisors(ApplicationForm application, ApprovalRound approvalRound) {
        ApprovalRound latestApprovalRound = application.getLatestApprovalRound();
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

    private void resetNotificationRecords(ApplicationForm application) {
        NotificationRecord restartRequestNotification = application.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION);
        if (restartRequestNotification != null) {
            application.removeNotificationRecord(restartRequestNotification);
        }
        NotificationRecord restartRequestReminder = application.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER);
        if (restartRequestReminder != null) {
            application.removeNotificationRecord(restartRequestReminder);
        }
        NotificationRecord adminAndAproverNotificationRecord = application.getNotificationForType(NotificationType.APPROVAL_NOTIFICATION);
        if (adminAndAproverNotificationRecord != null) {
            application.removeNotificationRecord(adminAndAproverNotificationRecord);
        }
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
        restartApprovalStage(application, approver, comment);
    }

    private void restartApprovalStage(ApplicationForm application, RegisteredUser approver, Comment comment) {
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

    private void checkSendToPorticoStatus(ApplicationForm application, ApprovalRound approvalRound) {
        boolean explanationProvided = !StringUtils.isBlank(approvalRound.getMissingQualificationExplanation());
        if (!application.isCompleteForSendingToPortico(explanationProvided)) {
            throw new IllegalStateException("Send to portico data is not valid");
        }
    }

    public void save(ApprovalRound approvalRound) {
        approvalRoundDAO.save(approvalRound);
    }

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
        return true;
    }
    
    public void sendToPortico(ApplicationForm application) {
        approvedSenderService.sendToPortico(application);
    }
    
    public void addSupervisorInPreviousApprovalRound(ApplicationForm applicationForm, RegisteredUser newUser) {
        Supervisor supervisor = newSupervisor();
        supervisor.setUser(newUser);
        supervisorDAO.save(supervisor);
        ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();
        if (latestApprovalRound == null) {
            ApprovalRound approvalRound = newApprovalRound();
            approvalRound.getSupervisors().add(supervisor);
            approvalRound.setApplication(applicationForm);
            save(approvalRound);
            applicationForm.setLatestApprovalRound(approvalRound);
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
