package com.zuehlke.pgadmissions.mail.refactor;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.EXPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.IMPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEW_ADMINISTRATION_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_PASSWORD_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REGISTRATION_CONFIRMATION;
import static com.zuehlke.pgadmissions.utils.Environment.getInstance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.exceptions.PrismMailMessageException;

@Service
public class MailSendingService {

	private static final Logger log = LoggerFactory.getLogger(MailSendingService.class);

	private final TemplateAwareMailSender mailSender;

	public MailSendingService() {
		this(null);
	}

	public MailSendingService(TemplateAwareMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendRefereeMailNotification(Referee referee, ApplicationForm applicationForm, String adminMails) {
		PrismEmailMessage message = null;
		try {
			EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "referee", "adminEmails", "applicant",
					"application", "programme", "host" }, new Object[] { referee, adminMails, applicationForm.getApplicant(),
					applicationForm, applicationForm.getProgrammeDetails(), getInstance().getApplicationHostName() });
			
			String subject = resolveMessage("reference.request", applicationForm);
			
	        
			message = buildMessage(referee.getUser(), subject, modelBuilder.build(), REFEREE_NOTIFICATION);
			mailSender.sendEmail(message);
		} catch (Exception e) {
			throw new PrismMailMessageException("Error while sending referee mail notification: ", e.getCause(), message);
		}
	}

	private String resolveMessage(String subjectCode, ApplicationForm applicationForm) {
		RegisteredUser applicant = applicationForm.getApplicant();
		if (applicant == null) {
        	return mailSender.resolveMessage(subjectCode,
        			applicationForm.getApplicationNumber(),
        			applicationForm.getProgram().getTitle());
        } else {
        	return mailSender.resolveMessage(subjectCode,
        			applicationForm.getApplicationNumber(),
        			applicationForm.getProgram().getTitle(),
        			 applicant.getFirstName(),
        			 applicant.getLastName()
        			 );
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
			throw new PrismMailMessageException("Error while sending export error message: messageCode is null",
					message);
		}
		String subject = mailSender.resolveMessage("reference.data.export.error", (Object[]) null);
		for (RegisteredUser user : users) {
			try {
				EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "message", "time", "host" },
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
     * Kevin to Insert
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
	public void sendReferenceSubmitConfirmationToAdministrators(Referee referee, List<RegisteredUser> admins, ApplicationForm applicationForm) {
//	    PrismEmailMessage message = null;
//		String subject = resolveMessage("reference.provided.admin", applicationForm);
//		for (RegisteredUser admin : admins) {
//			try {
//				EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "admin", "application", "referee", "host" },
//						new Object[] { admin, applicationForm, referee, getInstance().getApplicationHostName() });
//				message = buildMessage(admin, subject, modelBuilder.build(), REFERENCE_SUBMIT_CONFIRMATION);
//				mailSender.sendEmail(message);
//			} catch (Exception e) {
//				throw new PrismMailMessageException("Error while sending reference submit confirmation to administrator: ", e.getCause(), message);
//			}
//		}
	    
	    // TODO: This needs a DIGEST now: see ScheduledMailSendingService.sendReferenceSubmittedConfirmation();
	}

	/**
    * <p>
    * <b>Summary</b><br/>
    * Informs users when a data import has failed.
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
    *    <ol><li>A data import has failed.</li>
    *    </ol></li>
    * </ol>
    * </p><p>
    * <b>Notification Type</b>
    * Immediate Notification
    * </p>
    */
	public void sendImportErrorMessage(List<RegisteredUser> users, String messageCode, Date timestamp) {
		PrismEmailMessage message = null;
		if (messageCode == null) {
			throw new PrismMailMessageException("Error while sending import error message: messageCode is null",
					message);
		}
		String subject = mailSender.resolveMessage("reference.data.import.error", (Object[]) null);
		for (RegisteredUser user : users) {
			try {
				EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "message", "time", "host" },
						new Object[] { user, messageCode, timestamp, getInstance().getApplicationHostName() });
				message = buildMessage(user, subject, modelBuilder.build(), IMPORT_ERROR);
				mailSender.sendEmail(message);
			} catch (Exception e) {
				throw new PrismMailMessageException("Error while sending import error message: ", e.getCause(), message);
			}
		}
	}

	public void sendConfirmationEmailToRegisteringUser(RegisteredUser user, String action) {
		PrismEmailMessage message = null;
		if (action == null) {
			throw new PrismMailMessageException(
					"Error while sending confirmation email to registering user: action is null", message);
		}
		try {
			EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "action", "host" }, new Object[] {
					user, action, getInstance().getApplicationHostName() });
			String subject = mailSender.resolveMessage("registration.confirmation", (Object[]) null);
			message = buildMessage(user, subject, modelBuilder.build(), REGISTRATION_CONFIRMATION);
			mailSender.sendEmail(message);
		} catch (Exception e) {
			throw new PrismMailMessageException("Error while sending confirmation email to registering user: ",
					e.getCause(), message);
		}
	}

	public void sendInterviewAdministrationReminder(RegisteredUser user, List<RegisteredUser> admins,
			ApplicationForm form) {
		PrismEmailMessage message = null;
		try {

			EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "applicationForm", "host" },
					new Object[] { user, form, getInstance().getApplicationHostName() });
			String subject = mailSender.resolveMessage("application.interview.delegation", (Object[]) null);
			message = buildMessage(user, admins, subject, modelBuilder.build(), INTERVIEW_ADMINISTRATION_REMINDER);
			mailSender.sendEmail(message);
		} catch (Exception e) {
			throw new PrismMailMessageException("Error while sending interview administration reminder email: ",
					e.getCause(), message);
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
	public void sendResetPasswordMessage(final RegisteredUser user, final String newPassword)
			throws PrismMailMessageException {
		PrismEmailMessage message = null;
		try {

			EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "user", "newPassword", "host" },
					new Object[] { user, newPassword, getInstance().getApplicationHostName() });
			String subject = mailSender.resolveMessage("user.password.reset", (Object[]) null);
			message = buildMessage(user, subject, modelBuilder.build(), NEW_PASSWORD_CONFIRMATION);
			mailSender.sendEmail(message);
		} catch (Exception e) {
			throw new PrismMailMessageException("Error while sending reset password email: ", e.getCause(), message);
		}
	}

	private PrismEmailMessage buildMessage(RegisteredUser recipient, String subject, Map<String, Object> model,
			EmailTemplateName templateName) {
		return buildMessage(recipient, null, subject, model, templateName);
	}

	private PrismEmailMessage buildMessage(RegisteredUser recipient, List<RegisteredUser> ccRecipients, String subject,
			Map<String, Object> model, EmailTemplateName templateName) {
		return new PrismEmailMessageBuilder().to(recipient).cc(ccRecipients).subjectCode(subject).model(model)
				.emailTemplate(NEW_PASSWORD_CONFIRMATION).build();
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
