package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEW_VOTE_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_USER_SUGGESTION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_REMINDER;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.InterviewParticipantDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
public class ScheduledMailSendingService extends AbstractMailSendingService {

    private final Logger log = LoggerFactory.getLogger(ScheduledMailSendingService.class);

    private final CommentDAO commentDAO;

    private final SupervisorDAO supervisorDAO;

    private final StageDurationDAO stageDurationDAO;

    private final RefereeDAO refereeDAO;

    private final UserService userService;

    private final ApplicationContext applicationContext;

    private final InterviewParticipantDAO interviewParticipantDAO;

    @Autowired
    public ScheduledMailSendingService(final MailSender mailSender, final ApplicationFormDAO applicationFormDAO, final CommentDAO commentDAO,
            final SupervisorDAO supervisorDAO, final StageDurationDAO stageDurationDAO, 
            final ConfigurationService configurationService, 
            final RefereeDAO refereeDAO,
            final UserService userService, final UserDAO userDAO, final RoleDAO roleDAO, final EncryptionUtils encryptionUtils,
            @Value("${application.host}") final String host,
            final ApplicationContext applicationContext, InterviewParticipantDAO interviewParticipantDAO) {
        super(mailSender, applicationFormDAO, configurationService, userDAO, roleDAO, refereeDAO, encryptionUtils, host);
        this.commentDAO = commentDAO;
        this.supervisorDAO = supervisorDAO;
        this.stageDurationDAO = stageDurationDAO;
        this.refereeDAO = refereeDAO;
        this.userService = userService;
        this.applicationContext = applicationContext;
        this.interviewParticipantDAO = interviewParticipantDAO;
    }

    public ScheduledMailSendingService() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Transactional
    public void sendDigestsToUsers() {
        log.info("Sending daily digests to users");
        String taskNotificationSubject = resolveMessage(DIGEST_TASK_NOTIFICATION, (Object[]) null);
        String taskReminderSubject = resolveMessage(DIGEST_TASK_REMINDER, (Object[]) null);
        String updateNotificationSubject = resolveMessage(DIGEST_UPDATE_NOTIFICATION, (Object[]) null);
        List<Integer> users = userService.getAllUsersInNeedOfADigestNotification();
        for (Integer userId : users) {
            RegisteredUser user = userService.getUser(userId);
            if (applicationContext.getBean(this.getClass()).sendDigestToUser(user, taskNotificationSubject, taskReminderSubject, updateNotificationSubject)) {
                setDigestNotificationType(user, DigestNotificationType.NONE);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendDigestToUser(final RegisteredUser user, String taskNotificationSubject, String taskReminderSubject, String updateNotificationSubject) {
        try {
            EmailModelBuilder modelBuilder = new EmailModelBuilder() {

                @Override
                public Map<String, Object> build() {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("user", user);
                    model.put("host", getHostName());
                    return model;
                }
            };

            PrismEmailMessageBuilder messageBuilder = new PrismEmailMessageBuilder();
            messageBuilder.model(modelBuilder);
            messageBuilder.to(user);

            switch (user.getDigestNotificationType()) {
            case TASK_NOTIFICATION:
                messageBuilder.subject(taskNotificationSubject);
                messageBuilder.emailTemplate(EmailTemplateName.DIGEST_TASK_NOTIFICATION);
                sendEmail(messageBuilder.build());
                break;
            case TASK_REMINDER:
                messageBuilder.subject(taskReminderSubject);
                messageBuilder.emailTemplate(EmailTemplateName.DIGEST_TASK_REMINDER);
                sendEmail(messageBuilder.build());
                break;
            case UPDATE_NOTIFICATION:
                messageBuilder.subject(updateNotificationSubject);
                messageBuilder.emailTemplate(EmailTemplateName.DIGEST_UPDATE_NOTIFICATION);
                sendEmail(messageBuilder.build());
                break;
            case NONE:
            default:
                break;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to approve applications.<br/>
     * Finds all applications in the system that urgently require approval, and;<br/>
     * Schedules their Approvers to be reminded.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Approver
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Approvers can approve applications, while:
     * <ol>
     * <li>They are in the approval state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be reminded to do so, when:
     * <ol>
     * <li>They have previously been notified or reminded to do so, and;</li>
     * <li>The time elapsed since the previous notification or reminder:
     * <ol>
     * <li>Equals the system defined maximum time interval between reminders, or;</li>
     * <li>Exceeds the system defined maximum time interval between reminders.</li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    @Transactional
    public void scheduleApprovalRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (ApplicationForm form : applicationDAO.getApplicationsDueMovedToApprovalNotifications()) {
            if (form.getLatestApprovalRound().getPrimarySupervisor().hasResponded()) {
                createNotificationRecordIfNotExists(form, NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION);
                CollectionUtils.forAllDo(form.getProgram().getApprovers(), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
                idsForWhichRequestWasFired.add(form.getId());
            }
        }
        final StageDuration approvalDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.APPROVAL);
        for (ApplicationForm form : applicationDAO.getApplicationsDueApprovalReminder()) {
            if (!idsForWhichRequestWasFired.contains(form.getId())) {
                ApprovalRound approvalRound = form.getLatestApprovalRound();
                Supervisor primarySupervisor = null;
                if (approvalDuration != null) {
                    primarySupervisor = approvalRound.getPrimarySupervisor();
                }
                if (primarySupervisor != null && approvalRound.getPrimarySupervisor().hasResponded()) {
                    createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_REMINDER);
                    DateTime approvalRoundExpiryDate = DateUtils.addWorkingDaysInMinutes(new DateTime(approvalRound.getCreatedDate()),
                            approvalDuration.getDurationInMinutes());
                    if (approvalRoundExpiryDate.isAfterNow()) {
                        CollectionUtils.forAllDo(form.getProgram().getApprovers(), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
                    }
                }
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to evaluate interview feedback.<br/>
     * Finds all applications in the system that require interview feedback evaluation, and;<br/>
     * Schedules their Administrators and Delegated Interview Administrators to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * Delegated Interview Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators and Delegated Interview Administrators can evaluate interview feedback, while:
     * <ol>
     * <li>They are in the current interview state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be notified to do so, when:
     * <ol>
     * <li>The final Interviewer has provided feedback within the last 24 hours, or;</li>
     * <li>The system defined maximum duration for the Interview stage has elapsed within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    // TODO: Talk to Alastair about this, this might be a duplicate
    // public void scheduleInterviewFeedbackEvaluationRequest() {
    // for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW)) {
    // createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
    // CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
    // RegisteredUser delegate = form.getApplicationAdministrator();
    // if (delegate != null) {
    // setDigestNotificationType(delegate, DigestNotificationType.TASK_NOTIFICATION);
    // }
    // }
    // }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to evaluate interview feedback.<br/>
     * Finds all applications in the system that urgently require interview feedback evaluation, and;<br/>
     * Schedules their Administrators and Delegated Interview Administrators to be reminded.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * Delegated Interview Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators and Delegated Interview Administrators can evaluate interview feedback, while:
     * <ol>
     * <li>They are in the current interview state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be reminded to do so, when:
     * <ol>
     * <li>They have previously been notified or reminded to do so, and;</li>
     * <li>The time elapsed since the previous notification or reminder:
     * <ol>
     * <li>Equals the system defined maximum time interval between reminders, or;</li>
     * <li>Exceeds the system defined maximum time interval between reminders.</li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    @Transactional
    public void scheduleInterviewFeedbackEvaluationReminder() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_EVALUATION_REMINDER,
                ApplicationFormStatus.INTERVIEW)) {
            Interview latestInterview = form.getLatestInterview();

            boolean sendDigest = true;
            for (Interviewer interviewer : latestInterview.getInterviewers()) {
                if (interviewer.getInterviewComment() == null) {
                    sendDigest = false;
                    break;
                }
            }

            if (sendDigest && !form.hasInterviewEvaluationComment()) {
                createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_EVALUATION_REMINDER);
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
                RegisteredUser delegate = form.getApplicationAdministrator();
                if (delegate != null) {
                    setDigestNotificationType(delegate, DigestNotificationType.TASK_REMINDER);
                }
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to review applications.<br/>
     * Finds all applications in the system that require reviews, and;<br/>
     * Schedules their Reviewers to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Reviewer
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Reviewers can review applications, while:
     * <ol>
     * <li>They are in the current review state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be notified to do so, when:
     * <ol>
     * <li>Applications have been moved into the review state in the last 24 hours, and;
     * <ol>
     * <li>No closing dates have been specified, or;/li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * <li>Application closing dates have expired in the last 24 hours, and.</li>
     * </ol>
     * </li> </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    @Transactional
    public void scheduleReviewRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.REVIEW_REQUEST,
                ApplicationFormStatus.REVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REQUEST);
            idsForWhichRequestWasFired.add(form.getId());
            for (Reviewer reviewer : form.getLatestReviewRound().getReviewers()) {
                if (reviewer.getReview() == null) {
                    setDigestNotificationType(reviewer.getUser(), DigestNotificationType.TASK_NOTIFICATION);
                }
            }
        }
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW)) {
            if (!idsForWhichRequestWasFired.contains(form.getId())) {
                createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REMINDER);
                for (Reviewer reviewer : form.getLatestReviewRound().getReviewers()) {
                    if (reviewer.getReview() == null) {
                        setDigestNotificationType(reviewer.getUser(), DigestNotificationType.TASK_REMINDER);
                    }
                }
            }
        }

    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been updated.<br/>
     * Finds all applications in the system that have recently been updated, and;<br/>
     * Schedules their Administrators to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Applicants can update applications:
     * <ol>
     * <li>As soon as they are submitted, and;</li>
     * <li>While they are not in the rejected, approved or approval states.</li>
     * </ol>
     * </li>
     * <li>Administrators are scheduled to be notified of updates, when:
     * <ol>
     * <li>Applications have been updated within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    @Transactional
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
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can validate applications, while:
     * <ol>
     * <li>They are in the validation state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be notified to do so, when:
     * <ol>
     * <li>Applications have been moved into the validation state within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    @Transactional
    public void scheduleValidationRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.UPDATED_NOTIFICATION,
                ApplicationFormStatus.VALIDATION)) {
            idsForWhichRequestWasFired.add(form.getId());
            createNotificationRecordIfNotExists(form, NotificationType.UPDATED_NOTIFICATION);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
        }
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION)) {
            if (!idsForWhichRequestWasFired.contains(form.getId())) {
                createNotificationRecordIfNotExists(form, NotificationType.VALIDATION_REMINDER);
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to restart the approval of applications.<br/>
     * Finds all applications in the system that urgently require approval restarts, and;<br/>
     * Schedules their Administrators to be reminded.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can restart the approval of applications, while:
     * <ol>
     * <li>They are in the approval state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be reminded to do so, when:
     * <ol>
     * <li>They have previously been notified or reminded to do so, and;</li>
     * <li>The time elapsed since the previous notification or reminder:
     * <ol>
     * <li>Equals the system defined maximum time interval between reminders, or;</li>
     * <li>Exceeds the system defined maximum time interval between reminders.</li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    @Transactional
    public void scheduleRestartApprovalRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (ApplicationForm form : applicationDAO.getApplicationsDueApprovalRequestNotification()) {
            idsForWhichRequestWasFired.add(form.getId());
            createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
        }
        for (ApplicationForm form : applicationDAO.getApplicationDueApprovalRestartRequestReminder()) {
            if (!idsForWhichRequestWasFired.contains(form.getId())) {
                createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_RESTART_REQUEST_REMINDER);
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been approved.<br/>
     * Finds all applications in the system that have recently been approved, and;<br/>
     * Schedules their Primary Supervisors to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Primary Supervisor
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Approvers can approve applications, while:
     * <ol>
     * <li>They are in the approval state.</li>
     * </ol>
     * </li>
     * <li>Primary Supervisors are scheduled to be notified of approvals, when:
     * <ol>
     * <li>Applications have been approved within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    @Transactional
    public void scheduleApprovedConfirmation() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueApprovedNotifications()) {
            createNotificationRecordIfNotExists(form, NotificationType.APPROVED_NOTIFICATION);
            CollectionUtils.forAllDo(form.getProgram().getAdministrators(), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
            RegisteredUser primarySupervisor = getPrimarySupervisorAsUserFromLatestApprovalRound(form);
            if (null != primarySupervisor) {
                setDigestNotificationType(primarySupervisor, DigestNotificationType.UPDATE_NOTIFICATION);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users when they are required to administer interviews.<br/>
     * Finds all applications in the system that urgently require interviews to be administered, and;<br/>
     * Schedules their Delegate Interview Administrators to be reminded.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Delegate Interview Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can specify Delegate Interview Administrators, when:
     * <ol>
     * <li>They move applications into the interview state.</li>
     * </ol>
     * </li>
     * <li>Delegate Interview Administrators can administer interviews, while:
     * <ol>
     * <li>They are in the current interview state, and;</li>
     * <li>An interview has not been scheduled.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be reminded to do so, when:
     * <ol>
     * <li>They have previously been notified or reminded to do so, and;</li>
     * <li>The time elapsed since the previous notification or reminder:
     * <ol>
     * <li>Equals the system defined maximum time interval between reminders, or;</li>
     * <li>Exceeds the system defined maximum time interval between reminders.</li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    @Transactional
    public void scheduleInterviewAdministrationRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (ApplicationForm form : applicationDAO.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST)) {
            if (interviewAdministrationRequestHasToBeSent(form)) {
                createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_ADMINISTRATION_REQUEST);
                idsForWhichRequestWasFired.add(form.getId());
                setDigestNotificationType(form.getApplicationAdministrator(), DigestNotificationType.TASK_NOTIFICATION);
            }
        }

        for (ApplicationForm form : applicationDAO.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER)) {
            if (!idsForWhichRequestWasFired.contains(form.getId()) && interviewAdministrationRequestHasToBeSent(form)) {
                createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_ADMINISTRATION_REMINDER);
                setDigestNotificationType(form.getApplicationAdministrator(), DigestNotificationType.TASK_REMINDER);
            }
        }
    }

    private boolean interviewAdministrationRequestHasToBeSent(ApplicationForm form) {
        if (form.getApplicationAdministrator() == null) {
            return false;
        }
        Interview latestInterview = form.getLatestInterview();
        if (latestInterview == null) {
            return true;
        }
        Date interviewDueDate = latestInterview.getInterviewDueDate();
        return interviewDueDate != null && interviewDueDate.before(new Date());
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when interview feedback has been provided.<br/>
     * Finds all applications in the system for which interview feedback has recently been provided, and;<br/>
     * Schedules their Administrators and Delegate Interview Administrators to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * Delegate Interview Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Interviewers can provide interview feedback, while:
     * <ol>
     * <li>Applications are in the current interview state.</li>
     * </ol>
     * </li>
     * <li>Administrators and Delegate Interview Administrators are scheduled to be notified of interview feedback, when:
     * <ol>
     * <li>Interview feedback has been provided within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    @Transactional
    public void scheduleInterviewFeedbackConfirmation() {
        for (InterviewComment comment : commentDAO.getInterviewCommentsDueNotification()) {
            comment.setAdminsNotified(true);
            CollectionUtils.forAllDo(getProgramAdministrators(comment.getApplication()), new UpdateDigestNotificationClosure(
                    DigestNotificationType.UPDATE_NOTIFICATION));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to provide interview feedback.<br/>
     * Finds all applications in the system that urgently require interview feedback, and;<br/>
     * Schedules their Interviewers to be reminded.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Interviewer
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Interviewers can provide interview feedback, while:
     * <ol>
     * <li>They are in the current interview state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be reminded to do so, when:
     * <ol>
     * <li>They have previously been notified or reminded to do so, and;</li>
     * <li>The time elapsed since the previous notification or reminder:
     * <ol>
     * <li>Equals the system defined maximum time interval between reminders, or;</li>
     * <li>Exceeds the system defined maximum time interval between reminders.</li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    @Transactional
    public void scheduleInterviewFeedbackRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (ApplicationForm form : applicationDAO.getApplicationsDueInterviewFeedbackNotification()) {
            idsForWhichRequestWasFired.add(form.getId());
            createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_FEEDBACK_REQUEST);
            CollectionUtils.forAllDo(getInterviewersFromLatestInterviewRound(form), new UpdateDigestNotificationClosure(
                    DigestNotificationType.TASK_NOTIFICATION));
        }
        for (ApplicationForm form : applicationDAO
                .getApplicationsDueUserReminder(NotificationType.INTERVIEW_FEEDBACK_REMINDER, ApplicationFormStatus.INTERVIEW)) {
            if (!idsForWhichRequestWasFired.contains(form.getId())) {
                createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_FEEDBACK_REMINDER);
                for (Interviewer interviewer : form.getLatestInterview().getInterviewers()) {
                    if (interviewer.getInterviewComment() == null) {
                        setDigestNotificationType(interviewer.getUser(), DigestNotificationType.TASK_REMINDER);
                    }
                }
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been moved into the approval state.<br/>
     * Finds all applications in the system that have recently been moved into the approval state, and;<br/>
     * Schedules their Applicants to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Applicant
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can move applications into the approval state, while:
     * <ol>
     * <li>They are not in the rejected, approved or withdrawn states.</li>
     * </ol>
     * </li>
     * <li>Applicants are scheduled to be notified, when:
     * <ol>
     * <li>Applications have been moved into the approval state within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    @Transactional
    public void scheduleApplicationUnderApprovalNotification() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueMovedToApprovalNotifications()) {
            createNotificationRecordIfNotExists(form, NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION);
            setDigestNotificationType(form.getApplicant(), DigestNotificationType.UPDATE_NOTIFICATION);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been moved into the review.<br/>
     * Finds all applications in the system that have recently been moved into the review state, and;<br/>
     * Schedules their Applicants to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Applicant
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can move applications into the review state, while:
     * <ol>
     * <li>They are not in the rejected, approved or withdrawn states.</li>
     * </ol>
     * </li>
     * <li>Applicants are scheduled to be notified, when:
     * <ol>
     * <li>Applications have been moved into the review state within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    @Transactional
    public void scheduleApplicationUnderReviewNotification() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION,
                ApplicationFormStatus.REVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION);
            setDigestNotificationType(form.getApplicant(), DigestNotificationType.UPDATE_NOTIFICATION);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to provide references.<br/>
     * Finds all applications in the system that urgently require references, and;<br/>
     * Schedules their Referees to be reminded.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Referee
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Referees are scheduled to be reminded to provide references, when;</li>
     * <ol>
     * <li>They have previously been notified or reminded to do so, and;</li>
     * <li>The time elapsed since the previous notification or reminder:
     * <ol>
     * <li>Equals the system defined maximum time interval between reminders, or;</li>
     * <li>Exceeds the system defined maximum time interval between reminders.</li>
     * </ol>
     * </li>
     * </ol>
     * </li> </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Notification
     * </p>
     */
    @Transactional
    public void sendReferenceReminder() {
        log.info("Running sendReferenceReminder Task");
        List<Integer> refereesDueAReminder = refereeDAO.getRefereesIdsDueAReminder();
        for (Integer referee : refereesDueAReminder) {
            applicationContext.getBean(this.getClass()).sendReferenceReminder(referee);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendReferenceReminder(Integer refereeId) {
        PrismEmailMessage message;
        try {
            Referee referee = refereeDAO.getRefereeById(refereeId);
            String subject = resolveMessage(REFEREE_REMINDER, referee.getApplication());

            ApplicationForm applicationForm = referee.getApplication();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(applicationForm.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "referee", "application", "applicant", "host" }, new Object[] {
                    adminsEmails, referee, applicationForm, applicationForm.getApplicant(), getHostName() });

            message = buildMessage(referee.getUser(), subject, modelBuilder.build(), REFEREE_REMINDER);
            sendEmail(message);
            referee.setLastNotified(new Date());
            refereeDAO.save(referee);
        } catch (Exception e) {
            log.error("Error while sending reference reminder email to referee: ", e);
            return false;
        }
        return true;
    }

    @Transactional
    public void sendInterviewParticipantVoteReminder() {
        log.info("Running interviewParticipantVoteReminder Task");
        List<Integer> participantsDueAReminder = interviewParticipantDAO.getInterviewParticipantsIdsDueAReminder();
        for (Integer participantId : participantsDueAReminder) {
            applicationContext.getBean(this.getClass()).sendInterviewParticipantVoteReminder(participantId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendInterviewParticipantVoteReminder(Integer participantId) {
        PrismEmailMessage message;
        InterviewParticipant participant = interviewParticipantDAO.getParticipantById(participantId);
        ApplicationForm application = participant.getInterview().getApplication();
        try {
            String subject = resolveMessage(INTERVIEW_VOTE_REMINDER, application);

            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(application.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "participant", "application", "host" }, new Object[] {
                    adminsEmails, participant, application, getHostName() });

            message = buildMessage(participant.getUser(), subject, modelBuilder.build(), INTERVIEW_VOTE_REMINDER);
            sendEmail(message);
            participant.setLastNotified(new Date());
        } catch (Exception e) {
            log.error("Error while sending interview vote reminder email to interview participant: " + participant.getUser().getDisplayName(), e);
            return false;
        }
        return true;
    }

    @Transactional
    public void sendNewUserInvitation() {
        log.info("Running sendNewUserInvitation Task");
        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        for (Integer user : users) {
            applicationContext.getBean(this.getClass()).sendNewUserInvitation(user);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendNewUserInvitation(Integer userId) {
        PrismEmailMessage message = null;
        RegisteredUser user = userDAO.get(userId);
        String subject = resolveMessage(NEW_USER_SUGGESTION, (Object[]) null);
        for (PendingRoleNotification notification : user.getPendingRoleNotifications()) {
            if (notification.getNotificationDate() == null) {
                notification.setNotificationDate(new Date());
            }
        }
        RegisteredUser admin = user.getPendingRoleNotifications().get(0).getAddedByUser();

        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "newUser", "admin", "host" }, new Object[] { user, admin,
                    getHostName() });
            message = buildMessage(user, subject, modelBuilder.build(), NEW_USER_SUGGESTION);
            sendEmail(message);
            userDAO.save(user);
        } catch (Exception e) {
            log.error("Error while sending reference reminder email to referee: ", e);
            return false;
        }
        return true;
    }
    
    @Transactional
    public void scheduleRegistryRevalidationRequestAndReminder() {
        Set<Integer> idsForWitchRequestHasBeenFired = new HashSet<Integer>();
        List<Person> registryContacts = configurationService.getAllRegistryUsers();
        List<ApplicationForm> forms = applicationDAO.getApplicationsDueRevalidationRequest();
        for (ApplicationForm form : forms) {
            idsForWitchRequestHasBeenFired.add(form.getId());
            createNotificationRecordIfNotExists(form, NotificationType.REPEAT_VALIDATION_REQUEST);
            for (Person person : registryContacts) {
                RegisteredUser user = userDAO.getUserByEmailIncludingDisabledAccounts(person.getEmail());
                setDigestNotificationType(user, DigestNotificationType.TASK_NOTIFICATION);
            }
        }
        forms = applicationDAO.getApplicationsDueRevalidationReminder();
        for (ApplicationForm form : forms) {
            if (!idsForWitchRequestHasBeenFired.contains(form.getId())) {
                createNotificationRecordIfNotExists(form, NotificationType.REPEAT_VALIDATION_REMINDER);
                for (Person person : registryContacts) {
                    RegisteredUser user = userDAO.getUserByEmailIncludingDisabledAccounts(person.getEmail());
                    setDigestNotificationType(user, DigestNotificationType.TASK_REMINDER);
                }
            }
        }
    }


    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been rejected.<br/>
     * Finds all applications in the system which have recently been rejected, and;<br/>
     * Schedules their Primary Supervisors to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Primary Supervisor
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <li>Administrators can reject applications, when:
     * <ol>
     * <li>They are not in the rejected, approved or withdrawn states.</li>
     * </ol>
     * </li>
     * <li>Approvers can reject applications, when:
     * <ol>
     * <li>They are in the approval state.</li>
     * </ol>
     * </li>
     * <li>Primary Supervisors are notified of rejections, when:
     * <ol>
     * <li>Applications have been rejected within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    @Transactional
    public void scheduleRejectionConfirmationToAdministratorsAndSupervisor() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueRejectNotifications()) {
            form.setRejectNotificationDate(new Date());
            RegisteredUser supervisor = getPrimarySupervisorAsUserFromLatestApprovalRound(form);
            CollectionUtils.forAllDo(form.getProgram().getAdministrators(), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
            if (supervisor != null) {
                setDigestNotificationType(supervisor, DigestNotificationType.UPDATE_NOTIFICATION);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when reviews have been provided.<br/>
     * Finds all applications in the system for which reviews have recently been provided, and;<br/>
     * Schedules their Administrators to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Referees can provide reviews, while:
     * <ol>
     * <li>Applications in the current review state.</li>
     * </ol>
     * </li>
     * <li>Administrators are scheduled to be notified, when:
     * <ol>
     * <li>Reviews have been provided within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    @Transactional
    public void scheduleReviewSubmittedConfirmation() {
        for (ReviewComment comment : commentDAO.getReviewCommentsDueNotification()) {
            comment.setAdminsNotified(true);
            CollectionUtils.forAllDo(getProgramAdministrators(comment.getApplication()), new UpdateDigestNotificationClosure(
                    DigestNotificationType.UPDATE_NOTIFICATION));
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to evaluate reviews.<br/>
     * Finds all applications in the system that urgently require review evaluation, and;<br/>
     * Schedules their Reviewers to be reminded.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Admistrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can evaluate reviews, while:
     * <ol>
     * <li>Applications are in the current review state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be reminded to do so, when:
     * <ol>
     * <li>They have previously been notified or reminded to do so, and;</li>
     * <li>The time elapsed since the previous notification or reminder:
     * <ol>
     * <li>Equals the system defined maximum time interval between reminders, or;</li>
     * <li>Exceeds the system defined maximum time interval between reminders.</li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    @Transactional
    public void scheduleReviewEvaluationReminder() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_EVALUATION_REMINDER, ApplicationFormStatus.REVIEW)) {
            ReviewRound latestReviewRound = form.getLatestReviewRound();
            boolean sendDigest = true;
            for (Reviewer reviewer : latestReviewRound.getReviewers()) {
                if (reviewer.getReview() == null) {
                    sendDigest = false;
                    break;
                }
            }

            if (sendDigest && !form.hasReviewEvaluationComment()) {
                createNotificationRecordIfNotExists(form, NotificationType.REVIEW_EVALUATION_REMINDER);
                CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
                RegisteredUser delegate = form.getApplicationAdministrator();
                if (delegate != null) {
                    setDigestNotificationType(delegate, DigestNotificationType.TASK_REMINDER);
                }
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Reminds users that they are required to confirm supervision.<br/>
     * Finds all applications in the system that urgently require supervision confirmation, and;<br/>
     * Schedules their Primary Supervisors to be reminded.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Primary Supervisor
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Primary Supervisors can confirm supervision, while:
     * <ol>
     * <li>Applications are in the current approval state.</li>
     * </ol>
     * </li>
     * <li>They are scheduled to be reminded to do so, when:
     * <ol>
     * <li>They have previously been notified or reminded to do so, and;</li>
     * <li>The time elapsed since the previous notification or reminder:
     * <ol>
     * <li>Equals the system defined maximum time interval between reminders, or;</li>
     * <li>Exceeds the system defined maximum time interval between reminders.</li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 3 (Task Reminder)
     * </p>
     */
    @Transactional
    public void scheduleConfirmSupervisionRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (Supervisor supervisor : supervisorDAO.getPrimarySupervisorsDueNotification()) {
            idsForWhichRequestWasFired.add(supervisor.getId());
            supervisor.setLastNotified(new Date());
            setDigestNotificationType(supervisor.getUser(), DigestNotificationType.TASK_NOTIFICATION);
        }

        for (Supervisor supervisor : supervisorDAO.getPrimarySupervisorsDueReminder()) {
            if (!idsForWhichRequestWasFired.contains(supervisor.getId())) {
                supervisor.setLastNotified(new Date());
                setDigestNotificationType(supervisor.getUser(), DigestNotificationType.TASK_REMINDER);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been moved into the interview state.<br/>
     * Finds all applications in the system that have recently been moved into the interview state, and;<br/>
     * Schedules their Applicants to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Applicant
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Administrators can move applications into the interview state, while:
     * <ol>
     * <li>They are not in the rejected, approved or withdrawn states.</li>
     * </ol>
     * </li>
     * <li>Applicants are scheduled to be notified, when:
     * <ol>
     * <li>Applications have been moved into the interview state within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    // TODO: check if 'getApplicationsDueNotificationForStateChangeEvent' picks applications that have been moved
    // to interview but interview needs to be scheduled yet
    @Transactional
    public void scheduleApplicationUnderInterviewNotification() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(
                NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION, ApplicationFormStatus.INTERVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION);
            setDigestNotificationType(form.getApplicant(), DigestNotificationType.UPDATE_NOTIFICATION);
        }
    }

    @Transactional
    public NotificationRecord createNotificationRecordIfNotExists(final ApplicationForm form, final NotificationType type) {
        NotificationRecord notificationRecord = form.getNotificationForType(type);
        if (notificationRecord == null) {
            notificationRecord = new NotificationRecord(type);
            form.addNotificationRecord(notificationRecord);
        }
        notificationRecord.setDate(new Date());
        applicationDAO.save(form);
        return notificationRecord;
    }

    private RegisteredUser getPrimarySupervisorAsUserFromLatestApprovalRound(final ApplicationForm form) {
        if (form.getLatestApprovalRound() != null) {
            for (Supervisor supervisor : form.getLatestApprovalRound().getSupervisors()) {
                if (isTrue(supervisor.getIsPrimary())) {
                    return supervisor.getUser();
                }
            }
        }
        return null;
    }
}
