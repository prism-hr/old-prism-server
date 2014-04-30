package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
@Transactional
public class WithdrawService {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Transactional
    public void withdrawApplication(final ApplicationForm application) {
        applicationFormService.setApplicationStatus(application, ApplicationFormStatus.APPLICATION_WITHDRAWN);
    }

    @Transactional
    public void sendToPortico(final ApplicationForm application) {
        applicationFormService.queueApplicationForExport(application);
    }
    
}
