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

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignInterviewersComment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.RoleService;

@Service
public class MailSendingService extends AbstractMailSendingService {
    // TODO fix tests

    private static final Logger log = LoggerFactory.getLogger(MailSendingService.class);

    @Autowired
    @Value("${admissions.servicelevel.offer}")
    private String admissionsOfferServiceLevel;

    @Autowired
    @Value("${ucl.prospectus.url}")
    private String uclProspectusLink;
    
    private RoleService roleService;

    private void sendReferenceRequest(Referee referee, ApplicationForm application) {
        PrismEmailMessage message = null;
        try {
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(application.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "referee", "adminsEmails", "applicant", "application", "programme", "host" },
                    new Object[] { referee, adminsEmails, application.getApplicant(), application, application.getProgramDetails(), host });
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
            User applicant = form.getApplicant();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(form.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host",
                    "admissionOfferServiceLevel", "previousStage" },
                    new Object[] { adminsEmails, form, form.getApplicant(), roleService.getUsersInRole(roleService.getPrismSystem(), Authority.INSTITUTION_ADMITTER), getHostName(),
                            admissionsOfferServiceLevel, form.getLastState().getId().displayValue() });
            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(form.getState())) {
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
            User applicant = form.getApplicant();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(form.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host",
                    "admissionOfferServiceLevel", "previousStage" },
                    new Object[] { adminsEmails, form, form.getApplicant(), roleService.getUsersInRole(roleService.getPrismSystem(), Authority.INSTITUTION_ADMITTER), getHostName(),
                            admissionsOfferServiceLevel, form.getLastState().getId().displayValue() });
            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(form.getState())) {
                model.put("reason", form.getRejection().getRejectionReason());
                if (form.getRejection().isIncludeProspectusLink()) {
                    model.put("prospectusLink", uclProspectusLink);
                }
            }
            Object[] args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName(),
                    form.getLastState().getId().displayValue() };
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
            User applicant = form.getApplicant();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(form.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host",
                    "admissionOfferServiceLevel", "previousStage" },
                    new Object[] { adminsEmails, form, form.getApplicant(), roleService.getUsersInRole(roleService.getPrismSystem(), Authority.INSTITUTION_ADMITTER), getHostName(),
                            admissionsOfferServiceLevel, form.getLastState().getId().displayValue() });
            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(form.getState())) {
                model.put("reason", form.getRejection().getRejectionReason());
                if (form.getRejection().isIncludeProspectusLink()) {
                    model.put("prospectusLink", uclProspectusLink);
                }
            }
            String subject = resolveMessage(MOVED_TO_APPROVED_NOTIFICATION, form, form.getLastState().getId().displayValue());
            message = buildMessage(applicant, subject, model, MOVED_TO_APPROVED_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending approved notification email to applicant: {}", e);
        }
    }

    public void sendInterviewConfirmationToInterviewers(ApplicationForm application, List<User> interviewers) {
        PrismEmailMessage message = null;
        for (User interviewer : interviewers) {
            try {
                String subject = resolveMessage(INTERVIEWER_NOTIFICATION, application);
                List<User> admins = roleService.getProgramAdministrators(application.getProgram());
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "interviewer", "application", "applicant", "host" },
                        new Object[] { getAdminsEmailsCommaSeparatedAsString(admins), interviewer, application, application.getApplicant(),
                                getHostName() });
                message = buildMessage(interviewer, subject, modelBuilder.build(), INTERVIEWER_NOTIFICATION);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending interview confirmation email to interviewer: {}", e);
            }
        }
    }

    public void sendInterviewConfirmationToApplicant(ApplicationForm application) {
        PrismEmailMessage message = null;
        try {
            String subject = resolveMessage(MOVED_TO_INTERVIEW_NOTIFICATION, application, application.getLastState().getId().displayValue() );
            List<User> admins = roleService.getProgramAdministrators(application.getProgram());
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host", "admissionOfferServiceLevel", "previousStage" },
                    new Object[] { getAdminsEmailsCommaSeparatedAsString(admins), application, application.getApplicant(),
                            roleService.getUsersInRole(roleService.getPrismSystem(), Authority.INSTITUTION_ADMITTER), getHostName(), admissionsOfferServiceLevel, application.getLastState().getId().displayValue() });

            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(application.getState())) {
                model.put("reason", application.getRejection().getRejectionReason());
                if (application.getRejection().isIncludeProspectusLink()) {
                    model.put("prospectusLink", uclProspectusLink);
                }

            }
            message = buildMessage(application.getApplicant(), subject, model, MOVED_TO_INTERVIEW_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending interview confirmation email to applicant: {}", e);
        }
    }

  	public void sendInterviewVoteNotificationToInterviewerParticipants(AssignInterviewersComment assignInterviewersComment) {
        ApplicationForm application = assignInterviewersComment.getApplication();
        String subject = resolveMessage(EmailTemplateName.INTERVIEW_VOTE_NOTIFICATION, application);
        PrismEmailMessage message = null;
        
        List<User> recipients = Lists.newLinkedList();
        
        for (CommentAssignedUser assignedUser : assignInterviewersComment.getAssignedUsers()) {
            try {
                List<User> admins = roleService.getProgramAdministrators(application.getProgram());
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "participant", "application", "host" }, new Object[] {
                        getAdminsEmailsCommaSeparatedAsString(admins), assignedUser.getUser(), application, getHostName() });
                message = buildMessage(assignedUser.getUser(), subject, modelBuilder.build(), INTERVIEW_VOTE_NOTIFICATION);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending interview vote notification email to interview participant: " + assignedUser.getUser().getEmail(), e);
            }
        }
    }

    public void sendInterviewVoteConfirmationToAdministrators(ApplicationForm application, User user) {
        Collection<User> administrators = userDAO.getInterviewAdministrators(application);
        PrismEmailMessage message = null;
        String subject = resolveMessage(INTERVIEW_VOTE_CONFIRMATION, application);
        for (User administrator : administrators) {
            if (administrator.getId() == user.getId()) {
                continue;
            }
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "administrator", "application", "participant", "host" }, new Object[] {
                        administrator, application, user, getHostName() });
                message = buildMessage(administrator, subject, modelBuilder.build(), INTERVIEW_VOTE_CONFIRMATION);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending interview vote confirmation email to administrator: " + administrator.getUsername(), e.getMessage());
            }
        }
    }

    public void sendOpportunityRequestOutcome(OpportunityRequestComment comment) {
        User user = comment.getOpportunityRequest().getAuthor();
        PrismEmailMessage message = null;
        String subject = resolveMessage(OPPORTUNITY_REQUEST_OUTCOME);
        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "comment", "host" }, new Object[] { user, comment, getHostName() });
            message = buildMessage(user, subject, modelBuilder.build(), OPPORTUNITY_REQUEST_OUTCOME);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending opportunity request outcome confirmation: " + user.getUsername(), e.getMessage());
        }
    }

    public void sendExportErrorMessage(List<User> recipients, String messageCode, Date timestamp, ApplicationForm application) {
        PrismEmailMessage message = null;
        if (messageCode == null) {
            log.error("Error while sending export error message: messageCode is null");
            return;
        }
        String subject = resolveMessage(EXPORT_ERROR);
        for (User user : recipients) {
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "message", "time", "host", "application" }, new Object[] { user,
                        messageCode, timestamp, getHostName(), application });
                message = buildMessage(user, subject, modelBuilder.build(), EXPORT_ERROR);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending export error message: {}", e);
            }
        }
    }

    public void sendImportErrorMessage(List<User> recipients, String messageCode, Date timestamp) {
        PrismEmailMessage message = null;
        if (messageCode == null) {
            log.error("Error while sending import error message: messageCode is null");
            return;
        }
        String subject = resolveMessage(IMPORT_ERROR);
        for (User user : recipients) {
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

    public void sendRegistrationConfirmation(User user) {
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

    public void sendResetPasswordMessage(final User user, final String newPassword) throws PrismMailMessageException {
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
