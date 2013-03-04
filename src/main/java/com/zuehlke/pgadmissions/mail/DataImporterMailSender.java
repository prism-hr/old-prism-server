package com.zuehlke.pgadmissions.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

public class DataImporterMailSender extends MailSender {

	private final UserService userService;
	private final SessionFactory sessionFactory;

    public DataImporterMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender,
            MessageSource msgSource, UserService userService, SessionFactory sessionFactory) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
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
		for (RegisteredUser user : superadmins) {			
			internalSendMail("reference.data.import.error", message, user, "private/mail/import_error.ftl");
		}
	}

	private void internalSendMail(String subjectCode, String message, RegisteredUser user, String template) {
		InternetAddress toAddress = createAddress(user);
		String subject = resolveMessage(subjectCode);
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, template, createModel(user, message), null));
	}
}
