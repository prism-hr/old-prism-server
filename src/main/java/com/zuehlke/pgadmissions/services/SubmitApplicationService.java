package com.zuehlke.pgadmissions.services;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Service
public class SubmitApplicationService {

	private final ApplicationsService applicationService;
	private final MailService mailService;

	public SubmitApplicationService() {
		this(null, null);
	}

	@Autowired
	public SubmitApplicationService(ApplicationsService applicationService, MailService mailService) {
		this.mailService = mailService;
		this.applicationService = applicationService;

	}

	@Transactional
	public void saveApplicationFormAndSendMailNotifications(ApplicationForm form) {
		form.setLastUpdated(new Date());
		applicationService.save(form);
		mailService.sendSubmissionMailToApplicant(form);
		mailService.sendSubmissionMailToAdmins(form);
	}

}
