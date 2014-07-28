package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Application;

@Service
@Transactional
public class WithdrawService {

    @Autowired
    private ApplicationService applicationFormService;

    @Transactional
    public void withdrawApplication(final Application application) {
    //TODO: remove class and integrate with workflow engine
    //       applicationFormService.setApplicationStatus(application, PrismState.APPLICATION_WITHDRAWN);
    }

    @Transactional
    public void sendToPortico(final Application application) {
//        applicationFormService.queueApplicationForExport(application);
    }
    
}
