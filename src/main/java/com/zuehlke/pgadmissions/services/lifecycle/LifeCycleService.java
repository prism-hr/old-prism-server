package com.zuehlke.pgadmissions.services.lifecycle;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismMaintenanceTask;
import com.zuehlke.pgadmissions.services.SystemService;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LifeCycleService {

    private static final Logger logger = LoggerFactory.getLogger(LifeCycleService.class);

    private Object lock = new Object();

    private Set<PrismMaintenanceTask> executions = Sets.newHashSet();

    private ExecutorService executorService;

    @Value("${startup.workflow.initialize}")
    private Boolean initializeWorkflow;

    @Value("${startup.display.initialize.drop}")
    private Boolean destroyDisplayProperties;

    @Value("${startup.display.initialize}")
    private Boolean initializeDisplayProperties;

    @Value("${startup.import.system.data.overwrite}")
    private Boolean overwriteData;

    @Value("${startup.import.system.data}")
    private Boolean initializeData;

    @Value("${maintenance.run}")
    private Boolean maintain;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationContext applicationContext;

    @PostConstruct
    public void startup() throws Exception {
        boolean doInitializeWorkflow = BooleanUtils.isTrue(initializeWorkflow);

        if (doInitializeWorkflow) {
            systemService.initializeWorkflow();
        }

        if (BooleanUtils.isTrue(destroyDisplayProperties)) {
            systemService.destroyDisplayProperties();
        }

        if (BooleanUtils.isTrue(initializeDisplayProperties)) {
            systemService.initializeDisplayProperties();
        }

        if (doInitializeWorkflow) {
            systemService.initializeSystemUser();
        }

        if (BooleanUtils.isTrue(overwriteData)) {
            systemService.overwriteSystemData();
        }

        if (BooleanUtils.isTrue(initializeData)) {
            systemService.initializeSystemData();
        }

        if (BooleanUtils.isTrue(maintain)) {
            executorService = Executors.newFixedThreadPool((PrismMaintenanceTask.values().length));
        }
    }

    @PreDestroy
    public void shutdown() {
        if (BooleanUtils.isTrue(maintain)) {
            try {
                executorService.shutdownNow();
            } catch (Exception e) {
                logger.error("Error shutting down maintenance task executor", e);
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    private void maintain() {
        if (BooleanUtils.isTrue(maintain)) {
            for (final PrismMaintenanceTask execution : PrismMaintenanceTask.values()) {
                synchronized (lock) {
                    if (!executions.contains(execution)) {
                        executions.add(execution);
                        executorService.submit(() -> {
                            try {
                                applicationContext.getBean(execution.getExecutor()).execute();
                            } catch (Throwable e) {
                                logger.error("Error performing maintenance task: " + execution.name(), e);
                            } finally {
                                synchronized (lock) {
                                    executions.remove(execution);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

}
