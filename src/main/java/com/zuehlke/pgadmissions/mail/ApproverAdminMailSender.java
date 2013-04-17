package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPROVAL_NOTIFICATION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApproverAdminMailSender extends MailSender {

    public ApproverAdminMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory,
            JavaMailSender mailSender, MessageSource msgSource, EmailTemplateService emailTemplateService) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource, emailTemplateService);
	}

	Map<String, Object> createModel(RegisteredUser user, ApplicationForm application) {
		List<RegisteredUser> administrators = application.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);
		model.put("user", user);
		model.put("application", application);

		model.put("applicant", application.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendApprovalNotificationToApproversAndAdmins(ApplicationForm application) {
		List<RegisteredUser> approversAndAdmins = new ArrayList<RegisteredUser>();
		approversAndAdmins.addAll(application.getProgram().getApprovers());
		approversAndAdmins.addAll(application.getProgram().getAdministrators());
		Set<RegisteredUser> uniqueUsers = new HashSet<RegisteredUser>(approversAndAdmins);
		ApplicationFormStatus previousStage = application.getOutcomeOfStage();
		EmailTemplate template = getDefaultEmailtemplate(APPROVAL_NOTIFICATION);
		for (RegisteredUser user : uniqueUsers) {
			InternetAddress toAddress = createAddress(user);
			String subject = resolveMessage("approval.notification.approverAndAdmin", application, previousStage);
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,//
					APPROVAL_NOTIFICATION, template.getContent(), createModel(user, application), null));
		}
	}
}
