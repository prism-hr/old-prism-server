package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

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
        applicationForm.setSubmittedDate(DateUtils.truncateToDay(new Date()));
        
        applicationService.setApplicationStatus(applicationForm, ApplicationFormStatus.VALIDATION);
        
        mailSendingService.sendSubmissionConfirmationToApplicant(applicationForm);
        
        applicationFormUserRoleService.applicationSubmitted(applicationForm);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, applicationForm.getApplicant(), ApplicationUpdateScope.ALL_USERS);
    }

}
