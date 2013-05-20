package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPLICATION_SUBMIT_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.EXPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.IMPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEW_VOTE_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEW_VOTE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.MOVED_TO_APPROVED_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_PASSWORD_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REGISTRATION_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REJECTED_NOTIFICATION;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
public class MailSendingService extends AbstractMailSendingService {

    private final Logger log = LoggerFactory.getLogger(MailSendingService.class);

    private final String admissionsOfferServiceLevel;

    private final String uclProspectusLink;

    public MailSendingService() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public MailSendingService(final MailSender mailSender, final ConfigurationService configurationService, final ApplicationFormDAO formDAO,
            final UserDAO userDAO, final RoleDAO roleDAO, final RefereeDAO refereeDAO, final EncryptionUtils encryptionUtils,
            @Value("${application.host}") final String host, @Value("${admissions.servicelevel.offer}") final String admissionsOfferServiceLevel,
            @Value("${ucl.prospectus.url}") final String uclProspectusLink) {
        super(mailSender, formDAO, configurationService, userDAO, roleDAO, refereeDAO, encryptionUtils, host);
        this.admissionsOfferServiceLevel = admissionsOfferServiceLevel;
        this.uclProspectusLink = uclProspectusLink;
    }

    private void sendReferenceRequest(Referee referee, ApplicationForm applicationForm) {

        processRefereeAndGetAsUser(referee);

        PrismEmailMessage message = null;
        try {
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(applicationForm.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "referee", "adminsEmails", "applicant", "application", "programme", "host" },
                    new Object[] { referee, adminsEmails, applicationForm.getApplicant(), applicationForm, applicationForm.getProgrammeDetails(), host });

            String subject = resolveMessage(REFEREE_NOTIFICATION, applicationForm);

            message = buildMessage(referee.getUser(), subject, modelBuilder.build(), REFEREE_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending reference request mail: ", e, message);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to provide references.
     * <p/>
     * <p>
     * <b>Recipients</b> Referees
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Referees are notified to provide references, when:
     * <ol>
     * <li>Administrators move applications from the validation state into a state, that:
     * <ol>
     * <li>Is not the rejected or approved state</li>
     * </ol>
     * </li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    public void sendReferenceRequest(List<Referee> referees, ApplicationForm applicationForm) {
        for (Referee referee : referees) {
            referee.setLastNotified(new Date());
            sendReferenceRequest(referee, applicationForm);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they have submitted applications.
     * <p/>
     * <p>
     * <b>Recipients</b> Applicant
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * APPLICATION_SUBMIT_CONFIRMATION
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Applicants are notified, when:
     * <ol>
     * <li>They submit applications.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    public void sendSubmissionConfirmationToApplicant(ApplicationForm form) {
        PrismEmailMessage message = null;
        try {
            RegisteredUser applicant = form.getApplicant();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(form.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host",
                    "admissionOfferServiceLevel", "previousStage" },
                    new Object[] { adminsEmails, form, form.getApplicant(), configurationService.getAllRegistryUsers(), getHostName(),
                            admissionsOfferServiceLevel, form.getOutcomeOfStage() });

            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(form.getStatus())) {
                model.put("reason", form.getRejection().getRejectionReason());
                if (form.getRejection().isIncludeProspectusLink()) {
                    model.put("prospectusLink", uclProspectusLink);
                }

            }

            Object[] args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle() };
            String subject = resolveMessage(APPLICATION_SUBMIT_CONFIRMATION, args);

            message = buildMessage(applicant, subject, model, APPLICATION_SUBMIT_CONFIRMATION);
            sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending submission confirmation to applicant: ", e, message);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been rejected.
     * <p/>
     * <p>
     * <b>Recipients</b> Applicant
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
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
     * <li>Applicants are notified of rejections, when:
     * <ol>
     * <li>Applications are rejected by Administrators or Approvers.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    // TODO: Current business logic is incorrect. Administrator cannot reject application when it is in approval state.
    public void sendRejectionConfirmationToApplicant(ApplicationForm form) {
        PrismEmailMessage message = null;
        try {
            RegisteredUser applicant = form.getApplicant();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(form.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host",
                    "admissionOfferServiceLevel", "previousStage" },
                    new Object[] { adminsEmails, form, form.getApplicant(), configurationService.getAllRegistryUsers(), getHostName(),
                            admissionsOfferServiceLevel, form.getOutcomeOfStage() });

            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(form.getStatus())) {
                model.put("reason", form.getRejection().getRejectionReason());
                if (form.getRejection().isIncludeProspectusLink()) {
                    model.put("prospectusLink", uclProspectusLink);
                }
            }

            Object[] args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName(),
                    form.getOutcomeOfStage().displayValue() };
            String subject = resolveMessage(REJECTED_NOTIFICATION, args);

            message = buildMessage(applicant, subject, model, REJECTED_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending rejection confirmation to applicant: ", e, message);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been approved.
     * <p/>
     * <p>
     * <b>Recipients</b> Applicant
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Administrators can approve applications, while:
     * <ol>
     * <li>They are not in the rejected, approved or withdrawn states.</li>
     * </ol>
     * </li>
     * <li>Approvers can approve applications, while:
     * <ol>
     * <li>They are in the approval state.</li>
     * </ol>
     * </li>
     * <li>Applicants are notified, when:
     * <ol>
     * <li>Applications are approved.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    public void sendApprovedNotification(ApplicationForm form) {
        PrismEmailMessage message = null;
        try {
            RegisteredUser applicant = form.getApplicant();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(form.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host",
                    "admissionOfferServiceLevel", "previousStage" },
                    new Object[] { adminsEmails, form, form.getApplicant(), configurationService.getAllRegistryUsers(), getHostName(),
                            admissionsOfferServiceLevel, form.getOutcomeOfStage() });

            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(form.getStatus())) {
                model.put("reason", form.getRejection().getRejectionReason());
                if (form.getRejection().isIncludeProspectusLink()) {
                    model.put("prospectusLink", uclProspectusLink);
                }
            }

            String subject = resolveMessage(MOVED_TO_APPROVED_NOTIFICATION, form, form.getOutcomeOfStage());

            message = buildMessage(applicant, subject, model, MOVED_TO_APPROVED_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending approved notification email to applicant: ", e, message);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when interviews have been scheduled.
     * <p/>
     * <p>
     * <b>Recipients</b> Interviewer
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Administrators and Delegate Interview Administrators can schedule interviews, while:
     * <ol>
     * <li>Applications are in the current interview state, and;</li>
     * <li>Interviews have not been scheduled.</li>
     * </ol>
     * </li>
     * <li>Interviewers are notified, when:
     * <ol>
     * <li>Interviews have been scheduled.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    public void sendInterviewConfirmationToInterviewers(List<Interviewer> interviewers) {
        PrismEmailMessage message = null;
        for (Interviewer interviewer : interviewers) {
            try {
                ApplicationForm applicationForm = interviewer.getInterview().getApplication();
                String subject = resolveMessage(INTERVIEWER_NOTIFICATION, applicationForm);
                List<RegisteredUser> admins = applicationForm.getProgram().getAdministrators();
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "interviewer", "application", "applicant", "host" },
                        new Object[] { getAdminsEmailsCommaSeparatedAsString(admins), interviewer, applicationForm, applicationForm.getApplicant(),
                                getHostName() });
                message = buildMessage(interviewer.getUser(), subject, modelBuilder.build(), INTERVIEWER_NOTIFICATION);
                sendEmail(message);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new PrismMailMessageException("Error while sending interview confirmation email to interviewer: ", e, message);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when interviews have been scheduled.
     * <p/>
     * <p>
     * <b>Recipients</b> Applicant
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <li>Administrators and Delegate Interview Administrators can schedule interviews, while:
     * <ol>
     * <li>Applications are in the current interview state, and;</li>
     * <li>Interviews have not been scheduled.</li>
     * </ol>
     * </li>
     * <ol>
     * <li>Applicants are notified, when:
     * <ol>
     * <li>Interviews have been scheduled.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     * 
     * @param applicationForm
     */
    public void sendInterviewConfirmationToApplicant(ApplicationForm applicationForm) {
        PrismEmailMessage message = null;
        try {
            String subject = resolveMessage(MOVED_TO_INTERVIEW_NOTIFICATION, applicationForm, applicationForm.getOutcomeOfStage());
            List<RegisteredUser> admins = applicationForm.getProgram().getAdministrators();
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host", "admissionOfferServiceLevel", "previousStage" },
                    new Object[] { getAdminsEmailsCommaSeparatedAsString(admins), applicationForm, applicationForm.getApplicant(),
                            configurationService.getAllRegistryUsers(), getHostName(), admissionsOfferServiceLevel, applicationForm.getOutcomeOfStage() });

            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(applicationForm.getStatus())) {
                model.put("reason", applicationForm.getRejection().getRejectionReason());
                if (applicationForm.getRejection().isIncludeProspectusLink()) {
                    model.put("prospectusLink", uclProspectusLink);
                }

            }

            message = buildMessage(applicationForm.getApplicant(), subject, model, MOVED_TO_INTERVIEW_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PrismMailMessageException("Error while sending interview confirmation email to applicant: ", e, message);
        }
    }

    public void sendInterviewVoteNotificationToInterviewerParticipants(Interview interview) {
        ApplicationForm applicationForm = interview.getApplication();

        String subject = resolveMessage(INTERVIEW_VOTE_NOTIFICATION, applicationForm);

        PrismEmailMessage message = null;
        for (InterviewParticipant participant : interview.getParticipants()) {
            try {
                List<RegisteredUser> admins = applicationForm.getProgram().getAdministrators();
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "participant", "application", "host" }, new Object[] {
                        getAdminsEmailsCommaSeparatedAsString(admins), participant, applicationForm, getHostName() });
                message = buildMessage(participant.getUser(), subject, modelBuilder.build(), INTERVIEW_VOTE_NOTIFICATION);
                sendEmail(message);
                participant.setLastNotified(new Date());
            } catch (Exception e) {
                log.error("Error while sending interview vote notification email to interview participant: " + participant.getUser().getEmail(), e);
            }
        }
    }

    /**
     * @param participant
     *            interview participant who has posted a vote.
     */
    public void sendInterviewVoteConfirmationToAdministrators(InterviewParticipant participant) {
        Interview interview = participant.getInterview();
        ApplicationForm application = interview.getApplication();
        Collection<RegisteredUser> administrators = getApplicationOrProgramAdministrators(application);

        PrismEmailMessage message = null;
        String subject = resolveMessage(INTERVIEW_VOTE_CONFIRMATION, application);
        for (RegisteredUser administrator : administrators) {
            if(administrator.getId() == participant.getUser().getId()){
                continue; // administrator has voted himself, no need to notify him
            }
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "administrator", "application", "participant", "host" }, new Object[] {
                        administrator, application, participant, getHostName() });
                message = buildMessage(administrator, subject, modelBuilder.build(), INTERVIEW_VOTE_CONFIRMATION);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending interview vote confirmation email to administrator: " + administrator.getDisplayName(), e.getMessage());
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when a data export has failed.
     * <p/>
     * <p>
     * <b>Recipients</b> Super Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Super Administrators are notified, when:
     * <ol>
     * <li>A data export has failed.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    public void sendExportErrorMessage(List<RegisteredUser> superadmins, String messageCode, Date timestamp) {
        PrismEmailMessage message = null;
        if (messageCode == null) {
            throw new PrismMailMessageException("Error while sending export error message: messageCode is null", message);
        }
        String subject = resolveMessage(EXPORT_ERROR, (Object[]) null);
        for (RegisteredUser user : superadmins) {
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "message", "time", "host" }, new Object[] { user, messageCode,
                        timestamp, getHostName() });
                message = buildMessage(user, subject, modelBuilder.build(), EXPORT_ERROR);
                sendEmail(message);
            } catch (Exception e) {
                throw new PrismMailMessageException("Error while sending export error message: ", e, message);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when references have been provided.<br/>
     * Finds all applications in the system for which references have recently been provided, and;<br/>
     * Schedules their Applicants and Administrators to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Applicant<br/>
     * Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * REFERENCE_SUBMIT_CONFIRMATION
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Referees can provide references, while:
     * <ol>
     * <li>Applications are not in the rejected, approved or withdrawn states.</li>
     * </ol>
     * </li>
     * <li>Applicants and Administrators are scheduled to be notified, when:
     * <ol>
     * <li>References have been provided within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleReferenceSubmitConfirmation(ApplicationForm form) {
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        List<RegisteredUser> users = new ArrayList<RegisteredUser>(admins.size() + 1);
        users.add(form.getApplicant());
        users.addAll(admins);
        CollectionUtils.forAllDo(users, new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when supervision has been confirmed.<br/>
     * Finds all applications in the system for which supervision has recently been confirmed, and;<br/>
     * Schedules their Administrators to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Primary Supervisors can confirm supervision, while:
     * <ol>
     * <li>Applications are in the current approval state.</li>
     * </ol>
     * </li>
     * <li>Administrators are scheduled to be notified, when:
     * <ol>
     * <li>Supervision has been confirmed within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleSupervisionConfirmedNotification(final ApplicationForm form) {
        CollectionUtils.forAllDo(form.getProgram().getAdministrators(), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when applications have been withdrawn.<br/>
     * Finds all applications in the system that have recently been updated, and;<br/>
     * Schedules their Administrators to be notified.
     * <p/>
     * <p>
     * <b>Recipients</b><br/>
     * Administrator<br/>
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b>
     * <ol>
     * <li>Applicants can withdraw applications, while:
     * <ol>
     * <li>They are not in the rejected, approved or withdrawn states.</li>
     * </ol>
     * </li>
     * <li>Administrators are scheduled to be notified of withdrawals, when:
     * <ol>
     * <li>Applications have been withdrawn within the last 24 hours.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void scheduleWithdrawalConfirmation(List<Referee> referees, final ApplicationForm form) {
        Map<Integer, RegisteredUser> usersToNotify = new HashMap<Integer, RegisteredUser>();
        for (Referee referee : referees) {
            if (referee.getUser() != null) {
                usersToNotify.put(referee.getUser().getId(), referee.getUser());
            }
        }

        for (RegisteredUser admin : form.getProgram().getAdministrators()) {
            usersToNotify.put(admin.getId(), admin);
        }

        RegisteredUser applicationAdministrator = form.getApplicationAdministrator();
        if (applicationAdministrator != null) {
            usersToNotify.put(applicationAdministrator.getId(), applicationAdministrator);
        }

        for (RegisteredUser reviewers : getReviewersFromLatestReviewRound(form)) {
            usersToNotify.put(reviewers.getId(), reviewers);
        }

        for (RegisteredUser interviewer : getInterviewersFromLatestInterviewRound(form)) {
            usersToNotify.put(interviewer.getId(), interviewer);
        }

        for (RegisteredUser supervisor : getSupervisorsAsUsersFromLatestApprovalRound(form)) {
            usersToNotify.put(supervisor.getId(), supervisor);
        }

        CollectionUtils.forAllDo(usersToNotify.values(), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when a data import has failed.
     * <p/>
     * <p>
     * <b>Recipients</b> Super Administrator
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Super Administrators are notified, when:
     * <ol>
     * <li>A data import has failed.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    public void sendImportErrorMessage(List<RegisteredUser> superadmins, String messageCode, Date timestamp) {
        PrismEmailMessage message = null;
        if (messageCode == null) {
            throw new PrismMailMessageException("Error while sending import error message: messageCode is null", message);
        }
        String subject = resolveMessage(IMPORT_ERROR, (Object[]) null);
        for (RegisteredUser user : superadmins) {
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "message", "time", "host" }, new Object[] { user, messageCode,
                        timestamp, getHostName() });
                message = buildMessage(user, subject, modelBuilder.build(), IMPORT_ERROR);
                sendEmail(message);
            } catch (Exception e) {
                throw new PrismMailMessageException("Error while sending import error message: ", e, message);
            }
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users that they are required to confirm registrations.
     * <p/>
     * <p>
     * <b>Recipients</b> Users
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Users can register, when they are:
     * <ol>
     * <li>Invited to do so by Administrators, or;</li>
     * <li>In the process of initiating applications;</li>
     * </ol>
     * </li>
     * <li>They are notified to confirm registrations, when:
     * <ol>
     * <li>Submitted registrations.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    public void sendRegistrationConfirmation(RegisteredUser user, String action) {
        PrismEmailMessage message = null;
        if (action == null) {
            throw new PrismMailMessageException("Error while sending confirmation email to registering user: action is null", message);
        }

        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "action", "host" }, new Object[] { user, action, getHostName() });
            String subject = resolveMessage(REGISTRATION_CONFIRMATION, (Object[]) null);
            message = buildMessage(user, subject, modelBuilder.build(), REGISTRATION_CONFIRMATION);
            sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending confirmation email to registering user: ", e, message);
        }
    }

    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when temporary passwords have been set for their account.
     * <p/>
     * <p>
     * <b>Recipients</b> Any User Role
     * </p>
     * <p>
     * <b>Previous Email Template Name</b><br/>
     * Kevin to Insert
     * </p>
     * <p>
     * <b>Business Rules</b><br/>
     * <ol>
     * <li>Users can request a temporary password, at:
     * <ol>
     * <li>Any time.</li>
     * </ol>
     * </li>
     * <li>Users are notified, when:
     * <ol>
     * <li>Their temporary password has been created.</li>
     * </ol>
     * </li>
     * </ol>
     * </p>
     * <p>
     * <b>Notification Type</b> Immediate Notification
     * </p>
     */
    public void sendResetPasswordMessage(final RegisteredUser user, final String newPassword) throws PrismMailMessageException {
        PrismEmailMessage message = null;
        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "newPassword", "host" }, new Object[] { user, newPassword, getHostName() });
            String subject = resolveMessage(NEW_PASSWORD_CONFIRMATION, (Object[]) null);
            message = buildMessage(user, subject, modelBuilder.build(), NEW_PASSWORD_CONFIRMATION);
            sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending reset password email: ", e, message);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<RegisteredUser> getSupervisorsAsUsersFromLatestApprovalRound(final ApplicationForm form) {
        if (form.getLatestApprovalRound() != null) {
            return CollectionUtils.collect(form.getLatestApprovalRound().getSupervisors(), new Transformer() {
                @Override
                public Object transform(final Object input) {
                    return ((Supervisor) input).getUser();
                }
            });
        }
        return Collections.emptyList();
    }

    public void scheduleAdmitterProvidedCommentNotification(ApplicationForm form) {
        CollectionUtils.forAllDo(getProgramAdministrators(form), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
    }

}
