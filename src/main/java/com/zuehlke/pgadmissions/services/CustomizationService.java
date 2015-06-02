package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfigurationVersioned;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Service
@Transactional
public class CustomizationService {

    @Inject
    private CustomizationDAO customizationDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private Mapper mapper;

    public WorkflowDefinition getDefinitionById(PrismConfiguration configurationType, Enum<?> id) {
        return entityService.getById(configurationType.getDefinitionClass(), id);
    }

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, WorkflowDefinition definition) {
        PrismOpportunityType opportunityType = getConfiguredOpportunityType(resource, null);
        return getConfiguration(configurationType, resource, opportunityType, definition);
    }

    public Integer getActiveConfigurationVersion(PrismConfiguration configurationType, Resource resource, PrismScope scope) {
        PrismOpportunityType opportunityType = getConfiguredOpportunityType(resource, null);
        return customizationDAO.getActiveConfigurationVersion(configurationType, resource, opportunityType, scope);
    }

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                                  WorkflowDefinition definition) {
        return customizationDAO.getConfiguration(configurationType, resource, opportunityType, definition);
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, Enum<?> definitionId, Integer version) {
        WorkflowDefinition definition = getDefinitionById(configurationType, definitionId);
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, WorkflowDefinition definition, Integer version) {
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfigurationRepresentation getConfigurationRepresentation(PrismConfiguration configurationType, Resource resource,
                                                                              PrismOpportunityType opportunityType, WorkflowDefinition definition) throws Exception {
        Resource configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);

        WorkflowConfiguration configuration = getConfiguration(configurationType, configuredResource, configuredOpportunityType, definition);
        WorkflowConfigurationRepresentation representation = mapper.map(configuration, configurationType.getConfigurationRepresentationClass());

        return representation;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource)
            throws Exception {
        PrismOpportunityType opportunityType = getConfiguredOpportunityType(resource, null);
        return getConfigurationRepresentations(configurationType, resource, resource.getResourceScope(), opportunityType);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource,
                                                                                     PrismOpportunityType opportunityType, WorkflowDefinition definition) throws Exception {
        Resource configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, configuredOpportunityType,
                definition);
        return parseRepresentations(configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(
            PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope,
                configuredOpportunityType, false);
        return parseRepresentations(configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, PrismScope scope,
                                                                                     PrismOpportunityType opportunityType, Enum<?> category) {
        return getConfigurationRepresentations(configurationType, resource, scope, opportunityType, category, false);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsConfigurationMode(PrismConfiguration configurationType, Resource resource,
                                                                                                      PrismScope scope, PrismOpportunityType opportunityType, Enum<?> category) throws Exception {
        return getConfigurationRepresentations(configurationType, resource, scope, opportunityType, category, true);
    }

    public List<WorkflowConfiguration> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return customizationDAO.getConfigurationsWithVersion(configurationType, version);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithVersion(Resource resource, PrismConfiguration configurationType,
                                                                                                Integer version) throws Exception {
        List<WorkflowConfiguration> configurations = getConfigurationsWithVersion(configurationType, version);
        return parseRepresentations(configurationType, configurations);
    }

    public WorkflowConfiguration getConfigurationWithOrWithoutVersion(PrismConfiguration configurationType, Resource resource, User user, Enum<?> definitionId,
                                                                      Integer configurationVersion) {
        WorkflowPropertyDefinition definition = (WorkflowPropertyDefinition) getDefinitionById(configurationType, definitionId);

        WorkflowPropertyConfiguration configuration;
        if (configurationVersion == null) {
            configuration = (WorkflowPropertyConfiguration) getConfiguration(configurationType, resource, definition);
        } else {
            configuration = (WorkflowPropertyConfiguration) getConfigurationWithVersion(configurationType, definition, configurationVersion);
        }

        return configuration;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithOrWithoutVersion(PrismConfiguration configurationType,
                                                                                                         Resource resource, Integer configurationVersion) throws Exception {
        if (configurationVersion == null) {
            return getConfigurationRepresentations(configurationType, resource);
        } else {
            return getConfigurationRepresentationsWithVersion(resource, configurationType, configurationVersion);
        }
    }

    public List<WorkflowDefinition> getDefinitions(PrismConfiguration configurationType, PrismScope scope) {
        return customizationDAO.listDefinitions(configurationType, scope);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                            Enum<?> definitionId) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, opportunityType, definitionId);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, scope, opportunityType);
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                           Enum<?> definitionId) throws Exception {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, opportunityType, definitionId);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType)
            throws Exception {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, scope, opportunityType);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope,
                                                 PrismOpportunityType opportunityType,
                                                 List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {
        List<WorkflowDefinition> definitions = getDefinitions(configurationType, scope);

        if (configurationType.isValidateResponseSize()
                && (workflowConfigurationGroupDTO.isEmpty() || workflowConfigurationGroupDTO.size() != definitions.size())) {
            throw new Error();
        }

        createConfigurationGroup(configurationType, resource, scope, opportunityType, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                                 Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {

        createConfigurationGroup(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType,
                                         List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException, DeduplicationException,
            InstantiationException, IllegalAccessException {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, scope, opportunityType, valueDTOs);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, opportunityType, valueDTOs);
        }
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                         Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        }
    }

    public boolean isSystemDefault(WorkflowDefinition definition, PrismOpportunityType opportunityType) {
        Integer precedence = definition.getScope().getOrdinal();
        if (precedence > INSTITUTION.ordinal() && opportunityType == getSystemOpportunityType()) {
            return true;
        } else if (precedence < PROGRAM.ordinal() && opportunityType == null) {
            return true;
        }
        return false;
    }

    public List<DisplayPropertyConfiguration> getAllLocalizedProperties() {
        return entityService.list(DisplayPropertyConfiguration.class);
    }

    public void createOrUpdateConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                            WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException {
        WorkflowConfiguration configuration = createConfiguration(configurationType, resource, opportunityType, workflowConfigurationDTO);
        entityService.createOrUpdate(configuration);
    }

    public WorkflowConfiguration createOrUpdateConfigurationUser(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                                                 WorkflowConfigurationDTO workflowConfigurationDTO) throws Exception {
        WorkflowConfiguration configuration = createConfiguration(configurationType, resource, opportunityType, workflowConfigurationDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
        return entityService.createOrUpdate(configuration);
    }

    public boolean isConfigurationEnabled(PrismConfiguration configurationType, Resource resource, Enum<?> definitionId) {
        Class<?> configurationClass = configurationType.getConfigurationClass();
        if (WorkflowConfigurationVersioned.class.isAssignableFrom(configurationClass)) {
            WorkflowConfiguration configuration = getConfigurationWithVersion(configurationType, definitionId,
                    resource.getWorkflowPropertyConfigurationVersion());
            return configuration != null && BooleanUtils.isTrue((Boolean) PrismReflectionUtils.getProperty(configuration, "enabled"));
        }
        throw new UnsupportedOperationException();
    }

    private List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource,
                                                                                      PrismScope scope, PrismOpportunityType opportunityType, Enum<?> category, boolean configurationMode) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        if (configurationType.isCategorizable()) {
            List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope,
                    configuredOpportunityType, category, configurationMode);
            return parseRepresentations(configurationType, configurations);
        }
        return getConfigurationRepresentations(configurationType, configuredResource, scope, configuredOpportunityType);
    }

    private WorkflowConfiguration createConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                                      WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException {
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), workflowConfigurationDTO.getDefinitionId());
        WorkflowConfiguration configuration = mapper.map(workflowConfigurationDTO, configurationType.getConfigurationClass());
        configuration.setResource(resource);
        configuration.setOpportunityType(opportunityType);
        PrismReflectionUtils.setProperty(configuration, configurationType.getDefinitionPropertyName(), definition);
        configuration.setSystemDefault(isSystemDefault(definition, opportunityType));
        return configuration;
    }

    private Resource getConfiguredResource(Resource resource) {
        return resource.getResourceScope().ordinal() > PROJECT.ordinal() ? resource.getParentResource() : resource;
    }

    private List<WorkflowConfigurationRepresentation> parseRepresentations(
            PrismConfiguration configurationType, List<WorkflowConfiguration> configurations) {
        List<WorkflowConfigurationRepresentation> representations = Lists.newLinkedList();

        if (configurations.isEmpty()) {
            return representations;
        } else {
            WorkflowConfiguration stereotype = configurations.get(0);

            Resource stereotypeResource = stereotype.getResource();
            PrismOpportunityType stereotypeOpportunityType = stereotype.getOpportunityType();

            for (WorkflowConfiguration configuration : configurations) {
                if (Objects.equal(configuration.getResource(), stereotypeResource)
                        && Objects.equal(configuration.getOpportunityType(), stereotypeOpportunityType)) {
                    WorkflowConfigurationRepresentation representation = mapper.map(configuration, configurationType.getConfigurationRepresentationClass());
                    representations.add(representation);
                }
            }

            return representations;
        }
    }

    private void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                                  List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException, DeduplicationException, InstantiationException,
            IllegalAccessException {
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            createOrUpdateConfiguration(configurationType, resource, opportunityType, valueDTO);
        }
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                                         Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        restoreDefaultConfiguration(configurationType, resource, opportunityType, definitionId);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismScope scope,
                                                         PrismOpportunityType opportunityType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        restoreDefaultConfiguration(configurationType, resource, scope, opportunityType);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
                                                         List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        Integer version = null;
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            WorkflowConfiguration transientConfiguration = createConfiguration(configurationType, resource, opportunityType, valueDTO);
            PrismReflectionUtils.setProperty(transientConfiguration, "active", true);

            WorkflowConfiguration persistentConfiguration;
            if (version == null) {
                entityService.save(transientConfiguration);
                persistentConfiguration = transientConfiguration;
            } else {
                PrismReflectionUtils.setProperty(transientConfiguration, "version", version);
                persistentConfiguration = entityService.createOrUpdate(transientConfiguration);
            }

            version = version == null ? persistentConfiguration.getId() : version;
            PrismReflectionUtils.setProperty(persistentConfiguration, "version", version);
        }
    }

    private PrismOpportunityType getConfiguredOpportunityType(Resource resource, PrismOpportunityType opportunityType) {
        if (resource.getResourceScope() == PrismScope.PROGRAM) {
            return resource.getProgram().getOpportunityType().getPrismOpportunityType();
        } else if (resource.getResourceScope() == PrismScope.PROJECT) {
            return resource.getProject().getOpportunityType().getPrismOpportunityType();
        }
        return opportunityType;
    }

}
