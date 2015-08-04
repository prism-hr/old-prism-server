package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.ApplicationExportService;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationExportServiceHelper implements PrismServiceHelper {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ApplicationExportService applicationExportService;

    @Override
    public void execute() throws Exception {
        List<Integer> applicationIds = applicationService.getApplicationsForExport();
        for (Integer applicationId : applicationIds) {
            applicationExportService.submitExportRequest(applicationId);
        }
    }
    
    @Override
    public void shutdown() {
        return;
    }

}
