package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_REMINDER_FIRST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class InterviewerMailSender extends MailSender {

	public InterviewerMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource, EmailTemplateService emailTemplateService) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource, emailTemplateService);
	}

	Map<String, Object> createModel(Interviewer interviewer) {
		ApplicationForm form = interviewer.getInterview().getApplication();
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);
		model.put("interviewer", interviewer);
		model.put("application", form);

		model.put("applicant", form.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendInterviewerNotification(Interviewer interviewer) {
		EmailTemplate tempalte = getDefaultEmailtemplate(INTERVIEWER_NOTIFICATION);
		internalSendMail(interviewer, "interview.notification.interviewer", INTERVIEWER_NOTIFICATION, tempalte.getContent());
	}

	public void sendInterviewerReminder(Interviewer interviewer, boolean firstReminder) {
		String subject = "interview.feedback.request.reminder";
		EmailTemplateName templateName = INTERVIEWER_REMINDER;
		if (firstReminder) {
			subject = "interview.feedback.request";
			templateName= INTERVIEWER_REMINDER_FIRST;
		}
		EmailTemplate template = getDefaultEmailtemplate(templateName);
		internalSendMail(interviewer, subject, templateName, template.getContent());
	}

	private void internalSendMail(Interviewer interviewer, String subjectCode, EmailTemplateName templateName, String templateContent) {
		InternetAddress toAddress = createAddress(interviewer.getUser());
		String subject = resolveMessage(subjectCode, interviewer.getInterview().getApplication());

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,//
				templateName, templateContent, createModel(interviewer), null));
	}
}
