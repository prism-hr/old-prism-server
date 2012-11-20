package com.zuehlke.pgadmissions.lifecycle;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.zuehlke.pgadmissions.services.exporters.UclExportService;

@Component
public class UclExportServiceApplicationInitializer {

    private static final Logger log = Logger.getLogger(UclExportServiceApplicationInitializer.class);
    
    private final UclExportService exportService;
    
    private final TransactionTemplate transactionTemplate;
    
    public UclExportServiceApplicationInitializer() {
        this(null, null);
    }
    
    @Autowired
    public UclExportServiceApplicationInitializer(UclExportService exportService, TransactionTemplate transactionTemplate) {
        this.exportService = exportService;
        this.transactionTemplate = transactionTemplate;
    }
    
    /**
     * I am initialising the queue for transferring applications to PORTICO 
     * after the web application has been re-deployed.
     */
    @PostConstruct
    public void initialiseQueueAfterSystemStartup() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    exportService.systemStartupSendingQueuesRecovery();
                } catch (Throwable e) {
                    log.error("There was an error re-initialising the queues for UCL-Export processing", e);
                    status.setRollbackOnly();
                }
            }
        });
    }
}
