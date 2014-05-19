package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class SubmitApplicationFormService {

    @Autowired
    private ApplicationFormService applicationService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;
    
    @Autowired
    private MailSendingService mailSendingService;

    public void submitApplication(Application applicationForm) {
        applicationService.setApplicationStatus(applicationForm, PrismState.APPLICATION_VALIDATION);
        
        mailSendingService.sendSubmissionConfirmationToApplicant(applicationForm);
        
        applicationFormUserRoleService.applicationSubmitted(applicationForm);
        applicationFormUserRoleService.applicationUpdated(applicationForm, applicationForm.getUser());
    }

}
