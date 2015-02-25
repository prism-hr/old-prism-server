package com.zuehlke.pgadmissions.services.lifecycle;

import com.zuehlke.pgadmissions.exceptions.*;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LifeCycleService implements InitializingBean {

    @Value("${startup.workflow.initialize}")
    private Boolean initializeWorkflow;

    @Value("${startup.import.system.data}")
    private Boolean initializeData;

    @Autowired
    private ImportedEntityServiceHelperSystem importedEntityServiceHelperSystem;

    @Autowired
    private SystemService systemService;

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

}
