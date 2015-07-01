package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.services.ApplicationExportService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ApplicationExportServiceHelper implements AbstractServiceHelper {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ApplicationExportService applicationExportService;

    @Override
    public void execute() throws Exception {
        // TODO reimplement
//        List<Integer> applicationIds = applicationService.getApplicationsForExport();
//        for (Integer applicationId : applicationIds) {
//            applicationExportService.submitExportRequest(applicationId);
//        }
    }

}
