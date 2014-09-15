package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.services.ApplicationExportService;

@Component
public class ApplicationExportServiceHelper extends AbstractServiceHelper {
    
    private final Logger logger = LoggerFactory.getLogger(ApplicationExportService.class);
    
    @Autowired
    protected ApplicationExportService applicationExportService;

    @Override
    public void execute() {
        List<Application> applications = applicationExportService.getUclApplicationsForExport();
        for (Application application : applications) {
            try {
                String applicationCode = application.getCode();
                logger.info("Exporting data for application: " + applicationCode);
                String exportReference = applicationExportService.sendDataExportRequest(application);
                if (exportReference != null) {
                    logger.info("Exporting documents for application: " + applicationCode);
                    applicationExportService.sendDocumentExportRequest(application, exportReference);
                }
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }
    
}
