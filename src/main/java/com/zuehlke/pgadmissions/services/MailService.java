package com.zuehlke.pgadmissions.services;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

public class MailService {

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;	
	private final ApplicationsService applicationsService;
	private final RefereeDAO refereeDAO;

	private final Logger log = Logger.getLogger(MailService.class);
	
	public MailService() {
		this(null, null, null, null);
	}

	@Autowired
	public MailService(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender, ApplicationsService applicationsService,
			RefereeDAO refereeDAO) {
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.applicationsService = applicationsService;
		this.refereeDAO = refereeDAO;
	}

	public void sendMailToAdminsAndChangeLastReminderDate(ApplicationForm form) {
		List<RegisteredUser> administrators = form.getProject().getProgram().getAdministrators();

		for (RegisteredUser admin : administrators) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("admin", admin);
				model.put("application", form);
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());

				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application Validation Reminder",
						"private/staff/admin/mail/application_validation_reminder.ftl", model));
				form.setLastEmailReminderDate(new Date());
				applicationsService.save(form);
			} catch (Throwable e) {
				log.warn("error while sending email", e);
			}
		}

	}

	@Transactional
	public List<Referee> getRefereesDueAReminder() {
		return refereeDAO.getRefereesDueAReminder();
	}

	@Transactional
	public void sendReminderAndUpdateLastNotified(Referee referee) {

		try {
			ApplicationForm form = referee.getApplication();
			List<RegisteredUser> administrators = form.getProject().getProgram().getAdministrators();
			String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("adminsEmails", adminsEmails);
			model.put("referee", referee);
			model.put("application", form);
			model.put("programme", form.getProgrammeDetails());
			model.put("applicant", form.getApplicant());
			model.put("host", Environment.getInstance().getApplicationHostName());
			if (referee.getUser() != null && referee.getUser().isEnabled()) {
				InternetAddress toAddress = new InternetAddress(referee.getUser().getEmail(), referee.getUser().getFirstName() + " "
						+ referee.getUser().getLastName());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Reminder - reference required",
						"private/referees/mail/existing_user_referee_reminder_email.ftl", model));
			} else {
				InternetAddress toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Reminder - reference required",
						"private/referees/mail/referee_reminder_email.ftl", model));
			}
			referee.setLastNotified(Calendar.getInstance().getTime());
			refereeDAO.save(referee);

		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}

	}

	private String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		StringBuilder adminsMails = new StringBuilder();
		for (RegisteredUser admin : administrators) {
			adminsMails.append(admin.getEmail());
			adminsMails.append(", ");
		}
		String result = adminsMails.toString();
		if (!result.isEmpty()) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
