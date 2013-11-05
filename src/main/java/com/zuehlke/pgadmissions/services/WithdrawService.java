package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class WithdrawService {

    @Autowired
    private ApplicationsService applicationService;

    @Autowired
    private PorticoQueueService porticoQueueService;

    @Autowired
    private EventFactory eventFactory;

    @Transactional
    public void withdrawApplication(final ApplicationForm application) {

        if (!application.isSubmitted()) {
            application.setWithdrawnBeforeSubmit(true);
        }

        application.setStatus(ApplicationFormStatus.WITHDRAWN);
        application.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.WITHDRAWN));
        applicationService.save(application);
    }

    @Transactional
    public void sendToPortico(final ApplicationForm form) {
        if (!form.getWithdrawnBeforeSubmit()) {
            porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);
        }
    }
}
