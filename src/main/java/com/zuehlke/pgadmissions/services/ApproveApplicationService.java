package com.zuehlke.pgadmissions.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Service
public class ApproveApplicationService {

	private final ApplicationsService applicationService;
	private final MailService mailService;

	public ApproveApplicationService() {
		this(null, null);
	}

	@Autowired
	public ApproveApplicationService(ApplicationsService applicationService, MailService mailService) {
		this.mailService = mailService;
		this.applicationService = applicationService;

	}

	@Transactional
	public void saveApplicationFormAndSendMailNotifications(ApplicationForm form) {
		applicationService.save(form);
		mailService.sendSubmissionMailToReferees(form);
	}

}
