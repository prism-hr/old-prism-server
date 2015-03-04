package com.zuehlke.pgadmissions.services.lifecycle;

import java.io.IOException;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;

@Service
public class LifeCycleService implements InitializingBean, ApplicationListener<ContextClosedEvent> {

    @Value("${startup.workflow.initialize}")
    private Boolean initializeWorkflow;

    @Value("${startup.import.system.data}")
    private Boolean initializeData;

    @Inject
    private ImportedEntityServiceHelperSystem importedEntityServiceHelperSystem;

    @Inject
    private SystemService systemService;
    
	@Resource(name = "taskExecutor")
	private ThreadPoolTaskExecutor executor;

    @Override
    public void afterPropertiesSet() throws WorkflowConfigurationException, DeduplicationException, DataImportException, IOException, InterruptedException,
            CustomizationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IntegrationException {
        if (BooleanUtils.isTrue(initializeWorkflow)) {
            systemService.initializeSystem();
        }

        if (BooleanUtils.isTrue(initializeData)) {
            importedEntityServiceHelperSystem.execute();
        }
    }

	@Override
    public void onApplicationEvent(ContextClosedEvent event) {
	    executor.shutdown();
    }

}
