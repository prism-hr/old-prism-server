package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.MaintenanceTask;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.services.helpers.ImportedEntityServiceHelperSystem;

@Service
public class MaintenanceService implements InitializingBean, DisposableBean {

    @Value("${startup.hibernate.search.buildIndex}")
    private Boolean buildIndex;

    @Value("${startup.workflow.initialize}")
    private Boolean initializeWorkflow;

    @Value("${startup.institutionDomicile.import}")
    private Boolean importInstitutionDomicile;

    @Value("${startup.advertCategory.import}")
    private Boolean importAdvertCategory;

    @Value("${maintenance.shutdown.timeout}")
    private Integer shutdownTimeout;

    @Autowired
    private ImportedEntityServiceHelperSystem importedEntityServiceHelper;

    @Autowired
    private SystemService systemService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutorService executor = Executors.newFixedThreadPool(MaintenanceTask.values().length);

    private HashMap<MaintenanceTask, Future<Runnable>> monitor = Maps.newHashMap();

    @Override
    public void afterPropertiesSet() throws WorkflowConfigurationException, DeduplicationException, DataImportException, IOException, InterruptedException {
        if (BooleanUtils.isTrue(initializeWorkflow)) {
            systemService.initialiseSystem();
        }

        if (BooleanUtils.isTrue(importInstitutionDomicile)) {
            importedEntityServiceHelper.importInstitutionDomiciles();
        }

        if (BooleanUtils.isTrue(importAdvertCategory)) {
            importedEntityServiceHelper.importAdvertCategories();
        }

        if (BooleanUtils.isTrue(buildIndex)) {
            systemService.initialiseSearchIndexes();
        }

    }

    @Scheduled(cron = "${maintenance.ongoing}")
    public synchronized void maintain() {
        for (MaintenanceTask task : MaintenanceTask.values()) {
            submit(task);
        }
        monitor();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

    private synchronized void shutdown() throws InterruptedException {
        boolean idle = true;
        for (MaintenanceTask task : monitor.keySet()) {
            if (!monitor.get(task).isDone()) {
                idle = false;
                break;
            }
        }
        if (idle) {
            executor.shutdownNow();
        } else {
            executor.shutdown();
            wait(shutdownTimeout);
            executor.shutdownNow();
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void submit(final MaintenanceTask task) {
        for (MaintenanceTask precondition : task.getPreconditions(task)) {
            if (!monitor.containsKey(precondition) || !monitor.get(precondition).isDone()) {
                return;
            }
        }
        Future<Runnable> thread = (Future<Runnable>) executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("Executing maintenance task " + task.name());
                    task.getExecutor().newInstance().execute();
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        });
        monitor.put(task, thread);
    }

    private synchronized void monitor() {
        for (MaintenanceTask task : monitor.keySet()) {
            try {
                Future<Runnable> thread = monitor.get(task);
                thread.get();
                if (thread.isDone()) {
                    logger.debug("Completed maintenance task " + task.name());
                }
            } catch (Exception e) {
                if (e.getClass().equals(ExecutionException.class)) {
                    logger.error("Error executing maintenance task " + task.name(), e);
                }
            }
        }
    }

}
