package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowProperty;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private EntityService entityService;

    public WorkflowPropertyDefinition getWorkflowPropertyDefinitionById(PrismWorkflowProperty id) {
        return entityService.getById(WorkflowPropertyDefinition.class, id);
    }

    public WorkflowPropertyConfiguration getWorkflowPropertyConfiguration(Resource resource, User user, WorkflowPropertyDefinition workflowPropertyDefinition) {
        return customizationService.getConfiguration(WorkflowPropertyConfiguration.class, resource, user, "workflowPropertyDefinition",
                workflowPropertyDefinition);
    }

    // TODO: make this work with a list of representations
    public void updateWorkflowPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, WorkflowPropertyDefinition definition,
            WorkflowPropertyConfigurationDTO workflowPropertyConfigurationDTO) throws DeduplicationException, CustomizationException {
        createOrUpdateWorkflowPropertyConfiguration(resource, locale, programType, definition, workflowPropertyConfigurationDTO.getEnabled(),
                workflowPropertyConfigurationDTO.getMinimum(), workflowPropertyConfigurationDTO.getMaximum());
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_WORKFLOW_PROPERTY"));
    }

    public void createOrUpdateWorkflowPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowPropertyDefinition definition, Boolean enabled, Integer minimum, Integer maximum) throws CustomizationException, DeduplicationException {
        customizationService.validateConfiguration(resource, definition, locale, programType);
        WorkflowPropertyConfiguration transientConfiguration = new WorkflowPropertyConfiguration().withResource(resource).withLocale(locale)
                .withProgramType(programType).withWorkflowPropertyDefinition(definition).withEnabled(enabled).withMinimum(minimum).withMaximum(maximum)
                .withSystemDefault(customizationService.isSystemDefault(definition, locale, programType));
        entityService.createOrUpdate(transientConfiguration);
    }

}
