package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfigurationVersioned;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.mapping.CustomizationMapper;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;

@Service
@Transactional
public class CustomizationService {

    private static final Logger logger = LoggerFactory.getLogger(CustomizationService.class);

    @Inject
    private CustomizationDAO customizationDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private CustomizationMapper customizationMapper;

    public WorkflowDefinition getDefinitionById(PrismConfiguration configurationType, Enum<?> id) {
        return entityService.getById(configurationType.getDefinitionClass(), id);
    }

    public WorkflowConfiguration<?> getConfiguration(PrismConfiguration configurationType, Resource<?> resource, WorkflowDefinition definition) {
        PrismOpportunityType opportunityType = getConfiguredOpportunityType(resource, null);
        return getConfiguration(configurationType, resource, opportunityType, definition);
    }

    public Integer getActiveConfigurationVersion(PrismConfiguration configurationType, Resource<?> resource) {
        PrismOpportunityType opportunityType = getConfiguredOpportunityType(resource, null);
        return customizationDAO.getActiveConfigurationVersion(configurationType, resource, opportunityType);
    }

    public WorkflowConfiguration<?> getConfiguration(PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            WorkflowDefinition definition) {
        return customizationDAO.getConfiguration(configurationType, resource, opportunityType, definition);
    }

    public WorkflowConfiguration<?> getConfigurationWithVersion(PrismConfiguration configurationType, Enum<?> definitionId, Integer version) {
        WorkflowDefinition definition = getDefinitionById(configurationType, definitionId);
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfiguration<?> getConfigurationWithVersion(PrismConfiguration configurationType, WorkflowDefinition definition, Integer version) {
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfigurationRepresentation getConfigurationRepresentation(
            PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType, WorkflowDefinition definition) {
        Resource<?> configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);

        WorkflowConfiguration<?> configuration = getConfiguration(configurationType, configuredResource, configuredOpportunityType, definition);
        WorkflowConfigurationRepresentation representation = customizationMapper.getWorkflowConfigurationRepresentation(configuration);

        return representation;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource<?> resource) {
        PrismOpportunityType opportunityType = getConfiguredOpportunityType(resource, null);
        return getConfigurationRepresentations(configurationType, resource, resource.getResourceScope(), opportunityType);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(
            PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType, WorkflowDefinition definition) {
        Resource<?> configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        List<WorkflowConfiguration<?>> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, configuredOpportunityType,
                definition);
        return parseRepresentations(configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(
            PrismConfiguration configurationType, Resource<?> resource, PrismScope scope, PrismOpportunityType opportunityType) {
        Resource<?> configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        List<WorkflowConfiguration<?>> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope,
                configuredOpportunityType, false);
        return parseRepresentations(configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource<?> resource,
            PrismScope scope, PrismOpportunityType opportunityType, Enum<?> category) {
        return getConfigurationRepresentations(configurationType, resource, scope, opportunityType, category, false);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsConfigurationMode(PrismConfiguration configurationType, Resource<?> resource, PrismScope scope,
            PrismOpportunityType opportunityType, Enum<?> category) {
        return getConfigurationRepresentations(configurationType, resource, scope, opportunityType, category, true);
    }

    public List<WorkflowConfiguration<?>> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return customizationDAO.getConfigurationsWithVersion(configurationType, version);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithVersion(
            PrismConfiguration configurationType, Integer version) {
        List<WorkflowConfiguration<?>> configurations = getConfigurationsWithVersion(configurationType, version);
        return parseRepresentations(configurationType, configurations);
    }

    public WorkflowConfiguration<?> getConfigurationWithOrWithoutVersion(
            PrismConfiguration configurationType, Resource<?> resource, Enum<?> definitionId, Integer configurationVersion) {
        WorkflowPropertyDefinition definition = (WorkflowPropertyDefinition) getDefinitionById(configurationType, definitionId);

        WorkflowPropertyConfiguration configuration;
        if (configurationVersion == null) {
            configuration = (WorkflowPropertyConfiguration) getConfiguration(configurationType, resource, definition);
        } else {
            configuration = (WorkflowPropertyConfiguration) getConfigurationWithVersion(configurationType, definition, configurationVersion);
        }

        return configuration;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithOrWithoutVersion(
            PrismConfiguration configurationType, Resource<?> resource, Integer configurationVersion) {
        if (configurationVersion == null) {
            return getConfigurationRepresentations(configurationType, resource);
        } else {
            return getConfigurationRepresentationsWithVersion(configurationType, configurationVersion);
        }
    }

    public List<WorkflowDefinition> getDefinitions(PrismConfiguration configurationType, PrismScope scope) {
        return customizationDAO.listDefinitions(configurationType, scope);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            Enum<?> definitionId) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, opportunityType, definitionId);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource<?> resource, PrismScope scope, PrismOpportunityType opportunityType) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, scope, opportunityType);
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            Enum<?> definitionId) throws Exception {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, opportunityType, definitionId);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource<?> resource, PrismScope scope, PrismOpportunityType opportunityType)
            throws Exception {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, scope, opportunityType);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource<?> resource, PrismScope scope,
            PrismOpportunityType opportunityType, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {
        List<WorkflowDefinition> definitions = getDefinitions(configurationType, scope);

        if (configurationType.isValidateResponseSize()
                && (workflowConfigurationGroupDTO.isEmpty() || workflowConfigurationGroupDTO.size() != definitions.size())) {
            throw new Error();
        }

        createConfigurationGroup(configurationType, resource, scope, opportunityType, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {

        createConfigurationGroup(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource<?> resource, PrismScope scope, PrismOpportunityType opportunityType,
            List<? extends WorkflowConfigurationDTO> valueDTOs) {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, scope, opportunityType, valueDTOs);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, opportunityType, valueDTOs);
        }
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        }
    }

    public boolean isSystemDefault(WorkflowDefinition definition, PrismOpportunityType opportunityType) {
        Integer precedence = definition.getScope().getOrdinal();
        if (precedence > DEPARTMENT.ordinal() && opportunityType == getSystemOpportunityType()) {
            return true;
        } else if (precedence < PROGRAM.ordinal() && opportunityType == null) {
            return true;
        }
        return false;
    }

    public List<DisplayPropertyConfiguration> getAllLocalizedProperties() {
        return entityService.list(DisplayPropertyConfiguration.class);
    }

    public void createOrUpdateConfiguration(PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            WorkflowConfigurationDTO workflowConfigurationDTO) {
        WorkflowConfiguration<?> configuration = createConfiguration(configurationType, resource, opportunityType, workflowConfigurationDTO);
        entityService.createOrUpdate(configuration);
    }

    public WorkflowConfiguration<?> createOrUpdateConfigurationUser(PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            WorkflowConfigurationDTO workflowConfigurationDTO) throws Exception {
        WorkflowConfiguration<?> configuration = createConfiguration(configurationType, resource, opportunityType, workflowConfigurationDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
        return entityService.createOrUpdate(configuration);
    }

    public boolean isWorkflowConfigurationEnabled(Resource<?> resource, PrismWorkflowPropertyDefinition definition) {
        WorkflowPropertyConfiguration configuration = (WorkflowPropertyConfiguration) getConfigurationWithVersion(WORKFLOW_PROPERTY, definition,
                resource.getWorkflowPropertyConfigurationVersion());
        return configuration != null && BooleanUtils.isTrue(configuration.getEnabled());
    }

    private List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource<?> resource,
            PrismScope scope, PrismOpportunityType opportunityType, Enum<?> category, boolean configurationMode) {
        Resource<?> configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        if (configurationType.isCategorizable()) {
            StopWatch watch = new StopWatch();
            watch.start();

            List<WorkflowConfiguration<?>> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope,
                    configuredOpportunityType, category, configurationMode);

            logger.info("Got display properties for: " + scope.getLowerCamelName() + " " + watch.getTime() + "ms");

            return parseRepresentations(configurationType, configurations);
        }
        return getConfigurationRepresentations(configurationType, configuredResource, scope, configuredOpportunityType);
    }

    @SuppressWarnings("unchecked")
    private <T> WorkflowConfiguration<T> createConfiguration(PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            WorkflowConfigurationDTO workflowConfigurationDTO) {
        T definition = (T) entityService.getById(configurationType.getDefinitionClass(), workflowConfigurationDTO.getDefinitionId());
        WorkflowConfiguration<T> configuration = customizationMapper.getWorkflowConfiguration(workflowConfigurationDTO);
        configuration.setResource(resource);
        configuration.setOpportunityType(opportunityType);
        configuration.setDefinition(definition);
        configuration.setSystemDefault(isSystemDefault((WorkflowDefinition) definition, opportunityType));
        return configuration;
    }

    private Resource<?> getConfiguredResource(Resource<?> resource) {
        return resource.getResourceScope().ordinal() > PROJECT.ordinal() ? resource.getParentResource() : resource;
    }

    private List<WorkflowConfigurationRepresentation> parseRepresentations(PrismConfiguration configurationType, List<WorkflowConfiguration<?>> configurations) {
        if (configurations.isEmpty()) {
            return Collections.emptyList();
        } else {
            WorkflowConfiguration<?> stereotype = configurations.get(0);

            Resource<?> stereotypeResource = stereotype.getResource();
            PrismOpportunityType stereotypeOpportunityType = stereotype.getOpportunityType();

            return configurations.stream()
                    .filter(configuration -> Objects.equal(configuration.getResource(), stereotypeResource)
                            && Objects.equal(configuration.getOpportunityType(), stereotypeOpportunityType))
                    .map(customizationMapper::getWorkflowConfigurationRepresentation)
                    .collect(Collectors.toList());
        }
    }

    private void createOrUpdateConfigurationGroup(
            PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            List<? extends WorkflowConfigurationDTO> valueDTOs) {
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            createOrUpdateConfiguration(configurationType, resource, opportunityType, valueDTO);
        }
    }

    private void createOrUpdateConfigurationGroupVersion(
            PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> valueDTOs) {
        restoreDefaultConfiguration(configurationType, resource, opportunityType, definitionId);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(
            PrismConfiguration configurationType, Resource<?> resource, PrismScope scope,
            PrismOpportunityType opportunityType, List<? extends WorkflowConfigurationDTO> valueDTOs) {
        restoreDefaultConfiguration(configurationType, resource, scope, opportunityType);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(
            PrismConfiguration configurationType, Resource<?> resource, PrismOpportunityType opportunityType,
            List<? extends WorkflowConfigurationDTO> valueDTOs) {
        Integer version = null;
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            WorkflowConfigurationVersioned<?> transientConfiguration = (WorkflowConfigurationVersioned<?>) createConfiguration(configurationType, resource,
                    opportunityType, valueDTO);
            transientConfiguration.setActive(true);

            WorkflowConfigurationVersioned<?> persistentConfiguration;
            if (version == null) {
                entityService.save(transientConfiguration);
                persistentConfiguration = transientConfiguration;
            } else {
                transientConfiguration.setVersion(version);
                persistentConfiguration = entityService.createOrUpdate(transientConfiguration);
            }

            version = version == null ? persistentConfiguration.getId() : version;
            persistentConfiguration.setVersion(version);
        }
    }

    private PrismOpportunityType getConfiguredOpportunityType(Resource<?> resource, PrismOpportunityType opportunityType) {
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity<?> resourceOpportunity = (ResourceOpportunity<?>) resource;
            return PrismOpportunityType.valueOf(resourceOpportunity.getOpportunityType().getName());
        }
        return opportunityType;
    }

}
