package com.zuehlke.pgadmissions.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Service
public class WithdrawService {

	private final ApplicationsService applicationService;
	private final MailService mailService;
	private final RefereeService refereeService;
	

	public WithdrawService() {
		this(null, null, null);
	}

	@Autowired
	public WithdrawService(ApplicationsService applicationService, MailService mailService, RefereeService refereeService) {
		this.mailService = mailService;
		this.applicationService = applicationService;
		this.refereeService = refereeService;
	}
	
	@Transactional
	public void saveApplicationFormAndSendMailNotifications(ApplicationForm form) {
		applicationService.save(form);
		mailService.sendWithdrawMailToReferees(refereeService.getRefereesWhoHaveNotProvidedReference(form));
		mailService.sendWithdrawToAdmins(form);
		mailService.sendWithdrawToReviewers(form);
	}


}
