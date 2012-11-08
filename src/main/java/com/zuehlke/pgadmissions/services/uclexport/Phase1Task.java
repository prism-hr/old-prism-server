package com.zuehlke.pgadmissions.services.uclexport;

/**
 * Task for phase 1 of application form transfer. This task has the following general plan:
 *   1. Try to call PORTICO webserwice.
 *   2. Handle webservice call errors (if any).
 *   3. Schedule phase 2 if there were no errors
 */
class Phase1Task implements Runnable {
    private Integer applicationId;
    private Long transferId;
    private TransferListener listener;
    private UclExportServiceImpl uclExportService;

    Phase1Task(UclExportServiceImpl uclExportService, Integer applicationId, Long transferId, TransferListener listener) {
        this.uclExportService = uclExportService;
        this.applicationId = applicationId;
        this.transferId = transferId;
        this.listener = listener;
    }

    @Override
    public void run() {
        uclExportService.transactionallyExecuteWebserviceCallAndHandlePersistentQueue(transferId, listener);
    }

    @Override
    public String toString() {
        return "UCL transfer phase 1 task: applicationForm=" + applicationId + " transfer=" + transferId;
    }
}
