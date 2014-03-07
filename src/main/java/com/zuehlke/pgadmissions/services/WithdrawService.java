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

    @Autowired ApplicationFormUserRoleService applicationFormUserRoleService;

    @Transactional
    public void withdrawApplication(final ApplicationForm application) {
        application.setStatus(ApplicationFormStatus.WITHDRAWN);
        applicationService.save(application);
        applicationFormUserRoleService.deleteApplicationActions(application);
    }

    @Transactional
    public void sendToPortico(final ApplicationForm form) {
        if (form.getPreviousStatus() != ApplicationFormStatus.UNSUBMITTED && form.getProgram().getProgramFeed() != null) {
            porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);
        }
    }
}