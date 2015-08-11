package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.ApplicationExportService;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationExportServiceHelper extends PrismServiceHelperAbstract {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ApplicationExportService applicationExportService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        List<Integer> applicationIds = applicationService.getApplicationsForExport();
        for (Integer applicationId : applicationIds) {
            submitExportRequest(applicationId);
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void submitExportRequest(Integer applicationId) throws Exception {
        if (!isShuttingDown()) {
            applicationExportService.submitExportRequest(applicationId);
        }
    }

}
