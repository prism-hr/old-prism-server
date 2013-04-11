package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.SUPERVISOR_NOTIFICATION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class SupervisorMailSender extends MailSender {

    public SupervisorMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource messageSource, EmailTemplateService emailTemplateService) {
        super(mimeMessagePreparatorFactory, mailSender, messageSource, emailTemplateService);
    }

	Map<String, Object> createModel(Supervisor supervisor) {
		ApplicationForm form = supervisor.getApprovalRound().getApplication();
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("supervisor", supervisor);
		model.put("application", form);
		model.put("adminsEmails", adminsEmails);
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	/**
     * @deprecated This method is now being replaced by the {@link #sendPrimarySupervisorConfirmationNotification() sendPrimarySupervisorConfirmationNotification} method.
	 */
	@Deprecated
	public void sendSupervisorNotification(Supervisor supervisor) {
		InternetAddress toAddress = createAddress(supervisor.getUser());
		String subject = resolveMessage("supervisor.notification", supervisor.getApprovalRound().getApplication());
		EmailTemplate template = getDefaultEmailtemplate(SUPERVISOR_NOTIFICATION);
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, SUPERVISOR_NOTIFICATION,  template.getContent(), createModel(supervisor), null));
	}

    public void sendPrimarySupervisorConfirmationNotification(Supervisor supervisor) {
        InternetAddress toAddress = createAddress(supervisor.getUser());
        String subject = resolveMessage("supervisor.primary.notification", supervisor.getApprovalRound().getApplication());
        EmailTemplate template = getDefaultEmailtemplate(SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION);
        javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION, template.getContent(), createModel(supervisor), null));
    }
    
    public void sendPrimarySupervisorConfirmationNotificationAndCopyAdmins(Supervisor supervisor, List<RegisteredUser> admins) {
    	InternetAddress toAddress = createAddress(supervisor.getUser());
    	InternetAddress[] ccAddresses = new InternetAddress[admins.size()];
    	for (int i =0; i<admins.size(); i++) {
    		ccAddresses[i] = createAddress(admins.get(i));
    	}
    	String subject = resolveMessage("supervisor.primary.notification", supervisor.getApprovalRound().getApplication());
    	EmailTemplate template = getDefaultEmailtemplate(SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION);
    	javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses, subject, SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION, template.getContent(), createModel(supervisor), null));
    }

    public void sendPrimarySupervisorConfirmationNotificationReminder(Supervisor supervisor) {
        InternetAddress toAddress = createAddress(supervisor.getUser());
        String subject = resolveMessage("supervisor.primary.notification.reminder", supervisor.getApprovalRound().getApplication());
        EmailTemplate template = getDefaultEmailtemplate(SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER);
        javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER, template.getContent(), createModel(supervisor), null));
    }
}
