package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.dozer.Mapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
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
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
public class CustomizationService {

    @Autowired
    private CustomizationDAO customizationDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Mapper mapper;

    public WorkflowDefinition getDefinitionById(PrismConfiguration configurationType, Enum<?> id) {
        return entityService.getById(configurationType.getDefinitionClass(), id);
    }

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, User user, WorkflowDefinition definition) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                .getPrismProgramType() : null;
        return getConfiguration(configurationType, resource, locale, programType, definition);
    }

    public Integer getActiveConfigurationVersion(PrismConfiguration configurationType, Resource resource, PrismScope scope) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = userService.getCurrentUser() != null ? userService.getCurrentUser().getLocale() : resource.getLocale();
        PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                .getPrismProgramType() : null;
        return customizationDAO.getActiveConfigurationVersion(configurationType, resource, locale, programType, scope);
    }

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowDefinition definition) {
        return customizationDAO.getConfiguration(configurationType, resource, locale, programType, definition);
    }

    public List<DisplayPropertyConfiguration> getDisplayPropertyConfiguration(Resource resource, PrismScope scope,
            PrismDisplayPropertyCategory displayPropertyCategory, PrismLocale locale, PrismProgramType programType) {
        return customizationDAO.getDisplayPropertyConfiguration(resource, scope, displayPropertyCategory, locale, programType);
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, Enum<?> definitionId, Integer version) {
        WorkflowDefinition definition = getDefinitionById(configurationType, definitionId);
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, WorkflowDefinition definition, Integer version) {
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfigurationRepresentation getConfigurationRepresentation(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, WorkflowDefinition definition) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismProgramType configuredProgramType = getConfiguredProgramType(resource, programType);

        WorkflowConfiguration configuration = getConfiguration(configurationType, configuredResource, locale, configuredProgramType, definition);
        WorkflowConfigurationRepresentation representation = mapper.map(configuration, configurationType.getConfigurationRepresentationClass());

        if (configurationType.isLocalizable()) {
            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);
            localizeConfiguration(representation, definition, loader);
        }

        return representation;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, User user) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                .getPrismProgramType() : null;
        return getConfigurationRepresentations(configurationType, resource, resource.getResourceScope(), locale, programType);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource,
            PrismLocale locale, PrismProgramType programType, WorkflowDefinition definition) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismProgramType configuredProgramType = getConfiguredProgramType(resource, programType);

        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, locale, configuredProgramType,
                definition);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismLocale locale, PrismProgramType programType) {
        Resource configuredResource = getConfiguredResource(resource);
        PrismProgramType configuredProgramType = getConfiguredProgramType(resource, programType);

        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope, locale,
                configuredProgramType);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public List<WorkflowConfiguration> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return customizationDAO.getConfigurationsWithVersion(configurationType, version);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithVersion(Resource resource, PrismConfiguration configurationType,
            Integer version) {
        List<WorkflowConfiguration> configurations = getConfigurationsWithVersion(configurationType, version);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public WorkflowConfiguration getConfigurationWithOrWithoutVersion(PrismConfiguration configurationType, Resource resource, User user, Enum<?> definitionId,
            Integer configurationVersion) {
        WorkflowPropertyDefinition definition = (WorkflowPropertyDefinition) getDefinitionById(configurationType, definitionId);

        WorkflowPropertyConfiguration configuration;
        if (configurationVersion == null) {
            configuration = (WorkflowPropertyConfiguration) getConfiguration(configurationType, resource, user, definition);
        } else {
            configuration = (WorkflowPropertyConfiguration) getConfigurationWithVersion(configurationType, definition, configurationVersion);
        }

        return configuration;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithOrWithoutVersion(PrismConfiguration configurationType,
            Resource resource, Integer configurationVersion) {
        if (configurationVersion == null) {
            return getConfigurationRepresentations(configurationType, resource, userService.getCurrentUser());
        } else {
            return getConfigurationRepresentationsWithVersion(resource, configurationType, configurationVersion);
        }
    }

    public List<WorkflowDefinition> getDefinitions(PrismConfiguration configurationType, PrismScope scope) {
        return customizationDAO.listDefinitions(configurationType, scope);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            Enum<?> definitionId) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, locale, programType, definitionId);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, scope, locale, programType);
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            Enum<?> definitionId) throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException,
            IOException, IntegrationException {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, locale, programType, definitionId);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType) throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException,
            WorkflowEngineException, IOException, IntegrationException {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, scope, locale, programType);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws CustomizationException,
            DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
        List<WorkflowDefinition> definitions = getDefinitions(configurationType, scope);

        if (configurationType.isValidateResponseSize()
                && (workflowConfigurationGroupDTO.isEmpty() || workflowConfigurationGroupDTO.size() != definitions.size())) {
            throw new Error();
        }

        createConfigurationGroup(configurationType, resource, scope, locale, programType, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws CustomizationException,
            DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {

        createConfigurationGroup(configurationType, resource, locale, programType, definitionId, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException, DeduplicationException,
            InstantiationException, IllegalAccessException {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, scope, locale, programType, valueDTOs);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, locale, programType, valueDTOs);
        }
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws CustomizationException,
            DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, locale, programType, definitionId, workflowConfigurationGroupDTO);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, locale, programType, definitionId, workflowConfigurationGroupDTO);
        }
    }

    public boolean isSystemDefault(WorkflowDefinition definition, PrismLocale locale, PrismProgramType programType) {
        if (locale == getSystemLocale()) {
            Integer precedence = definition.getScope().getPrecedence();
            if (precedence > INSTITUTION.getPrecedence() && programType == getSystemProgramType()) {
                return true;
            } else if (precedence < PROGRAM.getPrecedence() && programType == null) {
                return true;
            }
        }
        return false;
    }

    public void validateConfiguration(Resource resource, WorkflowDefinition definition, PrismLocale locale, PrismProgramType programType)
            throws CustomizationException {
        Integer resourcePrecedence = resource.getResourceScope().getPrecedence();
        Integer definitionPrecedence = definition.getScope().getPrecedence();
        if (resourcePrecedence.equals(SYSTEM.getPrecedence()) && locale == null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with no locale. System scope configurations must specify locale.");
        } else if (resourcePrecedence > SYSTEM.getPrecedence() && locale != null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with locale. Only system scope configurations may specify locale.");
        } else if (resourcePrecedence < PROGRAM.getPrecedence() && definitionPrecedence > INSTITUTION.getPrecedence() && programType == null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with no program type. Scopes within program must specify program type.");
        } else if (definitionPrecedence < PROGRAM.getPrecedence() && programType != null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with program type. Only scopes within program may specify program type.");
        }
    }

    public void validateRestoreDefaultConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType) throws CustomizationException {
        if (!Arrays.asList(INSTITUTION, PROGRAM).contains(resource.getResourceScope())) {
            throw new CustomizationException("Tried to restore default configurations as a system level entity");
        }
    }

    public void validateRestoreGlobalConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType) throws CustomizationException {
        if (!Arrays.asList(SYSTEM, INSTITUTION).contains(resource.getResourceScope())) {
            throw new CustomizationException("Tried to restore global configurations as a program level entity");
        }
    }

    public List<DisplayPropertyConfiguration> getAllLocalizedProperties() {
        return entityService.list(DisplayPropertyConfiguration.class);
    }

    public void createOrUpdateConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException {
        WorkflowConfiguration configuration = createConfiguration(configurationType, resource, locale, programType, workflowConfigurationDTO);
        entityService.createOrUpdate(configuration);
    }

    public WorkflowConfiguration createOrUpdateConfigurationUser(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException, DeduplicationException,
            InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
        WorkflowConfiguration configuration = createConfiguration(configurationType, resource, locale, programType, workflowConfigurationDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
        return entityService.createOrUpdate(configuration);
    }

    public boolean isConfigurationEnabled(PrismConfiguration configurationType, Resource resource, Enum<?> definitionId) {
        Class<?> configurationClass = configurationType.getConfigurationClass();
        if (WorkflowConfigurationVersioned.class.isAssignableFrom(configurationClass)) {
            WorkflowConfiguration configuration = getConfigurationWithVersion(configurationType, definitionId,
                    resource.getWorkflowPropertyConfigurationVersion());
            return configuration != null && BooleanUtils.isTrue((Boolean) ReflectionUtils.getProperty(configuration, "enabled"));
        }
        throw new Error();
    }

    private WorkflowConfiguration createConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException {
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), workflowConfigurationDTO.getDefinitionId());
        validateConfiguration(resource, definition, locale, programType);
        WorkflowConfiguration configuration = mapper.map(workflowConfigurationDTO, configurationType.getConfigurationClass());
        configuration.setResource(resource);
        configuration.setLocale(locale);
        configuration.setProgramType(programType);
        ReflectionUtils.setProperty(configuration, configurationType.getDefinitionPropertyName(), definition);
        configuration.setSystemDefault(isSystemDefault(definition, locale, programType));
        return configuration;
    }

    private Resource getConfiguredResource(Resource resource) {
        return resource.getResourceScope().getPrecedence() > PrismScope.PROGRAM.getPrecedence() ? resource.getProgram() : resource;
    }

    private List<WorkflowConfigurationRepresentation> parseRepresentations(Resource resource, PrismConfiguration configurationType,
            List<WorkflowConfiguration> configurations) {
        List<WorkflowConfigurationRepresentation> representations = Lists.newLinkedList();
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);

        if (configurations.isEmpty()) {
            return representations;
        } else {
            WorkflowConfiguration stereotype = configurations.get(0);

            Resource stereotypeResource = stereotype.getResource();
            PrismLocale stereotypeLocale = stereotype.getLocale();
            PrismProgramType stereotypeProgramType = stereotype.getProgramType();

            for (WorkflowConfiguration configuration : configurations) {
                if (Objects.equal(configuration.getResource(), stereotypeResource) && Objects.equal(configuration.getLocale(), stereotypeLocale)
                        && Objects.equal(configuration.getProgramType(), stereotypeProgramType)) {
                    WorkflowConfigurationRepresentation representation = mapper.map(configuration, configurationType.getConfigurationRepresentationClass());
                    if (configurationType.isLocalizable()) {
                        localizeConfiguration(representation, configuration.getDefinition(), loader);
                    }
                    representations.add(representation);
                }
            }

            return representations;
        }
    }

    private void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException, DeduplicationException, InstantiationException,
            IllegalAccessException {
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            createOrUpdateConfiguration(configurationType, resource, locale, programType, valueDTO);
        }
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        restoreDefaultConfiguration(configurationType, resource, locale, programType, definitionId);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, locale, programType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        restoreDefaultConfiguration(configurationType, resource, scope, locale, programType);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, locale, programType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        Integer version = null;
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            WorkflowConfiguration transientConfiguration = createConfiguration(configurationType, resource, locale, programType, valueDTO);
            ReflectionUtils.setProperty(transientConfiguration, "active", true);

            WorkflowConfiguration persistentConfiguration;
            if (version == null) {
                entityService.save(transientConfiguration);
                persistentConfiguration = transientConfiguration;
            } else {
                ReflectionUtils.setProperty(transientConfiguration, "version", version);
                persistentConfiguration = entityService.createOrUpdate(transientConfiguration);
            }

            version = version == null ? persistentConfiguration.getId() : version;
            ReflectionUtils.setProperty(persistentConfiguration, "version", version);
        }
    }

    private void localizeConfiguration(WorkflowConfigurationRepresentation representation, WorkflowDefinition definition, PropertyLoader loader) {
        if (ReflectionUtils.hasProperty(representation, "category")) {
            ReflectionUtils.setProperty(representation, "category", ReflectionUtils.getProperty(definition, "category"));
        }

        ReflectionUtils.setProperty(representation, "label",
                loader.load((PrismDisplayPropertyDefinition) ReflectionUtils.getProperty(definition.getId(), "label")));
        ReflectionUtils.setProperty(representation, "tooltip",
                loader.load((PrismDisplayPropertyDefinition) ReflectionUtils.getProperty(definition.getId(), "tooltip")));
    }

    private PrismProgramType getConfiguredProgramType(Resource resource, PrismProgramType programType) {
        return resource.getResourceScope() == PrismScope.PROGRAM ? resource.getProgram().getProgramType().getPrismProgramType() : programType;
    }

}
