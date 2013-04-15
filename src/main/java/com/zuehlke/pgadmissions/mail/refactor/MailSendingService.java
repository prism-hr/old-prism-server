package com.zuehlke.pgadmissions.mail.refactor;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPLICATION_SUBMIT_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.EXPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.IMPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEW_ADMINISTRATION_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.MOVED_TO_APPROVED_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_PASSWORD_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFERENCE_RESPOND_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REGISTRATION_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REJECTED_NOTIFICATION;
import static com.zuehlke.pgadmissions.utils.Environment.getInstance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.exceptions.PrismMailMessageException;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

@Service
public class MailSendingService extends AbstractMailSendingService {

    private final TemplateAwareMailSender mailSender;
    
    private final RefereeService refereeService;
    
    private final ConfigurationService configurationService;

    public MailSendingService() {
        this(null, null, null, null);
    }

    public MailSendingService(final TemplateAwareMailSender mailSender, final UserService userSerivce,
    		final RefereeService refereeService, ConfigurationService configurationService) {
        super(userSerivce);
        this.mailSender = mailSender;
        this.refereeService = refereeService;
		this.configurationService = configurationService;
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
    public void sendReferenceRequest(Referee referee, ApplicationForm applicationForm) {
        PrismEmailMessage message = null;
        try {
        	String adminsEmails = getAdminsEmailsCommaSeparatedAsString(applicationForm.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "referee", "adminsEmails", "applicant", "application", "programme", "host" }, 
                    new Object[] { referee, adminsEmails, applicationForm.getApplicant(),
                    applicationForm, applicationForm.getProgrammeDetails(), getInstance().getApplicationHostName() });
            
            String subject = resolveMessage("reference.request", applicationForm);
            
            message = buildMessage(referee.getUser(), subject, modelBuilder.build(), REFEREE_NOTIFICATION);
            mailSender.sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending reference request mail: ", e.getCause(), message);
        }
    }

    private String resolveMessage(String subjectCode, ApplicationForm applicationForm) {
        RegisteredUser applicant = applicationForm.getApplicant();
        if (applicant == null) {
            return mailSender.resolveMessage(subjectCode, applicationForm.getApplicationNumber(), applicationForm.getProgram().getTitle());
        } else {
            return mailSender.resolveMessage(subjectCode, applicationForm.getApplicationNumber(), applicationForm
                    .getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName());
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
     * APPLICATION_SUBMIT_CONFIRMATION
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
    public void sendSubmissionConfirmationToApplicant(ApplicationForm form) {
    	   PrismEmailMessage message = null;
           try {
        	   RegisteredUser applicant = form.getApplicant();
        	   String adminsEmails = getAdminsEmailsCommaSeparatedAsString(form.getProgram().getAdministrators());
               EmailModelBuilder modelBuilder = getModelBuilder(
                       new String[] {"adminsEmails", "application", "applicant", "registryContacts", "host", "admissionOfferServiceLevel", "previousStage" }, 
                       new Object[] { adminsEmails, form, form.getApplicant(), configurationService.getAllRegistryUsers(), getInstance().getApplicationHostName(),
                    		   Environment.getInstance().getAdmissionsOfferServiceLevel(), form.getOutcomeOfStage()});
               
               Map<String, Object> model = modelBuilder.build();
               if (ApplicationFormStatus.REJECTED.equals(form.getStatus())) {
       				model.put("reason", form.getRejection().getRejectionReason());
       				if (form.getRejection().isIncludeProspectusLink()) {
       					model.put("prospectusLink", Environment.getInstance().getUCLProspectusLink());
       				}

       			}
               
               Object[] args;
               if (form.getOutcomeOfStage()==null) {
            	   args = new Object[]{form.getApplicationNumber(), form.getProgram().getTitle()};
               }
               else {
            	   args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName(),
            			   form.getOutcomeOfStage().displayValue() };
               }
               String subject = mailSender.resolveMessage("validation.submission.applicant", args);
               
               message = buildMessage(applicant, subject, model, APPLICATION_SUBMIT_CONFIRMATION);
               mailSender.sendEmail(message);
           } catch (Exception e) {
               throw new PrismMailMessageException("Error while sending submission confirmation to applicant: ", e.getCause(), message);
           }
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
    // TODO: Current business logic is incorrect. Administrator cannot reject application when it is in approval state.
    public void sendRejectionConfirmationToApplicant(ApplicationForm form) {
    	PrismEmailMessage message = null;
        try {
     	   RegisteredUser applicant = form.getApplicant();
     	   String adminsEmails = getAdminsEmailsCommaSeparatedAsString(form.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] {"adminsEmails", "application", "applicant", "registryContacts", "host", "admissionOfferServiceLevel", "previousStage" }, 
                    new Object[] { adminsEmails, form, form.getApplicant(), configurationService.getAllRegistryUsers(), getInstance().getApplicationHostName(),
                 		   Environment.getInstance().getAdmissionsOfferServiceLevel(), form.getOutcomeOfStage()});
            
            Map<String, Object> model = modelBuilder.build();
            if (ApplicationFormStatus.REJECTED.equals(form.getStatus())) {
    				model.put("reason", form.getRejection().getRejectionReason());
    				if (form.getRejection().isIncludeProspectusLink()) {
    					model.put("prospectusLink", Environment.getInstance().getUCLProspectusLink());
    				}

    			}
            
            Object[] args;
            if (form.getOutcomeOfStage()==null) {
         	   args = new Object[]{form.getApplicationNumber(), form.getProgram().getTitle()};
            }
            else {
         	   args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName(),
         			   form.getOutcomeOfStage().displayValue() };
            }
            String subject = mailSender.resolveMessage("rejection.notification", args);
            
            message = buildMessage(applicant, subject, model, REJECTED_NOTIFICATION);
            mailSender.sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending rejection confirmation to applicant: ", e.getCause(), message);
        }
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
     public void sendApprovedNotification(ApplicationForm form) {
    	 PrismEmailMessage message = null;
         try {
      	   RegisteredUser applicant = form.getApplicant();
      	   String adminsEmails = getAdminsEmailsCommaSeparatedAsString(form.getProgram().getAdministrators());
             EmailModelBuilder modelBuilder = getModelBuilder(
                     new String[] {"adminsEmails", "application", "applicant", "registryContacts", "host", "admissionOfferServiceLevel", "previousStage" }, 
                     new Object[] { adminsEmails, form, form.getApplicant(), configurationService.getAllRegistryUsers(), getInstance().getApplicationHostName(),
                  		   Environment.getInstance().getAdmissionsOfferServiceLevel(), form.getOutcomeOfStage()});
             
             Map<String, Object> model = modelBuilder.build();
             if (ApplicationFormStatus.REJECTED.equals(form.getStatus())) {
     				model.put("reason", form.getRejection().getRejectionReason());
     				if (form.getRejection().isIncludeProspectusLink()) {
     					model.put("prospectusLink", Environment.getInstance().getUCLProspectusLink());
     				}

     			}
             
             Object[] args;
             if (form.getOutcomeOfStage()==null) {
          	   args = new Object[]{form.getApplicationNumber(), form.getProgram().getTitle()};
             }
             else {
          	   args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName(),
          			   form.getOutcomeOfStage().displayValue() };
             }
             String subject = mailSender.resolveMessage("approved.notification.applicant", args);
             
             message = buildMessage(applicant, subject, model, MOVED_TO_APPROVED_NOTIFICATION);
             mailSender.sendEmail(message);
         } catch (Exception e) {
             throw new PrismMailMessageException("Error while sending approved notification email to applicant: ", e.getCause(), message);
         }
     }


    /**
    * <p>
    * <b>Summary</b><br/>
    * Informs users when a data export has failed.
    * <p/><p>
    * <b>Recipients</b>
    * Super Administrator
    * </p><p>
    * <b>Previous Email Template Name</b><br/>
    * Kevin to Insert
    * </p><p> 
    * <b>Business Rules</b><br/>
    * <ol>
    * <li>Super Administrators are notified, when:
    *    <ol><li>A data export has failed.</li>
    *    </ol></li>
    * </ol>
    * </p><p>
    * <b>Notification Type</b>
    * Immediate Notification
    * </p>
    */    
    public void sendExportErrorMessage(List<RegisteredUser> users, String messageCode, Date timestamp) {
        PrismEmailMessage message = null;
        if (messageCode == null) {
            throw new PrismMailMessageException("Error while sending export error message: messageCode is null", message);
        }
        String subject = mailSender.resolveMessage("reference.data.export.error", (Object[]) null);
        for (RegisteredUser user : users) {
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(
                        new String[] { "user", "message", "time", "host" }, 
                        new Object[] { user, messageCode, timestamp, getInstance().getApplicationHostName() });
                message = buildMessage(user, subject, modelBuilder.build(), EXPORT_ERROR);
                mailSender.sendEmail(message);
            } catch (Exception e) {
                throw new PrismMailMessageException("Error while sending export error message: ", e.getCause(), message);
            }
        }
    }
    
    /**
     * <p>
     * <b>Summary</b><br/>
     * Informs users when references have been provided.<br/>
     * Finds all applications in the system for which references have recently been provided, and;<br/> 
     * Schedules their Applicants and Administrators to be notified.
     * <p/><p>
     * <b>Recipients</b><br/>
     * Applicant<br/>
     * Administrator
     * </p><p>
     * <b>Previous Email Template Name</b><br/>
     * REFERENCE_SUBMIT_CONFIRMATION
     * </p><p> 
     * <b>Business Rules</b>
     * <ol>
     * <li>Referees can provide references, while:
     *    <ol>
     *    <li>Applications are not in the rejected, approved or withdrawn states.</li>
     *    </ol></li>
     * <li>Applicants and Administrators are scheduled to be notified, when:
     *    <ol>
     *    <li>References have been provided within the last 24 hours.</li>
     *    </ol></li>
     * </ol>
     * </p><p>
     * <b>Notification Type</b><br/>
     * Scheduled Digest Priority 1 (Update Notification)
     * </p>
     */
    public void sendReferenceSubmitConfirmationToAdministrators(List<RegisteredUser> admins) {
        CollectionUtils.forAllDo(admins, new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
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
    // TODO: Write a test for this
    public void scheduleWithdrawalConfirmation(final ApplicationForm form) {
        HashMap<Integer, RegisteredUser> usersToNotify = new HashMap<Integer, RegisteredUser>();
        for (Referee referee : refereeService.getRefereesWhoHaveNotProvidedReference(form)) {
            usersToNotify.put(referee.getUser().getId(), referee.getUser());
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
        
        for (RegisteredUser supervisor : getSupervisorsFromLatestApprovalRound(form)) {
            usersToNotify.put(supervisor.getId(), supervisor);
        }
        
        CollectionUtils.forAllDo(usersToNotify.values(), new UpdateDigestNotificationClosure(DigestNotificationType.UPDATE_NOTIFICATION));
    }

	public void sendReferenceSubmittedConfirmationToApplicant(Referee referee) {
		PrismEmailMessage message = null;
		try {
			ApplicationForm form = referee.getApplication();
			String adminsEmails = getAdminsEmailsCommaSeparatedAsString(form.getProgram().getAdministrators());
			RegisteredUser applicant = form.getApplicant();
			EmailModelBuilder modelBuilder = getModelBuilder(new String[] {"adminsEmails", "referee", "application", "host" }, new Object[] {
					adminsEmails, referee, form, getInstance().getApplicationHostName() });
			String subject = resolveMessage("reference.provided.applicant", form);
			message = buildMessage(applicant, subject, modelBuilder.build(), REFERENCE_RESPOND_CONFIRMATION);
			mailSender.sendEmail(message);
		}
		catch (Exception e) {
			throw new PrismMailMessageException("Error while sending reference submitted confirmation to applicant: ", e.getCause(), message);
		}
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
    public void sendImportErrorMessage(List<RegisteredUser> users, String messageCode, Date timestamp) {
        PrismEmailMessage message = null;
        if (messageCode == null) {
            throw new PrismMailMessageException("Error while sending import error message: messageCode is null", message);
        }
        String subject = mailSender.resolveMessage("reference.data.import.error", (Object[]) null);
        for (RegisteredUser user : users) {
            try {
                EmailModelBuilder modelBuilder = getModelBuilder(
                        new String[] { "user", "message", "time", "host" },
                        new Object[] { user, messageCode, timestamp, getInstance().getApplicationHostName() });
                message = buildMessage(user, subject, modelBuilder.build(), IMPORT_ERROR);
                mailSender.sendEmail(message);
            } catch (Exception e) {
                throw new PrismMailMessageException("Error while sending import error message: ", e.getCause(), message);
            }
        }
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
    public void sendRegistrationConfirmation(RegisteredUser user, String action) {
        PrismEmailMessage message = null;
        if (action == null) {
            throw new PrismMailMessageException("Error while sending confirmation email to registering user: action is null", message);
        }
        
        try {
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "user", "action", "host" }, 
                    new Object[] { user, action, getInstance().getApplicationHostName() });
            String subject = mailSender.resolveMessage("registration.confirmation", (Object[]) null);
            message = buildMessage(user, subject, modelBuilder.build(), REGISTRATION_CONFIRMATION);
            mailSender.sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending confirmation email to registering user: ", e.getCause(), message);
        }
    }

    public void sendInterviewAdministrationReminder(RegisteredUser user, List<RegisteredUser> admins, ApplicationForm form) {
        PrismEmailMessage message = null;
        try {
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "user", "applicationForm", "host" }, 
                    new Object[] { user, form, getInstance().getApplicationHostName() });
            String subject = mailSender.resolveMessage("application.interview.delegation", (Object[]) null);
            message = buildMessage(user, admins, subject, modelBuilder.build(), INTERVIEW_ADMINISTRATION_REMINDER);
            mailSender.sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending interview administration reminder email: ", e.getCause(), message);
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
            EmailModelBuilder modelBuilder = getModelBuilder(
                    new String[] { "user", "newPassword", "host" },
                    new Object[] { user, newPassword, getInstance().getApplicationHostName() });
            String subject = mailSender.resolveMessage("user.password.reset", (Object[]) null);
            message = buildMessage(user, subject, modelBuilder.build(), NEW_PASSWORD_CONFIRMATION);
            mailSender.sendEmail(message);
        } catch (Exception e) {
            throw new PrismMailMessageException("Error while sending reset password email: ", e.getCause(), message);
        }
    }

    private PrismEmailMessage buildMessage(RegisteredUser recipient, String subject, Map<String, Object> model, EmailTemplateName templateName) {
        return buildMessage(recipient, null, subject, model, templateName);
    }

    private PrismEmailMessage buildMessage(RegisteredUser recipient, List<RegisteredUser> ccRecipients, String subject, Map<String, Object> model, EmailTemplateName templateName) {
        return new PrismEmailMessageBuilder().to(recipient).cc(ccRecipients).subjectCode(subject).model(model).emailTemplate(NEW_PASSWORD_CONFIRMATION).build();
    }

    private EmailModelBuilder getModelBuilder(final String[] keys, final Object[] values) {
        return new EmailModelBuilder() {
            @Override
            public Map<String, Object> build() {
                Map<String, Object> model = new HashMap<String, Object>();
                for (int i = 0; i < keys.length; i++) {
                    model.put(keys[i], values[i]);
                }
                return model;
            }
        };
    }
    
}
