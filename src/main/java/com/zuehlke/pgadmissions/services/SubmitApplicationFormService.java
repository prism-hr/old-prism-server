package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
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

    public void submitApplication(ApplicationForm applicationForm) {
        applicationService.setApplicationStatus(applicationForm, ApplicationFormStatus.VALIDATION);
        
        mailSendingService.sendSubmissionConfirmationToApplicant(applicationForm);
        
        applicationFormUserRoleService.applicationSubmitted(applicationForm);
        applicationFormUserRoleService.applicationUpdated(applicationForm, applicationForm.getApplicant());
    }

}
