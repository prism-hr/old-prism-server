package com.zuehlke.pgadmissions.services.lifecycle;

import java.io.IOException;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;

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

    @Autowired
    private MaintenanceService maintenanceService;

    @Override
    public void afterPropertiesSet() throws WorkflowConfigurationException, DeduplicationException, DataImportException, IOException, InterruptedException,
            CustomizationException, InstantiationException, IllegalAccessException {
        if (BooleanUtils.isTrue(initializeWorkflow)) {
            systemService.initializeSystem();
        }

        if (BooleanUtils.isTrue(initializeData)) {
            importedEntityServiceHelperSystem.execute();
        }
    }

}
