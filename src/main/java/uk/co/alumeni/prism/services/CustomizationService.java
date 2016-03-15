package uk.co.alumeni.prism.services;

import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.CustomizationDAO;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.display.DisplayPropertyConfiguration;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;
import uk.co.alumeni.prism.domain.workflow.WorkflowConfiguration;
import uk.co.alumeni.prism.domain.workflow.WorkflowConfigurationVersioned;
import uk.co.alumeni.prism.domain.workflow.WorkflowDefinition;
import uk.co.alumeni.prism.mapping.CustomizationMapper;
import uk.co.alumeni.prism.rest.dto.WorkflowConfigurationDTO;
import uk.co.alumeni.prism.rest.representation.configuration.WorkflowConfigurationRepresentation;

import com.google.common.base.Objects;

@Service
@Transactional
public class CustomizationService {

    @Inject
    private CustomizationDAO customizationDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private PrismService prismService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Inject
    private CustomizationMapper customizationMapper;

    public WorkflowDefinition getDefinitionById(PrismConfiguration configurationType, Enum<?> id) {
        return entityService.getById(configurationType.getDefinitionClass(), id);
    }

    public WorkflowConfiguration<?> getConfiguration(PrismConfiguration configurationType, Resource resource, WorkflowDefinition definition) {
        PrismOpportunityType opportunityType = getConfiguredOpportunityType(resource, null);
        return getConfiguration(configurationType, resource, opportunityType, definition);
    }

    public WorkflowConfiguration<?> getConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
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

    public WorkflowConfigurationRepresentation getConfigurationRepresentation(PrismConfiguration configurationType, Resource resource,
            PrismOpportunityType opportunityType, WorkflowDefinition definition) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);

        WorkflowConfiguration<?> configuration = getConfiguration(configurationType, configuredResource, configuredOpportunityType, definition);
        WorkflowConfigurationRepresentation representation = customizationMapper.getWorkflowConfigurationRepresentation(configuration);

        return representation;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource) {
        PrismOpportunityType opportunityType = getConfiguredOpportunityType(resource, null);
        return getConfigurationRepresentations(configurationType, resource, resource.getResourceScope(), opportunityType);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(
            PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType, WorkflowDefinition definition) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        List<WorkflowConfiguration<?>> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, configuredOpportunityType,
                definition);
        return parseRepresentations(configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(
            PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        List<WorkflowConfiguration<?>> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope,
                configuredOpportunityType, false);
        return parseRepresentations(configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource,
            PrismScope scope, PrismOpportunityType opportunityType, Enum<?> category) {
        return getConfigurationRepresentations(configurationType, resource, scope, opportunityType, category, false);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsConfigurationMode(PrismConfiguration configurationType, Resource resource,
            PrismScope scope,
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

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithOrWithoutVersion(
            PrismConfiguration configurationType, Resource resource, Integer configurationVersion) {
        if (configurationVersion == null) {
            return getConfigurationRepresentations(configurationType, resource);
        } else {
            return getConfigurationRepresentationsWithVersion(configurationType, configurationVersion);
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
            Enum<?> definitionId) {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, opportunityType, definitionId);
        resourceService.executeUpdate(resource, userService.getCurrentUser(),
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType) {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, scope, opportunityType);
        resourceService.executeUpdate(resource, userService.getCurrentUser(),
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismOpportunityType opportunityType, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) {
        List<WorkflowDefinition> definitions = getDefinitions(configurationType, scope);

        if (configurationType.isValidateResponseSize()
                && (workflowConfigurationGroupDTO.isEmpty() || workflowConfigurationGroupDTO.size() != definitions.size())) {
            throw new Error();
        }

        createConfigurationGroup(configurationType, resource, scope, opportunityType, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource, userService.getCurrentUser(),
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) {

        createConfigurationGroup(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource, userService.getCurrentUser(),
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType,
            List<? extends WorkflowConfigurationDTO> valueDTOs) {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, scope, opportunityType, valueDTOs);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, opportunityType, valueDTOs);
        }
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, opportunityType, definitionId, workflowConfigurationGroupDTO);
        }
    }

    public boolean isSystemDefault(Resource resource, WorkflowDefinition definition, PrismOpportunityType opportunityType) {
        if (resource.getResourceScope().equals(SYSTEM)) {
            if (definition.getScope().getScopeCategory().hasOpportunityTypeConfigurations()) {
                return opportunityType.equals(getSystemOpportunityType());
            } else {
                return true;
            }
        }
        return false;
    }

    public List<DisplayPropertyConfiguration> getAllLocalizedProperties() {
        return entityService.getAll(DisplayPropertyConfiguration.class);
    }

    public void createOrUpdateConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
            WorkflowConfigurationDTO workflowConfigurationDTO) {
        WorkflowConfiguration<?> configuration = createConfiguration(configurationType, resource, opportunityType, workflowConfigurationDTO);
        entityService.createOrUpdate(configuration);
    }

    @SuppressWarnings("unchecked")
    public <T> WorkflowConfiguration<T> createConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType prismOpportunityType,
            WorkflowConfigurationDTO workflowConfigurationDTO) {
        T definition = (T) entityService.getById(configurationType.getDefinitionClass(), workflowConfigurationDTO.getDefinitionId());
        WorkflowConfiguration<T> configuration = customizationMapper.getWorkflowConfiguration(workflowConfigurationDTO);
        configuration.setResource(resource);
        configuration.setOpportunityType(prismService.getOpportunityTypeById(prismOpportunityType));
        configuration.setDefinition(definition);
        configuration.setSystemDefault(isSystemDefault(resource, (WorkflowDefinition) definition, prismOpportunityType));
        return configuration;
    }

    private List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource,
            PrismScope scope,
            PrismOpportunityType opportunityType, Enum<?> category, boolean configurationMode) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismOpportunityType configuredOpportunityType = getConfiguredOpportunityType(resource, opportunityType);
        if (configurationType.isCategorizable()) {
            List<WorkflowConfiguration<?>> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope,
                    configuredOpportunityType, category);
            return parseRepresentations(configurationType, configurations);
        }
        return getConfigurationRepresentations(configurationType, configuredResource, scope, configuredOpportunityType);
    }

    private Resource getConfiguredResource(Resource resource) {
        return resource.getResourceScope().ordinal() > PROJECT.ordinal() ? resource.getParentResource() : resource;
    }

    private List<WorkflowConfigurationRepresentation> parseRepresentations(PrismConfiguration configurationType, List<WorkflowConfiguration<?>> configurations) {
        if (configurations.isEmpty()) {
            return Collections.emptyList();
        } else {
            WorkflowConfiguration<?> stereotype = configurations.get(0);

            Resource stereotypeResource = stereotype.getResource();
            OpportunityType stereotypeOpportunityType = stereotype.getOpportunityType();

            return configurations.stream()
                    .filter(c -> c.getResource().sameAs(stereotypeResource) && Objects.equal(c.getOpportunityType(), stereotypeOpportunityType))
                    .map(customizationMapper::getWorkflowConfigurationRepresentation)
                    .collect(Collectors.toList());
        }
    }

    private void createOrUpdateConfigurationGroup(
            PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
            List<? extends WorkflowConfigurationDTO> valueDTOs) {
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            createOrUpdateConfiguration(configurationType, resource, opportunityType, valueDTO);
        }
    }

    private void createOrUpdateConfigurationGroupVersion(
            PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> valueDTOs) {
        restoreDefaultConfiguration(configurationType, resource, opportunityType, definitionId);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(
            PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismOpportunityType opportunityType, List<? extends WorkflowConfigurationDTO> valueDTOs) {
        restoreDefaultConfiguration(configurationType, resource, scope, opportunityType);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, opportunityType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(
            PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
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

    private PrismOpportunityType getConfiguredOpportunityType(Resource resource, PrismOpportunityType opportunityType) {
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            return ((ResourceOpportunity) resource).getOpportunityType().getId();
        }
        return opportunityType;
    }

}
