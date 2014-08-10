package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationExportService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;

@Service
public class MaintenanceTask {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private EntityImportService entityImportService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private StateService stateService;
    
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private ApplicationExportService applicationExportService;
    
    @Autowired
    private UserService userService;

    @Scheduled(cron = "${maintenance.daily}")
    public void runDaily() {
        try {
            logger.info("Sending update notifications.");
            notificationService.sendPendingUpdateNotifications();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        
        // TODO: request notifications
    }
    
    @Scheduled(cron = "${maintenance.ongoing}")
    public void runOngoing() {
        try {
            logger.info("Executing deferred workflow transitions");
            stateService.executeDeferredStateTransitions();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        
        try {
            logger.info("Importing reference data");
            entityImportService.importReferenceData();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        try {
            logger.trace("Exporting ucl applications");
            applicationExportService.exportUclApplications();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        
        try {
            logger.info("Deleting unused documents");
            documentService.deleteOrphanDocuments();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

    }
    
}
