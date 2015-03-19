package com.zuehlke.pgadmissions.services.lifecycle;

import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;

@Service
public class LifeCycleService implements InitializingBean, ApplicationListener<ContextClosedEvent> {

    @Value("${startup.workflow.initialize}")
    private Boolean initializeWorkflow;

    @Value("${startup.import.system.data}")
    private Boolean initializeData;

    @Value("${startup.display.initialize}")
    private Boolean initializeDisplayProperties;

    @Value("${startup.display.initialize.drop}")
    private Boolean dropDisplayProperties;

    @Inject
    private ImportedEntityServiceHelperSystem importedEntityServiceHelperSystem;

    @Inject
    private SystemService systemService;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (initializeWorkflow) {
            systemService.initializeSystem();
        }

        if (initializeData) {
            importedEntityServiceHelperSystem.execute();
        }

        if (initializeDisplayProperties) {
            if (dropDisplayProperties) {
                systemService.dropDisplayProperties();
            }
            systemService.initializeDisplayProperties();
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        executor.shutdown();
    }

}
