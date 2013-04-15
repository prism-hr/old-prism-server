package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.jms.PorticoQueueService;
import com.zuehlke.pgadmissions.mail.refactor.MailSendingService;

@Service
public class WithdrawService {

	private final ApplicationsService applicationService;
	
	private final PorticoQueueService porticoQueueService;

    private final MailSendingService mailSendingService;

	public WithdrawService() {
		this(null, null, null);
	}

	@Autowired
    public WithdrawService(final ApplicationsService applicationService, final MailSendingService mailSendingService, final PorticoQueueService porticoQueueService) {
		this.applicationService = applicationService;
		this.porticoQueueService = porticoQueueService;
        this.mailSendingService = mailSendingService;
	}
	
	@Transactional
	public void saveApplicationFormAndSendMailNotifications(final ApplicationForm form) {
		applicationService.save(form);
		mailSendingService.scheduleWithdrawalConfirmation(form);
	}
	
    @Transactional
    public void sendToPortico(final ApplicationForm form) {
        if (form.isSubmitted()) {
            porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);
        }
    }
}
