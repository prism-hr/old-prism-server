package com.zuehlke.pgadmissions.services.exporters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task for phase 1 of application form transfer. This task has the following general plan:
 *   1. Try to call PORTICO webserwice.
 *   2. Handle webservice call errors (if any).
 *   3. Schedule phase 2 if there were no errors
 */
public class Phase1Task implements Runnable {
    private static Logger log = LoggerFactory.getLogger(Phase1Task.class);

    private Integer applicationId;
    private Long transferId;
    private TransferListener listener;
    private UclExportService uclExportService;

    Phase1Task(UclExportService uclExportService, Integer applicationId, Long transferId, TransferListener listener) {
        this.uclExportService = uclExportService;
        this.applicationId = applicationId;
        this.transferId = transferId;
        this.listener = listener;
    }

    @Override
    public void run() {
        log.info("Starting phase-1 task for transfer " + transferId);
        uclExportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(transferId, listener);
        log.info("Finishing phase-1 task for transfer " + transferId);
    }

    @Override
    public String toString() {
        return "UCL transfer phase 1 task: applicationForm=" + applicationId + " transfer=" + transferId;
    }
}
