package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Service
@Transactional
public class WithdrawService {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Transactional
    public void withdrawApplication(final Application application) {
        applicationFormService.setApplicationStatus(application, PrismState.APPLICATION_WITHDRAWN);
    }

    @Transactional
    public void sendToPortico(final Application application) {
        applicationFormService.queueApplicationForExport(application);
    }
    
}
