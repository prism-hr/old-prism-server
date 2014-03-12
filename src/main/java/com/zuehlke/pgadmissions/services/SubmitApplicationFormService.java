package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class SubmitApplicationFormService {

    @Autowired
    private ApplicationsService applicationService;

    @Autowired
    private StageDurationService stageDurationService;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;
    
    @Autowired
    private MailSendingService mailSendingService;

    public void submitApplication(ApplicationForm applicationForm) {
        applicationForm.setStatus(ApplicationFormStatus.VALIDATION);
        applicationForm.setSubmittedDate(DateUtils.truncateToDay(new Date()));
        
        assignValidationDueDate(applicationForm);
        assignBatchDeadline(applicationForm);
        
        mailSendingService.sendSubmissionConfirmationToApplicant(applicationForm);
        
        applicationFormUserRoleService.applicationSubmitted(applicationForm);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, applicationForm.getApplicant(), ApplicationUpdateScope.ALL_USERS);
    }

    void assignValidationDueDate(ApplicationForm applicationForm) {
        StageDuration validationDuration = stageDurationService.getByStatus(ApplicationFormStatus.VALIDATION);
        Date dueDate = DateUtils.addWorkingDaysInMinutes(applicationForm.getSubmittedDate(), validationDuration.getDurationInMinutes());
        applicationForm.setDueDate(dueDate);
    }

    void assignBatchDeadline(ApplicationForm applicationForm) {
        if (applicationForm.getProject() != null) {
            applicationForm.setBatchDeadline(applicationForm.getProject().getClosingDate());
        } else {
            applicationForm.setBatchDeadline(applicationService.getBatchDeadlineForApplication(applicationForm));
        }
    }

}
