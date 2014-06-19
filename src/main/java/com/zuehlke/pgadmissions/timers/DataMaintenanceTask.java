package com.zuehlke.pgadmissions.timers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.StateService;

@Service
public class DataMaintenanceTask {
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private StateService stateService;

    // TODO: unify the timer tasks
    @Scheduled(cron = "${data.maintenance.cron}")
    public void performDataMaintenance() {
        documentService.deleteOrphanDocuments();
        programService.deleteInactiveAdverts();
        stateService.executeEscalatedStateTransitions();
    }
    
    @Scheduled(cron = "${workflow.maintenance.cron}")
    public void performWorkflowMaintenance() {
        stateService.executePropagatedStateTransitions();
    }

}
