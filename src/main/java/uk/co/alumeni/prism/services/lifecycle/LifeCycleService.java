package uk.co.alumeni.prism.services.lifecycle;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.co.alumeni.prism.utils.PrismExecutorUtils.shutdownExecutor;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.definitions.PrismMaintenanceTask;
import uk.co.alumeni.prism.mapping.StaticDataMapper;
import uk.co.alumeni.prism.services.SystemService;

@Service
public class LifeCycleService {

    private static final Logger logger = getLogger(LifeCycleService.class);

    private ExecutorService executorService;

    private Set<PrismMaintenanceTask> executions = newHashSet();

    @Value("${context.environment}")
    private String environment;

    @Value("${startup.workflow.initialize.drop}")
    private Boolean dropWorkflow;

    @Value("${startup.workflow.initialize}")
    private Boolean initializeWorkflow;

    @Value("${startup.display.initialize.drop}")
    private Boolean dropDisplayProperties;

    @Value("${startup.display.initialize}")
    private Boolean initializeDisplayProperties;

    @Value("${startup.profile.completeness.initialize}")
    private Boolean initializeProfileCompleteness;

    @Value("${startup.section.completeness.initialize}")
    private Boolean initializeSectionCompleteness;

    @Value("${startup.address.completeness.initialize}")
    private Boolean initializeAddressCompleteness;

    @Value("${maintenance.run}")
    private Boolean maintain;

    @Inject
    private StaticDataMapper staticDataMapper;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationContext applicationContext;

    @PostConstruct
    public void startup() throws Exception {
        boolean doInitializeWorkflow = BooleanUtils.isTrue(initializeWorkflow);
        if (BooleanUtils.isTrue(dropWorkflow)) {
            systemService.dropWorkflow();
        }

        if (doInitializeWorkflow) {
            systemService.initializeWorkflow();
        }

        if (isTrue(dropDisplayProperties)) {
            systemService.dropDisplayProperties();
        }

        if (isTrue(initializeDisplayProperties)) {
            systemService.initializeDisplayProperties();
        }

        if (doInitializeWorkflow) {
            systemService.initializeSystemUser();
        }

        if (isTrue(initializeProfileCompleteness)) {
            systemService.initializeProfileCompleteness();
        }

        if (isTrue(initializeSectionCompleteness)) {
            systemService.initializeSectionCompleteness();
        }

        if (isTrue(initializeAddressCompleteness)) {
            systemService.initializeAddressCompleteness();
        }

        if (!environment.equals("test")) {
            staticDataMapper.getData();
            systemService.initializePropertyLoader();
        }

        if (isTrue(maintain)) {
            executorService = newFixedThreadPool(PrismMaintenanceTask.values().length);
        }
    }

    @PreDestroy
    public void shutdown() throws Exception {
        if (isTrue(maintain)) {
            for (PrismMaintenanceTask execution : executions) {
                applicationContext.getBean(execution.getExecutor()).shutdown();
            }
            shutdownExecutor(executorService);
        }
    }

    @Scheduled(fixedDelay = 1000)
    private void maintain() {
        if (isTrue(maintain)) {
            for (final PrismMaintenanceTask execution : PrismMaintenanceTask.values()) {
                synchronized (this) {
                    if (!executions.contains(execution)) {
                        executions.add(execution);
                        executorService.submit(() -> {
                            try {
                                applicationContext.getBean(execution.getExecutor()).execute();
                            } catch (Throwable e) {
                                logger.error("Error performing maintenance task: " + execution.name(), e);
                            } finally {
                                synchronized (this) {
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
