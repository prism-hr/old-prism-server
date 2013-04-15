package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.IMPORT_ERROR;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

public class DataImporterMailSender extends MailSender {

	private final UserService userService;
	private final SessionFactory sessionFactory;

    public DataImporterMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender,
            MessageSource msgSource, UserService userService, SessionFactory sessionFactory, EmailTemplateService emailTemplateService) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource, emailTemplateService);
		this.userService = userService;
		this.sessionFactory = sessionFactory;
	}

	Map<String, Object> createModel(RegisteredUser user, String message) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", user);
		model.put("host", Environment.getInstance().getApplicationHostName());
		model.put("message", message);
		model.put("time", new Date());
		return model;
	}

	public void sendErrorMessage(String message) {
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<RegisteredUser> superadmins = userService.getUsersInRole(Authority.SUPERADMINISTRATOR);
		transaction.commit();
		EmailTemplate template = getDefaultEmailtemplate(IMPORT_ERROR);
		for (RegisteredUser user : superadmins) {			
			internalSendMail("reference.data.import.error", message, user, IMPORT_ERROR, template.getContent());
		}
	}

	private void internalSendMail(String subjectCode, String message, RegisteredUser user, EmailTemplateName tempalteName, String templateContent) {
		InternetAddress toAddress = createAddress(user);
		String subject = resolveMessage(subjectCode);
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, tempalteName, templateContent, createModel(user, message), null));
	}
}
