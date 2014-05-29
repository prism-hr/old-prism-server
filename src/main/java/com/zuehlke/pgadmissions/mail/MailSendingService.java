package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_COMPLETE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_CONFIRM_REJECTION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_PROVIDE_REFERENCE_REQUEST;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.SYSTEM_COMPLETE_REGISTRATION_REQUEST;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.SYSTEM_IMPORT_ERROR_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.SYSTEM_PASSWORD_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.SYSTEM_REGISTRATION_REQUEST;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AssignInterviewersComment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;

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

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private SystemService systemService;

    private void sendReferenceRequest(Referee referee, Application application) {
        PrismEmailMessage message = null;
        try {
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(application.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "referee", "adminsEmails", "applicant", "application", "programme", "host" },
                    new Object[] { referee, adminsEmails, application.getUser(), application, application.getProgramDetails(), host });
            String subject = resolveMessage(APPLICATION_PROVIDE_REFERENCE_REQUEST, application);
            message = buildMessage(referee.getUser(), subject, modelBuilder.build(), APPLICATION_PROVIDE_REFERENCE_REQUEST);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending reference request mail: {}", e);
        }
    }

    public void sendReferenceRequest(List<Referee> referees, Application applicationForm) {
        for (Referee referee : referees) {
            sendReferenceRequest(referee, applicationForm);
        }
    }

    public void sendSubmissionConfirmationToApplicant(Application form) {
        PrismEmailMessage message = null;
        try {
            User applicant = form.getUser();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(form.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host", "admissionOfferServiceLevel" },
                    new Object[] { adminsEmails, form, form.getUser(),
                            roleService.getUsersInRole(systemService.getSystem(), Authority.INSTITUTION_ADMITTER), getHostName(),
                            admissionsOfferServiceLevel });
            Map<String, Object> model = modelBuilder.build();
            Object[] args = new Object[] { form.getCode(), form.getProgram().getTitle() };
            String subject = resolveMessage(APPLICATION_COMPLETE_NOTIFICATION, args);
            message = buildMessage(applicant, subject, model, APPLICATION_COMPLETE_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending submission confirmation to applicant: {}", e);
        }
    }

    public void sendRejectionConfirmationToApplicant(Application form) {
        PrismEmailMessage message = null;
        try {
            User applicant = form.getUser();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(form.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host", "admissionOfferServiceLevel" },
                    new Object[] { adminsEmails, form, form.getUser(),
                            roleService.getUsersInRole(systemService.getSystem(), Authority.INSTITUTION_ADMITTER), getHostName(),
                            admissionsOfferServiceLevel });
            Map<String, Object> model = modelBuilder.build();
            // FIXME specify reason and prospectusLink in the model
//            if (PrismState.APPLICATION_REJECTED.equals(form.getState())) {
//                model.put("reason", form.getRejection().getRejectionReason());
//                if (form.getRejection().isIncludeProspectusLink()) {
//                    model.put("prospectusLink", uclProspectusLink);
//                }
//            }
            Object[] args = new Object[] { form.getCode(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName() };
            String subject = resolveMessage(APPLICATION_CONFIRM_REJECTION_NOTIFICATION, args);
            message = buildMessage(applicant, subject, model, APPLICATION_CONFIRM_REJECTION_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending rejection confirmation to applicant: {}", e);
        }
    }

    public void sendApprovedNotification(Application form) {
        PrismEmailMessage message = null;
        try {
            User applicant = form.getUser();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(form.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host", "admissionOfferServiceLevel" },
                    new Object[] { adminsEmails, form, form.getUser(),
                            roleService.getUsersInRole(systemService.getSystem(), Authority.INSTITUTION_ADMITTER), getHostName(),
                            admissionsOfferServiceLevel });
            Map<String, Object> model = modelBuilder.build();
            String subject = resolveMessage(APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION, form);
            message = buildMessage(applicant, subject, model, APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending approved notification email to applicant: {}", e);
        }
    }

    public void sendInterviewConfirmationToInterviewers(Application application, List<User> interviewers) {
        PrismEmailMessage message = null;
        for (User interviewer : interviewers) {
            try {
                String subject = resolveMessage(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER, application);
                List<User> admins = roleService.getProgramAdministrators(application.getProgram());
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "interviewer", "application", "applicant", "host" },
                        new Object[] { getAdminsEmailsCommaSeparatedAsString(admins), interviewer, application, application.getUser(), getHostName() });
                message = buildMessage(interviewer, subject, modelBuilder.build(), APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending interview confirmation email to interviewer: {}", e);
            }
        }
    }

    public void sendInterviewConfirmationToApplicant(Application application) {
        PrismEmailMessage message = null;
        try {
            String subject = resolveMessage(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE, application);
            List<User> admins = roleService.getProgramAdministrators(application.getProgram());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "application", "applicant", "registryContacts", "host",
                    "admissionOfferServiceLevel" }, new Object[] { getAdminsEmailsCommaSeparatedAsString(admins), application, application.getUser(),
                    roleService.getUsersInRole(systemService.getSystem(), Authority.INSTITUTION_ADMITTER), getHostName(), admissionsOfferServiceLevel });
            Map<String, Object> model = modelBuilder.build();
            message = buildMessage(application.getUser(), subject, model, APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending interview confirmation email to applicant: {}", e);
        }
    }

    public void sendInterviewVoteNotificationToInterviewerParticipants(AssignInterviewersComment assignInterviewersComment) {
        Application application = assignInterviewersComment.getApplication();
        String subject = resolveMessage(NotificationTemplateId.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST, application);
        PrismEmailMessage message = null;

        List<User> recipients = Lists.newLinkedList();

        for (CommentAssignedUser assignedUser : assignInterviewersComment.getAssignedUsers()) {
            try {
                List<User> admins = roleService.getProgramAdministrators(application.getProgram());
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "participant", "application", "host" }, new Object[] {
                        getAdminsEmailsCommaSeparatedAsString(admins), assignedUser.getUser(), application, getHostName() });
                message = buildMessage(assignedUser.getUser(), subject, modelBuilder.build(), APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending interview vote notification email to interview participant: " + assignedUser.getUser().getEmail(), e);
            }
        }
    }

    public void sendInterviewVoteConfirmationToAdministrators(Application application, User user) {
        Collection<User> administrators = userDAO.getInterviewAdministrators(application);
        PrismEmailMessage message = null;
        String subject = resolveMessage(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION, application);
        for (User administrator : administrators) {
            if (administrator.getId() == user.getId()) {
                continue;
            }
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "administrator", "application", "participant", "host" }, new Object[] {
                        administrator, application, user, getHostName() });
                message = buildMessage(administrator, subject, modelBuilder.build(), APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION);
                sendEmail(message);
            } catch (Exception e) {
                log.error("Error while sending interview vote confirmation email to administrator: " + administrator.getUsername(), e.getMessage());
            }
        }
    }

    public void sendOpportunityRequestOutcome(OpportunityRequestComment comment) {
        User user = comment.getOpportunityRequest().getAuthor();
        PrismEmailMessage message = null;
        String subject = resolveMessage(PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION);
        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "comment", "host" }, new Object[] { user, comment, getHostName() });
            message = buildMessage(user, subject, modelBuilder.build(), PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending opportunity request outcome confirmation: " + user.getUsername(), e.getMessage());
        }
    }

    public void sendImportErrorMessage(String messageText) {
        PrismEmailMessage message = null;
        if (messageText == null) {
            log.error("Error while sending import error message: messageCode is null");
            return;
        }
        String subject = resolveMessage(SYSTEM_IMPORT_ERROR_NOTIFICATION);
        List<User> recipients = roleService.getUsersInRole(systemService.getSystem(), Authority.SYSTEM_ADMINISTRATOR);
        for (User user : recipients) {
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "message", "time", "host" }, new Object[] { user, messageText,
                        new Date(), getHostName() });
                message = buildMessage(user, subject, modelBuilder.build(), SYSTEM_IMPORT_ERROR_NOTIFICATION);
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
            String subject = resolveMessage(SYSTEM_COMPLETE_REGISTRATION_REQUEST);
            message = buildMessage(user, subject, modelBuilder.build(), SYSTEM_COMPLETE_REGISTRATION_REQUEST);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending confirmation email to registering user: {}", e);
        }
    }

    public void sendResetPasswordMessage(final User user, final String newPassword) throws PrismMailMessageException {
        PrismEmailMessage message = null;
        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "newPassword", "host" }, new Object[] { user, newPassword, getHostName() });
            String subject = resolveMessage(SYSTEM_PASSWORD_NOTIFICATION);
            message = buildMessage(user, subject, modelBuilder.build(), SYSTEM_PASSWORD_NOTIFICATION);
            sendEmail(message);
        } catch (Exception e) {
            log.error("Error while sending reset password email: {}", e);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendNewUserInvitation(Integer userId) {
        PrismEmailMessage message = null;
        User user = userDAO.getById(userId);
        String subject = resolveMessage(SYSTEM_REGISTRATION_REQUEST, (Object[]) null);
        
        User admin = roleService.getInvitingAdmin(user);

        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "newUser", "admin", "host" }, new Object[] { user, admin, getHostName() });
            message = buildMessage(user, subject, modelBuilder.build(), SYSTEM_REGISTRATION_REQUEST);
            sendEmail(message);
            userDAO.save(user);
        } catch (Exception e) {
            log.error("Error while sending reference reminder email to referee: ", e);
            return false;
        }
        return true;
    }

}
