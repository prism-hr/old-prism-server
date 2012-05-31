package com.zuehlke.pgadmissions.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApproverMailSender extends MailSender {

	public ApproverMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);	
	}

	Map<String, Object> createModel(RegisteredUser approver, ApplicationForm application) {
		List<RegisteredUser> administrators = application.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);
		model.put("approver", approver);
		model.put("application", application);

		model.put("applicant", application.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendApprovalNotificationToApprovers(ApplicationForm application) {
		List<RegisteredUser> approvers = application.getProgram().getApprovers();
		for (RegisteredUser approver : approvers) {
			InternetAddress toAddress = createAddress(approver);
			String subject = resolveMessage("approval.notification.approver", application);
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,//
					"private/approvers/mail/approval_notification_email.ftl", createModel(approver, application), null));
		}
	}
}
