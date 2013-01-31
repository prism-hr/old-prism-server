package com.zuehlke.pgadmissions.lifecycle;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.zuehlke.pgadmissions.services.exporters.UclExportService;
import com.zuehlke.pgadmissions.timers.XMLDataImportTask;

@Component
public class UclExportServiceApplicationInitializer {

    private static final Logger log = Logger.getLogger(UclExportServiceApplicationInitializer.class);
    
    private final UclExportService exportService;
    
    private final TransactionTemplate transactionTemplate;
    
    private final XMLDataImportTask xMLDataImportTask;
    
    private final String loadReferenceDataOnStartup;
    
    public UclExportServiceApplicationInitializer() {
        this(null, null, null, null);
    }
    
    @Autowired
    public UclExportServiceApplicationInitializer(UclExportService exportService, 
            TransactionTemplate transactionTemplate, XMLDataImportTask xMLDataImportTask,
            @Value("${xml.data.import.onstartup}") String loadReferenceDataOnStartup) {
        this.exportService = exportService;
        this.transactionTemplate = transactionTemplate;
        this.xMLDataImportTask = xMLDataImportTask;
        this.loadReferenceDataOnStartup = loadReferenceDataOnStartup;
    }
    
    /**
     * I am initialising the queue for transferring applications to PORTICO 
     * after the web application has been re-deployed.
     */
    public void initialiseQueueAfterSystemStartup() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    exportService.systemStartupSendingQueuesRecovery();
                } catch (Exception e) {
                    log.error("There was an error re-initialising the queues for UCL-Export processing.", e);
                    status.setRollbackOnly();
                }
            }
        });
    }
    
    /**
     * I am downloading the latest set of reference data from UCL after the web application has been re-deployed.
     */
    public void initialiseReferenceDataAfterSystemStartup() {
        if (!BooleanUtils.toBoolean(loadReferenceDataOnStartup)) {
            return;
        }
        
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    xMLDataImportTask.imoprtData();
                } catch (Exception e) {
                    log.error("There was an error downloading the latest reference data.", e);
                    status.setRollbackOnly();
                }
            }
        });
    }
    
    @PostConstruct
    public void postConstruct() {
        initialiseReferenceDataAfterSystemStartup();
        initialiseQueueAfterSystemStartup();
    }
}
