package com.zuehlke.pgadmissions.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.DocumentService;

@Service
public class PrismMaintenanceService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private StateTransitionHelper stateTransitionHelper;
    
    @Autowired
    private EntityImportHelper entityImportHelper;
    
    @Autowired
    private AdvertHelper advertHelper;
    
    @Autowired
    private ProgramHelper programHelper;

    @Autowired
    private ApplicationExportHelper applicationExportHelper;
    
    @Autowired
    private NotificationHelper notificationHelper;

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
            stateTransitionHelper.executePendingStateTransitions();
        } catch (Exception e) {
            logger.info("Error executing pending state transitions", e);
        }
    }
    
    private void importReferenceData() {
        try {
            logger.info("Importing reference data");
            entityImportHelper.importReferenceData();
        } catch (Exception e) {
            logger.info("Error importing reference data", e);
        }
    }
    
    private void updateProgamStudyOptions() {
        try {
            logger.info("Updating program study options");
            programHelper.updateProgramStudyOptions();
        } catch (Exception e) {
            logger.info("Error updating program study options", e);
        }
    }
    
    private void updateAdvertClosingDates() {
        try {
            logger.info("Updating advert closing dates");
            advertHelper.updateAdvertClosingDates();
        } catch (Exception e) {
            logger.info("Error updating advert closing dates", e);
        }
    }
    
    private void exportUclApplications() {
        try {
            logger.trace("Exporting applications");
            applicationExportHelper.exportUclApplications();
        } catch (Exception e) {
            logger.info("Error exporting applications", e);
        }
    }
    
    private void sendDeferredWorkflowNotifications() {
        try {
            logger.info("Sending deferred workflow notifications.");
            notificationHelper.sendDeferredWorkflowNotifications();
        } catch (Exception e) {
            logger.info("Error sending deferred workflow notifications", e);
        }
    }
    
    private void sendRecommendationNotifications() {
        try {
            logger.info("Sending recommendation notifications");
            notificationHelper.sendRecommendationNotifications();     
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
