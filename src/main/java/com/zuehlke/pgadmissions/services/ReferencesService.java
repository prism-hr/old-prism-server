package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

@Service
public class ReferencesService {
	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final Logger log = Logger.getLogger(ReferencesService.class);
	private final ApplicationsService applicationService;

	public ReferencesService() {
		this(null, null, null);
	}

	@Autowired
	public ReferencesService(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender,
			ApplicationsService applicationService) {
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.applicationService = applicationService;
	}

	@Transactional
	public void saveApplicationFormAndSendMailNotifications(ApplicationForm form) {
		applicationService.save(form);
		try {
			sendMailToReferees(form);
			sendMailToApplicant(form);
			sendMailToAdmins(form);
		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}

	}
	
	private void sendMailToAdmins(ApplicationForm form) throws UnsupportedEncodingException {
		List<RegisteredUser> administrators = form.getProject().getProgram().getAdministrators();
		for (RegisteredUser admin : administrators) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("admin", admin);
			model.put("application", form);
			model.put("host", Environment.getInstance().getApplicationHostName());
			InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " "
					+ admin.getLastName());
			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress,
					"Application Submitted", "private/staff/admin/mail/application_submit_confirmation.ftl", model));
		}
		
	}

	private void sendMailToApplicant(ApplicationForm form) throws UnsupportedEncodingException {
		RegisteredUser applicant = form.getApplicant();
		List<RegisteredUser> administrators = form.getProject().getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);
		model.put("application", form);
		model.put("host", Environment.getInstance().getApplicationHostName());
		InternetAddress toAddress = new InternetAddress(applicant.getEmail(), applicant.getFirstName() + " "
				+ applicant.getLastName());
		mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress,
				"Application Submitted", "private/pgStudents/mail/application_submit_confirmation.ftl", model));
		
	}

	private String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		StringBuilder adminsMails = new StringBuilder();
		for (RegisteredUser admin : administrators) {
			adminsMails.append(admin.getEmail());
			adminsMails.append(", ");
		}
		String result = adminsMails.toString();
		if (!result.isEmpty()) {
			result = result.substring(0, result.length()-1);
		}
		return result;
	}

	public void sendMailToReferees(ApplicationForm form) throws UnsupportedEncodingException{
		List<Referee> referees = form.getReferees();
		List<RegisteredUser> administrators = form.getProject().getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		for (Referee referee : referees) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("referee", referee);
			model.put("adminsEmails", adminsEmails);
			model.put("applicant", form.getApplicant());
			model.put("programme", form.getProgrammeDetails());
			model.put("host", Environment.getInstance().getApplicationHostName());
			InternetAddress toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " "
					+ referee.getLastname());
			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress,
					"Referee Notification", "private/referees/mail/referee_notification_email.ftl", model));
	}
	}
}
