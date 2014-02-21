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
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.OPPORTUNITY_REQUEST_OUTCOME;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REGISTRATION_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REJECTED_NOTIFICATION;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
public class MailSendingService extends AbstractMailSendingService {

    private static final Logger log = LoggerFactory.getLogger(MailSendingService.class);

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

    private void sendReferenceRequest(Referee referee, ApplicationForm application) {

        processRefereeAndGetAsUser(referee);

        PrismEmailMessage message = null;
        try {
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(application.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "referee", "adminsEmails", "applicant", "application", "programme", "host" },
                    new Object[] { referee, adminsEmails, application.getApplicant(), application, application.getProgrammeDetails(), host });

            String subject = resolveMessage(REFEREE_NOTIFICATION, application);

            message = buildMessage(referee.getUser(), subject, modelBuilder.build(), REFEREE_NOTIFICATION);
            sendEmail(message);
            referee.setLastNotified(new Date());
        } catch (Exception e) {
            log.error("Error while sending reference request mail: {}", e);
        }
    }

    public void sendReferenceRequest(List<Referee> referees, ApplicationForm applicationForm) {
        for (Referee referee : referees) {
            sendReferenceRequest(referee, applicationForm);
        }
    }

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
            log.error("Error while sending submission confirmation to applicant: {}", e);
        }
    }

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
            log.error("Error while sending rejection confirmation to applicant: {}", e);
        }
    }

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
            log.error("Error while sending approved notification email to applicant: {}", e);
        }
    }

    public void sendInterviewConfirmationToInterviewers(ApplicationForm applicationForm, List<RegisteredUser> interviewers) {
        PrismEmailMessage message = null;
        for (RegisteredUser interviewer : interviewers) {
            try {
                String subject = resolveMessage(INTERVIEWER_NOTIFICATION, applicationForm);
                List<RegisteredUser> admins = applicationForm.getProgram().getAdministrators();
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "interviewer", "application", "applicant", "host" },
                        new Object[] { getAdminsEmailsCommaSeparatedAsString(admins), interviewer, applicationForm, applicationForm.getApplicant(),
                                getHostName() });
                message = buildMessage(interviewer, subject, modelBuilder.build(), INTERVIEWER_NOTIFICATION);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending interview confirmation email to interviewer: {}", e);
            }
        }
    }

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
            log.error("Error while sending interview confirmation email to applicant: {}", e);
        }
    }

    public void sendInterviewVoteNotificationToInterviewerParticipants(Interview interview) {
        ApplicationForm application = interview.getApplication();

        String subject = resolveMessage(INTERVIEW_VOTE_NOTIFICATION, application);

        PrismEmailMessage message = null;
        for (InterviewParticipant participant : interview.getParticipants()) {
            try {
                List<RegisteredUser> admins = application.getProgram().getAdministrators();
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "participant", "application", "host" }, new Object[] {
                        getAdminsEmailsCommaSeparatedAsString(admins), participant, application, getHostName() });
                message = buildMessage(participant.getUser(), subject, modelBuilder.build(), INTERVIEW_VOTE_NOTIFICATION);
                sendEmail(message);
                participant.setLastNotified(new Date());
            } catch (Exception e) {
                log.error("Error while sending interview vote notification email to interview participant: " + participant.getUser().getEmail(), e);
            }
        }
    }

    public void sendInterviewVoteConfirmationToAdministrators(InterviewParticipant participant) {
        Interview interview = participant.getInterview();
        ApplicationForm application = interview.getApplication();
        Collection<RegisteredUser> administrators = getApplicationOrProgramAdministrators(application);

        PrismEmailMessage message = null;
        String subject = resolveMessage(INTERVIEW_VOTE_CONFIRMATION, application);
        for (RegisteredUser administrator : administrators) {
            if (administrator.getId() == participant.getUser().getId()) {
                continue; // administrator has voted himself, no need to notify
                          // him
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

    public void sendOpportunityRequestRejectionConfirmation(OpportunityRequest opportunityRequest) {
        RegisteredUser author = opportunityRequest.getAuthor();
        PrismEmailMessage message = null;
        String subject = resolveMessage(OPPORTUNITY_REQUEST_OUTCOME);

        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "opportunityRequest", "host" }, new Object[] { opportunityRequest, getHostName() });
            message = buildMessage(author, subject, modelBuilder.build(), OPPORTUNITY_REQUEST_OUTCOME);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending opportunity request rejection confirmation: " + author.getDisplayName(), e.getMessage());
        }
    }

    public void sendExportErrorMessage(List<RegisteredUser> superadmins, String messageCode, Date timestamp) {
        PrismEmailMessage message = null;
        if (messageCode == null) {
            log.error("Error while sending export error message: messageCode is null");
            return;
        }
        String subject = resolveMessage(EXPORT_ERROR);
        for (RegisteredUser user : superadmins) {
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "message", "time", "host" }, new Object[] { user, messageCode,
                        timestamp, getHostName() });
                message = buildMessage(user, subject, modelBuilder.build(), EXPORT_ERROR);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending export error message: {}", e);
            }
        }
    }

    public void sendImportErrorMessage(List<RegisteredUser> superadmins, String messageCode, Date timestamp) {
        PrismEmailMessage message = null;
        if (messageCode == null) {
            log.error("Error while sending import error message: messageCode is null");
            return;
        }
        String subject = resolveMessage(IMPORT_ERROR);
        for (RegisteredUser user : superadmins) {
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "message", "time", "host" }, new Object[] { user, messageCode,
                        timestamp, getHostName() });
                message = buildMessage(user, subject, modelBuilder.build(), IMPORT_ERROR);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending import error message: {}", e);
            }
        }
    }

    public void sendRegistrationConfirmation(RegisteredUser user) {
        PrismEmailMessage message = null;

        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "host" }, new Object[] { user, getHostName() });
            String subject = resolveMessage(REGISTRATION_CONFIRMATION);
            message = buildMessage(user, subject, modelBuilder.build(), REGISTRATION_CONFIRMATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending confirmation email to registering user: {}", e);
        }
    }

    public void sendResetPasswordMessage(final RegisteredUser user, final String newPassword) throws PrismMailMessageException {
        PrismEmailMessage message = null;
        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "newPassword", "host" }, new Object[] { user, newPassword, getHostName() });
            String subject = resolveMessage(NEW_PASSWORD_CONFIRMATION);
            message = buildMessage(user, subject, modelBuilder.build(), NEW_PASSWORD_CONFIRMATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending reset password email: {}", e);
        }
    }

}
