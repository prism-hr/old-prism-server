package com.zuehlke.pgadmissions.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.AdvertService;
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
    private AdvertService advertService;
    
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
        executePendingStateTransitions();
        importReferenceData();
        exportUclApplications();
        updateProgamStudyOptions();
        updateAdvertClosingDates();
        sendDeferredWorkflowNotifications();
        sendRecommendationNotifications();
        deleteUnusedDocuments();
    }

    private void executePendingStateTransitions() {
        try {
            logger.info("Executing pending state transitions");
            stateService.executePendingStateTransitions();
        } catch (Exception e) {
            logger.info("Error executing pending state transitions", e);
        }
    }
    
    private void importReferenceData() {
        try {
            logger.info("Importing reference data");
            entityImportService.importReferenceData();
        } catch (Exception e) {
            logger.info("Error importing reference data", e);
        }
    }
    
    private void updateProgamStudyOptions() {
        try {
            logger.info("Updating program study options");
            programService.updateProgramStudyOptions();
        } catch (Exception e) {
            logger.info("Error updating program study options", e);
        }
    }
    
    private void updateAdvertClosingDates() {
        try {
            logger.info("Updating advert closing dates");
            advertService.updateAdvertClosingDates();
        } catch (Exception e) {
            logger.info("Error updating advert closing dates", e);
        }
    }
    
    private void exportUclApplications() {
        try {
            logger.trace("Exporting applications");
            applicationExportService.exportUclApplications();
        } catch (Exception e) {
            logger.info("Error exporting applications", e);
        }
    }
    
    private void sendDeferredWorkflowNotifications() {
        try {
            logger.info("Sending deferred workflow notifications.");
            notificationService.sendDeferredWorkflowNotifications();
        } catch (Exception e) {
            logger.info("Error sending deferred workflow notifications", e);
        }
    }
    
    private void sendRecommendationNotifications() {
        try {
            logger.info("Sending recommendation notifications");
            notificationService.sendRecommendationNotifications();     
        }  catch (Exception e) {
            logger.info("Error sending recommendation notifications", e);
        }
    }
    
    private void deleteUnusedDocuments() {
        try {
            logger.info("Deleting unused documents");
            documentService.deleteOrphanDocuments();
        } catch (Exception e) {
            logger.info("Error deleting unused documents", e);
        }
    }
    
}
