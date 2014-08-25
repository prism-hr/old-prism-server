package com.zuehlke.pgadmissions.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationExportService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;

@Service
public class PrismMaintenanceService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private StateService stateService;
    
    @Autowired
    private EntityImportService entityImportService;
    
    @Autowired
    private ProgramService programService;

    @Autowired
    private ApplicationExportService applicationExportService;
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DocumentService documentService;

    @Scheduled(cron = "${maintenance.ongoing}")
    public void maintainSystem() {
        try {
            logger.info("Executing pending state transitions");
            stateService.executePendingStateTransitions();
        } catch (Exception e) {
            logger.info("Error executing pending state transitions", e);
        }

        if (!stateService.hasPendingStateTransitions()) {
            try {
                logger.info("Importing reference data");
                entityImportService.importReferenceData();
            } catch (Exception e) {
                logger.info("Error importing reference data", e);
            }
            
            try {
                logger.info("Updating program closing dates");
                programService.updateProgramClosingDates();
            } catch (Exception e) {
                logger.info("Error updating program closing dates");
            }

            try {
                logger.trace("Exporting applications");
                applicationExportService.exportUclApplications();
            } catch (Exception e) {
                logger.info("Error exporting applications", e);
            }

            try {
                logger.info("Sending deferred workflow notifications.");
                notificationService.sendDeferredWorkflowNotifications();
            } catch (Exception e) {
                logger.info("Error sending deferred workflow notifications", e);
            }
        }

        try {
            logger.info("Sending recommendation notifications");
            notificationService.sendRecommendationNotifications();     
        }  catch (Exception e) {
            logger.info("Error sending recommendation notifications", e);
        }
        
        try {
            logger.info("Deleting unused documents");
            documentService.deleteOrphanDocuments();
        } catch (Exception e) {
            logger.info("Error deleting unused documents", e);
        }
    }
    
}
