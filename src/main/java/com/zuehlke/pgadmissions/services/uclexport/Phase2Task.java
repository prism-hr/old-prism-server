package com.zuehlke.pgadmissions.services.uclexport;

/**
 * Task for phase 2 of application form transfer. This task has the following general plan:
 *   1. Extract application attachments and prepare zip package containing them all.
 *   2. Send this package via sftp to PORTICO.
 *   3. Handle sftp errors (if any).
 */
class Phase2Task implements Runnable {
    private Integer applicationId;
    private Long transferId;
    private TransferListener listener;
    private UclExportServiceImpl uclExportService;

    Phase2Task(UclExportServiceImpl uclExportService, Integer applicationId, Long transferId, TransferListener listener) {
        this.uclExportService = uclExportService;
        this.applicationId = applicationId;
        this.transferId = transferId;
        this.listener = listener;
    }

    @Override
    public void run() {
        uclExportService.transactionallyExecuteSftpTransferAndHandlePersistentQueue(transferId, listener);
    }

    @Override
    public String toString() {
        return "UCL transfer phase 2 task: applicationForm=" + applicationId + " transfer=" + transferId;
    }

}
