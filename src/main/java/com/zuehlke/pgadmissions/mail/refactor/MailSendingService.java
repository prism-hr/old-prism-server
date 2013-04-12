package com.zuehlke.pgadmissions.mail.refactor;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_PASSWORD_CONFIRMATION;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

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
	
	public void sendInterviewAdministrationReminder() {
		
	}
	
	public void sendResetPasswordMessage(final RegisteredUser user, final String newPassword) {
        EmailModelBuilder modelBuilder = new EmailModelBuilder() {
            @Override
            public Map<String, Object> build() {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("user", user);
                model.put("newPassword", newPassword);
                model.put("host", Environment.getInstance().getApplicationHostName());
                return model;
            }
        };
        String subject = mailSender.resolveMessage("user.password.reset", (Object[]) null);
        PrismEmailMessageBuilder messageBuilder = new PrismEmailMessageBuilder()
            .to(user).subjectCode(subject).model(modelBuilder.build()).emailTemplate(NEW_PASSWORD_CONFIRMATION);
        PrismEmailMessage message = messageBuilder.build();
        mailSender.sendEmail(message);
	}
}
