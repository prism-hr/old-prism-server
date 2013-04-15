package com.zuehlke.pgadmissions.mail.refactor;

import java.util.Date;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
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
            userService.setDigestNotificationType((RegisteredUser) input, type);
        }
    }

    @Autowired
    public ScheduledMailSendingService(final TemplateAwareMailSender mailSender, final UserService userService,
            final ApplicationFormDAO applicationFormDAO, final NotificationRecordDAO notificationRecordDAO,
            final CommentDAO commentDAO, final SupervisorDAO supervisorDAO) {
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
        scheduleUpdateConfirmation();
        scheduleValidationRequest();
        scheduleValidationReminder();
        scheduleWithdrawalConfirmation();
        scheduleRestartApprovalRequest();
        scheduleRestartApprovalReminder();
        scheduleApprovedConfirmation();
        scheduleInterviewAdministrationReminder();
        scheduleInterviewAdministrationRequest();
        scheduleInterviewFeedbackConfirmation();
        scheduleInterviewFeedbackRequest();
        scheduleInterviewFeedbackReminder();
        scheduleUnderApprovalNotification();
        scheduleRejectionConfirmationToAdministrator();
        scheduleReviewSubmittedConfirmation();
        scheduleReviewEvaluationRequest();
        scheduleReviewEvaluationReminder();
        scheduleConfirmSupervisionRequest();
        scheduleConfirmSupervisionReminder();
        
        sendDigestsToUsers();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendDigestsToUsers() {
        for (Integer userId : userService.getAllUsersInNeedOfADigestNotification()) {
            RegisteredUser user = userService.getUser(userId);
            PrismEmailMessageBuilder emailMessage = new PrismEmailMessageBuilder().to(user)
                    .subjectCode("Prism Digest Notification")
                    .emailTemplate(user.getDigestNotificationType().toString());
            try {
                mailSender.sendEmail(emailMessage.build());
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        userService.resetDigestNotificationsForAllUsers();
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to approve applications.<br/>
     * Finds all applications in the system that require approval, and;<br/> 
     * Schedules their Approvers to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Approver
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Approvers can approve applications, while:
     *    <ol>
     *    <li>They are in the approval state.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>The Primary Supervisor has confirmed supervision within the last 24 hours, or;</li>
     *    <li>The system defined maximum duration for the Approval stage has elapsed within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    // Business logic is currently incorrect
    // Approver cannot approve as soon as the application is moved into the approval state
    // Approver is notified to approve as soon as the application is moved into the approval state
    public void scheduleApprovalRequest() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to approve applications.<br/>
     * Finds all applications in the system that urgently require approval, and;<br/> 
     * Schedules their Approvers to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Approver
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Approvers can approve applications, while:
     *    <ol>
     *    <li>They are in the approval state.</li>
     *    </ol></li>
     * <li>They are scheduled to be reminded to do so, when:
     *    <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    public void scheduleApprovalReminder() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.APPROVAL_REMINDER, ApplicationFormStatus.APPROVAL)) {
            createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_REMINDER);
            userService.setDigestNotificationType(form.getApprover(), DigestNotificationType.TASK_REMINDER);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to evaluate interview feedback.<br/>
     * Finds all applications in the system that require interview feedback evaluation, and;<br/> 
     * Schedules their Administrators and Delegated Interview Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * Delegated Interview Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators and Delegated Interview Administrators can evaluate interview feedback, while:
     *    <ol>
     *    <li>They are in the current interview state.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>The final Interviewer has provided feedback within the last 24 hours, or;</li>
     *    <li>The system defined maximum duration for the Interview stage has elapsed within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    public void scheduleInterviewFeedbackEvaluationRequest() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
            RegisteredUser interviewer = form.getApplicationAdministrator();
            if (interviewer != null) {
                userService.setDigestNotificationType(interviewer, DigestNotificationType.TASK_NOTIFICATION);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to evaluate interview feedback.<br/>
     * Finds all applications in the system that urgently require interview feedback evaluation, and;<br/> 
     * Schedules their Administrators and Delegated Interview Administrators to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * Delegated Interview Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators and Delegated Interview Administrators can evaluate interview feedback, while:
     *    <ol>
     *    <li>They are in the current interview state.</li>
     *    </ol></li>
     * <li>They are scheduled to be reminded to do so, when:
     *    <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    public void scheduleInterviewFeedbackEvaluationReminder() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
            RegisteredUser interviewer = form.getApplicationAdministrator();
            if (interviewer != null) {
                userService.setDigestNotificationType(interviewer, DigestNotificationType.TASK_REMINDER);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to review applications.<br/>
     * Finds all applications in the system that urgently require reviews, and;<br/> 
     * Schedules their Reviewers to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Reviewer
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Reviewers can review applications, while:
     *    <ol>
     *    <li>They are in the current review state.</li>
     *    </ol></li>
     * <li>They are scheduled to be reminded to do so, when:
     *    <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    public void scheduleReviewReminder() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REMINDER);
            CollectionUtils.forAllDo(getReviewersFromLatestReviewRound(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to review applications.<br/>
     * Finds all applications in the system that require reviews, and;<br/> 
     * Schedules their Reviewers to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Reviewer
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Reviewers can review applications, while:
     *    <ol>
     *    <li>They are in the current review state.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>Applications have been moved into the review state in the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    public void scheduleReviewRequest() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REMINDER);
            CollectionUtils.forAllDo(getReviewersFromLatestReviewRound(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
        }
    }
    
    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they have submitted applications.
     * <p/><p>
     * <b>Recipients</b>
     * Applicant
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Applicants are notified, when:
     *    <ol><li>They submit applications.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b>
     * Immediate Notification
     * </p>
     */
    public void sendSubmissionConfirmation() {
    }
    
    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been updated.<br/>
     * Finds all applications in the system that have recently been updated, and;<br/> 
     * Schedules their Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Applicants can update applications:
     *    <ol>
     *    <li>As soon as they are submitted, and;</li>
     *    <li>While they are not in the rejected, approved or approval states.</li>
     *    </ol></li>
     * <li>Administrators are scheduled to be notified of updates, when:
     *    <ol>
     *    <li>Applications have been updated within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleUpdateConfirmation() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUpdateNotification()) {
            createNotificationRecordIfNotExists(form, NotificationType.UPDATED_NOTIFICATION);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to validate applications.<br/>
     * Finds all applications in the system that require validation, and;<br/> 
     * Schedules their Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can validate applications, while:
     *    <ol>
     *    <li>They are in the validation state.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>Applications have been moved into the validation state within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    public void scheduleValidationRequest() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.UPDATED_NOTIFICATION, ApplicationFormStatus.VALIDATION)) {
            createNotificationRecordIfNotExists(form, NotificationType.UPDATED_NOTIFICATION);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to validate applications.<br/>
     * Finds all applications in the system that urgently require validation, and;<br/> 
     * Schedules their Administrators to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can validate applications, while:
     *    <ol>
     *    <li>They are in the validation state.</li>
     *    </ol></li>
     * <li>They are scheduled to be reminded to do so, when:
     *    <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    public void scheduleValidationReminder() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION)) {
            createNotificationRecordIfNotExists(form, NotificationType.VALIDATION_REMINDER);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been withdrawn.<br/>
     * Finds all applications in the system that have recently been updated, and;<br/> 
     * Schedules their Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Applicants can withdraw applications, while:
     *    <ol>
     *    <li>They are not in the rejected, approved or withdrawn states.</li>
     *    </ol></li>
     * <li>Administrators are scheduled to be notified of withdrawals, when:
     *    <ol>
     *    <li>Applications have been withdrawn within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleWithdrawalConfirmation() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when they are required to restart the approval of applications.<br/>
     * Finds all applications in the system that require approval restarts, and;<br/> 
     * Schedules their Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can restart the approval of applications, while:
     *    <ol>
     *    <li>They are in the approval state.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>Approvers have requested this within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    // Business logic is currently incorrect
    // Administrator cannot restart the approval state until requested to do so by an Approver.
    public void scheduleRestartApprovalRequest() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueApprovalRequestNotification()) {
            createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to restart the approval of applications.<br/>
     * Finds all applications in the system that urgently require approval restarts, and;<br/> 
     * Schedules their Administrators to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can restart the approval of applications, while:
     *    <ol>
     *    <li>They are in the approval state.</li>
     *    </ol></li>
     * <li>They are scheduled to be reminded to do so, when:
     *    <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    public void scheduleRestartApprovalReminder() {
        for (ApplicationForm form : applicationDAO.getApplicationDueApprovalRestartRequestReminder()) {
            createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_RESTART_REQUEST_REMINDER);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been approved.<br/>
     * Finds all applications in the system that have recently been approved, and;<br/> 
     * Schedules their Primary Supervisors to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Primary Supervisor
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Approvers can approve applications, while:
     *    <ol>
     *    <li>They are in the approval state.</li>
     *    </ol></li>
     * <li>Primary Supervisors are scheduled to be notified of approvals, when:
     *    <ol>
     *    <li>Applications have been approved within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    // Current business logic is incorrect
    // Should send to Primary Supervisor only - currently goes to a wider range of users.
    public void scheduleApprovedConfirmation() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueApprovalNotifications()) {
            createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_NOTIFICATION);
            CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form), new Closure() {
                @Override
                public void execute(final Object input) {
                    Supervisor supervisor = (Supervisor) input;
                    if (BooleanUtils.isTrue(supervisor.getIsPrimary())) {
                        userService.setDigestNotificationType(supervisor.getUser(), DigestNotificationType.UPDATE_NOTIFICATION);
                    }
                }
            });
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users when they are required to administer interviews.<br/>
     * Finds all applications in the system that urgently require interviews to be administered, and;<br/> 
     * Schedules their Delegate Interview Administrators to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Delegate Interview Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can specify Delegate Interview Administrators, when:
     *    <ol>
     *    <li>They move applications into the interview state.</li>
     *    </ol></li>
     * <li>Delegate Interview Administrators can administer interviews, while:
     *    <ol>
     *    <li>They are in the current interview state, and;</li>
     *    <li>An interview has not been scheduled.</li>
     *    </ol></li>
     * <li>They are scheduled to be reminded to do so, when:
     *    <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    public void scheduleInterviewAdministrationReminder() {
        DateTime yesterday = new DateTime().minusDays(1);
        for (NotificationRecord notificationRecord : notificationRecordDAO.getNotificationsWithTimeStampGreaterThan(yesterday.toDate(), NotificationType.INTERVIEW_ADMINISTRATION_REMINDER)) {
            notificationRecord.setDate(new Date());
            RegisteredUser delegate = notificationRecord.getApplication().getApplicationAdministrator();
            if (delegate != null) {
                userService.setDigestNotificationType(delegate, DigestNotificationType.TASK_REMINDER);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when they are required to administer interviews.<br/>
     * Finds all applications in the system that require interviews to be administered, and;<br/> 
     * Schedules their Delegate Interview Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Delegate Interview Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can specify Delegate Interview Administrators, when:
     *    <ol>
     *    <li>They move applications into the interview state.</li>
     *    </ol></li>
     * <li>Delegate Interview Administrators can administer interviews, while:
     *    <ol>
     *    <li>They are in the current interview state, and;</li>
     *    <li>An interview has not been scheduled.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>Applications have been delegated to them within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    public void scheduleInterviewAdministrationRequest() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when interview feedback has been provided.<br/>
     * Finds all applications in the system for which interview feedback has recently been provided, and;<br/> 
     * Schedules their Administrators and Delegate Interview Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * Delegate Interview Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Interviewers can provide interview feedback, while:
     *    <ol>
     *    <li>Applications are in the current interview state.</li>
     *    </ol></li>
     * <li>Administrators and Delegate Interview Administrators are scheduled to be notified of interview feedback, when:
     *    <ol>
     *    <li>Interview feedback has been provided within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleInterviewFeedbackConfirmation() {
    }

    /**
    * <p>
    * <b>Summary</b><br/>
    * Informs users when interviews have been scheduled.
    * <p/><p>
    * <b>Recipients</b>
    * Interviewer
    * </p><p>
    * <b>Previous Email Template Name</b><br/>
    * Kevin to Insert
    * </p><p> 
    * <b>Business Rules</b><br/>
    * <ol>
    * <li>Administrators and Delegate Interview Administrators can schedule interviews, while:
    *    <ol>
    *    <li>Applications are in the current interview state, and;</li>
    *    <li>Interviews have not been scheduled.</li>
    *    </ol></li>
    * <li>Interviewers are notified, when:
    *    <ol>
    *    <li>Interviews have been scheduled.</li>
    *    </ol></li>
    * </ol>
    * </p><p>
    * <b>Notification Type</b>
    * Immediate Notification
    * </p>
    */
    public void sendInterviewConfirmationToInterviewer() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when interviews have been scheduled.
     * <p/><p>
     * <b>Recipients</b>
     * Applicant
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b><br/>
     * <li>Administrators and Delegate Interview Administrators can schedule interviews, while:
     *    <ol>
     *    <li>Applications are in the current interview state, and;</li>
     *    <li>Interviews have not been scheduled.</li>
     *    </ol></li>
     * <ol>
     * <li>Applicants are notified, when:
     *    <ol>
     *    <li>Interviews have been scheduled.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b>
     * Immediate Notification
     * </p>
     */ 
    public void sendInterviewConfirmationToApplicant() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to provide interview feedback.<br/>
     * Finds all applications in the system that require interview feedback, and;<br/> 
     * Schedules their Interviewers to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Interviewer
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Interviewers can provide interview feedback, while:
     *    <ol>
     *    <li>They are in the current interview state.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>When interview have taken place within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    public void scheduleInterviewFeedbackRequest() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to provide interview feedback.<br/>
     * Finds all applications in the system that urgently require interview feedback, and;<br/> 
     * Schedules their Interviewers to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Interviewer
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Interviewers can provide interview feedback, while:
     *    <ol>
     *    <li>They are in the current interview state.</li>
     *    </ol></li>
     * <li>They are scheduled to be reminded to do so, when:
     *    <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    public void scheduleInterviewFeedbackReminder() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been moved into the approval state.<br/>
     * Finds all applications in the system that have recently been moved into the approval state, and;<br/> 
     * Schedules their Applicants to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Applicant
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can move applications into the approval state, while:
     *    <ol>
     *    <li>They are not in the rejected, approved or withdrawn states.</li>
     *    </ol></li>
     * <li>Applicants are scheduled to be notified, when:
     *    <ol>
     *    <li>Applications have been moved into the approval state within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleUnderApprovalNotification() {
    }

    /**
    * <p>
    * <b>Summary</b><br/>
    * Informs users when applications have been approved.
    * <p/><p>
    * <b>Recipients</b>
    * Applicant
    * </p><p>
    * <b>Previous Email Template Name</b><br/>
    * Kevin to Insert
    * </p><p> 
    * <b>Business Rules</b><br/>
    * <ol>
    * <li>Administrators can approve applications, while:
    *    <ol>
    *    <li>They are not in the rejected, approved or withdrawn states.</li>
    *    </ol></li>
    * <li>Approvers can approve applications, while:
    *    <ol>
    *    <li>They are in the approval state.</li>
    *    </ol></li>
    * <li>Applicants are notified, when:
    *    <ol>
    *    <li>Applications are approved.</li>
    *    </ol></li>
    * </ol>
    * </p><p>
    * <b>Notification Type</b>
    * Immediate Notification
    * </p>
    */
    public void sendApprovedNotification() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been moved into the review.<br/>
     * Finds all applications in the system that have recently been moved into the review state, and;<br/> 
     * Schedules their Applicants to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Applicant
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can move applications into the review state, while:
     *    <ol>
     *    <li>They are not in the rejected, approved or withdrawn states.</li>
     *    </ol></li>
     * <li>Applicants are scheduled to be notified, when:
     *    <ol>
     *    <li>Applications have been moved into the review state within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void sendApplicationUnderReviewNotification() {
    }

    /**
    * <p>
    * <b>Summary</b><br/>
    * Informs users that they are required to provide references.
    * <p/><p>
    * <b>Recipients</b>
    * Referees
    * </p><p>
    * <b>Previous Email Template Name</b><br/>
    * Kevin to Insert
    * </p><p> 
    * <b>Business Rules</b><br/>
    * <ol>
    * <li>Referees are notified to provide references, when:
    *    <ol>
    *    <li>Administrators move applications from the validation state into a state, that:
    *       <ol>
    *       <li>Is not the rejected or approved state</li>
    *       </ol></li>
    *    </ol></li>
    * </ol>
    * </p><p>
    * <b>Notification Type</b>
    * Immediate Notification
    * </p>
    */
    public void sendReferenceRequest() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to provide references.<br/>
     * Finds all applications in the system that urgently require references, and;<br/> 
     * Schedules their Referees to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Referee
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Referees are scheduled to be reminded to provide references, when;</li>
     * <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Notification
     * </p>
     */
    public void sendReferenceReminder() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to confirm registrations.
     * <p/><p>
     * <b>Recipients</b>
     * Users
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Users can register, when they are:
     *    <ol>
     *    <li>Invited to do so by Administrators, or;</li>
     *    <li>In the process of initiating applications;</li>
     *    </ol></li>
     * <li>They are notified to confirm registrations, when:
     *    <ol>
     *    <li>Submitted registrations.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b>
     * Immediate Notification
     * </p>
     */
    public void sendRegistrationConfirmation() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs registry staff that they are required to confirm validation decisions.
     * <p/><p>
     * <b>Recipients</b>
     * Registry Staff
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Administrators can ask Registry Staff to confirm validation decisions, while:
     *    <ol>
     *    <li>Applications are in the validation state.</li>
     *    </ol></li>
     * <li>Registry staff are notified to confirm validation decisions, when:
     *    <ol>
     *    <li>Requested to do so by Administrators.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b>
     * Immediate Notification
     * </p>
     */
    public void sendValidationRequestToRegistry() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been rejected.
     * <p/><p>
     * <b>Recipients</b>
     * Applicant
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Administrators can reject applications, when:
     *    <ol>
     *    <li>They are not in the rejected, approved or withdrawn states.</li>
     *    </ol></li>
     * <li>Approvers can reject applications, when:
     *    <ol>
     *    <li>They are in the approval state.</li>
     *    </ol></li>
     * <li>Applicants are notified of rejections, when:
     *    <ol>
     *    <li>Applications are rejected by Administrators or Approvers.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b>
     * Immediate Notification
     * </p>
     */
    // Current business logic is incorrect
    // Administrator cannot reject application when it is in approval state.
    public void sendRejectionConfirmationToApplicant() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been rejected.<br/>
     * Finds all applications in the system which have recently been rejected, and;<br/> 
     * Schedules their Primary Supervisors to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Primary Supervisor
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <li>Administrators can reject applications, when:
     *    <ol>
     *    <li>They are not in the rejected, approved or withdrawn states.</li>
     *    </ol></li>
     * <li>Approvers can reject applications, when:
     *    <ol>
     *    <li>They are in the approval state.</li>
     *    </ol></li>
     * <li>Primary Supervisors are notified of rejections, when:
     *    <ol>
     *    <li>Applications have been rejected within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleRejectionConfirmationToAdministrator() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueRejectNotifications()) {
            form.setRejectNotificationDate(new Date());
            applicationDAO.save(form);
            CollectionUtils.forAllDo(getSupervisorsFromLatestApprovalRound(form), new Closure() {
                @Override
                public void execute(final Object input) {
                    Supervisor supervisor = (Supervisor) input;
                    if (BooleanUtils.isTrue(supervisor.getIsPrimary())) {
                        userService.setDigestNotificationType(supervisor.getUser(), DigestNotificationType.UPDATE_NOTIFICATION);
                    }
                }
            });
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when reviews have been provided.<br/>
     * Finds all applications in the system for which reviews have recently been provided, and;<br/> 
     * Schedules their Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Referees can provide reviews, while:
     *    <ol>
     *    <li>Applications in the current review state.</li>
     *    </ol></li>
     * <li>Administrators are scheduled to be notified, when:
     *    <ol>
     *    <li>Reviews have been provided within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleReviewSubmittedConfirmation() {
        for (ReviewComment comment : commentDAO.getReviewCommentsDueNotification()) {
            comment.setAdminsNotified(true);
            comment.getUser().setDigestNotificationType(DigestNotificationType.UPDATE_NOTIFICATION);
            CollectionUtils.forAllDo(getProgramAdministrators(comment.getApplication()), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to evaluate reviews.<br/>
     * Finds all applications in the system that require review evaluation, and;<br/> 
     * Schedules their Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Admistrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administators can evaluate reviews, while:
     *    <ol>
     *    <li>Applications are in the current review state.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>The final Reviewer has provided feedback within the last 24 hours, or;</li>
     *    <li>The system defined maximum duration for the Review stage has elapsed within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    public void scheduleReviewEvaluationRequest() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to evaluate reviews.<br/>
     * Finds all applications in the system that urgently require review evaluation, and;<br/> 
     * Schedules their Reviewers to be reminded.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Reviewer
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can evaluate reviews, while:
     *    <ol>
     *    <li>Applications are in the current review state.</li>
     *    </ol></li>
     * <li>They are scheduled to be reminded to do so, when:
     *    <ol>
     *    <li>They have previously been notified or reminded to do so, and;</li>
     *    <li>The time elapsed since the previous notification or reminder:
     *       <ol>
     *       <li>Equals the system defined maximum time interval between reminders, or;</li>
     *       <li>Exceeds the system defined maximum time interval between reminders.</li>
     *       </ol></li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    public void scheduleReviewEvaluationReminder() {
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to confirm supervision.<br/>
     * Finds all applications in the system that require supervision confirmation, and;<br/> 
     * Schedules their Primary Supervisors to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Primary Supervisor
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Primary Supervisors can confirm supervision, while:
     *    <ol>
     *    <li>Applications are in the current approval state.</li>
     *    </ol></li>
     * <li>They are scheduled to be notified to do so, when:
     *    <ol>
     *    <li>Administrators move applications into the current approval state.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    public void scheduleConfirmSupervisionRequest() {
        for (Supervisor supervisor : supervisorDAO.getPrimarySupervisorsDueNotification()) {
            supervisor.setLastNotified(new Date());
            userService.setDigestNotificationType(supervisor.getUser(), DigestNotificationType.TASK_NOTIFICATION);
            supervisorDAO.save(supervisor);
        }
    }

    /**
    * <p>
    * <b>Summary</b><br/>
    * Reminds users that they are required to confirm supervision.<br/>
    * Finds all applications in the system that urgently require supervision confirmation, and;<br/> 
    * Schedules their Primary Supervisors to be reminded.
    * <p/><p>
    * <b>Recipients</b><br/>
    * Primary Supervisor
    * </p><p>
    * <b>Previous Email Template Name</b><br/>
    * Kevin to Insert
    * </p><p> 
    * <b>Business Rules</b>
    * <ol>
    * <li>Primary Supervisors can confirm supervision, while:
    *    <ol>
    *    <li>Applications are in the current approval state.</li>
    *    </ol></li>
    * <li>They are scheduled to be reminded to do so, when:
    *    <ol>
    *    <li>They have previously been notified or reminded to do so, and;</li>
    *    <li>The time elapsed since the previous notification or reminder:
    *       <ol>
    *       <li>Equals the system defined maximum time interval between reminders, or;</li>
    *       <li>Exceeds the system defined maximum time interval between reminders.</li>
    *       </ol></li>
    *    </ol></li>
    * </ol>
    * </p><p>
    * <b>Notification Type</b><br/>
    * Scheduled Digest Priority 3 (Task Reminder)
    * </p>
    */
    public void scheduleConfirmSupervisionReminder() {
        for (Supervisor supervisor : supervisorDAO.getPrimarySupervisorsDueReminder()) {
            supervisor.setLastNotified(new Date());
            userService.setDigestNotificationType(supervisor.getUser(), DigestNotificationType.TASK_REMINDER);
            supervisorDAO.save(supervisor);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when supervision has been confirmed.<br/>
     * Finds all applications in the system for which supervision has recently been confirmed, and;<br/> 
     * Schedules their Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Primary Supervisors can confirm supervision, while:
     *    <ol>
     *    <li>Applications are in the current approval state.</li>
     *    </ol></li>
     * <li>Administrators are scheduled to be notified, when:
     *    <ol>
     *    <li>Supervision has been confirmed within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleSupervisionConfirmedNotification(final ApplicationForm form) {
        CollectionUtils.forAllDo(form.getProgram().getAdministrators(), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
    }
}
