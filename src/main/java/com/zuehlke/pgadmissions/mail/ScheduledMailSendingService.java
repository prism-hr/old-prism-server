package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_USER_SUGGESTION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REGISTRY_VALIDATION_REQUEST;
import static com.zuehlke.pgadmissions.domain.enums.NotificationType.INTERVIEW_REMINDER;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.NotificationRecordDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSourceFactory;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.Environment;

@Service
@Transactional
public class ScheduledMailSendingService extends AbstractMailSendingService {

    private final Logger log = LoggerFactory.getLogger(ScheduledMailSendingService.class);

    private final NotificationRecordDAO notificationRecordDAO;

    private final CommentDAO commentDAO;

    private final SupervisorDAO supervisorDAO;

    private final StageDurationDAO stageDurationDAO;
    
    private final ReviewerDAO reviewerDAO;
    
    private final ApplicationsService applicationsService;
    
    private final CommentFactory commentFactory;

    private final CommentService commentService;
    
    private final PdfAttachmentInputSourceFactory pdfAttachmentInputSourceFactory;
    
    private final PdfDocumentBuilder pdfDocumentBuilder;

	private final RefereeDAO refereeDAO;
	
	private final UserService userService;
	
    @Autowired
    public ScheduledMailSendingService(final MailSender mailSender,
            final ApplicationFormDAO applicationFormDAO, final NotificationRecordDAO notificationRecordDAO,
            final CommentDAO commentDAO, final SupervisorDAO supervisorDAO, final StageDurationDAO stageDurationDAO,
            final ReviewerDAO reviewerDAO, final ApplicationsService applicationsService, final ConfigurationService configurationService,
            final CommentFactory commentFactory, final CommentService commentService,
            final PdfAttachmentInputSourceFactory pdfAttachmentInputSourceFactory, final PdfDocumentBuilder pdfDocumentBuilder,
            final RefereeDAO refereeDAO, final UserService userService, final UserDAO userDAO, final RoleDAO roleDAO, final EncryptionUtils encryptionUtils) {
        super(mailSender, applicationFormDAO, configurationService, userDAO, roleDAO, refereeDAO, encryptionUtils);
        this.notificationRecordDAO = notificationRecordDAO;
        this.commentDAO = commentDAO;
        this.supervisorDAO = supervisorDAO;
        this.stageDurationDAO = stageDurationDAO;
        this.reviewerDAO = reviewerDAO;
		this.applicationsService = applicationsService;
		this.commentService = commentService;
		this.commentFactory = commentFactory;
		this.pdfAttachmentInputSourceFactory = pdfAttachmentInputSourceFactory;
		this.pdfDocumentBuilder = pdfDocumentBuilder;
		this.refereeDAO = refereeDAO;
		this.userService = userService;
    }

    public ScheduledMailSendingService() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

	public void sendDigestsToUsers() {
		log.info("Sending daily digests to users");
		
		String taskNotificationSubject = resolveMessage(DIGEST_TASK_NOTIFICATION, (Object[])null);
		String taskReminderSubject = resolveMessage(DIGEST_TASK_REMINDER, (Object[])null);
		String updateNotificationSubject = resolveMessage(DIGEST_UPDATE_NOTIFICATION, (Object[])null);

		PrismEmailMessageBuilder digestTaskNotification = new PrismEmailMessageBuilder().subject(
		        taskNotificationSubject).emailTemplate(EmailTemplateName.DIGEST_TASK_NOTIFICATION);

		PrismEmailMessageBuilder digestTaskReminder = new PrismEmailMessageBuilder().subject(
		        taskReminderSubject).emailTemplate(EmailTemplateName.DIGEST_TASK_REMINDER);

		PrismEmailMessageBuilder digestUpdateNotification = new PrismEmailMessageBuilder().subject(
		        updateNotificationSubject).emailTemplate(EmailTemplateName.DIGEST_UPDATE_NOTIFICATION);

		for (Integer userId : userService.getAllUsersInNeedOfADigestNotification()) {
			try {
				final RegisteredUser user = userService.getUser(userId);
				EmailModelBuilder modelBuilder = new EmailModelBuilder() {

					@Override
					public Map<String, Object> build() {
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("user", user);
						model.put("host", getHostName());
						return model;
					}
				};

				digestTaskNotification.model(modelBuilder);
				digestTaskReminder.model(modelBuilder);
				digestUpdateNotification.model(modelBuilder);

				switch (user.getDigestNotificationType()) {
				case TASK_NOTIFICATION:
					digestTaskNotification.to(user);
					sendEmail(digestTaskNotification.build());
					break;
				case TASK_REMINDER:
					digestTaskReminder.to(user);
					sendEmail(digestTaskReminder.build());
					break;
				case UPDATE_NOTIFICATION:
					digestUpdateNotification.to(user);
					sendEmail(digestUpdateNotification.build());
					break;
				case NONE:
				default:
					break;
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		log.info("Reseting daily digests to users");
		userService.resetDigestNotificationsForAllUsers();
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
        final StageDuration approvalDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.APPROVAL);
        for (ApplicationForm form : applicationDAO.getApplicationsDueApprovalReminder()) {
            ApprovalRound approvalRound = form.getLatestApprovalRound();
            if (approvalRound != null) {
                createNotificationRecordIfNotExists(form, NotificationType.APPROVAL_REMINDER);
                DateTime approvalRoundExpiryDate = DateUtils.addWorkingDaysInMinutes(new DateTime(approvalRound.getCreatedDate()), approvalDuration.getDurationInMinutes());
                if (approvalRoundExpiryDate.isAfterNow()) {
                    CollectionUtils.forAllDo(form.getProgram().getApprovers(), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
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
            RegisteredUser delegate = form.getApplicationAdministrator();
            if (delegate != null) {
                setDigestNotificationType(delegate, DigestNotificationType.TASK_NOTIFICATION);
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
            RegisteredUser delegate = form.getApplicationAdministrator();
            if (delegate != null) {
            	setDigestNotificationType(delegate, DigestNotificationType.TASK_REMINDER);
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
            for (Reviewer reviewer : form.getLatestReviewRound().getReviewers()) {
                if (reviewer.getReview()==null) {
                    setDigestNotificationType(reviewer.getUser(), DigestNotificationType.TASK_REMINDER);
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
     *    <li>Applications have been moved into the review state in the last 24 hours, and;
     *       <ol>
     *       <li>No closing dates have been specified, or;/li>
     *       </ol></li>
     *    </ol></li>
     *    <li>Application closing dates have expired in the last 24 hours, and.</li>
     *    </ol></li>
     * </ol>
        * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 2 (Task Notification)
     * </p>
     */
    @Deprecated
    public void scheduleReviewRequest() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.REVIEW_REQUEST, ApplicationFormStatus.REVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REQUEST);
            for (Reviewer reviewer : form.getLatestReviewRound().getReviewers()) {
                if (reviewer.getReview()==null) {
                    setDigestNotificationType(reviewer.getUser(), DigestNotificationType.TASK_NOTIFICATION);
                }
            }
        }
    }
    
    public void scheduleReviewRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.REVIEW_REQUEST, ApplicationFormStatus.REVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REQUEST);
            idsForWhichRequestWasFired.add(form.getId());
            for (Reviewer reviewer : form.getLatestReviewRound().getReviewers()) {
                if (reviewer.getReview()==null) {
                    setDigestNotificationType(reviewer.getUser(), DigestNotificationType.TASK_NOTIFICATION);
                }
            }
        }
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW)) {
            if (!idsForWhichRequestWasFired.contains(form.getId())) {
                createNotificationRecordIfNotExists(form, NotificationType.REVIEW_REMINDER);
                for (Reviewer reviewer : form.getLatestReviewRound().getReviewers()) {
                    if (reviewer.getReview()==null) {
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
    
    public void scheduleValidationRequestAndReminder() {
        Set<Integer> idsForWhichRequestWasFired = new HashSet<Integer>();
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.UPDATED_NOTIFICATION, ApplicationFormStatus.VALIDATION)) {
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
    // TODO: Business logic is currently incorrect. Administrator cannot restart the approval state until requested to do so by an Approver.
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
    public void scheduleApprovedConfirmation() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueApprovedNotifications()) {
            createNotificationRecordIfNotExists(form, NotificationType.APPROVED_NOTIFICATION);
            RegisteredUser primarySupervisor = getPrimarySupervisorsAsUserFromLatestApprovalRound(form);
            if (null!=primarySupervisor) {
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
                setDigestNotificationType(delegate, DigestNotificationType.TASK_REMINDER);
            }
        }
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
    	for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW)) {
            createNotificationRecordIfNotExists(form, INTERVIEW_REMINDER);
            CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
        }
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
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
            CollectionUtils.forAllDo(getInterviewersFromLatestInterviewRound(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_NOTIFICATION));
        }
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
        for (ApplicationForm form : applicationDAO.getApplicationsDueUserReminder(NotificationType.INTERVIEW_REMINDER, ApplicationFormStatus.INTERVIEW)) {
            createNotificationRecordIfNotExists(form, NotificationType.INTERVIEW_REMINDER);
            CollectionUtils.forAllDo(getInterviewersFromLatestInterviewRound(form), new UpdateDigestNotificationClosure(DigestNotificationType.TASK_REMINDER));
        }
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
    public void scheduleApplicationUnderReviewNotification() {
        for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW)) {
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
    	log.info("Running sendReferenceReminder Task");
		PrismEmailMessage message = null;
		try {
    		List<Referee> refereesDueAReminder = refereeDAO.getRefereesDueAReminder();
    		for (Referee referee : refereesDueAReminder) {
    			String subject = resolveMessage(REFEREE_REMINDER, referee.getApplication());
    			
    			ApplicationForm applicationForm = referee.getApplication();
    			String adminsEmails = getAdminsEmailsCommaSeparatedAsString(applicationForm.getProgram().getAdministrators());
    			EmailModelBuilder modelBuilder = getModelBuilder(
    					new String[] {"adminsEmails", "referee", "application", "applicant", "host"},
    					new Object[] {adminsEmails, referee, applicationForm, applicationForm.getApplicant(), getHostName()}
    					);
    			
    			message = buildMessage(referee.getUser(), subject, modelBuilder.build(), REFEREE_REMINDER);
    			sendEmail(message);
    			referee.setLastNotified(new Date());
				refereeDAO.save(referee);
    		}
		} catch (Exception e) {
			throw new PrismMailMessageException("Error while sending reference reminder email to referee: ",
					e.getCause(), message);
		}
	}
    
    public void sendNewUserInvitation() {
        log.info("Running sendNewUserInvitation Task");
        PrismEmailMessage message = null;
        try {
            List<RegisteredUser> users = userDAO.getUsersWithPendingRoleNotifications();
            String subject = resolveMessage(NEW_USER_SUGGESTION, (Object[])null);
            for (RegisteredUser user : users) {
                for (PendingRoleNotification notification : user.getPendingRoleNotifications()) {
                    if (notification.getNotificationDate() == null) {
                        notification.setNotificationDate(new Date());
                    }
                }
                RegisteredUser admin =user.getPendingRoleNotifications().get(0).getAddedByUser();
                Program program =user.getPendingRoleNotifications().get(0).getProgram();
                String rolesString = constructRolesString(user);
                EmailModelBuilder modelBuilder = getModelBuilder(
                        new String[] {"newUser", "admin", "program", "newRoles", "host"},
                        new Object[] {user, admin, program, rolesString, getHostName()}
                        );
                message = buildMessage(user, subject, modelBuilder.build(), NEW_USER_SUGGESTION);
                sendEmail(message);
                userDAO.save(user);
            }
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending reference reminder email to referee: ",
                    e.getCause(), message);
        }
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
    	log.info("Running sendValidationRequestToRegistry Task");
    	PrismEmailMessage message = null;
    	try {
    		List<ApplicationForm> applications = applicationsService.getApplicationsDueRegistryNotification();
    		List<Person> registryContacts = configurationService.getAllRegistryUsers();
    		for (ApplicationForm applicationForm : applications) {
    			PrismEmailMessageBuilder messageBuilder = new PrismEmailMessageBuilder();
    			
    			String subject = resolveMessage(REGISTRY_VALIDATION_REQUEST, applicationForm);
    			messageBuilder.subject(subject);
    			
    			RegisteredUser currentUser = applicationForm.getAdminRequestedRegistry();
    			
    			messageBuilder.to(registryContacts, new Transformer() {
					
					@Override
					public Object transform(final Object input) {
						RegisteredUser user = new RegisteredUser();
						Person person = (Person)input;
						user.setId(person.getId());
						user.setEmail(person.getEmail());
						user.setFirstName(person.getFirstname());
						user.setLastName(person.getLastname());
						return user;
					}
				});
    			
    			messageBuilder.cc(currentUser);
    			
    			messageBuilder.templateName=REGISTRY_VALIDATION_REQUEST;
    			
    			String recipientList = createRecipientString(registryContacts);
    			EmailModelBuilder modelBuilder = getModelBuilder(
    					new String[] {"application", "sender", "host", "recipients", "admissionsValidationServiceLevel"},
    					new Object[] {applicationForm, currentUser, getHostName(), recipientList, Environment.getInstance().getAdmissionsValidationServiceLevel()}
    					);
    			
    			messageBuilder.model(modelBuilder);
    			
    			PdfAttachmentInputSource pdfAttachement = pdfAttachmentInputSourceFactory.getAttachmentDataSource(applicationForm.getApplicationNumber() + ".pdf",
						pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), applicationForm));
    			messageBuilder.attachments(pdfAttachement);
    			
    			message = messageBuilder.build();
    			sendEmail(message);
    			
    			
    			applicationForm.setRegistryUsersDueNotification(false);
    			Comment comment = commentFactory.createComment(applicationForm, applicationForm.getAdminRequestedRegistry(), getCommentText(registryContacts), CommentType.GENERIC, null);
    			commentService.save(comment);
    			applicationsService.save(applicationForm);
   				log.info("Notification sent to registry persons for application " + applicationForm.getApplicationNumber());
    		}
    	}
    	catch (Exception e) {
    		log.warn("Error while sending validation request email to registry: {}", e);
    	}
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
            RegisteredUser supervisor = getPrimarySupervisorsAsUserFromLatestApprovalRound(form);
            if (supervisor!=null) {
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
    //REMOVE
    //the old system was not sending request to admins to evaluate the review but just a notification of review submitted
    //which means "evaluate it". This is done by the method 'scheduleReviewSubmittedConfirmation'.
    //This explains why the code below is sending digests  to reviewers instead of admins (as it should be by spec!).
    public void scheduleReviewEvaluationRequest() {
//        for (Reviewer reviewer : reviewerDAO.getReviewersDueNotification()) {
//            reviewer.setLastNotified(new Date());
//            setDigestNotificationType(reviewer.getUser(), DigestNotificationType.TASK_NOTIFICATION);
//        }
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
    //REMOVE
    //the old system was not sending reminder to admins to evaluate the review but just a notification of review submitted
    //which means "evaluate it". This is done by the method 'scheduleReviewSubmittedConfirmation'.
    //This explains why the code below is sending digests  to reviewers instead of admins (as it should be by spec!)
    public void scheduleReviewEvaluationReminder() {
//        for (Reviewer reviewer : reviewerDAO.getReviewersDueReminder()) {
//            reviewer.setLastNotified(new Date());
//            setDigestNotificationType(reviewer.getUser(), DigestNotificationType.TASK_REMINDER);
//        }
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
            setDigestNotificationType(supervisor.getUser(), DigestNotificationType.TASK_NOTIFICATION);
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
            setDigestNotificationType(supervisor.getUser(), DigestNotificationType.TASK_REMINDER);
        }
    }
    
    /**
     * <p>
     * <b>Summary</b><br/>
        * Informs users when applications have been moved into the interview state.<br/>
     * Finds all applications in the system that have recently been moved into the interview state, and;<br/> 
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
     * <li>Administrators can move applications into the interview state, while:
     *    <ol>
        *    <li>They are not in the rejected, approved or withdrawn states.</li>
     *    </ol></li>
     * <li>Applicants are scheduled to be notified, when:
     *    <ol>
     *    <li>Applications have been moved into the interview state within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
        * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
        */
    // TODO: check if 'getApplicationsDueNotificationForStateChangeEvent' picks applications that have been moved
    // to interview but interview needs to be scheduled yet
	public void scheduleApplicationUnderInterviewNotification() {
		for (ApplicationForm form : applicationDAO.getApplicationsDueNotificationForStateChangeEvent(
				NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION, ApplicationFormStatus.INTERVIEW)) {
			createNotificationRecordIfNotExists(form, NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION);
			setDigestNotificationType(form.getApplicant(), DigestNotificationType.UPDATE_NOTIFICATION);
		}
	}
	
	private Collection<RegisteredUser> getProgramAdministrators(final ApplicationForm form) {
		return form.getProgram().getAdministrators();
	}
	
	private NotificationRecord createNotificationRecordIfNotExists(final ApplicationForm form,
			final NotificationType type) {
		NotificationRecord notificationRecord = form.getNotificationForType(type);
		if (notificationRecord == null) {
			notificationRecord = new NotificationRecord(type);
			form.addNotificationRecord(notificationRecord);
		}
		notificationRecord.setDate(new Date());
		applicationDAO.save(form);
		return notificationRecord;
	}
    
    private RegisteredUser getPrimarySupervisorsAsUserFromLatestApprovalRound(final ApplicationForm form) {
    	if (form.getLatestApprovalRound() != null) {
    		for (Supervisor supervisor : form.getLatestApprovalRound().getSupervisors()) {
    			if (isTrue(supervisor.getIsPrimary())) {
    				return supervisor.getUser();
    			}
    		}
    	}
    	return null;
    }
    
	private String createRecipientString(List<Person> registryContacts) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Person person : registryContacts) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(person.getFirstname());
			first = false;
		}
		return sb.toString();
	}
	
    private String getCommentText(List<Person> registryContacts) {
		StringBuilder sb = new StringBuilder();
		sb.append("Referred to UCL Admissions for advice on eligibility and fees status. Referral send to ");
		for (int i = 0; i < registryContacts.size(); i++) {
			Person contact = registryContacts.get(i);
			if (i > 0 && i < registryContacts.size() - 1) {
				sb.append(", ");
			}
			if (registryContacts.size() > 1 && i == (registryContacts.size() - 1)) {
				sb.append(" and ");
			}
			sb.append(contact.getFirstname() + " " + contact.getLastname() + " (" + contact.getEmail() + ")");
		}
		sb.append(".");
		return sb.toString();
	}
    
    private String constructRolesString(RegisteredUser user) {
        List<String> rolesList = new ArrayList<String>();
        String programTitle = null;

        for (PendingRoleNotification roleNotification : user.getPendingRoleNotifications()) {
            Authority authority = roleNotification.getRole().getAuthorityEnum();
            String roleAsString = StringUtils.capitalize(authority.toString().toLowerCase());
            
            if (authority != Authority.SUPERADMINISTRATOR && StringUtils.isBlank(programTitle)) {//looks like a bug
                programTitle = roleNotification.getProgram().getTitle();
            }
            
            switch (authority) {
            case INTERVIEWER:
            case REVIEWER:
            case SUPERVISOR:
                rolesList.add("Default " + roleAsString);
                break;
            default:
                rolesList.add(roleAsString);
                break;
            }
        }
        
        StringBuilder messageBuilder = new StringBuilder(StringUtils.join(rolesList.toArray(new String[]{}), ", ", 0, rolesList.size() - 1));
        messageBuilder.append(rolesList.get(rolesList.size() - 1));
        if (StringUtils.isNotBlank(programTitle)) {
            messageBuilder.append(" for ").append(programTitle);
        }
        
        return messageBuilder.toString();
    }
}
