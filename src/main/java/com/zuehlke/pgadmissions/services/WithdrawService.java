package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.jms.PorticoQueueService;

@Service
public class WithdrawService {

	private final ApplicationsService applicationService;
	
	private final MailService mailService;
	
	private final RefereeService refereeService;
	
	private final PorticoQueueService porticoQueueService;

	public WithdrawService() {
		this(null, null, null, null);
	}

	@Autowired
    public WithdrawService(ApplicationsService applicationService, MailService mailService,
            RefereeService refereeService, PorticoQueueService porticoQueueService) {
		this.mailService = mailService;
		this.applicationService = applicationService;
		this.refereeService = refereeService;
		this.porticoQueueService = porticoQueueService;
	}
	
	@Transactional
	public void saveApplicationFormAndSendMailNotifications(final ApplicationForm form) {
		applicationService.save(form);
		mailService.sendWithdrawMailToAdminsReviewersInterviewersSupervisors(refereeService.getRefereesWhoHaveNotProvidedReference(form), form);
	}
	
    @Transactional
    public void sendToPortico(final ApplicationForm form) {
        // TODO: Enable when ready for production
        if (form.isSubmitted()) {
            porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);
        }
    }
}
