package com.zuehlke.pgadmissions.mail.refactor;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class ScheduledMailSendingService extends AbstractScheduledMailSendingService {

    private final Logger log = LoggerFactory.getLogger(ScheduledMailSendingService.class);
    
    private final EmailDigestService emailDigestService;
    
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
    public ScheduledMailSendingService(final EmailTemplateAwareMailSender mailSender, 
            final UserService userService,
            final EmailDigestService emailDigestService,
            final ApplicationFormDAO applicationFormDAO) {
        super(mailSender, userService, applicationFormDAO);
        this.emailDigestService = emailDigestService;
    }
    
    public ScheduledMailSendingService() {
        this(null, null, null, null);
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
    @Transactional
    @Scheduled(fixedRate = 300000, fixedDelay = 4000)
    public void scheduleApprovalRequest() {
       
    }
    
    // DIGEST FORCED
    public void scheduleApprovalReminder() {
        CollectionUtils.forAllDo(applicationFormDAO.getApplicationsDueUserReminder(NotificationType.APPROVAL_REMINDER, ApplicationFormStatus.APPROVAL), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                
                createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_REMINDER);
                
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(
                        DigestNotificationType.REMINDER_DIGEST));
                
                CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form),
                        new UpdateDigestNotificationClosure(DigestNotificationType.REMINDER_DIGEST));
            }
        });
    }
    
    // DIGEST
    public void scheduleInterviewFeedbackEvaluationRequest() {
        CollectionUtils.forAllDo(applicationFormDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                
                createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
                
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(
                        DigestNotificationType.DIGEST));
                
                CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form),
                        new UpdateDigestNotificationClosure(DigestNotificationType.DIGEST));
            }
        });
    }
    
    // DIGEST FORCED
    public void scheduleInterviewFeedbackEvaluationReminder() {
        CollectionUtils.forAllDo(applicationFormDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                
                createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
                
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(
                        DigestNotificationType.REMINDER_DIGEST));
                
                CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form),
                        new UpdateDigestNotificationClosure(DigestNotificationType.REMINDER_DIGEST));
            }
        });
    }
    
    // DIGEST FORCED
    public void scheduleReviewReminder() {
        CollectionUtils.forAllDo(applicationFormDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                
                createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REMINDER);
                
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(
                        DigestNotificationType.REMINDER_DIGEST));
                
                CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form),
                        new UpdateDigestNotificationClosure(DigestNotificationType.REMINDER_DIGEST));
            }
        });
    }
    
    // DIGEST
    public void scheduleReviewRequest() {
        CollectionUtils.forAllDo(applicationFormDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                
                createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REMINDER);
                
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(
                        DigestNotificationType.DIGEST));
                
                CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form),
                        new UpdateDigestNotificationClosure(DigestNotificationType.DIGEST));
            }
        });
    }
    
    // IMMEDIATELY
    public void sendApplicationConfirmationToApplicant() {
    }
    
    // DIGEST (submitConfirmationToAdmin)
    public void scheduleApplicationConfirmationToAdministrator() {
        CollectionUtils.forAllDo(applicationFormDAO.getApplicationsDueUserReminder(NotificationType.UPDATED_NOTIFICATION, ApplicationFormStatus.VALIDATION), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                
                createNotificationRecordIfNotExists(form, NotificationType.UPDATED_NOTIFICATION);
                
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(
                        DigestNotificationType.DIGEST));
                
                CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form),
                        new UpdateDigestNotificationClosure(DigestNotificationType.DIGEST));
            }
        });
    }
    
    // DIGEST
    public void scheduleUpdatedApplicationConfirmation() {
        CollectionUtils.forAllDo(applicationFormDAO..getApplicationsDueUpdateNotification(), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                
                createNotificationRecordIfNotExists(form, NotificationType.UPDATED_NOTIFICATION);
                
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(
                        DigestNotificationType.DIGEST));
                
                if (form.getStatus() == ApplicationFormStatus.INTERVIEW) {
                    if (form.getLatestInterview() != null) {
                        CollectionUtils.forAllDo(form.getLatestInterview().getInterviewers()
                        }
                    }
                        }
                }
                else if (form.getStatus() == ApplicationFormStatus.APPROVAL) {
                    
                }
                
                CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form),
                        new UpdateDigestNotificationClosure(DigestNotificationType.DIGEST));
            }
        });
    }

    // DIGEST
    public void scheduleApplicationValidationRequest() {
    }
    
    // DIGEST FORCED
    public void scheduleApplicationValidationReminder() {
    }
    
    // IMMEDIATELY
    public void sendApplicationWithdrawnConfirmation() {
    }

    // DIGEST
    public void scheduleRestartApprovalRequest() {
    }
    
    // DIGEST FORCED
    public void scheduleRestartApprovalReminder() {
    }
    
    // DIGEST (schedule to Admins)
    public void scheduleApprovalConfirmation() {
    }

    // IMMEDIATELY
    public void sendDataExportError() {
    }

    // IMMEDIATELY
    public void sendDataImportError() {
    }
    
    // DIGEST
    public void scheduleInterviewAdministrationReminder() {
    }
    
    // DIGEST (scheduling, etc.)
    public void scheduleInterviewAdministrationRequest() {
    }
    
    // DIGEST
    public void scheduleInterviewFeedbackConfirmation() {
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
        
        
        CollectionUtils.forAllDo(applicationFormDAO.getApplicationsDueApprovedNotifications(), new Closure() {
            @Override
            public void execute(final Object input) {
                ApplicationForm form = (ApplicationForm) input;
                
                createNotificationRecordIfNotExists(form, NotificationType.APPROVED_NOTIFICATION);
                
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(
                        DigestNotificationType.DIGEST));
                
                CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form),
                        new UpdateDigestNotificationClosure(DigestNotificationType.DIGEST));
            }
        });
        
        
        
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
    public void scheduleReferenceSubmittedConfirmationToAdministrator() {
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
    }

    // DIGEST
    public void scheduleReviewSubmittedConfirmation() {
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
    }
    
    // DIGEST FORCED
    public void scheduleConfirmSupervisionReminder() {
    }
    
    // Supervisor notification - DELETE
    
}
