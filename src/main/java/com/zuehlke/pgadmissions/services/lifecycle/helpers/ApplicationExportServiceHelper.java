package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.services.ApplicationExportService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationExportServiceHelper implements AbstractServiceHelper {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationExportService applicationExportService;

    @Override
    public void execute() throws Exception {
        List<Integer> applicationIds = applicationService.getApplicationsForExport();
        for (Integer applicationId : applicationIds) {
            applicationExportService.submitExportRequest(applicationId);
        }
    }

}
