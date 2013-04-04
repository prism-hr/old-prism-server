package com.zuehlke.pgadmissions.mail;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

public abstract class StateChangeMailSender extends MailSender {

	public StateChangeMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource, EmailTemplateService emailTemplateService) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource, emailTemplateService);
	}

	public abstract void sendMailsForApplication(ApplicationForm form, String messageCode, EmailTemplateName templateName, String templateContent, NotificationType notificationType);


}