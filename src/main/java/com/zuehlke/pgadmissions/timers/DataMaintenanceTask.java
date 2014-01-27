package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;

@Service
public class DataMaintenanceTask {

    private final Logger log = LoggerFactory.getLogger(DataMaintenanceTask.class);

    @Autowired
    private ProgramInstanceService programInstanceService;
    
    @Autowired
    private DocumentService documentService;

    @Scheduled(cron = "${data.maintenance.cron}")
    public void maintainData() {
        log.info("Running maintenance task");
        programInstanceService.disableLapsedInstances();
        documentService.deleteOrphanDocuments();
    }

}
