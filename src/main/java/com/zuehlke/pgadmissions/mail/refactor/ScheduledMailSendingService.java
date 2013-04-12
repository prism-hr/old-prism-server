package com.zuehlke.pgadmissions.mail.refactor;

import java.util.Date;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.NotificationRecordDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ScheduledMailSendingService extends AbstractScheduledMailSendingService {

    private final Logger log = LoggerFactory.getLogger(ScheduledMailSendingService.class);
    
    private final NotificationRecordDAO notificationRecordDAO;
    
    private final CommentDAO commentDAO;

    private final SupervisorDAO supervisorDAO;
    
    private class UpdateDigestNotificationClosure implements Closure {
        private final DigestNotificationType type;
        public UpdateDigestNotificationClosure(final DigestNotificationType type) {
            this.type = type;
        }
        
        @Override
        public void execute(final Object input) {
            userService.setNeedsDailyDigestNotification((RegisteredUser) input, type);
        }
    }
    
    @Autowired
    public ScheduledMailSendingService(
            final TemplateAwareMailSender mailSender, 
            final UserService userService,
            final ApplicationFormDAO applicationFormDAO,
            final NotificationRecordDAO notificationRecordDAO,
            final CommentDAO commentDAO,
            final SupervisorDAO supervisorDAO) {
        super(mailSender, userService, applicationFormDAO);
        this.notificationRecordDAO = notificationRecordDAO;
        this.commentDAO = commentDAO;
        this.supervisorDAO = supervisorDAO;
    }
    
    public ScheduledMailSendingService() {
        this(null, null, null, null, null, null);
    }
    
    @Scheduled(cron = "${email.digest.cron}")
    public void run() {
        scheduleApprovalRequest();
        scheduleApprovalReminder();
        scheduleInterviewFeedbackEvaluationRequest();
        scheduleInterviewFeedbackEvaluationReminder();
        scheduleReviewReminder();
        scheduleReviewRequest();
        scheduleApplicationConfirmationToAdministrator();
        scheduleUpdatedApplicationConfirmation();
        scheduleApplicationValidationRequest();
        scheduleApplicationValidationReminder();
        scheduleRestartApprovalRequest();
        scheduleRestartApprovalReminder();
        scheduleApprovalConfirmation();
        scheduleInterviewAdministrationReminder();
        scheduleInterviewAdministrationRequest();
        scheduleInterviewFeedbackConfirmation();
        scheduleInterviewFeedbackRequest();
        scheduleInterviewFeedbackReminder();
        scheduleRejectionConfirmationToAdministrator();
        scheduleReviewSubmittedConfirmation();
        scheduleReviewEvaluationRequest();
        scheduleReviewEvaluationReminder();
        scheduleConfirmSupervisionRequest();
        scheduleConfirmSupervisionReminder();
        
        sendDigestsToUsers();
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void sendDigestsToUsers() {
        for (Integer userId : userService.getAllUsersInNeedOfADigestNotification()) {
            RegisteredUser user = userService.getUser(userId);
            PrismEmailMessageBuilder emailMessage = new PrismEmailMessageBuilder().to(user).subjectCode("Prism Digest Notification").emailTemplate(user.getDigestNotificationType().toString());
            try {
                mailSender.sendEmail(emailMessage.build());
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        userService.resetDigestNotificationsForAllUsers();
    }
    
    /**
     * Description:
     * 
     * <p>
     * 
     * TO:
     * <p>
     * CC:
     * <p>
     * BCC:
     * <p>
     * Subject: 
     * <p>
     * Template Name:
     * <p> 
     * Sending Conditions: 
     * <p>
     * Sending Strategy: DIGEST
     */
    public void scheduleApprovalRequest() {
       
    }
    
    // DIGEST FORCED
    public void scheduleApprovalReminder() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueUserReminder(NotificationType.APPROVAL_REMINDER, ApplicationFormStatus.APPROVAL), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_REMINDER);
            }
        });
    }
    
    // DIGEST
    public void scheduleInterviewFeedbackEvaluationRequest() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
            }
        });
    }
    
    // DIGEST FORCED
    public void scheduleInterviewFeedbackEvaluationReminder() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
            }
        });
    }
    
    // DIGEST FORCED
    public void scheduleReviewReminder() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REMINDER);
            }
        });
    }
    
    // DIGEST
    public void scheduleReviewRequest() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REMINDER);
            }
        });
    }
    
    // IMMEDIATELY
    public void sendApplicationConfirmationToApplicant() {
    }
    
    // DIGEST (submitConfirmationToAdmin)
    public void scheduleApplicationConfirmationToAdministrator() {
        // DUPLICATE see scheduleApplicationValidationRequest
    }
    
    // DIGEST
    public void scheduleUpdatedApplicationConfirmation() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueUpdateNotification(), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.UPDATED_NOTIFICATION);
            }
        });
    }

    // DIGEST
    public void scheduleApplicationValidationRequest() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.UPDATED_NOTIFICATION, ApplicationFormStatus.VALIDATION), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.UPDATED_NOTIFICATION);
            }
        });
    }
    
    // DIGEST FORCED
    public void scheduleApplicationValidationReminder() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueUserReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.VALIDATION_REMINDER);
            }
        });
    }
    
    // IMMEDIATELY
    public void sendApplicationWithdrawnConfirmation() {
    }

    // DIGEST
    public void scheduleRestartApprovalRequest() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueApprovalRequestNotification(), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION);
            }
        });
    }
    
    // DIGEST FORCED
    public void scheduleRestartApprovalReminder() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationDueApprovalRestartRequestReminder(), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_RESTART_REQUEST_REMINDER);
            }
        });
    }
    
    // DIGEST (schedule to Admins)
    public void scheduleApprovalConfirmation() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueApprovalNotifications(), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_NOTIFICATION);
            }
        });
    }

    // IMMEDIATELY
    public void sendDataExportError() {
    }

    // IMMEDIATELY
    public void sendDataImportError() {
    }
    
    // DIGEST
    public void scheduleInterviewAdministrationReminder() {
        DateTime yesterday = new DateTime().minusDays(1);
        CollectionUtils.forAllDo(notificationRecordDAO.getNotificationsWithTimeStampGreaterThan(yesterday.toDate() , NotificationType.INTERVIEW_ADMINISTRATION_REMINDER), new Closure() {
            @Override
            public void execute(final Object input) {
                NotificationRecord notificationRecord = (NotificationRecord) input;
                notificationRecord.setDate(new Date());
                RegisteredUser delegate = notificationRecord.getUser();
                delegate.setDigestNotificationType(DigestNotificationType.DIGEST);
            }
        });
    }
    
    // DIGEST (scheduling, etc.)
    public void scheduleInterviewAdministrationRequest() {
    }
    
    // DIGEST
    public void scheduleInterviewFeedbackConfirmation() {
//        NOT SURE
//        CollectionUtils.forAllDo(commentDAO.getInterviewCommentsDueNotification(), new Closure() {
//            @Override
//            public void execute(final Object input) {
//                InterviewComment comment = (InterviewComment) input;
//                RegisteredUser user = comment.getUser();
//                comment.setAdminsNotified(true);
//            }
//        });
    }
    
    // IMMEDIATELY 
    public void sendInterviewConfirmationToInterviewer() {
    }
    
    // IMMEDIATELY 
    public void sendInterviewConfirmationToApplicant() {
    }

    // DIGEST
    public void scheduleInterviewFeedbackRequest() {
    }
    
    // DIGEST FORCED
    public void scheduleInterviewFeedbackReminder() {
    }
    
    // IMMEDIATELY (applicant)
    public void sendApplicationUnderApprovalNotification() {
        
    }
    
    // IMMEDIATELY (applicant)
    public void sendApplicationApprovedNotification() {
    }

    // IMMEDIATELY (applicant)
    public void sendApplicationUnderInterviewNotification() {
    }
    
    // IMMEDIATELY (applicant)
    public void sendApplicationUnderReviewNotification() {
    }

    // IMMEDIATELY
    public void sendPasswordResetConfirmation() {
    }
    
    // IMMEDIATELY
    public void sendNewUserInvitation() {
    }
    
    // IMMEDIATELY
    public void sendReferenceRequest() {
    }

    // IMMEDIATELY
    public void sendReferenceReminder() {
    }

    // IMMEDIATELY
    public void sendReferenceSubmittedConfirmationToApplicant() {
    }

    // DIGEST
    public void scheduleReferenceSubmittedConfirmationToAdministrator(final Referee referee) {
        ApplicationForm form = referee.getApplication();
        CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.DIGEST));
    }
    
    // IMMEDIATELY
    public void sendRegistrationConfirmation() {
    }
    
    // Register referee confirmation - DELETE

    // IMMEDIATELY (Note: becomes redundant soon)
    public void sendValidationRequestToRegistry() {
    }
    
    // IMMEDIATELY 
    public void sendRejectionConfirmationToApplicant() {
    }
    
    // DIGEST 
    public void scheduleRejectionConfirmationToAdministrator() {
        CollectionUtils.forAllDo(applicationDAO.getApplicationsDueRejectNotifications(), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm application = (ApplicationForm) input;
                application.setRejectNotificationDate(new Date());
                applicationDAO.save(application);
            }
        });
    }

    // DIGEST
    public void scheduleReviewSubmittedConfirmation() {
        CollectionUtils.forAllDo(commentDAO.getReviewCommentsDueNotification(), new Closure() {
            @Override
            public void execute(final Object input) {
                ReviewComment comment = (ReviewComment) input;
                comment.setAdminsNotified(true);
                comment.getUser().setDigestNotificationType(DigestNotificationType.DIGEST);
            }
        });
    }
    
    // Reviewer assigned notification - DELETE
    
    // DIGEST
    public void scheduleReviewEvaluationRequest() {
    }
    
    // DIGEST FORCED
    public void scheduleReviewEvaluationReminder() {
    }
    
    // DIGEST
    public void scheduleConfirmSupervisionRequest() {
        CollectionUtils.forAllDo(supervisorDAO.getPrimarySupervisorsDueNotification(), new Closure() {
            @Override
            public void execute(final Object input) {
                Supervisor supervisor = (Supervisor) input;
                supervisor.setLastNotified(new Date());
                supervisor.getUser().setDigestNotificationType(DigestNotificationType.DIGEST);
                supervisorDAO.save(supervisor);
            }
        });
    }
    
    // DIGEST FORCED
    public void scheduleConfirmSupervisionReminder() {
        CollectionUtils.forAllDo(supervisorDAO.getPrimarySupervisorsDueReminder(), new Closure() {
            @Override
            public void execute(final Object input) {
                Supervisor supervisor = (Supervisor) input;
                supervisor.setLastNotified(new Date());
                supervisor.getUser().setDigestNotificationType(DigestNotificationType.REMINDER_DIGEST);
                supervisorDAO.save(supervisor);
            }
        });
    }
    
    // Supervisor notification - DELETE
    
}
