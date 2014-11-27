package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private EntityService entityService;

    public WorkflowPropertyDefinition getWorkflowPropertyDefinitionById(PrismWorkflowPropertyDefinition id) {
        return entityService.getById(WorkflowPropertyDefinition.class, id);
    }

    public WorkflowPropertyConfiguration getWorkflowPropertyConfiguration(Resource resource, User user, WorkflowPropertyDefinition definition) {
        Integer workflowPropertyConfigurationVersion = resource.getWorkflowPropertyConfigurationVersion();
        if (workflowPropertyConfigurationVersion == null) {
            return (WorkflowPropertyConfiguration) customizationService.getConfiguration(PrismConfiguration.WORKFLOW_PROPERTY, resource, user, definition);
        } else {
            return (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(PrismConfiguration.WORKFLOW_PROPERTY, definition,
                    workflowPropertyConfigurationVersion);
        }
    }

}
