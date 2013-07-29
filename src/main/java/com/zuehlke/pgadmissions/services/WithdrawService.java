package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Service
public class WithdrawService {

    private final ApplicationsService applicationService;

    private final PorticoQueueService porticoQueueService;

    public WithdrawService() {
        this(null, null);
    }

    @Autowired
    public WithdrawService(final ApplicationsService applicationService, final PorticoQueueService porticoQueueService) {
        this.applicationService = applicationService;
        this.porticoQueueService = porticoQueueService;
    }

    @Transactional
    public void saveApplicationFormAndSendMailNotifications(final ApplicationForm form) {
        applicationService.save(form);
    }

    @Transactional
    public void sendToPortico(final ApplicationForm form) {
        if (!form.getWithdrawnBeforeSubmit()) {
            porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);
        }
    }
}
