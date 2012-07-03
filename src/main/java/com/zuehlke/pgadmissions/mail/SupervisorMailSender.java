package com.zuehlke.pgadmissions.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.utils.Environment;

public class SupervisorMailSender extends MailSender {

	private static final String SUPERVISOR_NOTIFICATION_TEMPLATE = "private/supervisors/mail/supervisor_notification_email.ftl";

	public SupervisorMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource messageSource) {
		super(mimeMessagePreparatorFactory, mailSender, messageSource);
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

	public void sendSupervisorNotification(Supervisor supervisor) {
		InternetAddress toAddress = createAddress(supervisor.getUser());
		String subject = resolveMessage("supervisor.notification", supervisor.getApprovalRound().getApplication());

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, SUPERVISOR_NOTIFICATION_TEMPLATE, createModel(supervisor), null));

	}

}
