package com.zuehlke.pgadmissions.services.uclexport;

/**
 * Taks for phase 1 of application form transfer: this is:
 *   1. try to call PORTICO webserwice
 *   2. handle errors
 *   3. schedule phase 2 if there were no errors
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
        listener.transferStarted();
        uclExportService.transactionallyExecuteWebserviceCallAndHandlePersistentQueue(transferId, listener);
        listener.webserviceCallCompleted();
    }
}
