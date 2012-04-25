package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

public class MailService {

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final Logger log = Logger.getLogger(MailService.class);
	private final ApplicationsService applicationsService;
	
	public MailService(){
		this(null, null, null);
	}
	
	@Autowired
	public MailService(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender, ApplicationsService applicationsService) {
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.applicationsService = applicationsService;
	}
	
	
	public void sendMailToAdmins(ApplicationForm form) {
		List<RegisteredUser> administrators = form.getProject().getProgram().getAdministrators();

		for (RegisteredUser admin : administrators) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("admin", admin);
				model.put("application", form);
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());

				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application Validation Reminder", "private/staff/admin/mail/application_validation_reminder.ftl", model));
			} catch (Throwable e) {
				log.warn("error while sending email", e);
			}
		}
		form.setLastEmailReminderDate(new Date());
		applicationsService.save(form);

	}
}
