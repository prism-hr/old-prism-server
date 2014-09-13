package com.zuehlke.pgadmissions.services.lifecycle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.MaintenanceTask;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;

@Service
public class MaintenanceService {

    @Autowired
    private ImportedEntityServiceHelperSystem importedEntityServiceHelperSystem;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutorService executor = Executors.newFixedThreadPool(MaintenanceTask.values().length);

//    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public void maintain() {
        for (MaintenanceTask task : MaintenanceTask.values()) {
            if (task.isParallelize()) {
                submit(task);
            } else {
                execute(task);
            }
        }
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
        executor.shutdownNow();
    }

    private void submit(final MaintenanceTask task) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                execute(task);
            }
        });
        logger.info("Scheduling maintenance task " + task.name());
    }

    private void execute(final MaintenanceTask task) {
        logger.info("Executing maintenance task " + task.name());
        try {
            applicationContext.getBean(task.getExecutor()).execute();
        } catch (BeansException e) {
            throw new Error(e);
        } catch (Exception e) {
            logger.error("Error executing maintenance task " + task.name(), e);
        }
    }

}
