package com.zuehlke.pgadmissions.services.exporters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task for phase 2 of application form transfer. This task has the following general plan:
 *   1. Extract application attachments and prepare zip package containing them all.
 *   2. Send this package via sftp to PORTICO.
 *   3. Handle sftp errors (if any).
 */
public class Phase2Task implements Runnable {
    private final Logger log = LoggerFactory.getLogger(Phase1Task.class);

    private Integer applicationId;
    private Long transferId;
    private TransferListener listener;
    private UclExportService uclExportService;

    Phase2Task(UclExportService uclExportService, Integer applicationId, Long transferId, TransferListener listener) {
        this.uclExportService = uclExportService;
        this.applicationId = applicationId;
        this.transferId = transferId;
        this.listener = listener;
    }

    @Override
    public void run() {
        log.info("Starting phase-2 task for transfer " + transferId);
        uclExportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(transferId, listener);
        log.info("Finishing phase-2 task for transfer " + transferId);
    }

    @Override
    public String toString() {
        return "UCL transfer phase 2 task: applicationForm=" + applicationId + " transfer=" + transferId;
    }

}
