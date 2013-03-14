package com.zuehlke.pgadmissions.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.services.exporters.UclExportService;

@Service
@Transactional
public class WithdrawService {

    private final ApplicationsService applicationService;

    private final MailService mailService;

    private final RefereeService refereeService;

    private final UclExportService uclExportService;

    public WithdrawService() {
        this(null, null, null, null);
    }

    @Autowired
    public WithdrawService(ApplicationsService applicationService, MailService mailService,
            RefereeService refereeService, UclExportService exportService) {
        this.mailService = mailService;
        this.applicationService = applicationService;
        this.refereeService = refereeService;
        this.uclExportService = exportService;
    }

    public void saveApplicationFormAndSendMailNotifications(ApplicationForm applicationForm) {
        applicationService.save(applicationForm);
        mailService.sendWithdrawMailToAdminsReviewersInterviewersSupervisors(refereeService.getRefereesWhoHaveNotProvidedReference(applicationForm), applicationForm);
        // TODO: Enable when ready for production
        // if (applicationForm.isSubmitted()) {
        // uclExportService.sendToPortico(applicationForm);
        // }
    }
}
