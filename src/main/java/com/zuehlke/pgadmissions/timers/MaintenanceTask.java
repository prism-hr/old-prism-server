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
    private ApplicationExportService applicationExportService;
    
    @Autowired
    private UserService userService;

    @Scheduled(cron = "${daily.maintenance.cron}")
    public void runDaily() {
        log.trace("Deleting unused documents");
        documentService.deleteOrphanDocuments();
        
        log.trace("Escalating workflow transitions");
        stateService.executeEscalatedStateTransitions();
        
        log.trace("Sending update notifications.");
        notificationService.sendPendingUpdateNotifications();
        
        // TODO: email requests & reminders
    }
    
    @Scheduled(cron = "${ongoing.maintenance.cron}")
    public void runOngoing() {
        log.trace("Flushing workflow tranistions");
        stateService.executePropagatedStateTransitions();
        
        log.trace("Importing reference data");
        entityImportService.importReferenceData();
        
        log.trace("Exporting ucl applications");
        applicationExportService.exportUclApplications();
    }
    
}
