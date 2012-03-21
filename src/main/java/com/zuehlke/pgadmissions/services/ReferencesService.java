package com.zuehlke.pgadmissions.services;

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
	public void saveApplicationFormAndSendMailToReferees(ApplicationForm form) {
		applicationService.save(form);
		try {
			List<Referee> referees = form.getReferees();
			for (Referee referee : referees) {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("referee", referee);
				model.put("applicant", form.getApplicant());
				model.put("programme", form.getProgrammeDetails());
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " "
						+ referee.getLastname());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress,
						"Referee Notification", "private/referees/mail/referee_notification_email.ftl", model));
			}

		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}

	}
}
