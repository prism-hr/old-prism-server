package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;

@Service
@Transactional
@SuppressWarnings("unchecked")
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
        }
        return (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(PrismConfiguration.WORKFLOW_PROPERTY, definition,
                workflowPropertyConfigurationVersion);
    }

    public void updateWorkflowPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, PrismScope definitionScope,
            WorkflowPropertyConfigurationDTO configurationDTO) throws DeduplicationException, CustomizationException {
        createOrUpdateWorkflowPropertyConfiguration(resource, locale, programType, definitionScope, configurationDTO);
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_WORKFLOW_PROPERTY"));
    }

    public void createOrUpdateWorkflowPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, PrismScope scope,
            WorkflowPropertyConfigurationDTO configurationDTO) throws CustomizationException, DeduplicationException {
        validateWorkflowPropertyConfiguration(resource, scope, configurationDTO);

        Integer version = null;
        customizationService.restoreDefaultConfiguration(PrismConfiguration.WORKFLOW_PROPERTY, resource, scope, locale, programType);
        for (WorkflowPropertyConfigurationValueDTO valueDTO : configurationDTO.getValues()) {
            WorkflowPropertyDefinition definition = getWorkflowPropertyDefinitionById((PrismWorkflowPropertyDefinition) valueDTO.getDefinitionId());
            WorkflowPropertyConfiguration configuration = createOrUpdateWorkflowPropertyConfiguration(resource, locale, programType, definition, version,
                    valueDTO.getEnabled(), valueDTO.getMinimum(), valueDTO.getMaximum());
            version = configuration.getVersion();
        }
    }

    private WorkflowPropertyConfiguration createOrUpdateWorkflowPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowPropertyDefinition definition, Integer version, Boolean enabled, Integer minimum, Integer maximum) throws CustomizationException,
            DeduplicationException {
        customizationService.validateConfiguration(resource, definition, locale, programType);
        WorkflowPropertyConfiguration transientConfiguration = new WorkflowPropertyConfiguration().withResource(resource).withLocale(locale)
                .withProgramType(programType).withWorkflowPropertyDefinition(definition).withVersion(version).withEnabled(enabled).withMinimum(minimum)
                .withMaximum(maximum).withActive(true).withSystemDefault(customizationService.isSystemDefault(definition, locale, programType));

        WorkflowPropertyConfiguration persistentConfiguration;
        if (version == null) {
            entityService.save(transientConfiguration);
            persistentConfiguration = transientConfiguration;
        } else {
            persistentConfiguration = entityService.createOrUpdate(transientConfiguration);
        }

        persistentConfiguration.setVersion(version == null ? persistentConfiguration.getId() : version);
        return persistentConfiguration;
    }

    private void validateWorkflowPropertyConfiguration(Resource resource, PrismScope scope, WorkflowPropertyConfigurationDTO configurationDTO)
            throws CustomizationException {
        List<WorkflowPropertyDefinition> definitions = (List<WorkflowPropertyDefinition>) (List<?>) customizationService.getDefinitions(
                PrismConfiguration.WORKFLOW_PROPERTY, scope);
        List<WorkflowPropertyConfigurationValueDTO> valueDTOs = configurationDTO.getValues();

        if (valueDTOs.size() != definitions.size()) {
            throw new CustomizationException("Incomplete workflow configuration passed for " + resource.getCode());
        } else {
            HashMap<PrismWorkflowPropertyDefinition, WorkflowPropertyDefinition> constraints = Maps.newHashMap();
            for (WorkflowPropertyDefinition definition : definitions) {
                constraints.put((PrismWorkflowPropertyDefinition) definition.getId(), definition);
            }

            for (WorkflowPropertyConfigurationValueDTO valueDTO : valueDTOs) {
                PrismWorkflowPropertyDefinition definitionId = (PrismWorkflowPropertyDefinition) valueDTO.getDefinitionId();

                if (valueDTO.getEnabled() == null) {
                    throw new CustomizationException("Enabled value not set for " + definitionId.name() + " in workflow configuration for "
                            + resource.getCode());
                }

                WorkflowPropertyDefinition constraint = constraints.get(definitionId);
                Integer minimum = constraint.getMinimumPermitted();
                Integer maximum = constraint.getMaximumPermitted();

                if (minimum == null && maximum == null) {
                    continue;
                } else if (minimum != null && valueDTO.getMinimum() < minimum) {
                    throw new CustomizationException("Minimum value not set for " + definitionId.name() + " in workflow configuration for "
                            + resource.getCode());
                } else if (maximum != null && valueDTO.getMaximum() > maximum) {
                    throw new CustomizationException("Maximum value not set for " + definitionId.name() + " in workflow configuration for "
                            + resource.getCode());
                }
            }
        }
    }

}
