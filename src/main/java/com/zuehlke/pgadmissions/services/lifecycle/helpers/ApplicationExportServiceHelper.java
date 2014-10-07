package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.ApplicationExportService;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationExportServiceHelper extends AbstractServiceHelper {

    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private ApplicationExportService applicationExportService;
    
    @Override
    public void execute() throws DeduplicationException {
        List<Integer> applicationIds = applicationService.getApplicationsForExport();
        for (Integer applicationId : applicationIds) {
            applicationExportService.submitExportRequest(applicationId);
        }
    }

}
