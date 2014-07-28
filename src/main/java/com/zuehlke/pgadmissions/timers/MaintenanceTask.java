package com.zuehlke.pgadmissions.timers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate.SYSTEM_IMPORT_ERROR_NOTIFICATION;

import java.net.Authenticator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinition;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;

@Service
public class MaintenanceTask {

    private final Logger log = LoggerFactory.getLogger(MaintenanceTask.class);

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
    private UserService userService;

    @Scheduled(cron = "${daily.maintenance.cron}")
    public void runDaily() {
        log.trace("Importing reference data");
        importReferenceData();
        
        log.trace("Deleting unused documents");
        documentService.deleteOrphanDocuments();
        
        log.trace("Escalating workflow transitions");
        stateService.executeEscalatedStateTransitions();
        
        log.trace("Sending update notifications.");
        sendPendingUpdateNotifications();
        
        // TODO: email requests
    }
    
    @Scheduled(cron = "${ongoing.maintenance.cron}")
    public void runOngoing() {
        log.trace("Flushing workflow tranistions");
        stateService.executePropagatedStateTransitions();
        
        // TODO: email reminders
    }
    
    
    public void importReferenceData() {
        for (ImportedEntityFeed importedEntityFeed : entityImportService.getImportedEntityFeeds()) {
            String maxRedirects = null;
            try {
                maxRedirects = System.getProperty("http.maxRedirects");
                System.setProperty("http.maxRedirects", "5");

                entityImportService.importEntities(importedEntityFeed);
            } catch (XMLDataImportException e) {
                log.error("Error importing reference data.", e);
                String message = e.getMessage();
                Throwable cause = e.getCause();
                if (cause != null) {
                    message += "\n" + cause.toString();
                }

                com.zuehlke.pgadmissions.domain.System system = systemService.getSystem();
                for (User user : userService.getUsersForResourceAndRole(system, PrismRole.SYSTEM_ADMINISTRATOR)) {
                    NotificationTemplate importError = notificationService.getById(SYSTEM_IMPORT_ERROR_NOTIFICATION);
                    notificationService.sendNotification(user, system, importError, ImmutableMap.of("errorMessage", message));
                }

            } finally {
                Authenticator.setDefault(null);
                if (maxRedirects != null) {
                    System.setProperty("http.maxRedirects", maxRedirects);
                } else {
                    System.clearProperty("http.maxRedirects");
                }
            }
        }
    }
    
    public void sendPendingUpdateNotifications() {
        List<UserNotificationDefinition> definitions = notificationService.getPendingUpdateNotifications();
        for (UserNotificationDefinition definition : definitions) {
            notificationService.sendPendingNotification(definition);
        }
    }

}
