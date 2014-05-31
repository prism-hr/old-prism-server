package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.StateService;

@Service
public class DataMaintenanceTask {

    private final Logger log = LoggerFactory.getLogger(DataMaintenanceTask.class);
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private StateService stateService;

    // TODO: unify the timer tasks
    @Scheduled(cron = "${data.maintenance.cron}")
    public void maintainData() {
        log.info("Running data maintenance tasks");
        documentService.deleteOrphanDocuments();
        programService.deleteInactiveAdverts();
        stateService.executeEscalatedStateTransitions();
        log.info("Completed data maintenance tasks");
    }

}
